package server.poptato.external.oauth.apple;

import com.google.gson.*;
import io.jsonwebtoken.Jwts;
import lombok.val;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.poptato.auth.status.AuthErrorStatus;
import server.poptato.external.oauth.SocialService;
import server.poptato.external.oauth.SocialUserInfo;
import server.poptato.global.exception.CustomException;

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
public class AppleSocialService extends SocialService {

    /**
     * Apple OAuth를 통해 사용자 정보를 가져옵니다.
     *
     * @param accessToken 사용자 인증을 위한 액세스 토큰
     * @return 소셜 사용자 정보 객체
     */
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
                userInfoObject.get("sub").getAsString(),      // 소셜 ID
                userInfoObject.has("nickname") ? userInfoObject.get("nickname").getAsString() : null,
                userInfoObject.has("email") ? userInfoObject.get("email").getAsString() : null,
                null
        );
    }

    /**
     * Apple의 공개 키 목록을 가져옵니다.
     *
     * @return Apple 공개 키 리스트
     */
    private JsonArray getApplePublicKeys() {
        val connection = sendHttpRequest();
        val result = getHttpResponse(connection);
        val keys = (JsonObject) JsonParser.parseString(result.toString());
        return (JsonArray) keys.get("keys");
    }

    /**
     * Apple 공개 키를 가져오기 위해 HTTP 요청을 보냅니다.
     *
     * @return HTTP 연결 객체
     */
    private HttpURLConnection sendHttpRequest() {
        try {
            val url = new URL("https://appleid.apple.com/auth");
            val connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(HttpMethod.GET.name());
            return connection;
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Apple 서버로부터 받은 HTTP 응답을 반환합니다.
     *
     * @param connection HTTP 연결 객체
     * @return HTTP 응답 데이터
     */
    private StringBuilder getHttpResponse(HttpURLConnection connection) {
        try {
            val bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            return splitHttpResponse(bufferedReader);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * HTTP 응답 데이터를 라인별로 읽어와 문자열로 변환합니다.
     *
     * @param bufferedReader 버퍼 리더 객체
     * @return 응답 문자열
     */
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

    /**
     * Apple JWT를 검증하기 위한 PublicKey를 생성합니다.
     *
     * @param accessToken 액세스 토큰
     * @param publicKeyList 공개 키 리스트
     * @return 생성된 PublicKey 객체
     */
    private PublicKey makePublicKey(String accessToken, JsonArray publicKeyList) {
        val decodeArray = accessToken.split("\\.");
        val header = new String(Base64.getDecoder().decode(getTokenFromBearerString(decodeArray[0])));

        val kid = ((JsonObject) JsonParser.parseString(header)).get("kid");
        val alg = ((JsonObject) JsonParser.parseString(header)).get("alg");
        val matchingPublicKey = findMatchingPublicKey(publicKeyList, kid, alg);

        if (Objects.isNull(matchingPublicKey)) {
            throw new CustomException(AuthErrorStatus._NOT_FOUND_VALID_PUBLIC_KEY);
        }

        return getPublicKey(matchingPublicKey);
    }

    /**
     * Bearer 문자열에서 토큰을 추출합니다.
     *
     * @param token Bearer 토큰 문자열
     * @return Bearer를 제외한 순수 토큰 문자열
     */
    private String getTokenFromBearerString(String token) {
        return token.replaceFirst("Bearer ", "");
    }

    /**
     * Apple 공개 키 리스트에서 매칭되는 키를 찾습니다.
     *
     * @param publicKeyList 공개 키 리스트
     * @param kid Key ID
     * @param alg 알고리즘
     * @return 매칭되는 공개 키 객체
     */
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

    /**
     * 공개 키 데이터를 기반으로 PublicKey 객체를 생성합니다.
     *
     * @param object 공개 키 데이터 객체
     * @return 생성된 PublicKey 객체
     */
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
            throw new CustomException(AuthErrorStatus._PUBLIC_KEY_GENERATION_FAILED);
        }
    }
}
