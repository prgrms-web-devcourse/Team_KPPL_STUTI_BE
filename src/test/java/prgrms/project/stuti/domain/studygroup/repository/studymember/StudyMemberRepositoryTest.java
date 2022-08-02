package prgrms.project.stuti.domain.studygroup.repository.studymember;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Optional;

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
import prgrms.project.stuti.domain.studygroup.repository.studygroup.StudyGroupRepository;

class StudyMemberRepositoryTest extends RepositoryTestConfig {

	@Autowired
	private StudyMemberRepository studyMemberRepository;

	@Autowired
	private StudyGroupRepository studyGroupRepository;

	private StudyGroup studyGroup;

	private StudyMember studyMember;

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
				.studyPeriod(new StudyPeriod(LocalDateTime.now().plusDays(10), LocalDateTime.now().plusMonths(10)))
				.description("description")
				.build()
		);

		this.studyMember = studyMemberRepository.save(new StudyMember(StudyMemberRole.LEADER, member, studyGroup));
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
		studyMemberRepository.save(new StudyMember(StudyMemberRole.STUDY_MEMBER, otherMember, studyGroup));

		//when
		boolean isLeader = studyMemberRepository.isLeader(otherMember.getId(), studyGroup.getId());

		//then
		assertFalse(isLeader);
	}

	@Test
	@DisplayName("스터디에 이미 가입신청을 했거나 가입이 된 멤버라면 true 를 반환한다.")
	void testExistsByMemberIdAndStudyGroupId() {
		//given
		Long memberId = member.getId();
		Long studyGroupId = studyGroup.getId();
		studyMemberRepository.save(new StudyMember(StudyMemberRole.STUDY_MEMBER, member, studyGroup));

		//when
		boolean isExists = studyMemberRepository.existsByMemberIdAndStudyGroupId(memberId, studyGroupId);

		//then
		assertTrue(isExists);
	}

	@Test
	@DisplayName("삭제되지 않은 스터디 그룹의 스터디 멤버를 찾아온다.")
	void testFindStudyMemberById() {
		//given
		Long studyMemberId = studyMember.getId();

		//when
		Optional<StudyMember> studyMember = studyMemberRepository.findStudyMemberById(studyMemberId);

		//then
		assertTrue(studyMember.isPresent());
		assertEquals(studyMemberId, studyMember.get().getId());
	}

	@Test
	@DisplayName("참여하고 있는 스터디 그룹이 삭제되면 해당 스터디의 스터디 멤버를 찾을 수 없다.")
	void testFindStudyMemberByIdWithDeletedStudyGroup() {
		//given
		StudyMember studyMember = studyMemberRepository.save(
			new StudyMember(StudyMemberRole.LEADER, member, studyGroup));
		StudyGroup studyGroup = studyMember.getStudyGroup();
		studyGroup.delete();
		Long studyMemberId = studyMember.getId();

		//when
		Optional<StudyMember> retrievedStudyMember = studyMemberRepository.findStudyMemberById(studyMemberId);

		//then
		assertTrue(retrievedStudyMember.isEmpty());
	}
}

