package prgrms.project.stuti.global.cache.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.global.cache.model.BlackListToken;
import prgrms.project.stuti.global.cache.repository.BlackListTokenRepository;

@Service
@RequiredArgsConstructor
public class BlackListTokenService {

    private final BlackListTokenRepository blackListTokenRepository;

    public Optional<BlackListToken> findById(String id) {
        return blackListTokenRepository.findById(id);
    }

    public void logout(String accessTokenWithType, long expiration) {
        // 1. 먼저 요청받은 AccessToken 유효성을 검증합니다.
        // 2. 유효성 검증이 끝나고 액세스 토큰을 통해 Authentication 객체를 그리고 저장된 User email 정보를 가져옵니다.
        // 3. user email (Redis key 값)을 통해 저장된 RefreshToken이 있는지 여부를 확인하여 있다면 삭제합니다.
        // 4. 요청으로 들어온 액세스 토큰의 유효시간을 가져와서 해당 액세스 토큰을 키 값으로 하고,
        // 유효시간을 적용시켜 Redis에 블랙리스트로 등록합니다.
        BlackListToken blackListToken = new BlackListToken(accessTokenWithType, expiration);
        blackListTokenRepository.save(blackListToken);
    }

}
