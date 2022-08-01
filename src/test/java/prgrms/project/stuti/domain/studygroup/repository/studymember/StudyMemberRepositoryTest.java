package prgrms.project.stuti.domain.studygroup.repository.studymember;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import prgrms.project.stuti.config.RepositoryTestConfig;
import prgrms.project.stuti.domain.studygroup.model.Region;
import prgrms.project.stuti.domain.studygroup.model.StudyGroup;
import prgrms.project.stuti.domain.studygroup.model.StudyMember;
import prgrms.project.stuti.domain.studygroup.model.StudyMemberRole;
import prgrms.project.stuti.domain.studygroup.model.StudyPeriod;
import prgrms.project.stuti.domain.studygroup.model.Topic;
import prgrms.project.stuti.domain.studygroup.repository.StudyGroupRepository;

class StudyMemberRepositoryTest extends RepositoryTestConfig {

	@Autowired
	private StudyMemberRepository studyMemberRepository;

	@Autowired
	private StudyGroupRepository studyGroupRepository;

	private StudyGroup studyGroup;

	@BeforeAll
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
				.studyPeriod(new StudyPeriod(LocalDateTime.now().plusDays(10), LocalDateTime.now().plusMonths(10)))
				.description("description")
				.build()
		);
	}

	@Test
	@DisplayName("해당 스터디의 리더라면 true 를 반환한다.")
	void testIsLeader() {
		//given
		studyMemberRepository.save(new StudyMember(StudyMemberRole.LEADER, member, studyGroup));

		//when
		boolean isLeader = studyMemberRepository.isLeader(member.getId(), studyGroup.getId());

		//then
		assertTrue(isLeader);
	}

	@Test
	@DisplayName("해당 스터디의 리더가 아니라면 false 를 반환한다.")
	void testIsNotLeader() {
		//given
		studyMemberRepository.save(new StudyMember(StudyMemberRole.STUDY_MEMBER, member, studyGroup));

		//when
		boolean isLeader = studyMemberRepository.isLeader(member.getId(), studyGroup.getId());

		//then
		assertFalse(isLeader);
	}
}

