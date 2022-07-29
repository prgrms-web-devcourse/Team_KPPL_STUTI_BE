package prgrms.project.stuti.domain.member.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import prgrms.project.stuti.domain.member.service.dto.MemberIdResponse;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberConverter {

	public static MemberIdResponse toMemberResponse(Long memberId) {
		return new MemberIdResponse(memberId);
	}
}
