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

	public Optional<RefreshToken> findById(String accessToken) {
		return refreshTokenRepository.findById(accessToken);
	}

	public void save(RefreshToken refreshToken) {
		refreshTokenRepository.save(refreshToken);
	}

	public void save(Long memberId, Tokens tokens, long refreshPeriod) {
		Date now = new Date();
		RefreshToken refreshToken = RefreshToken.builder()
			.accessTokenValue(tokens.accessToken())
			.memberId(memberId)
			.refreshTokenValue(tokens.refreshToken())
			.createdTime(now)
			.expirationTime(new Date(now.getTime() + refreshPeriod))
			.expiration(refreshPeriod)
			.build();

		refreshTokenRepository.save(refreshToken);
	}

	public void delete(RefreshToken refreshToken) {
		refreshTokenRepository.delete(refreshToken);
	}

}
