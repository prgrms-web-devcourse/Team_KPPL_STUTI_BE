package prgrms.project.stuti.domain.studygroup.repository.studygroup;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import prgrms.project.stuti.config.RepositoryTestConfig;
import prgrms.project.stuti.domain.member.model.Mbti;
import prgrms.project.stuti.domain.studygroup.model.PreferredMbti;
import prgrms.project.stuti.domain.studygroup.model.Region;
import prgrms.project.stuti.domain.studygroup.model.StudyGroup;
import prgrms.project.stuti.domain.studygroup.model.StudyMember;
import prgrms.project.stuti.domain.studygroup.model.StudyMemberRole;
import prgrms.project.stuti.domain.studygroup.model.StudyPeriod;
import prgrms.project.stuti.domain.studygroup.model.Topic;
import prgrms.project.stuti.domain.studygroup.repository.PreferredMbtiRepository;
import prgrms.project.stuti.domain.studygroup.repository.studymember.StudyMemberRepository;
import prgrms.project.stuti.domain.studygroup.service.dto.StudyGroupDetailDto;

class StudyGroupRepositoryTest extends RepositoryTestConfig {

	@Autowired
	private PreferredMbtiRepository preferredMbtiRepository;

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

		preferredMbtiRepository.save(new PreferredMbti(Mbti.ENFJ, studyGroup));

		studyMemberRepository.save(new StudyMember(StudyMemberRole.LEADER, member, studyGroup));
	}

	@Test
	@DisplayName("스터디 그룹을 상세조회한다. 반환 타입은 dto 로 변환해서 반환한다.")
	void testFindStudyGroupDetailById() {
		//given
		Long studyGroupId = studyGroup.getId();

		//when
		List<StudyGroupDetailDto> detailDtos = studyGroupRepository.findStudyGroupDetailById(studyGroupId);

		//then
		StudyGroupDetailDto detailDto = detailDtos.get(0);

		assertEquals(studyGroup.getId(), detailDto.studyGroupId());
		assertEquals(studyGroup.getTitle(), detailDto.title());
		assertEquals(studyGroup.getDescription(), detailDto.description());
		assertEquals(member.getId(), detailDto.memberId());
		assertEquals(member.getNickName(), detailDto.nickname());
		assertEquals(member.getField(), detailDto.field());
	}
}
