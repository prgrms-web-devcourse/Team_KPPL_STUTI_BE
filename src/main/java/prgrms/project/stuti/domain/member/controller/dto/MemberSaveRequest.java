package prgrms.project.stuti.domain.member.controller.dto;

import lombok.Builder;

@Builder
public record MemberSaveRequest(
    String email,
    String nickname,
	String field,
    String career,
	String MBTI
) {

}