package prgrms.project.stuti.domain.member.controller;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.member.controller.dto.MemberUpdateRequest;
import prgrms.project.stuti.domain.member.service.MemberService;
import prgrms.project.stuti.domain.member.service.dto.MemberResponse;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberRestController {
	private final MemberService memberService;

	@GetMapping("/{memberId}")
	public ResponseEntity<MemberResponse> getMember(@PathVariable Long memberId) {
		return ResponseEntity
			.ok()
			.body(memberService.getMember(memberId));
	}

	@PatchMapping("/{memberId}")
	public ResponseEntity<MemberResponse> updateMember(
		@Valid @RequestBody MemberUpdateRequest memberUpdateRequest, @PathVariable Long memberId
	) {
		MemberResponse memberResponse = memberService.editMember(memberId, MemberMapper.toMemberPutDto(
			memberUpdateRequest));

		return ResponseEntity
			.ok()
			.body(memberResponse);
	}
}
