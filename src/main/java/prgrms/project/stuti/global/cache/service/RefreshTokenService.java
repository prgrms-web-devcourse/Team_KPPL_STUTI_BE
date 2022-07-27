package prgrms.project.stuti.global.cache.service;

import java.util.Date;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.global.cache.model.RefreshToken;
import prgrms.project.stuti.global.cache.repository.RefreshTokenRepository;
import prgrms.project.stuti.global.token.Tokens;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public void findAndDelete(String accessToken) {
        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findById(accessToken);
        if (optionalRefreshToken.isPresent()) {
            RefreshToken refreshToken = optionalRefreshToken.get();
            refreshTokenRepository.delete(refreshToken);
        }
    }

    public void save(Tokens tokens, long refreshPeriod) {
        Date now = new Date();
        RefreshToken refreshToken = new RefreshToken(
            tokens.getAccessToken(),
            tokens.getRefreshToken(),
            now,
            new Date(now.getTime() + refreshPeriod)
        );
        refreshTokenRepository.save(refreshToken);
    }

}
