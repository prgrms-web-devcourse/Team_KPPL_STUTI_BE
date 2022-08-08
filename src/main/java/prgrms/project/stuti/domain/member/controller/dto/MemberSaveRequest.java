package prgrms.project.stuti.domain.member.controller.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Builder;
import prgrms.project.stuti.domain.member.model.Career;
import prgrms.project.stuti.domain.member.model.Field;
import prgrms.project.stuti.domain.member.model.Mbti;

public record MemberSaveRequest(
	@NotBlank
	String email,

	@NotBlank
	String nickname,

	@NotNull
	Field field,

	@NotNull
	Career career,

	@NotNull
	Mbti MBTI
) {
	@Builder
	public MemberSaveRequest {
	}
}