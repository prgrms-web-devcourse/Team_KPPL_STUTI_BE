package prgrms.project.stuti.domain.member.service.dto;

import lombok.Builder;

@Builder
public record MemberResponse(
    Long memberId
) {

}
