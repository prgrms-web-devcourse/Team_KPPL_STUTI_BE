package prgrms.project.stuti.domain.studygroup.controller;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupQuestionListResponse;
import prgrms.project.stuti.domain.studygroup.controller.dto.StudyGroupQuestionCreateRequest;
import prgrms.project.stuti.domain.studygroup.controller.dto.StudyGroupQuestionUpdateRequest;
import prgrms.project.stuti.domain.studygroup.service.StudyGroupQuestionService;
import prgrms.project.stuti.domain.studygroup.service.dto.StudyGroupQuestionCreateDto;
import prgrms.project.stuti.domain.studygroup.service.dto.StudyGroupQuestionUpdateDto;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupQuestionResponse;
import prgrms.project.stuti.global.page.PageResponse;

@RestController
@RequestMapping("/api/v1/study-groups/{studyGroupId}/questions")
@RequiredArgsConstructor
public class StudyGroupQuestionRestController {

	private final StudyGroupQuestionService studyGroupQuestionService;

	@PostMapping
	public ResponseEntity<StudyGroupQuestionResponse> createStudyGroupQuestion(@AuthenticationPrincipal Long memberId,
		@PathVariable Long studyGroupId, @Valid @RequestBody StudyGroupQuestionCreateRequest createRequest) {
		StudyGroupQuestionCreateDto createDto = StudyGroupMapper.toStudyGroupQuestionCreateDto(memberId, studyGroupId,
			createRequest);
		StudyGroupQuestionResponse questionResponse = studyGroupQuestionService.createStudyGroupQuestion(createDto);
		URI uri = URI.create(
			"/api/v1/study-groups/" + studyGroupId + "/questions/" + questionResponse.studyGroupQuestionId());

		return ResponseEntity.created(uri).body(questionResponse);
	}

	@GetMapping
	public ResponseEntity<PageResponse<StudyGroupQuestionListResponse>> testQuestionResponse(@PathVariable Long studyGroupId,
		@RequestParam(defaultValue = "5") Long size, @RequestParam(required = false) Long lastStudyGroupQuestionId) {
		final PageResponse<StudyGroupQuestionListResponse> questionResponsePageResponse =
			studyGroupQuestionService.getStudyGroupQuestions(studyGroupId, size, lastStudyGroupQuestionId);

		return ResponseEntity.ok(questionResponsePageResponse);
	}

	@PatchMapping("/{studyGroupQuestionId}")
	public ResponseEntity<StudyGroupQuestionResponse> updateStudyGroupQuestion(@AuthenticationPrincipal Long memberId,
		@PathVariable Long studyGroupId, @PathVariable Long studyGroupQuestionId,
		@Valid @RequestBody StudyGroupQuestionUpdateRequest updateRequest) {
		StudyGroupQuestionUpdateDto updateDto =
			StudyGroupMapper.toStudyGroupQuestionUpdateDto(memberId, studyGroupId, studyGroupQuestionId, updateRequest);
		StudyGroupQuestionResponse questionResponse = studyGroupQuestionService.updateStudyGroupQuestion(updateDto);

		return ResponseEntity.ok(questionResponse);
	}

	@DeleteMapping("/{studyGroupQuestionId}")
	public ResponseEntity<StudyGroupQuestionResponse> deleteStudyGroupQuestion(@AuthenticationPrincipal Long memberId,
		@PathVariable Long studyGroupId, @PathVariable Long studyGroupQuestionId) {
		StudyGroupQuestionResponse questionResponse = studyGroupQuestionService.deleteStudyGroupQuestion(
			memberId, studyGroupId, studyGroupQuestionId);

		return ResponseEntity.ok(questionResponse);
	}
}
