package prgrms.project.stuti.domain.studygroup.repository.studymember;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import prgrms.project.stuti.config.RepositoryTestConfig;
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
import prgrms.project.stuti.domain.studygroup.repository.dto.StudyGroupMemberQueryDto;
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
				.title("test title")
				.topic(Topic.NETWORK)
				.isOnline(true)
				.preferredMBTIs(Set.of(new PreferredMbti(Mbti.ENFJ)))
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
	void testIsStudyLeader() {
		//given
		studyGroupMemberRepository.save(new StudyGroupMember(
			StudyGroupMemberRole.STUDY_LEADER, member, studyGroup));

		//when
		boolean isStudyLeader = studyGroupMemberRepository.isStudyLeader(member.getId(), studyGroup.getId());

		//then
		assertThat(isStudyLeader).isTrue();
	}

	@Test
	@DisplayName("해당 스터디 그룹의 리더가 아니라면 false 를 반환한다.")
	void testIsNotStudyLeader() {
		//given
		studyGroupMemberRepository.save(
			new StudyGroupMember(StudyGroupMemberRole.STUDY_MEMBER, otherMember, studyGroup));

		//when
		boolean isStudyLeader = studyGroupMemberRepository.isStudyLeader(otherMember.getId(), studyGroup.getId());

		//then
		assertThat(isStudyLeader).isFalse();
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
		assertThat(isExists).isTrue();
	}

	@Test
	@DisplayName("삭제되지 않은 스터디 그룹의 스터디 그룹 멤버를 찾아온다.")
	void testFindStudyGroupMemberById() {
		//given
		Long studyGroupMemberId = studyGroupMember.getId();

		//when
		Optional<StudyGroupMember> studyGroupMember = studyGroupMemberRepository
			.findStudyGroupMemberById(studyGroupMemberId);

		//then
		assertThat(studyGroupMember).isPresent();
		assertThat(studyGroupMember.get().getId()).isEqualTo(studyGroupMemberId);
	}

	@Test
	@DisplayName("참여하고 있는 스터디 그룹이 삭제되면 해당 스터디의 스터디 그룹 멤버를 찾을 수 없다.")
	void testFindStudyGroupMemberByIdWithDeletedStudyGroup() {
		//given
		StudyGroupMember studyGroupMember = studyGroupMemberRepository
			.save(new StudyGroupMember(StudyGroupMemberRole.STUDY_LEADER, member, studyGroup));
		StudyGroup studyGroup = studyGroupMember.getStudyGroup();
		Long studyGroupMemberId = studyGroupMember.getId();

		//when
		studyGroup.delete();
		Optional<StudyGroupMember> retrievedStudyGroupMember =
			studyGroupMemberRepository.findStudyGroupMemberById(studyGroupMemberId);

		//then
		assertThat(studyGroupMember).isNotNull();
		assertThat(retrievedStudyGroupMember).isEmpty();
	}

	@Test
	@DisplayName("스터디 그룹 아이디로 스터디에 참여하는 스터디 멤버들을 조회한다.")
	void testFindStudyGroupMembersByStudyGroupId() {
		//given
		for (int i = 0; i < 2; i++) {
			saveStudyGroupMember(StudyGroupMemberRole.STUDY_MEMBER);
			saveStudyGroupMember(StudyGroupMemberRole.STUDY_APPLICANT);
		}

		Long studyGroupId = studyGroup.getId();

		//when
		Map<StudyGroup, List<StudyGroupMemberQueryDto>> studyGroupMembersMap =
			studyGroupMemberRepository.findStudyGroupMembersByStudyGroupId(studyGroupId);

		//then
		Optional<StudyGroup> studyGroup = studyGroupMembersMap.keySet().stream().findFirst();
		assertThat(studyGroup).isPresent();
		assertThat(
			studyGroupMembersMap
				.get(studyGroup.get())
				.stream()
				.filter(m -> m.studyGroupMemberRole().equals(StudyGroupMemberRole.STUDY_MEMBER)))
			.hasSize(2);
		assertThat(
			studyGroupMembersMap
				.get(studyGroup.get())
				.stream()
				.filter(m -> m.studyGroupMemberRole().equals(StudyGroupMemberRole.STUDY_APPLICANT)))
			.hasSize(2);
	}

	public void saveStudyGroupMember(StudyGroupMemberRole studyGroupMemberRole) {
		Member newMember = saveMember();
		studyGroupMemberRepository.save(new StudyGroupMember(studyGroupMemberRole, newMember, studyGroup));
	}

	public Member saveMember() {
		String randomString = RandomStringUtils.randomAlphabetic(5);

		return memberRepository.save(
			Member.builder()
				.memberRole(MemberRole.ROLE_MEMBER)
				.field(Field.ANDROID)
				.career(Career.SENIOR)
				.mbti(Mbti.ENFJ)
				.email(randomString + "@gmail.com")
				.nickName(randomString)
				.build());
	}
}
