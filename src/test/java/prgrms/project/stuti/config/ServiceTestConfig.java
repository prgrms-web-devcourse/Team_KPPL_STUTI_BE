package prgrms.project.stuti.config;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import prgrms.project.stuti.domain.member.model.Career;
import prgrms.project.stuti.domain.member.model.Field;
import prgrms.project.stuti.domain.member.model.Mbti;
import prgrms.project.stuti.domain.member.model.Member;
import prgrms.project.stuti.domain.member.model.MemberRole;
import prgrms.project.stuti.domain.member.repository.MemberRepository;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class ServiceTestConfig {

	@Autowired
	protected MemberRepository memberRepository;

	protected Member member;

	@BeforeAll
	void init() {
		this.member = memberRepository.save(
			Member
				.builder()
				.email("test@gmail.com")
				.nickName("nickname")
				.career(Career.JUNIOR)
				.field(Field.ANDROID)
				.githubUrl("github.com")
				.blogUrl("blog.com")
				.mbti(Mbti.ENFJ)
				.profileImageUrl("www.s3.com")
				.memberRole(MemberRole.ROLE_MEMBER)
				.build()
		);
	}
}
