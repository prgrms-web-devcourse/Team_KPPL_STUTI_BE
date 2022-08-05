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
import prgrms.project.stuti.domain.studygroup.service.StudyGroupMemberService;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupMemberIdResponse;

@RestController
@RequestMapping("/api/v1/study-groups/{studyGroupId}/members")
@RequiredArgsConstructor
public class StudyGroupMemberRestController {

	private final StudyGroupMemberService studyGroupMemberService;

	@PostMapping
	public ResponseEntity<StudyGroupMemberIdResponse> applyForJoinStudyGroup(@AuthenticationPrincipal Long memberId,
		@PathVariable Long studyGroupId) {
		StudyGroupMemberIdResponse idResponse = studyGroupMemberService.applyForJoinStudyGroup(memberId, studyGroupId);

		return ResponseEntity.ok(idResponse);
	}

	@PatchMapping("/{studyGroupMemberId}")
	public ResponseEntity<StudyGroupMemberIdResponse> acceptRequestForJoin(@AuthenticationPrincipal Long memberId,
		@PathVariable Long studyGroupId, @PathVariable Long studyGroupMemberId) {
		StudyGroupMemberIdResponse idResponse = studyGroupMemberService.acceptRequestForJoin(memberId, studyGroupId,
			studyGroupMemberId);

		return ResponseEntity.ok(idResponse);
	}

	@DeleteMapping(value = "/{studyGroupMemberId}")
	public ResponseEntity<Void> deleteStudyGroupMember(@AuthenticationPrincipal Long memberId,
		@PathVariable Long studyGroupId, @PathVariable Long studyGroupMemberId) {
		studyGroupMemberService.deleteStudyGroupMember(memberId, studyGroupId, studyGroupMemberId);

		return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).build();
	}
}
