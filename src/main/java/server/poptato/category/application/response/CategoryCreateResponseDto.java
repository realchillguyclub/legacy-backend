package server.poptato.category.application.response;

public record CategoryCreateResponseDto (
        Long categoryId
){

    public static CategoryCreateResponseDto of(Long categoryId) {
        return new CategoryCreateResponseDto(
                categoryId
        );
    }
}
