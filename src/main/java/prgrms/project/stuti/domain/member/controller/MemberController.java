package prgrms.project.stuti.domain.member.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.feed.model.Feed;
import prgrms.project.stuti.domain.member.controller.dto.MemberIdRequest;
import prgrms.project.stuti.domain.member.service.MemberFacade;
import prgrms.project.stuti.domain.member.service.dto.MemberResponse;
import prgrms.project.stuti.domain.studygroup.model.StudyGroup;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {
	private final MemberFacade memberFacade;

	@GetMapping("/{member_id}")
	public ResponseEntity<MemberResponse> members(@Valid @RequestBody MemberIdRequest memberIdRequest,
		@RequestParam(value = "member_id") Long memberId) {
		return ResponseEntity
			.ok()
			.body(memberFacade.getMember(memberId));
	}

	@GetMapping("/{member_id}/studies?page={page_number}&amount={amount_number}")
	public ResponseEntity<List<StudyGroup>> studies(@PathVariable("member_id") Long memberId,
		@RequestParam("page_number") Long pageNumber,
		@RequestParam("amount_number") Long amountNumber) {

		// 개발중

		return ResponseEntity
			.ok()
			.body(null);
	}

	@GetMapping("/{member_id}/feeds?page={page_number}&amount={amount_number}")
	public ResponseEntity<List<Feed>> feeds(@RequestParam(value = "member_id") Long memberId,
		@RequestParam(value = "page_number") Long pageNumber,
		@RequestParam(value = "amount_number") Long amountNumber) {

		// 개발중

		return ResponseEntity
			.ok()
			.body(null);
	}
}
