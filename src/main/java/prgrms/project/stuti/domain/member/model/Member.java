package prgrms.project.stuti.domain.member.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import prgrms.project.stuti.domain.member.service.dto.MemberPutDto;
import prgrms.project.stuti.global.base.BaseTimeEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id", unique = true, nullable = false, updatable = false)
	private Long id;

	@Column(name = "email", length = 40, unique = true, nullable = false)
	private String email;

	@Column(name = "nick_name", length = 30, unique = true, nullable = false)
	private String nickName;

	@Enumerated(value = EnumType.STRING)
	@Column(name = "field", length = 20, nullable = false)
	private Field field;

	@Enumerated(value = EnumType.STRING)
	@Column(name = "career", length = 20, nullable = false)
	private Career career;

	@Column(name = "profile_image_url", length = 150)
	private String profileImageUrl;

	@Enumerated(value = EnumType.STRING)
	@Column(name = "mbti", length = 5, nullable = false)
	private Mbti mbti;

	@Column(name = "github_url", length = 100)
	private String githubUrl;

	@Column(name = "blog_url", length = 100)
	private String blogUrl;

	@Enumerated(value = EnumType.STRING)
	@Column(name = "member_role", length = 16, nullable = false)
	private MemberRole memberRole;

	@Column(name = "is_deleted", nullable = false)
	private boolean isDeleted;

	@Builder
	public Member(String email, String nickName, Field field, Career career, String profileImageUrl, String githubUrl,
		Mbti mbti, String blogUrl, MemberRole memberRole) {
		this.email = new Email(email).getAddress();
		this.nickName = nickName;
		this.field = field;
		this.career = career;
		this.profileImageUrl = profileImageUrl;
		this.mbti = mbti;
		this.githubUrl = githubUrl;
		this.blogUrl = blogUrl;
		this.memberRole = memberRole;
		this.isDeleted = false;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this,
			ToStringStyle.SHORT_PREFIX_STYLE)
			.append("id", id)
			.append("email", email)
			.append("nickName", nickName)
			.append("field", field)
			.append("career", career)
			.append("profileImageUrl", profileImageUrl)
			.append("githubUrl", githubUrl)
			.append("blogUrl", blogUrl)
			.append("memberRole", memberRole)
			.toString();
	}

	public void change(MemberPutDto memberPutDto) {
		this.email = memberPutDto.email();
		this.nickName = memberPutDto.nickname();
		this.field = memberPutDto.field();
		this.career = memberPutDto.career();
		this.profileImageUrl = memberPutDto.profileImageUrl();
		this.mbti = memberPutDto.MBTI();
		this.githubUrl = memberPutDto.githubUrl();
		this.blogUrl = memberPutDto.blogUrl();
		this.isDeleted = false;
	}
}
