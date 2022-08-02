package prgrms.project.stuti.domain.studygroup.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.studygroup.service.response.StudyMemberIdResponse;
import prgrms.project.stuti.domain.studygroup.service.studymember.StudyMemberService;

@RestController
@RequestMapping("/api/v1/study-groups/{studyGroupId}/study-members")
@RequiredArgsConstructor
public class StudyMemberRestController {

	private final StudyMemberService studyMemberService;

	@PatchMapping("/{studyMemberId}")
	public ResponseEntity<StudyMemberIdResponse> acceptRequestForJoin(@AuthenticationPrincipal Long memberId,
		@PathVariable Long studyGroupId, @PathVariable Long studyMemberId) {
		StudyMemberIdResponse idResponse = studyMemberService.acceptRequestForJoin(memberId, studyGroupId,
			studyMemberId);

		return ResponseEntity.ok(idResponse);
	}
}
