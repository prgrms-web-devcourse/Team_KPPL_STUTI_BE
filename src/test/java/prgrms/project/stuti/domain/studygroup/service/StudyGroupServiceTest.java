package prgrms.project.stuti.domain.studygroup.service;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import prgrms.project.stuti.config.ServiceTestConfig;
import prgrms.project.stuti.domain.member.model.Mbti;
import prgrms.project.stuti.domain.studygroup.model.Region;
import prgrms.project.stuti.domain.studygroup.model.StudyGroup;
import prgrms.project.stuti.domain.studygroup.model.Topic;
import prgrms.project.stuti.domain.studygroup.repository.studygroup.StudyGroupRepository;
import prgrms.project.stuti.domain.studygroup.service.dto.StudyGroupCreateDto;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupDetailResponse;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupIdResponse;
import prgrms.project.stuti.domain.studygroup.service.dto.StudyGroupUpdateDto;
import prgrms.project.stuti.domain.studygroup.service.studygroup.StudyGroupService;
import prgrms.project.stuti.global.error.exception.StudyGroupException;

@Transactional
class StudyGroupServiceTest extends ServiceTestConfig {

	@Autowired
	private StudyGroupService studyGroupService;

	@Autowired
	private StudyGroupRepository studyGroupRepository;

	private StudyGroup studyGroup;

	@BeforeEach
	void setup() throws IOException {
		StudyGroupCreateDto createDto = toCreateDto(member.getId());
		StudyGroupIdResponse idResponse = studyGroupService.createStudyGroup(createDto);
		this.studyGroup = studyGroupRepository.findStudyGroupById(idResponse.studyGroupId())
			.orElseThrow(() -> new IllegalArgumentException("failed to find studyGroup"));
	}

	@Test
	@DisplayName("새로운 스터디 그룹을 생성한다.")
	void testCreateStudyGroup() throws IOException {
		//given
		StudyGroupCreateDto createDto = toCreateDto(member.getId());

		//when
		StudyGroupIdResponse idResponse = studyGroupService.createStudyGroup(createDto);
		StudyGroup studyGroup = studyGroupRepository.findStudyGroupById(idResponse.studyGroupId())
			.orElseThrow(() -> StudyGroupException.notFoundStudyGroup(idResponse.studyGroupId()));

		//then
		assertEquals(createDto.title(), studyGroup.getTitle());
		assertEquals(createDto.description(), studyGroup.getDescription());
	}

	@Test
	@DisplayName("스터디 그룹을 상세조회한다.")
	void testGetStudyGroup() {
		//given
		Long studyGroupId = studyGroup.getId();

		//when
		StudyGroupDetailResponse detailResponse = studyGroupService.getStudyGroup(studyGroupId);

		//then
		assertAll(
			() -> assertEquals(studyGroup.getId(), detailResponse.studyGroupId()),
			() -> assertEquals(studyGroup.getTitle(), detailResponse.title()),
			() -> assertEquals(studyGroup.getDescription(), detailResponse.description())
		);
	}

	@Test
	@DisplayName("스터디의 이미지, 제목, 설명을 업데이트한다.")
	void testUpdateStudyGroup() throws IOException {
		//given
		String updateTitle = "update title";
		String updateDescription = "update description";
		String imageUrlBeforeUpdate = studyGroup.getImageUrl();
		StudyGroupUpdateDto updateDto = toUpdateDto(member.getId(), studyGroup.getId(), updateTitle, getMultipartFile(),
			updateDescription);

		//when
		StudyGroupIdResponse idResponse = studyGroupService.updateStudyGroup(updateDto);

		//then
		assertNotNull(idResponse);
		assertEquals(studyGroup.getId(), idResponse.studyGroupId());
		assertNotEquals(imageUrlBeforeUpdate, studyGroup.getImageUrl());
		assertEquals(updateTitle, studyGroup.getTitle());
		assertEquals(updateDescription, studyGroup.getDescription());
	}

	@Test
	@DisplayName("새로운 이미지를 업로드 하지 않고, 제목, 설명이 전과 같다면 업데이트하지 않는다.")
	void testUpdateStudyGroupWithSameValueAsBefore() {
		//given
		String updateTitle = studyGroup.getTitle();
		String updateDescription = studyGroup.getDescription();
		String imageUrlBeforeUpdate = studyGroup.getImageUrl();
		StudyGroupUpdateDto updateDto = toUpdateDto(member.getId(), studyGroup.getId(), updateTitle, null,
			updateDescription);

		//when
		StudyGroupIdResponse idResponse = studyGroupService.updateStudyGroup(updateDto);

		//then
		assertNotNull(idResponse);
		assertEquals(studyGroup.getId(), idResponse.studyGroupId());
		assertEquals(imageUrlBeforeUpdate, studyGroup.getImageUrl());
		assertEquals(updateTitle, studyGroup.getTitle());
		assertEquals(updateDescription, studyGroup.getDescription());
	}

	@Test
	@DisplayName("회원이 스터디 그룹에 가입신청을한다.")
	void testApplyStudyGroup() {
		//given
		Long memberId = member2.getId();
		Long studyGroupId = studyGroup.getId();

		//when
		StudyGroupIdResponse idResponse = studyGroupService.applyStudyGroup(memberId, studyGroupId);

		//then
		assertNotNull(idResponse);
		assertEquals(studyGroupId, idResponse.studyGroupId());
	}

	@Test
	@DisplayName("이미 가입 했거나 가입신청을한 스터디 그룹에 가입신청을 한다면 예외가 발생한다.")
	void testExistingStudyMember() {
		//given
		Long memberId = member2.getId();
		Long studyGroupId = studyGroup.getId();

		//when, then
		studyGroupService.applyStudyGroup(memberId, studyGroupId);
		assertThrows(StudyGroupException.class, () -> studyGroupService.applyStudyGroup(memberId, studyGroupId));
	}

	@Test
	@DisplayName("스터디 그룹을 삭제한다.")
	void testDeleteStudyGroup() {
	    //given
		Long memberId = member.getId();
		Long studyGroupId = studyGroup.getId();

		//when
		studyGroupService.deleteStudyGroup(memberId, studyGroupId);
		Optional<StudyGroup> deletedStudyGroup = studyGroupRepository.findStudyGroupById(studyGroup.getId());

	    //then
		assertTrue(deletedStudyGroup.isEmpty());
	}

	@Test
	@DisplayName("리더가 아닌 회원이 스터디 그룹을 삭제하려고 접근한다면 예외가 발생한다.")
	void testNotLeaderAccessToDeleteStudyGroup() {
		//given
		Long memberId = member2.getId();
		Long studyGroupId = studyGroup.getId();
		studyGroupService.applyStudyGroup(memberId, studyGroupId);

		//when, then
		assertThrows(StudyGroupException.class, () -> studyGroupService.deleteStudyGroup(memberId, studyGroupId));
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

	private StudyGroupUpdateDto toUpdateDto(Long memberId, Long studyGroupId, String updateTitle,
		MultipartFile imageFile, String updateDescription) {
		return StudyGroupUpdateDto
			.builder()
			.memberId(memberId)
			.studyGroupId(studyGroupId)
			.title(updateTitle)
			.imageFile(imageFile)
			.description(updateDescription)
			.build();
	}

	private MultipartFile getMultipartFile() throws IOException {
		File imageFile = new File(Paths.get("src", "test", "resources") + File.separator + "test.png");
		FileInputStream inputStream = new FileInputStream(imageFile);
		String[] split = imageFile.getName().split("\\.");

		return new MockMultipartFile(split[0], imageFile.getName(), "image/" + split[1], inputStream);
	}
}
