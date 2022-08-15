package prgrms.project.stuti.domain.member.controller;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import prgrms.project.stuti.domain.member.controller.dto.MemberUpdateRequest;
import prgrms.project.stuti.domain.member.controller.dto.MemberSaveRequest;
import prgrms.project.stuti.domain.member.service.dto.MemberDto;
import prgrms.project.stuti.domain.member.service.dto.MemberPatchDto;

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

	public static MemberPatchDto toMemberPutDto(MemberUpdateRequest memberUpdateRequest) {
		return MemberPatchDto.builder()
			.nickname(memberUpdateRequest.nickname())
			.career(memberUpdateRequest.career())
			.field(memberUpdateRequest.field())
			.career(memberUpdateRequest.career())
			.MBTI(memberUpdateRequest.MBTI())
			.githubUrl(memberUpdateRequest.githubUrl())
			.blogUrl(memberUpdateRequest.blogUrl())
			.build();
	}
}
