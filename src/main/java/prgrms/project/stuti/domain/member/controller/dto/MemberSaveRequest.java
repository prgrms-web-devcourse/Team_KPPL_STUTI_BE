package prgrms.project.stuti.domain.member.controller.dto;

import javax.validation.constraints.NotNull;

import lombok.Builder;

public record MemberSaveRequest(
	@NotNull String email,
	@NotNull String nickname,
	@NotNull String field,
	@NotNull String career,
	@NotNull String MBTI
) {
	@Builder
	public MemberSaveRequest(String email, String nickname, String field, String career, String MBTI) {
		this.email = email;
		this.nickname = nickname;
		this.field = field;
		this.career = career;
		this.MBTI = MBTI;
	}
}