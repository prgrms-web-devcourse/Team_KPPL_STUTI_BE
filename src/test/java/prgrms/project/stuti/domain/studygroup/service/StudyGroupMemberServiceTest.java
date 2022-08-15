package prgrms.project.stuti.domain.studygroup.service;

import static org.assertj.core.api.Assertions.*;
import static prgrms.project.stuti.domain.studygroup.model.StudyGroupMemberRole.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import prgrms.project.stuti.config.ServiceTestConfig;
import prgrms.project.stuti.domain.member.model.Career;
import prgrms.project.stuti.domain.member.model.Field;
import prgrms.project.stuti.domain.member.model.Mbti;
import prgrms.project.stuti.domain.member.model.Member;
import prgrms.project.stuti.domain.member.model.MemberRole;
import prgrms.project.stuti.domain.studygroup.model.PreferredMbti;
import prgrms.project.stuti.domain.studygroup.model.Region;
import prgrms.project.stuti.domain.studygroup.model.StudyGroup;
import prgrms.project.stuti.domain.studygroup.model.StudyGroupMember;
import prgrms.project.stuti.domain.studygroup.model.StudyGroupMemberRole;
import prgrms.project.stuti.domain.studygroup.model.StudyPeriod;
import prgrms.project.stuti.domain.studygroup.model.Topic;
import prgrms.project.stuti.domain.studygroup.repository.studygroup.StudyGroupRepository;
import prgrms.project.stuti.domain.studygroup.repository.studymember.StudyGroupMemberRepository;
import prgrms.project.stuti.domain.studygroup.service.dto.StudyGroupMemberDto;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupMemberIdResponse;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupMembersResponse;
import prgrms.project.stuti.global.error.exception.StudyGroupException;

class StudyGroupMemberServiceTest extends ServiceTestConfig {

	@Autowired
	private StudyGroupMemberService studyGroupMemberService;

	@Autowired
	private StudyGroupRepository studyGroupRepository;

	@Autowired
	private StudyGroupMemberRepository studyGroupMemberRepository;

	private final String errorMessageOfNotLeader = "스터디 그룹의 리더가 아닙니다";

	private StudyGroup studyGroup;

	private StudyGroupMember studyLeader;

	@BeforeEach
	void setup() {
		studyGroup = saveStudyGroup();
		studyLeader = saveStudyGroupMember(STUDY_LEADER, member, studyGroup);
	}

	@Nested
	@DisplayName("스터디 신청 기능은")
	class StudyGroupMemberApplyTest {

		@Test
		@DisplayName("최초 가입신청일 경우 가입신청이 완료된다.")
		void testApplyForJoinStudyGroup() {
			//given
			Member newMember = saveMember();
			StudyGroupMemberDto.CreateDto createDto =
				new StudyGroupMemberDto.CreateDto(newMember.getId(), studyGroup.getId());

			//when
			StudyGroupMemberIdResponse idResponse = studyGroupMemberService.applyForJoinStudyGroup(createDto);
			Optional<StudyGroupMember> retrievedStudyGroupMember =
				studyGroupMemberRepository.findById(idResponse.studyGroupMemberId());

			//then
			assertThat(retrievedStudyGroupMember).isPresent();
			assertThat(retrievedStudyGroupMember.get().getId()).isEqualTo(idResponse.studyGroupMemberId());
		}

		@Test
		@DisplayName("이미 가입신청을 했다면 예외가 발생한다.")
		void testFailedToApplyForJoinStudyGroup() {
			//given
			Member newMember = saveMember();
			StudyGroup studyGroup = saveStudyGroup();
			StudyGroupMemberDto.CreateDto createDto =
				new StudyGroupMemberDto.CreateDto(newMember.getId(), studyGroup.getId());

			//when
			studyGroupMemberService.applyForJoinStudyGroup(createDto);

			//then
			assertThatThrownBy(() -> studyGroupMemberService.applyForJoinStudyGroup(createDto))
				.isInstanceOf(StudyGroupException.class)
				.hasMessageContaining("이미 존재하는 스터디 그룹 멤버입니다");
		}
	}

	@Nested
	@DisplayName("스터디 멤버조회 기능은")
	class StudyGroupMemberGetTest {

		@Test
		@DisplayName("스터디 리더라면 멤버를 조회한다.")
		void testGetStudyGroupMembers() {
			//given
			Member newMember = saveMember();
			Member studyLeaderMember = studyLeader.getMember();
			StudyGroupMember studyGroupMember = saveStudyGroupMember(STUDY_MEMBER, newMember, studyGroup);
			StudyGroupMemberDto.ReadDto readDto =
				new StudyGroupMemberDto.ReadDto(studyLeaderMember.getId(), studyGroup.getId());

			//when
			StudyGroupMembersResponse membersResponse = studyGroupMemberService.getStudyGroupMembers(readDto);

			//then
			assertThat(membersResponse.studyMembers()).hasSize(2);
			assertThat(membersResponse.studyMembers())
				.extracting("studyGroupMemberId")
				.contains(studyLeader.getId(), studyGroupMember.getId());
		}

		@Test
		@DisplayName("스터디 리더가 아니라면 예외가 발생한다.")
		void testFailedToGetStudyGroupMembers() {
			//given
			Member newMember = saveMember();
			StudyGroupMemberDto.ReadDto readDto =
				new StudyGroupMemberDto.ReadDto(newMember.getId(), studyGroup.getId());

			//when, then
			assertThatThrownBy(() -> studyGroupMemberService.getStudyGroupMembers(readDto))
				.isInstanceOf(StudyGroupException.class)
				.hasMessageContaining(errorMessageOfNotLeader);
		}
	}

