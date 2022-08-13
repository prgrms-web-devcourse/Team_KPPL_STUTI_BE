package prgrms.project.stuti.global.security;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import prgrms.project.stuti.domain.member.model.Email;
import prgrms.project.stuti.domain.member.model.Member;
import prgrms.project.stuti.domain.member.service.MemberService;
import prgrms.project.stuti.global.cache.model.TemporaryMember;
import prgrms.project.stuti.global.cache.repository.TemporaryMemberRepository;
import prgrms.project.stuti.global.util.CoderUtil;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
	private final MemberService memberService;
	private final TemporaryMemberRepository temporaryMemberRepository;

	@Value("${app.oauth.signupTime}")
	private Long signupTime;

	@Value("${app.oauth.domain}")
	private String domain;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException {
		// 인증 된 principal 를 가지고 온다.
		OAuth2User oAuth2User = (OAuth2User)authentication.getPrincipal();
		Map<String, Object> attributes = oAuth2User.getAttributes();
		String email = (String)attributes.get("email");
		Optional<String> optionalName = Optional.ofNullable((String)attributes.get("name"));
		String name = optionalName.orElse("");
		String picture = (String)attributes.get("picture");

		Optional<Member> optionalMember = memberService.getMember(new Email(email));
		// 최초 로그인이라면 추가 회원가입 처리를 한다.
		if (optionalMember.isEmpty()) {
			Optional<TemporaryMember> optionalTemporaryMember = temporaryMemberRepository.findById(email);
			TemporaryMember temporaryMember = TemporaryMember.builder()
				.email(email)
				.nickname(name)
				.imageUrl(picture)
				.expiration(signupTime)
				.build();

			// temporary member 가 없으면 생성
			if (optionalTemporaryMember.isEmpty()) {
				temporaryMemberRepository.save(temporaryMember);
			} else {
				// 있으면 기존의 회원가입시도가 있었으므로 그냥 가지고 온다.
				temporaryMember = optionalTemporaryMember.get();
			}

			String temporaryMemberEmail = temporaryMember.getEmail();

			String param1 = "?email=" + CoderUtil.encode(temporaryMemberEmail);
			String param2 = "&name=" + CoderUtil.encode(name);
			String targetUri = domain + "/signup" + param1 + param2;

			response.sendRedirect(targetUri);
			return;
		}
		// 이미 회원가입을 한 유저의 경우
		Long memberId = optionalMember.get().getId();

		String targetUri = domain + "/login" + "?id=" + memberId;
		response.sendRedirect(targetUri);

	}
}
