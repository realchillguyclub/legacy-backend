package server.poptato.user.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import server.poptato.user.domain.entity.User;
import server.poptato.user.domain.repository.UserRepository;
import server.poptato.user.exception.UserException;
import server.poptato.user.exception.errorcode.UserExceptionErrorCode;

import static server.poptato.user.exception.errorcode.UserExceptionErrorCode.USER_NOT_EXIST;

@Component
@RequiredArgsConstructor
public class UserValidator {
    private final UserRepository userRepository;

    public void checkIsExistUser(Long userId){
        userRepository.findById(userId).orElseThrow(() -> new UserException(USER_NOT_EXIST));
    }

    public User checkIsExistAndReturnUser(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserExceptionErrorCode.USER_NOT_EXIST));
    }
}
