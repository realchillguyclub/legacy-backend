package server.poptato.external.oauth.apple;

import com.google.gson.*;
import io.jsonwebtoken.Jwts;
import lombok.val;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.poptato.auth.exception.AuthException;
import server.poptato.auth.exception.errorcode.AuthExceptionErrorCode;
import server.poptato.external.oauth.SocialService;
import server.poptato.external.oauth.SocialUserInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
public class AppleSocialService  extends SocialService {

    @Override
    public SocialUserInfo getUserData(String accessToken) {
        val publicKeyList = getApplePublicKeys();
        val publicKey = makePublicKey(accessToken, publicKeyList);

        val userInfo = Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(getTokenFromBearerString(accessToken))
                .getBody();

        val userInfoObject = (JsonObject) JsonParser.parseString(new Gson().toJson(userInfo));

        return new SocialUserInfo(
                userInfoObject.get("sub").getAsString(),      // socialId
                userInfoObject.has("nickname") ? userInfoObject.get("nickname").getAsString() : null,
                userInfoObject.has("email") ? userInfoObject.get("email").getAsString() : null,
                null);
    }

    private JsonArray getApplePublicKeys() {
        val connection = sendHttpRequest();
        val result = getHttpResponse(connection);
        val keys = (JsonObject)JsonParser.parseString(result.toString());
        return (JsonArray)keys.get("keys");
    }

    private HttpURLConnection sendHttpRequest() {
        try {
            val url = new URL("https://appleid.apple.com/auth");
            val connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod(HttpMethod.GET.name());
            return connection;
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    private StringBuilder getHttpResponse(HttpURLConnection connection) {
        try {
            val bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            return splitHttpResponse(bufferedReader);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    private StringBuilder splitHttpResponse(BufferedReader bufferedReader) {
        try {
            val result = new StringBuilder();

            String line;
            while (Objects.nonNull(line = bufferedReader.readLine())) {
                result.append(line);
            }
            bufferedReader.close();

            return result;
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    private PublicKey makePublicKey(String accessToken, JsonArray publicKeyList) {
        val decodeArray = accessToken.split("\\.");
        val header = new String(Base64.getDecoder().decode(getTokenFromBearerString(decodeArray[0])));

        val kid = ((JsonObject)JsonParser.parseString(header)).get("kid");
        val alg = ((JsonObject)JsonParser.parseString(header)).get("alg");
        val matchingPublicKey = findMatchingPublicKey(publicKeyList, kid, alg);

        if (Objects.isNull(matchingPublicKey)) {
            throw new AuthException(AuthExceptionErrorCode.INVALID_TOKEN);
        }

        return getPublicKey(matchingPublicKey);
    }

    private String getTokenFromBearerString(String token) {
        return token.replaceFirst("Bearer ", "");
    }

    private JsonObject findMatchingPublicKey(JsonArray publicKeyList, JsonElement kid, JsonElement alg) {
        for (JsonElement publicKey : publicKeyList) {
            val publicKeyObject = publicKey.getAsJsonObject();
            val publicKid = publicKeyObject.get("kid");
            val publicAlg = publicKeyObject.get("alg");

            if (Objects.equals(kid, publicKid) && Objects.equals(alg, publicAlg)) {
                return publicKeyObject;
            }
        }

        return null;
    }

    private PublicKey getPublicKey(JsonObject object) {
        try {
            val modulus = object.get("n").toString();
            val exponent = object.get("e").toString();

            val quotes = 1;
            val modulusBytes = Base64.getUrlDecoder().decode(modulus.substring(quotes, modulus.length() - quotes));
            val exponentBytes = Base64.getUrlDecoder().decode(exponent.substring(quotes, exponent.length() - quotes));

            val positiveNumber = 1;
            val modulusValue = new BigInteger(positiveNumber, modulusBytes);
            val exponentValue = new BigInteger(positiveNumber, exponentBytes);

            val publicKeySpec = new RSAPublicKeySpec(modulusValue, exponentValue);
            val keyFactory = KeyFactory.getInstance("RSA");

            return keyFactory.generatePublic(publicKeySpec);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException exception) {
            throw new AuthException(AuthExceptionErrorCode.INVALID_TOKEN);
        }
    }
}
