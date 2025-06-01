package server.poptato.user.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import server.poptato.global.exception.CustomException;
import server.poptato.user.domain.entity.User;
import server.poptato.user.domain.repository.UserRepository;
import server.poptato.user.status.UserErrorStatus;

@Component
@RequiredArgsConstructor
public class UserValidator {
    private final UserRepository userRepository;

    public void checkIsExistUser(Long userId){
        userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorStatus._USER_NOT_EXIST));
    }

    public User checkIsExistAndReturnUser(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorStatus._USER_NOT_EXIST));
    }
}
