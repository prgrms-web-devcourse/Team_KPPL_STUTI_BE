package prgrms.project.stuti.domain.member.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import prgrms.project.stuti.domain.member.model.Member;
import prgrms.project.stuti.domain.member.service.dto.MemberIdResponse;
import prgrms.project.stuti.domain.member.service.dto.MemberResponse;

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
}
