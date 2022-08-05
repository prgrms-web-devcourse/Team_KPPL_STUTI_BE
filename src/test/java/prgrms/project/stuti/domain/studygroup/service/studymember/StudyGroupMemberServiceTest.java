package prgrms.project.stuti.domain.studygroup.service.studymember;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import prgrms.project.stuti.config.ServiceTestConfig;
import prgrms.project.stuti.domain.studygroup.model.Region;
import prgrms.project.stuti.domain.studygroup.model.StudyGroup;
import prgrms.project.stuti.domain.studygroup.model.StudyGroupMember;
import prgrms.project.stuti.domain.studygroup.model.StudyGroupMemberRole;
import prgrms.project.stuti.domain.studygroup.model.StudyPeriod;
import prgrms.project.stuti.domain.studygroup.model.Topic;
import prgrms.project.stuti.domain.studygroup.repository.studygroup.StudyGroupRepository;
import prgrms.project.stuti.domain.studygroup.repository.studymember.StudyGroupMemberRepository;
import prgrms.project.stuti.domain.studygroup.service.StudyGroupMemberService;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupMemberIdResponse;
import prgrms.project.stuti.global.error.exception.StudyGroupException;

class StudyGroupMemberServiceTest extends ServiceTestConfig {

	@Autowired
	private StudyGroupMemberService studyGroupMemberService;

	@Autowired
	private StudyGroupRepository studyGroupRepository;

	@Autowired
	private StudyGroupMemberRepository studyGroupMemberRepository;

	private StudyGroup studyGroup;

	private StudyGroupMember studyLeader;

	@BeforeEach
	void setup() {
		this.studyGroup = studyGroupRepository.save(
			StudyGroup
				.builder()
				.imageUrl("image")
				.thumbnailUrl("thumbnail")
				.title("title")
				.topic(Topic.AI)
				.isOnline(false)
				.region(Region.SEOUL)
				.numberOfRecruits(5)
				.studyPeriod(new StudyPeriod(LocalDateTime.now().plusDays(10), LocalDateTime.now().plusMonths(3)))
				.description("this is new study group")
				.build());

		this.studyLeader = studyGroupMemberRepository.save(
			new StudyGroupMember(StudyGroupMemberRole.STUDY_LEADER, member, studyGroup));
	}

	@Test
	@DisplayName("회원이 스터디 그룹에 가입신청을한다.")
	void testApplyStudyGroup() {
		//given
		Long memberId = otherMember.getId();
		Long studyGroupId = studyGroup.getId();

		//when
		StudyGroupMemberIdResponse idResponse = studyGroupMemberService.applyForJoinStudyGroup(memberId, studyGroupId);
		Optional<StudyGroupMember> studyGroupMember = studyGroupMemberRepository.findStudyGroupMemberById(
			idResponse.studyGroupMemberId());

		//then
		assertTrue(studyGroupMember.isPresent());
		assertEquals(StudyGroupMemberRole.STUDY_APPLICANT, studyGroupMember.get().getStudyGroupMemberRole());
	}

	@Test
	@DisplayName("이미 가입 했거나 가입신청을한 스터디 그룹에 가입신청을 한다면 예외가 발생한다.")
	void testExistingStudyGroupMember() {
		//given
		Long memberId = member.getId();
		Long studyGroupId = studyGroup.getId();

		//when, then
		assertThrows(StudyGroupException.class,
			() -> studyGroupMemberService.applyForJoinStudyGroup(memberId, studyGroupId));
	}

	@Test
	@DisplayName("스터디 가입신청을 수락하면 스터디 멤버로 역할이 변경된다.")
	void testAcceptRequestForJoin() {
		//given
		StudyGroupMember studyApplicant = studyGroupMemberRepository.save(
			new StudyGroupMember(StudyGroupMemberRole.STUDY_APPLICANT, otherMember, studyGroup));

		//when
		StudyGroupMemberIdResponse newStudyGroupMember = studyGroupMemberService.acceptRequestForJoin(member.getId(),
			studyGroup.getId(), studyApplicant.getId());
		Optional<StudyGroupMember> retrievedStudyGroupMember = studyGroupMemberRepository.findStudyGroupMemberById(
			newStudyGroupMember.studyGroupMemberId());

		//then
		assertEquals(studyApplicant.getId(), newStudyGroupMember.studyGroupMemberId());
		assertTrue(retrievedStudyGroupMember.isPresent());
		assertEquals(StudyGroupMemberRole.STUDY_MEMBER, retrievedStudyGroupMember.get().getStudyGroupMemberRole());
	}

	@Test
	@DisplayName("스터디 그룹의 리더가 스터디 멤버를 삭제한다.")
	void testDeleteStudyGroupMember() {
		//given
		Long studyGroupMemberId = studyLeader.getId();

		//when
		studyGroupMemberService.deleteStudyGroupMember(member.getId(), studyGroup.getId(), studyGroupMemberId);
		Optional<StudyGroupMember> retrievedStudyGroupMember = studyGroupMemberRepository.findStudyGroupMemberById(studyGroupMemberId);

		//then
		assertTrue(retrievedStudyGroupMember.isEmpty());
	}

	@Test
	@DisplayName("스터디 그룹의 리더가 아닌 스터디 멤버가 삭제를 하려고 접근하면 예외가 발생한다.")
	void testNotLeaderAccessToDeleteStudyGroupMember() {
		//given
		StudyGroupMember studyGroupMember = studyGroupMemberRepository.save(
			new StudyGroupMember(StudyGroupMemberRole.STUDY_MEMBER, otherMember, studyGroup));

		//when, then
		assertThrows(StudyGroupException.class,
			() -> studyGroupMemberService.deleteStudyGroupMember(otherMember.getId(), studyGroup.getId(),
				studyGroupMember.getId()));
	}
}
