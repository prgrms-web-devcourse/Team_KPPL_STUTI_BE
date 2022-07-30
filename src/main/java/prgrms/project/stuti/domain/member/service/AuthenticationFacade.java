package prgrms.project.stuti.domain.member.service;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.member.controller.MemberMapper;
import prgrms.project.stuti.domain.member.controller.dto.MemberSaveRequest;
import prgrms.project.stuti.domain.member.model.Member;
import prgrms.project.stuti.domain.member.model.MemberRole;
import prgrms.project.stuti.domain.member.service.dto.MemberIdResponse;
import prgrms.project.stuti.global.cache.model.TemporaryMember;
import prgrms.project.stuti.global.cache.service.BlackListTokenService;
import prgrms.project.stuti.global.cache.service.RefreshTokenService;
import prgrms.project.stuti.global.cache.service.TemporaryMemberService;
import prgrms.project.stuti.global.error.exception.TokenException;
import prgrms.project.stuti.global.token.TokenGenerator;
import prgrms.project.stuti.global.token.TokenService;
import prgrms.project.stuti.global.token.TokenType;
import prgrms.project.stuti.global.token.Tokens;

@Service
@RequiredArgsConstructor
public class AuthenticationFacade {
	private final MemberService memberService;
	private final TokenService tokenService;
	private final BlackListTokenService blackListTokenService;
	private final RefreshTokenService refreshTokenService;
	private final TemporaryMemberService temporaryMemberService;
	private final TokenGenerator tokenGenerator;

	public MemberIdResponse signupMember(MemberSaveRequest memberSaveRequest) {
		Optional<TemporaryMember> optionalMember = temporaryMemberService.findById(memberSaveRequest.email());

		if (optionalMember.isEmpty()) {
			TokenException.TOKEN_EXPIRATION.get();
		}
		TemporaryMember temporaryMember = optionalMember.get();
		Member member = memberService.signup(MemberMapper.toMemberDto(memberSaveRequest), temporaryMember);

		return MemberConverter.toMemberIdResponse(member.getId());
	}

	public Tokens makeTokens(Long memberId) {
		Tokens tokens = tokenGenerator.generateTokens(memberId.toString(),
			MemberRole.ROLE_MEMBER.name());
		refreshTokenService.save(memberId, tokens, tokenService.getRefreshPeriod());

		return tokens;
	}

	public long accessTokenPeriod() {
		return tokenService.getAccessTokenPeriod();
	}

	public void logout(HttpServletRequest request) {
		String accessToken = tokenService.resolveToken(request);

		if (accessToken != null) {
			long expiration = tokenService.getExpiration(accessToken);
			refreshTokenService.findById(accessToken).ifPresent((refreshTokenService::delete));
			blackListTokenService.logout(tokenService.tokenWithType(accessToken, TokenType.JWT_BLACKLIST), expiration);
		}
	}

	public List<Member> getMembers() {
		return memberService.members();
	}
}
