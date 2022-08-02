package prgrms.project.stuti.domain.member.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import prgrms.project.stuti.domain.member.model.Member;
import prgrms.project.stuti.domain.member.model.MemberRole;
import prgrms.project.stuti.domain.member.service.dto.MemberDto;
import prgrms.project.stuti.domain.member.service.dto.MemberIdResponse;
import prgrms.project.stuti.domain.member.service.dto.MemberResponse;
import prgrms.project.stuti.global.cache.model.TemporaryMember;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberConverter {

	public static MemberIdResponse toMemberIdResponse(Long memberId) {
		return new MemberIdResponse(memberId);
	}

	public static MemberResponse toMemberResponse(Member member){
		return MemberResponse.builder()
			.id(member.getId())
			.email(member.getEmail())
			.profileImageUrl(member.getProfileImageUrl())
			.nickname(member.getNickName())
			.field(member.getField())
			.career(member.getCareer())
			.MBTI(member.getMbti())
			.githubUrl(member.getGithubUrl())
			.blogUrl(member.getBlogUrl())
			.build();
	}
	public static Member toMember(MemberDto memberDto, TemporaryMember temporaryMember){
		return Member.builder()
			.email(memberDto.email())
			.nickName(memberDto.nickname())
			.field(memberDto.field())
			.career(memberDto.career())
			.profileImageUrl(temporaryMember.getImageUrl())
			.mbti(memberDto.MBTI())
			.memberRole(MemberRole.ROLE_MEMBER)
			.build();
	}
}
