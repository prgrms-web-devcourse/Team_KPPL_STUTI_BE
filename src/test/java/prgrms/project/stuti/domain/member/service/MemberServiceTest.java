package prgrms.project.stuti.domain.member.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import prgrms.project.stuti.config.ServiceTestConfig;
import prgrms.project.stuti.domain.member.controller.dto.MemberUpdateRequest;
import prgrms.project.stuti.domain.member.controller.MemberMapper;
import prgrms.project.stuti.domain.member.model.Career;
import prgrms.project.stuti.domain.member.model.Email;
import prgrms.project.stuti.domain.member.model.Field;
import prgrms.project.stuti.domain.member.model.Mbti;
import prgrms.project.stuti.domain.member.model.Member;
import prgrms.project.stuti.domain.member.service.dto.MemberResponse;
import prgrms.project.stuti.global.error.exception.MemberException;

class MemberServiceTest extends ServiceTestConfig {

	@Autowired
	MemberService memberService;

	@Test
	@DisplayName("이메일을 통해 유저를 가지고 온다")
	void testGetUserByEmail() {
		// given
		String testEmail = "test@gmail.com";

		// when
		Member member = memberService.getMember(new Email(testEmail))
			.orElseThrow(() -> MemberException.notFoundMember(1L));

		// then
		assertAll(
			() -> assertThat(member.getEmail()).isEqualTo("test@gmail.com"),
			() -> assertThat(member.getNickName()).isEqualTo("nickname"),
			() -> assertThat(member.getField()).isEqualTo(Field.ANDROID),
			() -> assertThat(member.getCareer()).isEqualTo(Career.JUNIOR),
			() -> assertThat(member.getMbti()).isEqualTo(Mbti.ENFJ),
			() -> assertThat(member.getProfileImageUrl()).isEqualTo("www.s3.com")
		);
	}

	@Test
	@DisplayName("멤버를 수정한다")
	void testInvalidPutMember() {
		// given
		Long memberId = member.getId();

		MemberUpdateRequest memberUpdateRequest = MemberUpdateRequest.builder()
			.nickname("test6")
			.field(Field.ANDROID)
			.career(Career.JUNIOR)
			.MBTI(Mbti.ENFJ)
			.githubUrl("edit3.github")
			.blogUrl("edit3.blog")
			.build();

		// when
		MemberResponse memberResponse = memberService.editMember(memberId,
			MemberMapper.toMemberPutDto(memberUpdateRequest));

		// then
		assertAll(
			() -> assertThat(memberResponse.id()).isEqualTo(memberId),
			() -> assertThat(memberResponse.email()).isEqualTo("test@gmail.com"),
			() -> assertThat(memberResponse.nickname()).isEqualTo("test6"),
			() -> assertThat(memberResponse.field()).isEqualTo(Field.ANDROID),
			() -> assertThat(memberResponse.career()).isEqualTo(Career.JUNIOR),
			() -> assertThat(memberResponse.MBTI()).isEqualTo(Mbti.ENFJ),
			() -> assertThat(memberResponse.githubUrl()).isEqualTo("edit3.github"),
			() -> assertThat(memberResponse.blogUrl()).isEqualTo("edit3.blog")
		);
	}

	@Test
	@DisplayName("멤버 수정 시 다른 유저와 동일한 닉네임은 사용할 수 없다")
	void testPutMember() {
		// given
		Long memberId = member.getId();
		String duplicatedNickname = otherMember.getNickName();

		MemberUpdateRequest memberUpdateRequest = MemberUpdateRequest.builder()
			.nickname(duplicatedNickname)
			.field(Field.ANDROID)
			.career(Career.JUNIOR)
			.MBTI(Mbti.ENFJ)
			.githubUrl("edit3.github")
			.blogUrl("edit3.blog")
			.build();

		// when // then
		assertThrows(MemberException.class, () -> {
			memberService.editMember(memberId, MemberMapper.toMemberPutDto(memberUpdateRequest));
		});
	}
}