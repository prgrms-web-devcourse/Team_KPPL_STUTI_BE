package prgrms.project.stuti.domain.studygroup.controller;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.studygroup.controller.dto.QuestionCreateRequest;
import prgrms.project.stuti.domain.studygroup.service.question.QuestionService;
import prgrms.project.stuti.domain.studygroup.service.dto.QuestionCreateDto;
import prgrms.project.stuti.domain.studygroup.service.response.QuestionIdResponse;

@RestController
@RequestMapping("/api/v1/study-groups/{studyGroupId}/questions")
@RequiredArgsConstructor
public class QuestionRestController {

	private final QuestionService questionService;

	@PostMapping
	public ResponseEntity<QuestionIdResponse> createQuestion(@AuthenticationPrincipal Long memberId,
		@PathVariable Long studyGroupId, @Valid @RequestBody QuestionCreateRequest createRequest) {
		QuestionCreateDto askDto = StudyGroupMapper.toQuestionCreateDto(memberId, studyGroupId, createRequest);
		QuestionIdResponse idResponse = questionService.createQuestion(askDto);
		URI uri = URI.create("/api/v1/study-groups/" + studyGroupId + "/questions/" + idResponse.questionId());

		return ResponseEntity.created(uri).body(idResponse);
	}
}
