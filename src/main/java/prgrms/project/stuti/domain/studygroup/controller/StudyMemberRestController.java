package prgrms.project.stuti.domain.studygroup.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

	@PostMapping
	public ResponseEntity<StudyMemberIdResponse> applyForJoinStudyGroup(@AuthenticationPrincipal Long memberId,
		@PathVariable Long studyGroupId) {
		StudyMemberIdResponse idResponse = studyMemberService.applyForJoinStudyGroup(memberId, studyGroupId);

		return ResponseEntity.ok(idResponse);
	}

	@PatchMapping("/{studyMemberId}")
	public ResponseEntity<StudyMemberIdResponse> acceptRequestForJoin(@AuthenticationPrincipal Long memberId,
		@PathVariable Long studyGroupId, @PathVariable Long studyMemberId) {
		StudyMemberIdResponse idResponse = studyMemberService.acceptRequestForJoin(memberId, studyGroupId,
			studyMemberId);

		return ResponseEntity.ok(idResponse);
	}

	@DeleteMapping(value = "/{studyMemberId}")
	public ResponseEntity<Void> deleteStudyMember(@AuthenticationPrincipal Long memberId,
		@PathVariable Long studyGroupId, @PathVariable Long studyMemberId) {
		studyMemberService.deleteStudyMember(memberId, studyGroupId, studyMemberId);

		return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).build();
	}
}
