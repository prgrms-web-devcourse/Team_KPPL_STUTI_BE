package prgrms.project.stuti.domain.member.service;

import java.util.Date;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.member.model.Member;
import prgrms.project.stuti.domain.member.model.MemberRole;
import prgrms.project.stuti.domain.member.repository.MemberRepository;
import prgrms.project.stuti.domain.member.service.dto.MemberDto;
import prgrms.project.stuti.domain.member.service.dto.MemberIdResponse;
import prgrms.project.stuti.global.cache.model.BlackListToken;
import prgrms.project.stuti.global.cache.model.RefreshToken;
import prgrms.project.stuti.global.cache.model.TemporaryMember;
import prgrms.project.stuti.global.cache.repository.BlackListTokenRepository;
import prgrms.project.stuti.global.cache.repository.RefreshTokenRepository;
import prgrms.project.stuti.global.cache.repository.TemporaryMemberRepository;
import prgrms.project.stuti.global.error.exception.MemberException;
import prgrms.project.stuti.global.token.Tokens;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
	private final MemberRepository memberRepository;
	private final BlackListTokenRepository blackListTokenRepository;
	private final RefreshTokenRepository refreshTokenRepository;
	private final TemporaryMemberRepository temporaryMemberRepository;

	public MemberIdResponse signupMember(MemberDto memberDto) {
		Optional<TemporaryMember> optionalMember = temporaryMemberRepository.findById(memberDto.email());

		if (optionalMember.isEmpty()) {
			throw MemberException.tokenExpiration();
		}
		TemporaryMember temporaryMember = optionalMember.get();

		String email = memberDto.email();
		memberRepository.findMemberByEmail(email).ifPresent(member -> {
			throw MemberException.registeredMember(email);
		});

		Member member = memberRepository.save(Member.builder()
			.email(memberDto.email())
			.nickName(memberDto.nickname())
			.field(memberDto.field())
			.career(memberDto.career())
			.profileImageUrl(temporaryMember.getImageUrl())
			.mbti(memberDto.MBTI())
			.memberRole(MemberRole.ROLE_MEMBER)
			.build());

		return MemberConverter.toMemberIdResponse(member.getId());
	}

	public Tokens saveRefreshToken(Long memberId, Tokens tokens, long refreshPeriod) {
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

		return tokens;
	}

	public void logout(String accessToken, long expiration, String accessTokenWithType) {
		if (accessToken != null) {
			refreshTokenRepository.findById(accessToken)
				.ifPresent((refreshTokenRepository::delete));

			BlackListToken blackListToken = new BlackListToken(accessTokenWithType, expiration);
			blackListTokenRepository.save(blackListToken);
		}
	}
}
