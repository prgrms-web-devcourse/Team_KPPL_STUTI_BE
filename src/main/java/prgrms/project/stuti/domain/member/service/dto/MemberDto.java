package prgrms.project.stuti.domain.member.service.dto;

import lombok.Builder;

@Builder
public record MemberDto(
	String email,
	String nickname,
	String field,
	String career,
	String MBTI
) {

}

