package prgrms.project.stuti.domain.member.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import prgrms.project.stuti.domain.member.controller.dto.MemberPutRequest;
import prgrms.project.stuti.domain.member.controller.dto.MemberSaveRequest;
import prgrms.project.stuti.domain.member.controller.MemberMapper;
import prgrms.project.stuti.domain.member.model.Career;
import prgrms.project.stuti.domain.member.model.Email;
import prgrms.project.stuti.domain.member.model.Field;
import prgrms.project.stuti.domain.member.model.Mbti;
import prgrms.project.stuti.domain.member.model.Member;
import prgrms.project.stuti.domain.member.service.dto.MemberResponse;
import prgrms.project.stuti.global.cache.model.TemporaryMember;

@SpringBootTest
@Transactional
class MemberServiceTest {

	@Autowired
	MemberService memberService;

	@Test
	@DisplayName("회원가입을 한다.")
	void testSignup() {
		// given
		MemberSaveRequest memberSaveRequest = MemberSaveRequest.builder()
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

		// when
		Member savedMember = memberService.signup(MemberMapper.toMemberDto(memberSaveRequest), temporaryMember);

		// then
		assertThat(savedMember.getId()).isNotNull();
	}

	@Test
	@DisplayName("이메일을 통해 유저를 가지고 온다")
	void testGetUserByEmail() {
		// given
		String testEmail = saveMember().getEmail();

		// when
		Member member = memberService.getMember(new Email(testEmail)).orElseThrow(RuntimeException::new);

		// then
		assertAll(
			() -> assertThat(member.getEmail()).isEqualTo("test@test.com"),
			() -> assertThat(member.getNickName()).isEqualTo("test"),
			() -> assertThat(member.getField()).isEqualTo(Field.ANDROID),
			() -> assertThat(member.getCareer()).isEqualTo(Career.JUNIOR),
			() -> assertThat(member.getMbti()).isEqualTo(Mbti.ENFJ),
			() -> assertThat(member.getProfileImageUrl()).isEqualTo("test.s3.com")
		);
	}

	@Test
	@DisplayName("멤버를 수정한다")
	void testPutMember() {
		// given
		Member member = saveMember();

		Long memberId = member.getId();

		MemberPutRequest memberPutRequest = MemberPutRequest.builder()
			.id(memberId)
			.email("edit@test.com")
			.profileImageUrl("s3.edit.com")
			.nickname("edit")
			.field(Field.ANDROID)
			.career(Career.JUNIOR)
			.MBTI(Mbti.ENFJ)
			.githubUrl("edit.github")
			.blogUrl("edit.blog")
			.build();

		// when
		MemberResponse memberResponse = memberService.putMember(memberId, memberPutRequest);

		// then
		assertAll(
			() -> assertThat(memberResponse.id()).isEqualTo(memberId),
			() -> assertThat(memberResponse.email()).isEqualTo("edit@test.com"),
			() -> assertThat(memberResponse.profileImageUrl()).isEqualTo("s3.edit.com"),
			() -> assertThat(memberResponse.nickname()).isEqualTo("edit"),
			() -> assertThat(memberResponse.field()).isEqualTo(Field.ANDROID),
			() -> assertThat(memberResponse.career()).isEqualTo(Career.JUNIOR),
			() -> assertThat(memberResponse.MBTI()).isEqualTo(Mbti.ENFJ),
			() -> assertThat(memberResponse.githubUrl()).isEqualTo("edit.github"),
			() -> assertThat(memberResponse.blogUrl()).isEqualTo("edit.blog")
		);
	}

	private Member saveMember() {
		String testEmail = "test@test.com";

		MemberSaveRequest memberSaveRequest = MemberSaveRequest.builder()
			.email(testEmail)
			.nickname("test")
			.field(Field.ANDROID)
			.career(Career.JUNIOR)
			.MBTI(Mbti.ENFJ)
			.build();

		TemporaryMember temporaryMember = TemporaryMember.builder()
			.email(testEmail)
			.imageUrl("test.s3.com")
			.nickname("test")
			.expiration(500000L)
			.build();

		return memberService.signup(MemberMapper.toMemberDto(memberSaveRequest), temporaryMember);
	}
}