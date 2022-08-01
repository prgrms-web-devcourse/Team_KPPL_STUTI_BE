package prgrms.project.stuti.domain.member.service;

import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import prgrms.project.stuti.domain.member.model.Email;
import prgrms.project.stuti.domain.member.model.Member;
import prgrms.project.stuti.domain.member.model.MemberRole;
import prgrms.project.stuti.domain.member.repository.MemberRepository;
import prgrms.project.stuti.domain.member.service.dto.MemberDto;
import prgrms.project.stuti.domain.member.service.dto.MemberPutDto;
import prgrms.project.stuti.domain.member.service.dto.MemberResponse;
import prgrms.project.stuti.global.cache.model.TemporaryMember;
import prgrms.project.stuti.global.error.exception.MemberException;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;

	@Transactional(readOnly = true)
	public Optional<Member> getMember(Email email) {
		return memberRepository.findByEmail(email.getAddress());
	}

	@Transactional(readOnly = true)
	public MemberResponse getMember(Long id) {
		Member member = memberRepository.findById(id).orElseThrow(() -> MemberException.notFoundMember(id));

		return MemberConverter.toMemberResponse(member);
	}

	@Transactional
	public Member signup(MemberDto memberDto, TemporaryMember temporaryMember) throws DataIntegrityViolationException {
		return memberRepository.save(Member.builder()
			.email(memberDto.email())
			.nickName(memberDto.nickname())
			.field(memberDto.field())
			.career(memberDto.career())
			.profileImageUrl(temporaryMember.getImageUrl())
			.mbti(memberDto.MBTI())
			.memberRole(MemberRole.ROLE_MEMBER)
			.build());
	}

	@Transactional
	public MemberResponse putMember(Long memberId, MemberPutDto memberPutDto) throws
		DataIntegrityViolationException {
		String nickname = memberPutDto.nickname();
		memberRepository.findByNickName(nickname).ifPresent(member -> {
			if(!member.getId().equals(memberId)){
				throw MemberException.nicknameDuplication(nickname);
			}
		});

		Member member = memberRepository.findById(memberId).orElseThrow(() -> MemberException.notFoundMember(memberId));
		member.change(memberPutDto);

		return MemberConverter.toMemberResponse(member);
	}
}
