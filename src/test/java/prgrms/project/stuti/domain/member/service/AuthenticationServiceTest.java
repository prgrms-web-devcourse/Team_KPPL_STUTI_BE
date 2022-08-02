package prgrms.project.stuti.domain.member.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import prgrms.project.stuti.domain.member.model.Career;
import prgrms.project.stuti.domain.member.model.Field;
import prgrms.project.stuti.domain.member.model.Mbti;
import prgrms.project.stuti.domain.member.service.dto.MemberDto;
import prgrms.project.stuti.domain.member.service.dto.MemberIdResponse;
import prgrms.project.stuti.global.cache.model.TemporaryMember;
import prgrms.project.stuti.global.cache.repository.TemporaryMemberRepository;
import prgrms.project.stuti.global.token.Tokens;

@SpringBootTest
class AuthenticationServiceTest {
	@Autowired
	AuthenticationService authenticationService;
	@MockBean
	TemporaryMemberRepository temporaryMemberRepository;

	@Test
	@DisplayName("회원가입을 한다.")
	void testSignupMember() {
		// given
		MemberDto memberDto = MemberDto.builder()
			.email("test@test.com")
			.nickname("test")
			.field(Field.ANDROID)
			.career(Career.JUNIOR)
			.MBTI(Mbti.ENFJ)
			.build();

		TemporaryMember temporaryMember = TemporaryMember.builder()
			.email("test@test.com")
			.imageUrl("test.s3.com")
			.nickname("test")
			.expiration(500000L)
			.build();

		given(temporaryMemberRepository.findById("test@test.com")).willReturn(Optional.of(temporaryMember));
		// when
		MemberIdResponse memberIdResponse = authenticationService.signupMember(memberDto);

		// then
		assertThat(memberIdResponse.memberId()).isNotNull();
	}

	@Test
	@DisplayName("RefreshToken 을 저장한다.")
	void testSaveRefreshToken() {
		// given
		Long memberId = 1L;
		Tokens tokens = new Tokens("accessToken", "refreshToken");
		Long refreshPeriod = 500L;

		// when
		Tokens madeTokens = authenticationService.saveRefreshToken(memberId, tokens, refreshPeriod);

		// then
		assertThat(madeTokens.accessToken()).isEqualTo("accessToken");
		assertThat(madeTokens.refreshToken()).isEqualTo("refreshToken");
	}
}