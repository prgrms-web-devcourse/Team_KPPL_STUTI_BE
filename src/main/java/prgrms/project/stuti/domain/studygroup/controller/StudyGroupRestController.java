package prgrms.project.stuti.domain.studygroup.controller;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.studygroup.controller.dto.StudyGroupRequest;
import prgrms.project.stuti.domain.studygroup.service.StudyGroupService;
import prgrms.project.stuti.domain.studygroup.service.dto.StudyGroupDto;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupIdResponse;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupResponse;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupsResponse;
import prgrms.project.stuti.global.page.CursorPageResponse;

@RestController
@RequestMapping("/api/v1/study-groups")
@RequiredArgsConstructor
public class StudyGroupRestController {

	private final StudyGroupService studyGroupService;

	@PostMapping
	public ResponseEntity<StudyGroupIdResponse> createStudyGroup(
		@AuthenticationPrincipal Long memberId, @Valid @ModelAttribute StudyGroupRequest.CreateRequest createRequest
	) {
		StudyGroupDto.CreateDto createDto = StudyGroupMapper.toStudyGroupCreateDto(memberId, createRequest);
		StudyGroupIdResponse idResponse = studyGroupService.createStudyGroup(createDto);
		URI uri = URI.create("/api/v1/study-groups/" + idResponse.studyGroupId());

		return ResponseEntity.created(uri).body(idResponse);
	}

	@GetMapping
	public ResponseEntity<CursorPageResponse<StudyGroupsResponse>> getStudyGroups(
		@RequestParam(defaultValue = "20") Long size, StudyGroupRequest.FindCondition condition
	) {
		StudyGroupDto.FindCondition conditionDto =
			StudyGroupMapper.toStudyGroupFindConditionDto(size, condition);
		CursorPageResponse<StudyGroupsResponse> studyGroupsResponse = studyGroupService.getStudyGroups(conditionDto);

		return ResponseEntity.ok(studyGroupsResponse);
	}

	@GetMapping("/my-page")
	public ResponseEntity<CursorPageResponse<StudyGroupsResponse>> getMyStudyGroups(
		@AuthenticationPrincipal Long memberId, @RequestParam(defaultValue = "20") Long size,
		StudyGroupRequest.FindCondition condition
	) {
		StudyGroupDto.FindCondition conditionDto =
			StudyGroupMapper.toStudyGroupFindConditionDto(memberId, size, condition);
		CursorPageResponse<StudyGroupsResponse> studyGroupsResponse = studyGroupService.getMyStudyGroups(conditionDto);

		return ResponseEntity.ok(studyGroupsResponse);
	}

	@GetMapping("/{studyGroupId}")
	public ResponseEntity<StudyGroupResponse> getStudyGroup(@PathVariable Long studyGroupId) {
		StudyGroupDto.ReadDto readDto = StudyGroupMapper.toStudyGroupReadDto(studyGroupId);
		StudyGroupResponse studyGroupResponse = studyGroupService.getStudyGroup(readDto);

		return ResponseEntity.ok(studyGroupResponse);
	}

	@PostMapping("/{studyGroupId}")
	public ResponseEntity<StudyGroupIdResponse> updateStudyGroup(
		@AuthenticationPrincipal Long memberId, @PathVariable Long studyGroupId,
		@Valid @ModelAttribute StudyGroupRequest.UpdateRequest updateRequest
	) {
		StudyGroupDto.UpdateDto updateDto =
			StudyGroupMapper.toStudyGroupUpdateDto(memberId, studyGroupId, updateRequest);
		StudyGroupIdResponse idResponse = studyGroupService.updateStudyGroup(updateDto);

		return ResponseEntity.ok(idResponse);
	}

	@DeleteMapping("/{studyGroupId}")
	public ResponseEntity<Void> deleteStudyGroup(
		@AuthenticationPrincipal Long memberId, @PathVariable Long studyGroupId
	) {
		StudyGroupDto.DeleteDto deleteDto = StudyGroupMapper.toStudyGroupDeleteDto(memberId, studyGroupId);
		studyGroupService.deleteStudyGroup(deleteDto);

		return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).build();
	}
}