	@Nested
	@DisplayName("스터디 가입수락 기능은")
	class StudyGroupMemberAcceptTest {

		@Test
		@DisplayName("스터디 리더라면 신청자의 가입을 수락한다.")
		void testAcceptRequestForJoin() {
			//given
			Member newMember = saveMember();
			Member studyLeaderMember = studyLeader.getMember();
			int numberOfMembersBeforeAcceptStudyApplicant = studyGroup.getNumberOfMembers();
			StudyGroupMember studyApplicant = saveStudyGroupMember(STUDY_APPLICANT, newMember, studyGroup);
			StudyGroupMemberDto.UpdateDto updateDto = new StudyGroupMemberDto
				.UpdateDto(studyLeaderMember.getId(), studyGroup.getId(), studyApplicant.getId());

			//when
			StudyGroupMemberIdResponse idResponse = studyGroupMemberService.acceptRequestForJoin(updateDto);
			Optional<StudyGroupMember> retrievedStudyGroupMember =
				studyGroupMemberRepository.findById(idResponse.studyGroupMemberId());

			//then
			assertThat(retrievedStudyGroupMember).isPresent();
			assertThat(retrievedStudyGroupMember.get().getStudyGroupMemberRole()).isEqualTo(STUDY_MEMBER);
			assertThat(numberOfMembersBeforeAcceptStudyApplicant + 1)
				.isEqualTo(studyGroup.getNumberOfMembers());
		}

		@Test
		@DisplayName("스터디 리더가 아니면 예외가 발생한다.")
		void testFailedToAcceptRequestForJoin() {
			//given
			Member newMember = saveMember();
			StudyGroupMember studyApplicant = saveStudyGroupMember(STUDY_APPLICANT, newMember, studyGroup);
			StudyGroupMemberDto.UpdateDto updateDto = new StudyGroupMemberDto
				.UpdateDto(newMember.getId(), studyGroup.getId(), studyApplicant.getId());

			//when, then
			assertThatThrownBy(() -> studyGroupMemberService.acceptRequestForJoin(updateDto))
				.isInstanceOf(StudyGroupException.class)
				.hasMessageContaining(errorMessageOfNotLeader);
		}
	}

	@Nested
	@DisplayName("스터디 멤버 삭제 기능은")
	class StudyGroupMemberDeleteTest {

		@Test
		@DisplayName("스터디 리더라면 스터디 멤버를 삭제한다.")
		void testDeleteStudyGroupMember() {
			//given
			Member newMember = saveMember();
			Member studyLeaderMember = studyLeader.getMember();
			StudyGroupMember studyGroupMember = saveStudyGroupMember(STUDY_MEMBER, newMember, studyGroup);
			StudyGroupMemberDto.DeleteDto deleteDto = new StudyGroupMemberDto
				.DeleteDto(studyLeaderMember.getId(), studyGroup.getId(), studyGroupMember.getId());

			//when
			studyGroupMemberService.deleteStudyGroupMember(deleteDto);
			Optional<StudyGroupMember> retrievedStudyGroupMember =
				studyGroupMemberRepository.findById(studyGroupMember.getId());

			//then
			assertThat(retrievedStudyGroupMember).isNotPresent();
		}

		@Test
		@DisplayName("스터디 리더가 아니라면 예외가 발생한다.")
		void testFailedToDeleteStudyGroupMember() {
			//given
			Member newMember = saveMember();
			StudyGroupMember studyMember = saveStudyGroupMember(STUDY_MEMBER, newMember, studyGroup);
			StudyGroupMemberDto.DeleteDto deleteDto = new StudyGroupMemberDto
				.DeleteDto(newMember.getId(), studyGroup.getId(), studyMember.getId());

			//when, then
			assertThatThrownBy(() -> studyGroupMemberService.deleteStudyGroupMember(deleteDto))
				.isInstanceOf(StudyGroupException.class)
				.hasMessageContaining(errorMessageOfNotLeader);
		}
	}

	public StudyGroupMember saveStudyGroupMember(StudyGroupMemberRole role, Member member, StudyGroup studyGroup) {
		return studyGroupMemberRepository.save(new StudyGroupMember(role, member, studyGroup));
	}

	private Member saveMember() {
		String randomString = RandomStringUtils.randomAlphabetic(5);

		return memberRepository.save(Member
			.builder()
			.email(randomString + "@gmail.com")
			.nickName(randomString)
			.career(Career.JUNIOR)
			.field(Field.ANDROID)
			.githubUrl("github.com")
			.blogUrl("blog.com")
			.mbti(Mbti.ENFJ)
			.profileImageUrl("www.s3.com")
			.memberRole(MemberRole.ROLE_MEMBER)
			.build());
	}

	private StudyGroup saveStudyGroup() {
		return studyGroupRepository.save(
			StudyGroup
				.builder()
				.imageUrl("image")
				.title("title")
				.topic(Topic.AI)
				.isOnline(false)
				.region(Region.SEOUL)
				.numberOfRecruits(5)
				.studyPeriod(new StudyPeriod(LocalDateTime.now().plusDays(10), LocalDateTime.now().plusMonths(3)))
				.preferredMBTIs(Set.of(new PreferredMbti(Mbti.ENFJ)))
				.description("this is new study group")
				.build());
	}
}
