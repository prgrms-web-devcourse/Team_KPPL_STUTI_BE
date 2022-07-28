package prgrms.project.stuti.domain.member.controller.dto;

import javax.validation.constraints.NotNull;

import lombok.Builder;

@Builder
public record MemberSaveRequest(
	@NotNull
	String email,
	@NotNull
	String nickname,
	@NotNull
	String field,
	@NotNull
	String career,
	@NotNull
	String MBTI
) {

}