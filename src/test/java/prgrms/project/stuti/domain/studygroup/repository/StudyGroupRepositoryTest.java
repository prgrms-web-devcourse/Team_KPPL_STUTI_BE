package prgrms.project.stuti.domain.studygroup.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
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
import prgrms.project.stuti.domain.studygroup.repository.studygroup.StudyGroupRepository;
import prgrms.project.stuti.domain.studygroup.repository.studymember.StudyGroupMemberRepository;
import prgrms.project.stuti.domain.studygroup.service.dto.StudyGroupDto;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupsResponse;
import prgrms.project.stuti.global.page.CursorPageResponse;

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
	@DisplayName("스터디 그룹을 상세조회한다.")
	void testFindStudyGroupDetailById() {
		//given
		Long studyGroupId = studyGroup.getId();

		// when
		List<StudyGroupQueryDto.StudyGroupDetailDto> detailDtos =
			studyGroupRepository.findStudyGroupDetailById(studyGroupId);

		// then
		assertFalse(detailDtos.isEmpty());

		StudyGroupQueryDto.StudyGroupDetailDto detailDto = detailDtos.get(0);
		assertEquals(studyGroup.getId(), detailDto.studyGroupId());
		assertEquals(member.getId(), detailDto.memberId());
	}

	@Test
	@DisplayName("전체 스터디 그룹을 동적으로 페이징 조회한다.")
	void dynamicFindAllWithCursorPagination() {
		//given
		studyGroupRepository.save(
			StudyGroup
				.builder()
				.imageUrl("imageUrl")
				.title("test title")
				.topic(Topic.NETWORK)
				.isOnline(true)
				.region(Region.ONLINE)
				.numberOfRecruits(5)
				.preferredMBTIs(Set.of(new PreferredMbti(Mbti.ESFJ)))
				.studyPeriod(new StudyPeriod(LocalDateTime.now().plusDays(10), LocalDateTime.now().plusMonths(10)))
				.description("description")
				.build());

		StudyGroupDto.FindCondition conditionDto = StudyGroupDto.FindCondition
			.builder()
			.topic(Topic.NETWORK)
			.size(20L)
			.build();

		//when
		CursorPageResponse<StudyGroupsResponse> pageResponse =
			studyGroupRepository.findAllWithCursorPaginationByConditions(conditionDto);

		//then
		assertFalse(pageResponse.contents().isEmpty());
		assertEquals(Topic.NETWORK.getValue(), pageResponse.contents().get(0).topic());
		assertFalse(pageResponse.hasNext());
	}
}
