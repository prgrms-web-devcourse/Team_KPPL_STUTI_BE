package prgrms.project.stuti.domain.studygroup.service;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import prgrms.project.stuti.config.ServiceTestConfig;
import prgrms.project.stuti.domain.member.model.Mbti;
import prgrms.project.stuti.domain.studygroup.model.Region;
import prgrms.project.stuti.domain.studygroup.model.Topic;
import prgrms.project.stuti.domain.studygroup.service.dto.StudyGroupCreateDto;
import prgrms.project.stuti.domain.studygroup.service.dto.StudyGroupIdResponse;

class StudyGroupServiceTest extends ServiceTestConfig {

	@Autowired
	private StudyGroupService studyGroupService;

	@Test
	@DisplayName("새로운 스터디 그룹을 생성한다.")
	void testCreateStudyGroup() throws IOException {
		//given
		StudyGroupCreateDto createDto = toCreateDto(member.getId());

		//when
		StudyGroupIdResponse idResponse = studyGroupService.createStudyGroup(createDto);

		//then
		assertNotNull(idResponse);
		assertEquals(1L, idResponse.studyGroupId());
	}

	private StudyGroupCreateDto toCreateDto(Long memberId) throws IOException {
		return StudyGroupCreateDto
			.builder()
			.memberId(memberId)
			.imageFile(getMultipartFile())
			.title("title")
			.topic(Topic.AI)
			.isOnline(false)
			.region(Region.SEOUL)
			.preferredMBTIs(List.of(Mbti.ENFJ, Mbti.ENFP))
			.numberOfRecruits(5)
			.startDateTime(LocalDateTime.now().plusDays(10))
			.endDateTime(LocalDateTime.now().plusMonths(3))
			.description("this is new study group")
			.build();
	}

	private MultipartFile getMultipartFile() throws IOException {
		File imageFile = new File(Paths.get("src", "test", "resources") + File.separator + "test.png");
		FileInputStream inputStream = new FileInputStream(imageFile);
		String[] split = imageFile.getName().split("\\.");

		return new MockMultipartFile(split[0], imageFile.getName(), "image/" + split[1], inputStream);
	}
}
