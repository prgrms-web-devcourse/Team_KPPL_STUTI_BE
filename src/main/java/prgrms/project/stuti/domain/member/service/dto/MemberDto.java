package prgrms.project.stuti.domain.member.service.dto;

import lombok.Builder;

public record MemberDto(
	String email,
	String nickname,
	String field,
	String career,
	String MBTI
) {
	@Builder
	public MemberDto(String email, String nickname, String field, String career, String MBTI) {
		this.email = email;
		this.nickname = nickname;
		this.field = field;
		this.career = career;
		this.MBTI = MBTI;
	}
}

