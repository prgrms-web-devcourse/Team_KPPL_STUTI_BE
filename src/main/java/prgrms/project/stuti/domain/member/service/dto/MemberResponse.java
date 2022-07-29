package prgrms.project.stuti.domain.member.service.dto;

import lombok.Builder;
import prgrms.project.stuti.domain.member.model.Career;
import prgrms.project.stuti.domain.member.model.Field;
import prgrms.project.stuti.domain.member.model.Mbti;

public record MemberResponse (
	Long id,
	String email,
	String profileImageUrl,
	String nickname,
	Field field,
	Career career,
	Mbti MBTI,
	String githubUrl,
	String blogUrl
){
	@Builder
	public MemberResponse(Long id, String email, String profileImageUrl, String nickname, Field field, Career career,
		Mbti MBTI, String githubUrl, String blogUrl) {
		this.id = id;
		this.email = email;
		this.profileImageUrl = profileImageUrl;
		this.nickname = nickname;
		this.field = field;
		this.career = career;
		this.MBTI = MBTI;
		this.githubUrl = githubUrl;
		this.blogUrl = blogUrl;
	}
}
