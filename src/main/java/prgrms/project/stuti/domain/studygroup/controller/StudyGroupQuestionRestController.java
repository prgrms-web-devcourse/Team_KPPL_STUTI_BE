package prgrms.project.stuti.domain.studygroup.controller;

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
import prgrms.project.stuti.domain.studygroup.controller.dto.StudyGroupQuestionRequest;
import prgrms.project.stuti.domain.studygroup.service.StudyGroupQuestionService;
import prgrms.project.stuti.domain.studygroup.service.dto.StudyGroupQuestionDto;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupQuestionResponse;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupQuestionsResponse;
import prgrms.project.stuti.global.page.PageResponse;

@RestController
@RequestMapping("/api/v1/study-groups/{studyGroupId}/questions")
@RequiredArgsConstructor
public class StudyGroupQuestionRestController {

	private final StudyGroupQuestionService studyGroupQuestionService;
	private static final String DEFAULT_SIZE = "5";

	@PostMapping
	public ResponseEntity<StudyGroupQuestionResponse> createStudyGroupQuestion(
		@AuthenticationPrincipal Long memberId, @PathVariable Long studyGroupId,
		@Valid @RequestBody StudyGroupQuestionRequest.CreateRequest createRequest
	) {
		StudyGroupQuestionDto.CreateDto createDto =
			StudyGroupQuestionMapper.toStudyGroupQuestionCreateDto(memberId, studyGroupId, createRequest);
		StudyGroupQuestionResponse questionResponse = studyGroupQuestionService.createStudyGroupQuestion(createDto);

		return ResponseEntity.ok(questionResponse);
	}

	@GetMapping
	public ResponseEntity<PageResponse<StudyGroupQuestionsResponse>> getStudyGroupQuestions(
		@PathVariable Long studyGroupId, @RequestParam(defaultValue = DEFAULT_SIZE) Long size,
		@RequestParam(required = false) Long lastStudyGroupQuestionId
	) {
		StudyGroupQuestionDto.PageDto pageDto =
			StudyGroupQuestionMapper.toStudyGroupQuestionPageDto(studyGroupId, size, lastStudyGroupQuestionId);
		PageResponse<StudyGroupQuestionsResponse> questionsResponse =
			studyGroupQuestionService.getStudyGroupQuestions(pageDto);

		return ResponseEntity.ok(questionsResponse);
	}

	@PatchMapping("/{studyGroupQuestionId}")
	public ResponseEntity<StudyGroupQuestionResponse> updateStudyGroupQuestion(
		@AuthenticationPrincipal Long memberId, @PathVariable Long studyGroupId,
		@PathVariable Long studyGroupQuestionId,
		@Valid @RequestBody StudyGroupQuestionRequest.UpdateRequest updateRequest
	) {
		StudyGroupQuestionDto.UpdateDto updateDto = StudyGroupQuestionMapper
			.toStudyGroupQuestionUpdateDto(memberId, studyGroupId, studyGroupQuestionId, updateRequest);
		StudyGroupQuestionResponse questionResponse = studyGroupQuestionService.updateStudyGroupQuestion(updateDto);

		return ResponseEntity.ok(questionResponse);
	}

	@DeleteMapping("/{studyGroupQuestionId}")
	public ResponseEntity<StudyGroupQuestionResponse> deleteStudyGroupQuestion(
		@AuthenticationPrincipal Long memberId, @PathVariable Long studyGroupId, @PathVariable Long studyGroupQuestionId
	) {
		StudyGroupQuestionDto.DeleteDto deleteDto =
			StudyGroupQuestionMapper.toStudyGroupQuestionDeleteDto(memberId, studyGroupId, studyGroupQuestionId);
		StudyGroupQuestionResponse questionResponse = studyGroupQuestionService.deleteStudyGroupQuestion(deleteDto);

		return ResponseEntity.ok(questionResponse);
	}
}
