package prgrms.project.stuti.domain.member.service.converter;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import prgrms.project.stuti.domain.member.service.dto.MemberResponse;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberConverter {

	public static MemberResponse toMemberResponse(Long memberId) {
		return new MemberResponse(memberId);
	}
}
