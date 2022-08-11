package prgrms.project.stuti.domain.member.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import prgrms.project.stuti.domain.member.model.Email;
import prgrms.project.stuti.domain.member.model.Member;
import prgrms.project.stuti.domain.member.repository.MemberRepository;
import prgrms.project.stuti.domain.member.service.dto.MemberPutDto;
import prgrms.project.stuti.domain.member.service.dto.MemberResponse;
import prgrms.project.stuti.global.error.exception.MemberException;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;

	@Transactional(readOnly = true)
	public Optional<Member> getMember(Email email) {
		return memberRepository.findMemberByEmail(email.getAddress());
	}

	@Transactional(readOnly = true)
	public MemberResponse getMember(Long id) {
		Member member = memberRepository.findMemberById(id).orElseThrow(() -> MemberException.notFoundMember(id));

		return MemberConverter.toMemberResponse(member);
	}

	@Transactional
	public MemberResponse editMember(Long memberId, MemberPutDto memberPutDto) {
		checkDuplicatedNickname(memberPutDto.nickname(), memberId);
		Member member = memberRepository.findMemberById(memberId)
			.orElseThrow(() -> MemberException.notFoundMember(memberId));
		member.change(memberPutDto);

		return MemberConverter.toMemberResponse(member);
	}

	private void checkDuplicatedNickname(String nickname, Long memberId) {
		memberRepository.findMemberByNickName(nickname).ifPresent(member -> {
			if (member.getId() != memberId) {
				throw MemberException.nicknameDuplication(nickname);
			}
		});
	}
}
