package prgrms.project.stuti.domain.member.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import prgrms.project.stuti.domain.member.model.Email;
import prgrms.project.stuti.domain.member.model.Field;
import prgrms.project.stuti.domain.member.model.Mbti;
import prgrms.project.stuti.domain.member.model.Member;
import prgrms.project.stuti.domain.member.model.MemberRole;
import prgrms.project.stuti.domain.member.repository.MemberRepository;
import prgrms.project.stuti.domain.member.service.converter.MemberConverter;
import prgrms.project.stuti.domain.member.service.dto.MemberDto;
import prgrms.project.stuti.domain.member.service.dto.MemberResponse;
import prgrms.project.stuti.global.cache.model.TemporaryMember;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;

	@Transactional(readOnly = true)
	public Optional<Member> getUser(Email email) {
		return memberRepository.findByEmail(email);
	}

	@Transactional(readOnly = true)
	public List<Member> getUsers() {
		return memberRepository.findAll();
	}

	@Transactional
	public MemberResponse signup(MemberDto memberDto, TemporaryMember temporaryMember) {
		Member member = memberRepository.save(Member.builder()
			.email(memberDto.email())
			.nickName(memberDto.nickname())
			.field(Field.valueOf(memberDto.field()))
			.career(memberDto.career())
			.profileImageUrl(temporaryMember.getImageUrl())
			.mbti(Mbti.valueOf(memberDto.MBTI()))
			.memberRole(MemberRole.ROLE_USER)
			.build());

		return MemberConverter.toMemberResponse(member.getId());
	}
}
