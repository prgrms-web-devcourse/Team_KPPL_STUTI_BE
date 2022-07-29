package prgrms.project.stuti.domain.member.service.dto;

import lombok.Builder;
import prgrms.project.stuti.domain.member.model.Career;
import prgrms.project.stuti.domain.member.model.Field;
import prgrms.project.stuti.domain.member.model.Mbti;

public record MemberDto(
	String email,
	String nickname,
	Field field,
	Career career,
	Mbti mbti
) {
	@Builder
	public MemberDto(String email, String nickname, Field field, Career career, Mbti mbti) {
		this.email = email;
		this.nickname = nickname;
		this.field = field;
		this.career = career;
		this.mbti = mbti;
	}
}

