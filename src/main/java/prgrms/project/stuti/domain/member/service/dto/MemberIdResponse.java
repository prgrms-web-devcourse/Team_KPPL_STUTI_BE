package prgrms.project.stuti.domain.member.service.dto;

import lombok.Builder;

public record MemberIdResponse(
	Long memberId
) {
	@Builder
	public MemberIdResponse {
	}
}
