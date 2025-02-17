package server.poptato.global.util;

public class FileUtil {

    private static String DEFAULT_IMAGE_URL_EXTENSION = ".svg";

    /**
     * 파일의 확장자명을 mobileType에 맞게 변환합니다.
     * @param imageUrl 기존 이미지 url
     * @param extension 유저의 모바일 타입에 대해 이미지에 적용할 확장자
     * @return 확장자가 바뀐 이미지 url
     */
    public static String changeFileExtension(String imageUrl, String extension) {
        return imageUrl.replace(DEFAULT_IMAGE_URL_EXTENSION, extension);
    }
}
