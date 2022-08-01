package prgrms.project.stuti.domain.studygroup.controller;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.studygroup.controller.dto.StudyGroupCreateRequest;
import prgrms.project.stuti.domain.studygroup.service.StudyGroupService;
import prgrms.project.stuti.domain.studygroup.service.dto.StudyGroupCreateDto;
import prgrms.project.stuti.domain.studygroup.service.dto.StudyGroupIdResponse;

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
}
