package server.poptato.external.firebase.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import server.poptato.user.domain.repository.MobileRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FcmTokenService {

    private final MobileRepository mobileRepository;

    /**
     * 1개월 이상 사용되지 않은 토큰을 삭제합니다.
     */
    public void deleteOldFcmTokens() {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        mobileRepository.deleteOldTokens(oneMonthAgo);
    }

    /**
     * 전송 실패로 판단된 토큰을 삭제합니다.
     *
     * @param clientId 삭제할 토큰 ID
     */
    public void deleteInvalidToken(String clientId) {
        mobileRepository.deleteByClientId(clientId);
    }
}
