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
import prgrms.project.stuti.domain.studygroup.model.StudyGroupMember;
import prgrms.project.stuti.domain.studygroup.model.StudyGroupMemberRole;
import prgrms.project.stuti.domain.studygroup.model.StudyPeriod;
import prgrms.project.stuti.domain.studygroup.model.Topic;
import prgrms.project.stuti.domain.studygroup.repository.studygroup.StudyGroupRepository;

class StudyGroupMemberRepositoryTest extends RepositoryTestConfig {

	@Autowired
	private StudyGroupMemberRepository studyGroupMemberRepository;

	@Autowired
	private StudyGroupRepository studyGroupRepository;

	private StudyGroup studyGroup;

	private StudyGroupMember studyGroupMember;

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

		this.studyGroupMember = studyGroupMemberRepository.save(
			new StudyGroupMember(StudyGroupMemberRole.STUDY_LEADER, member, studyGroup));
	}

	@Test
	@DisplayName("해당 스터디의 리더라면 true 를 반환한다.")
	void testIsLeader() {
		//given
		studyGroupMemberRepository.save(new StudyGroupMember(
			StudyGroupMemberRole.STUDY_LEADER, member, studyGroup));

		//when
		boolean isLeader = studyGroupMemberRepository.isStudyLeader(member.getId(), studyGroup.getId());

		//then
		assertTrue(isLeader);
	}

	@Test
	@DisplayName("해당 스터디 그룹의 리더가 아니라면 false 를 반환한다.")
	void testIsNotLeader() {
		//given
		studyGroupMemberRepository.save(
			new StudyGroupMember(StudyGroupMemberRole.STUDY_MEMBER, otherMember, studyGroup));

		//when
		boolean isLeader = studyGroupMemberRepository.isStudyLeader(otherMember.getId(), studyGroup.getId());

		//then
		assertFalse(isLeader);
	}

	@Test
	@DisplayName("스터디에 이미 가입신청을 했거나 가입이 된 스터디 그룹 멤버라면 true 를 반환한다.")
	void testExistsByMemberIdAndStudyGroupId() {
		//given
		Long memberId = member.getId();
		Long studyGroupId = studyGroup.getId();
		studyGroupMemberRepository.save(new StudyGroupMember(StudyGroupMemberRole.STUDY_MEMBER, member, studyGroup));

		//when
		boolean isExists = studyGroupMemberRepository.existsByMemberIdAndStudyGroupId(memberId, studyGroupId);

		//then
		assertTrue(isExists);
	}

	@Test
	@DisplayName("삭제되지 않은 스터디 그룹의 스터디 그룹 멤버를 찾아온다.")
	void testFindStudyGroupMemberById() {
		//given
		Long studyGroupMemberId = studyGroupMember.getId();

		//when
		Optional<StudyGroupMember> studyGroupMember = studyGroupMemberRepository.findStudyGroupMemberById(
			studyGroupMemberId);

		//then
		assertTrue(studyGroupMember.isPresent());
		assertEquals(studyGroupMemberId, studyGroupMember.get().getId());
	}

	@Test
	@DisplayName("참여하고 있는 스터디 그룹이 삭제되면 해당 스터디의 스터디 그룹 멤버를 찾을 수 없다.")
	void testFindStudyGroupMemberByIdWithDeletedStudyGroup() {
		//given
		StudyGroupMember studyGroupMember = studyGroupMemberRepository.save(
			new StudyGroupMember(StudyGroupMemberRole.STUDY_LEADER, member, studyGroup));
		StudyGroup studyGroup = studyGroupMember.getStudyGroup();
		studyGroup.delete();
		Long studyGroupMemberId = studyGroupMember.getId();

		//when
		Optional<StudyGroupMember> retrievedStudyGroupMember = studyGroupMemberRepository.findStudyGroupMemberById(
			studyGroupMemberId);

		//then
		assertTrue(retrievedStudyGroupMember.isEmpty());
	}
}
