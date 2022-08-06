package prgrms.project.stuti.domain.studygroup.controller;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.studygroup.controller.dto.StudyGroupCreateRequest;
import prgrms.project.stuti.domain.studygroup.controller.dto.StudyGroupFindCondition;
import prgrms.project.stuti.domain.studygroup.controller.dto.StudyGroupUpdateRequest;
import prgrms.project.stuti.domain.studygroup.service.StudyGroupService;
import prgrms.project.stuti.domain.studygroup.service.dto.StudyGroupCreateDto;
import prgrms.project.stuti.domain.studygroup.service.dto.StudyGroupFindConditionDto;
import prgrms.project.stuti.domain.studygroup.service.dto.StudyGroupUpdateDto;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupDetailResponse;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupIdResponse;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupResponse;
import prgrms.project.stuti.global.error.exception.MemberException;
import prgrms.project.stuti.global.page.CursorPageResponse;

@RestController
@RequestMapping("/api/v1/study-groups")
@RequiredArgsConstructor
public class StudyGroupRestController {

	private final StudyGroupService studyGroupService;

	@PostMapping
	public ResponseEntity<StudyGroupIdResponse> createStudyGroup(@AuthenticationPrincipal Long memberId,
		@Valid @ModelAttribute StudyGroupCreateRequest createRequest) {
		StudyGroupCreateDto createDto = StudyGroupMapper.toStudyGroupCreateDto(memberId, createRequest);
		StudyGroupIdResponse idResponse = studyGroupService.createStudyGroup(createDto);
		URI uri = URI.create("/api/v1/study-groups/" + idResponse.studyGroupId());

		return ResponseEntity.created(uri).body(idResponse);
	}

	@GetMapping
	public ResponseEntity<CursorPageResponse<StudyGroupResponse>> getStudyGroups(
		@RequestParam(defaultValue = "20") Long size, StudyGroupFindCondition condition) {
		StudyGroupFindConditionDto conditionDto =
			StudyGroupMapper.toStudyGroupFindConditionDto(null, size, condition);

		return ResponseEntity.ok(studyGroupService.getStudyGroups(conditionDto));
	}

	@GetMapping("/my-page/{memberId}")
	public ResponseEntity<CursorPageResponse<StudyGroupResponse>> getMyStudyGroups(
		@AuthenticationPrincipal Long authMemberId, @PathVariable Long memberId,
		@RequestParam(defaultValue = "20") Long size, StudyGroupFindCondition condition) {
		validateMyPageMember(authMemberId, memberId);

		StudyGroupFindConditionDto conditionDto =
			StudyGroupMapper.toStudyGroupFindConditionDto(memberId, size, condition);

		return ResponseEntity.ok(studyGroupService.getStudyGroups(conditionDto));
	}

	@GetMapping("/{studyGroupId}")
	public ResponseEntity<StudyGroupDetailResponse> getStudyGroup(@PathVariable Long studyGroupId) {
		StudyGroupDetailResponse detailResponse = studyGroupService.getStudyGroup(studyGroupId);

		return ResponseEntity.ok(detailResponse);
	}

	@PatchMapping("/{studyGroupId}")
	public ResponseEntity<StudyGroupIdResponse> updateStudyGroup(@AuthenticationPrincipal Long memberId,
		@PathVariable Long studyGroupId, @Valid @ModelAttribute StudyGroupUpdateRequest updateRequest) {
		StudyGroupUpdateDto updateDto = StudyGroupMapper.toStudyGroupUpdateDto(memberId, studyGroupId, updateRequest);
		StudyGroupIdResponse idResponse = studyGroupService.updateStudyGroup(updateDto);

		return ResponseEntity.ok(idResponse);
	}

	@DeleteMapping("/{studyGroupId}")
	public ResponseEntity<Void> deleteStudyGroup(@AuthenticationPrincipal Long memberId,
		@PathVariable Long studyGroupId) {
		studyGroupService.deleteStudyGroup(memberId, studyGroupId);

		return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).build();
	}

	private void validateMyPageMember(Long authMemberId, Long pathMemberId) {
		if (!authMemberId.equals(pathMemberId)) {
			throw MemberException.notMatchWithMyPageMember(authMemberId);
		}
	}
}
