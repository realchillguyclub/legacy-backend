package server.poptato.external.oauth.apple;

import com.google.gson.*;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import server.poptato.auth.status.AuthErrorStatus;
import server.poptato.global.exception.CustomException;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class AppleTokenVerifier {

    private final AppleApiClient appleApiClient;

    /**
     * Apple JWT 토큰을 검증하고 클레임을 추출함.
     *
     * @param idToken Apple에서 받은 id_token
     * @return JWT 클레임 정보 (sub, email 등)
     */
    public JsonObject verifyIdToken(String idToken) {
        try {
            String keysResponse = appleApiClient.getApplePublicKeys();
            JsonArray publicKeys = JsonParser.parseString(keysResponse).getAsJsonObject().getAsJsonArray("keys");

            PublicKey publicKey = getMatchingPublicKey(idToken, publicKeys);

            return JsonParser.parseString(new Gson().toJson(
                    Jwts.parserBuilder()
                            .setSigningKey(publicKey)
                            .build()
                            .parseClaimsJws(idToken)
                            .getBody()
            )).getAsJsonObject();
        } catch (ExpiredJwtException e) {
            throw new CustomException(AuthErrorStatus._EXPIRED_APPLE_ID_TOKEN);
        } catch (JwtException e) {
            throw new CustomException(AuthErrorStatus._INVALID_APPLE_ID_TOKEN);
        }
    }

    /**
     * Apple 공개 키 리스트에서 id_token에 맞는 공개 키를 찾아 PublicKey 객체로 변환.
     */
    private PublicKey getMatchingPublicKey(String idToken, JsonArray publicKeys) {
        String[] tokenParts = idToken.split("\\.");
        String headerJson = new String(Base64.getUrlDecoder().decode(tokenParts[0]));

        JsonObject headerObject = JsonParser.parseString(headerJson).getAsJsonObject();
        String kid = headerObject.get("kid").getAsString();
        String alg = headerObject.get("alg").getAsString();

        JsonObject matchingKey = findMatchingPublicKey(publicKeys, kid, alg);
        if (matchingKey == null) {
            throw new CustomException(AuthErrorStatus._NOT_FOUND_VALID_PUBLIC_KEY);
        }

        return generatePublicKey(matchingKey);
    }

    /**
     * 공개 키 JSON 데이터에서 kid, alg가 일치하는 키 찾기.
     */
    private JsonObject findMatchingPublicKey(JsonArray publicKeys, String kid, String alg) {
        for (JsonElement key : publicKeys) {
            JsonObject keyObject = key.getAsJsonObject();
            if (kid.equals(keyObject.get("kid").getAsString()) &&
                    alg.equals(keyObject.get("alg").getAsString())) {
                return keyObject;
            }
        }
        return null;
    }

    /**
     * 공개 키 JSON 데이터를 실제 PublicKey 객체로 변환.
     */
    private PublicKey generatePublicKey(JsonObject keyObject) {
        try {
            BigInteger modulus = new BigInteger(1, Base64.getUrlDecoder().decode(keyObject.get("n").getAsString()));
            BigInteger exponent = new BigInteger(1, Base64.getUrlDecoder().decode(keyObject.get("e").getAsString()));

            RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(modulus, exponent);
            return KeyFactory.getInstance("RSA").generatePublic(publicKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new CustomException(AuthErrorStatus._PUBLIC_KEY_GENERATION_FAILED);
        }
    }
}
