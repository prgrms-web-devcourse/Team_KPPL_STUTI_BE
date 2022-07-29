package prgrms.project.stuti.domain.member.service;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.member.model.Member;
import prgrms.project.stuti.domain.member.service.dto.MemberResponse;

@Service
@RequiredArgsConstructor
public class MemberFacade {
	private final MemberService memberService;

	public MemberResponse getMember(Long memberId) {
		Member member = memberService.getMember(memberId)
			.orElseThrow(() -> new EntityNotFoundException("error"));

		return MemberConverter.toMemberResponse(member);
	}
}
