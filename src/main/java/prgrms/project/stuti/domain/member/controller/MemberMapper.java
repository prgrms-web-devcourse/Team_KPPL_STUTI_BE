package prgrms.project.stuti.domain.member.controller;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import prgrms.project.stuti.domain.member.controller.dto.MemberPutRequest;
import prgrms.project.stuti.domain.member.controller.dto.MemberSaveRequest;
import prgrms.project.stuti.domain.member.service.dto.MemberDto;
import prgrms.project.stuti.domain.member.service.dto.MemberPutDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberMapper {

	public static MemberDto toMemberDto(MemberSaveRequest memberSaveRequest) {
		return MemberDto.builder()
			.email(memberSaveRequest.email())
			.nickname(memberSaveRequest.nickname())
			.career(memberSaveRequest.career())
			.field(memberSaveRequest.field())
			.career(memberSaveRequest.career())
			.MBTI(memberSaveRequest.MBTI())
			.build();
	}

	public static MemberPutDto toMemberPutDto(MemberPutRequest memberPutRequest) {
		return MemberPutDto.builder()
			.email(memberPutRequest.email())
			.profileImageUrl(memberPutRequest.profileImageUrl())
			.nickname(memberPutRequest.nickname())
			.career(memberPutRequest.career())
			.field(memberPutRequest.field())
			.career(memberPutRequest.career())
			.MBTI(memberPutRequest.MBTI())
			.githubUrl(memberPutRequest.githubUrl())
			.blogUrl(memberPutRequest.blogUrl())
			.build();
	}
}
