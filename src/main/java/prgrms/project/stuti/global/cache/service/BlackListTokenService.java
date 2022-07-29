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
		BlackListToken blackListToken = new BlackListToken(accessTokenWithType, expiration);
		blackListTokenRepository.save(blackListToken);
	}

}
