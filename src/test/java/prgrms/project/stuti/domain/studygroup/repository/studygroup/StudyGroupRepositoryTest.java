package prgrms.project.stuti.domain.studygroup.repository.studygroup;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import prgrms.project.stuti.config.RepositoryTestConfig;
import prgrms.project.stuti.domain.member.model.Mbti;
import prgrms.project.stuti.domain.studygroup.model.PreferredMbti;
import prgrms.project.stuti.domain.studygroup.model.Region;
import prgrms.project.stuti.domain.studygroup.model.StudyGroup;
import prgrms.project.stuti.domain.studygroup.model.StudyGroupMember;
import prgrms.project.stuti.domain.studygroup.model.StudyGroupMemberRole;
import prgrms.project.stuti.domain.studygroup.model.StudyPeriod;
import prgrms.project.stuti.domain.studygroup.model.Topic;
import prgrms.project.stuti.domain.studygroup.repository.dto.StudyGroupQueryDto;
import prgrms.project.stuti.domain.studygroup.repository.studymember.StudyGroupMemberRepository;
import prgrms.project.stuti.domain.studygroup.service.dto.StudyGroupDto;

class StudyGroupRepositoryTest extends RepositoryTestConfig {

	@Autowired
	private StudyGroupMemberRepository studyGroupMemberRepository;

	@Autowired
	private StudyGroupRepository studyGroupRepository;

	private StudyGroup studyGroup;

	@BeforeEach
	void setup() {
		this.studyGroup = studyGroupRepository.save(
			StudyGroup
				.builder()
				.imageUrl("imageUrl")
				.title("test title")
				.topic(Topic.NETWORK)
				.isOnline(true)
				.region(Region.ONLINE)
				.numberOfRecruits(5)
				.preferredMBTIs(Set.of(new PreferredMbti(Mbti.ENFJ)))
				.studyPeriod(new StudyPeriod(LocalDateTime.now().plusDays(10), LocalDateTime.now().plusMonths(10)))
				.description("description")
				.build());

		studyGroupMemberRepository.save(new StudyGroupMember(StudyGroupMemberRole.STUDY_LEADER, member, studyGroup));
	}

	@Test
	@DisplayName("스터디 그룹을 소프트 딜리트를 체크하여 아이디로 조회한다.")
	void testFindStudyGroupById() {
		//given
		Long studyGroupId = studyGroup.getId();

		//when
		Optional<StudyGroup> retrievedStudyGroup = studyGroupRepository.findStudyGroupById(studyGroupId);

		//then
		assertThat(retrievedStudyGroup).isPresent();
		assertThat(retrievedStudyGroup.get().getId()).isEqualTo(studyGroupId);
	}

	@Test
	@DisplayName("스터디 그룹을 아이디로 상세조회한다.")
	void testFindStudyGroupDetailById() {
		//given
		Long studyGroupId = studyGroup.getId();

		// when
		List<StudyGroupQueryDto.StudyGroupDetailDto> detailDtos =
			studyGroupRepository.findStudyGroupDetailById(studyGroupId);

		// then
		assertThat(detailDtos).isNotNull();

		StudyGroupQueryDto.StudyGroupDetailDto detailDto = detailDtos.get(0);
		assertThat(detailDto.studyGroupId()).isEqualTo(studyGroupId);
		assertThat(detailDto.memberId()).isEqualTo(member.getId());
	}

	@Test
	@DisplayName("전체 스터디 그룹을 동적 쿼리 && 커서 페이징 방식으로 조회한다.")
	void testFindAllWithCursorPaginationByConditions() {
		//given
		for (int i = 0; i < 5; i++) {
			saveStudyGroup(Topic.FRONTEND);
			saveStudyGroup(Topic.BACKEND);
		}

		StudyGroupDto.FindCondition conditionDto = StudyGroupDto.FindCondition
			.builder()
			.topic(Topic.BACKEND)
			.size(3L)
			.build();

		//when
		StudyGroupQueryDto.StudyGroupsDto studyGroupsDto =
			studyGroupRepository.findAllWithCursorPaginationByConditions(conditionDto);

		//then
		assertThat(studyGroupsDto.studyGroupDtos()).isNotNull();
		assertThat(studyGroupsDto.studyGroupDtos()).hasSize(3);
		assertThat(studyGroupsDto.hasNext()).isTrue();
		studyGroupsDto.studyGroupDtos()
			.forEach(dto -> assertThat(dto.studyGroup().getTopic()).isEqualTo(Topic.BACKEND));
	}

	@Test
	@DisplayName("멤버가 참여했던 혹은 생성한 스터디 그룹들을 커서 페이징 방식으로 조회한다.")
	void testFindMembersAllWithCursorPaginationByConditions() {
		//given
		for (int i = 0; i < 5; i++) {
			saveStudyGroup(Topic.FRONTEND);
			saveStudyGroup(Topic.BACKEND);
		}

		StudyGroupDto.FindCondition conditionDto = StudyGroupDto.FindCondition
			.builder()
			.memberId(member.getId())
			.size(3L)
			.build();

		//when
		StudyGroupQueryDto.StudyGroupsDto studyGroupsDto =
			studyGroupRepository.findAllWithCursorPaginationByConditions(conditionDto);

		//then
		assertThat(studyGroupsDto.studyGroupDtos()).isNotNull();
		assertThat(studyGroupsDto.studyGroupDtos()).hasSize(3);
		assertThat(studyGroupsDto.hasNext()).isTrue();
		studyGroupsDto.studyGroupDtos().forEach(content -> assertThat(content.memberId()).isEqualTo(member.getId()));
	}

	public void saveStudyGroup(Topic topic) {
		StudyGroup newStudyGroup = studyGroupRepository.save(
			StudyGroup
				.builder()
				.imageUrl("imageUrl")
				.title("test title")
				.topic(topic)
				.isOnline(true)
				.region(Region.ONLINE)
				.numberOfRecruits(5)
				.preferredMBTIs(Set.of(new PreferredMbti(Mbti.ENFJ)))
				.studyPeriod(new StudyPeriod(LocalDateTime.now().plusDays(10), LocalDateTime.now().plusMonths(10)))
				.description("description")
				.build());

		studyGroupMemberRepository.save(new StudyGroupMember(StudyGroupMemberRole.STUDY_LEADER, member, newStudyGroup));
	}
}
