package prgrms.project.stuti.domain.studygroup.repository.studygroup;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import prgrms.project.stuti.config.RepositoryTestConfig;
import prgrms.project.stuti.domain.member.model.Mbti;
import prgrms.project.stuti.domain.studygroup.model.Region;
import prgrms.project.stuti.domain.studygroup.model.StudyGroup;
import prgrms.project.stuti.domain.studygroup.model.StudyMember;
import prgrms.project.stuti.domain.studygroup.model.StudyMemberRole;
import prgrms.project.stuti.domain.studygroup.model.StudyPeriod;
import prgrms.project.stuti.domain.studygroup.model.Topic;
import prgrms.project.stuti.domain.studygroup.repository.studymember.StudyMemberRepository;

class StudyGroupRepositoryTest extends RepositoryTestConfig {

	@Autowired
	private StudyMemberRepository studyMemberRepository;

	@Autowired
	private StudyGroupRepository studyGroupRepository;

	private StudyGroup studyGroup;

	@BeforeEach
	void setup() {
		this.studyGroup = studyGroupRepository.save(
			StudyGroup
				.builder()
				.imageUrl("imageUrl")
				.thumbnailUrl("thumbnailUrl")
				.title("test title")
				.topic(Topic.NETWORK)
				.isOnline(true)
				.region(Region.ONLINE)
				.numberOfRecruits(5)
				.preferredMBTIs(Set.of(Mbti.ESFJ))
				.studyPeriod(new StudyPeriod(LocalDateTime.now().plusDays(10), LocalDateTime.now().plusMonths(10)))
				.description("description")
				.build());

		studyMemberRepository.save(new StudyMember(StudyMemberRole.LEADER, member, studyGroup));
	}

	@Test
	@DisplayName("스터디 그룹을 상세조회한다.")
	void testFindStudyGroupDetailById() {
		//given
		Long studyGroupId = studyGroup.getId();

		//when
		Optional<StudyMember> studyGroupDetail = studyGroupRepository.findStudyGroupDetailById(studyGroupId);

		// then
		assertTrue(studyGroupDetail.isPresent());

		StudyMember detail = studyGroupDetail.get();
		assertEquals(studyGroup.getId(), detail.getStudyGroup().getId());
		assertEquals(member.getId(), detail.getMember().getId());
	}
}
