package prgrms.project.stuti.domain.member.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
import prgrms.project.stuti.global.error.exception.MemberException;

@SpringBootTest
class MemberServiceTest {

	@Autowired
	MemberService memberService;

	@Test
	@DisplayName("회원가입을 한다.")
	void testSignup() {
		// given
		MemberSaveRequest memberSaveRequest = MemberSaveRequest.builder()
			.email("test1@test.com")
			.nickname("test1")
			.field(Field.ANDROID)
			.career(Career.JUNIOR)
			.MBTI(Mbti.ENFJ)
			.build();

		TemporaryMember temporaryMember = TemporaryMember.builder()
			.email("test1@test.com")
			.imageUrl("test1.s3.com")
			.nickname("test1")
			.expiration(500000L)
			.build();

		// when
		Member savedMember = memberService.signup(MemberMapper.toMemberDto(memberSaveRequest), temporaryMember);

		// then
		assertThat(savedMember.getId()).isNotNull();
	}

	@Test
	@DisplayName("회원가입 시 동일 nickname 존재시 예외발생한다")
	void testInvalidSignup() {
		// given
		saveMember("test2@test.com", "test2", "test2.s3.com");
		MemberSaveRequest memberSaveRequest = MemberSaveRequest.builder()
			.email("test3@test.com")
			.nickname("test2")
			.field(Field.ANDROID)
			.career(Career.JUNIOR)
			.MBTI(Mbti.ENFJ)
			.build();

		TemporaryMember temporaryMember = TemporaryMember.builder()
			.email("test3@test.com")
			.imageUrl("test3.s3.com")
			.nickname("test2")
			.expiration(500000L)
			.build();

		// when // then
		assertThrows(MemberException.class,
			() -> memberService.signup(MemberMapper.toMemberDto(memberSaveRequest), temporaryMember));
	}

	@Test
	@DisplayName("이메일을 통해 유저를 가지고 온다")
	void testGetUserByEmail() {
		// given
		String testEmail = saveMember("test4@test.com", "test4", "test4.s3.com").getEmail();

		// when
		Member member = memberService.getMember(new Email(testEmail))
			.orElseThrow(() -> MemberException.notFoundMember(1L));

		// then
		assertAll(
			() -> assertThat(member.getEmail()).isEqualTo("test4@test.com"),
			() -> assertThat(member.getNickName()).isEqualTo("test4"),
			() -> assertThat(member.getField()).isEqualTo(Field.ANDROID),
			() -> assertThat(member.getCareer()).isEqualTo(Career.JUNIOR),
			() -> assertThat(member.getMbti()).isEqualTo(Mbti.ENFJ),
			() -> assertThat(member.getProfileImageUrl()).isEqualTo("test4.s3.com")
		);
	}

	@Test
	@DisplayName("멤버를 수정한다")
	void testInvalidPutMember() {
		// given
		Member member = saveMember("test6@test.com", "test6", "test6.s3.com");

		Long memberId = member.getId();

		MemberPutRequest memberPutRequest = MemberPutRequest.builder()
			.id(memberId)
			.email("test6@test.com")
			.profileImageUrl("s3.edit3.com")
			.nickname("test6")
			.field(Field.ANDROID)
			.career(Career.JUNIOR)
			.MBTI(Mbti.ENFJ)
			.githubUrl("edit3.github")
			.blogUrl("edit3.blog")
			.build();

		// when
		MemberResponse memberResponse = memberService.editMember(memberId,
			MemberMapper.toMemberPutDto(memberPutRequest));

		// then
		assertAll(
			() -> assertThat(memberResponse.id()).isEqualTo(memberId),
			() -> assertThat(memberResponse.email()).isEqualTo("test6@test.com"),
			() -> assertThat(memberResponse.profileImageUrl()).isEqualTo("s3.edit3.com"),
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
		Member member = saveMember("test7@test.com", "test7", "test7.s3.com");
		saveMember("test8@test.com", "test8", "test8.s3.com");

		Long memberId = member.getId();

		MemberPutRequest memberPutRequest = MemberPutRequest.builder()
			.id(memberId)
			.email("test7@test.com")
			.profileImageUrl("s3.edit3.com")
			.nickname("test8")
			.field(Field.ANDROID)
			.career(Career.JUNIOR)
			.MBTI(Mbti.ENFJ)
			.githubUrl("edit3.github")
			.blogUrl("edit3.blog")
			.build();

		// when // then
		assertThrows(MemberException.class, () -> {
			memberService.editMember(memberId, MemberMapper.toMemberPutDto(memberPutRequest));
		});
	}

	private Member saveMember(String email, String nickname, String url) {
		MemberSaveRequest memberSaveRequest = MemberSaveRequest.builder()
			.email(email)
			.nickname(nickname)
			.field(Field.ANDROID)
			.career(Career.JUNIOR)
			.MBTI(Mbti.ENFJ)
			.build();

		TemporaryMember temporaryMember = TemporaryMember.builder()
			.email(email)
			.imageUrl(url)
			.nickname(nickname)
			.expiration(500000L)
			.build();

		return memberService.signup(MemberMapper.toMemberDto(memberSaveRequest), temporaryMember);
	}
}