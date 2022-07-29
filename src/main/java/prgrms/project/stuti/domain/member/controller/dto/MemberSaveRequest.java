package prgrms.project.stuti.domain.member.controller.dto;

import javax.validation.constraints.NotNull;

import lombok.Builder;
import prgrms.project.stuti.domain.member.model.Career;
import prgrms.project.stuti.domain.member.model.Field;
import prgrms.project.stuti.domain.member.model.Mbti;

public record MemberSaveRequest(
	@NotNull
	String email,

	@NotNull
	String nickname,

	@NotNull
	Field field,

	@NotNull
	Career career,

	@NotNull
	Mbti MBTI
) {
	@Builder
	public MemberSaveRequest(String email, String nickname, Field field, Career career, Mbti MBTI) {
		this.email = email;
		this.nickname = nickname;
		this.field = field;
		this.career = career;
		this.MBTI = MBTI;
	}
}