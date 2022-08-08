package prgrms.project.stuti.domain.member.controller.dto;

import prgrms.project.stuti.domain.member.service.dto.MemberResponse;

public record MemberSignupResponse(
	MemberResponse member,
	String accesstoken
) {
}
