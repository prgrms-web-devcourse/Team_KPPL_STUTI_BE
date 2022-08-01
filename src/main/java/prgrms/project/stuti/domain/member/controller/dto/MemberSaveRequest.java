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
	public MemberSaveRequest {
	}
}