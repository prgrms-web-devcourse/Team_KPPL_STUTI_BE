package prgrms.project.stuti.domain.studygroup.service.studymember;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import prgrms.project.stuti.config.ServiceTestConfig;
import prgrms.project.stuti.domain.studygroup.model.Region;
import prgrms.project.stuti.domain.studygroup.model.StudyGroup;
import prgrms.project.stuti.domain.studygroup.model.StudyMember;
import prgrms.project.stuti.domain.studygroup.model.StudyMemberRole;
import prgrms.project.stuti.domain.studygroup.model.StudyPeriod;
import prgrms.project.stuti.domain.studygroup.model.Topic;
import prgrms.project.stuti.domain.studygroup.repository.studygroup.StudyGroupRepository;
import prgrms.project.stuti.domain.studygroup.repository.studymember.StudyMemberRepository;
import prgrms.project.stuti.domain.studygroup.service.response.StudyMemberIdResponse;

class StudyMemberServiceTest extends ServiceTestConfig {

	@Autowired
	private StudyMemberService studyMemberService;

	@Autowired
	private StudyGroupRepository studyGroupRepository;

	@Autowired
	private StudyMemberRepository studyMemberRepository;

	private StudyGroup studyGroup;

	@BeforeAll
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

		studyMemberRepository.save(new StudyMember(StudyMemberRole.LEADER, member, studyGroup));
	}

	@Test
	@DisplayName("스터디 가입신청을 수락하면 스터디 멤버로 역할이 변경된다.")
	void testAcceptRequestForJoin() {
		//given
		StudyMember applicant = studyMemberRepository.save(
			new StudyMember(StudyMemberRole.APPLICANT, otherMember, studyGroup));

		//when
		StudyMemberIdResponse newStudyMember = studyMemberService.acceptRequestForJoin(member.getId(),
			studyGroup.getId(), applicant.getId());
		Optional<StudyMember> retrievedStudyMember = studyMemberRepository.findStudyMemberById(
			newStudyMember.studyMemberId());

		//then
		assertEquals(applicant.getId(), newStudyMember.studyMemberId());
		assertTrue(retrievedStudyMember.isPresent());
		assertEquals(StudyMemberRole.STUDY_MEMBER, retrievedStudyMember.get().getStudyMemberRole());
	}
}
