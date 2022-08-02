package prgrms.project.stuti.config;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import prgrms.project.stuti.domain.member.model.Career;
import prgrms.project.stuti.domain.member.model.Field;
import prgrms.project.stuti.domain.member.model.Mbti;
import prgrms.project.stuti.domain.member.model.Member;
import prgrms.project.stuti.domain.member.model.MemberRole;
import prgrms.project.stuti.domain.member.repository.MemberRepository;
import prgrms.project.stuti.global.config.JpaAuditConfig;
import prgrms.project.stuti.global.config.QuerydslConfig;

@DataJpaTest
@Import( { QuerydslConfig.class, JpaAuditConfig.class } )
public abstract class RepositoryTestConfig {

	@Autowired
	protected MemberRepository memberRepository;

	protected Member member;

	@BeforeEach
	void init() {
		this.member = memberRepository.save(
			Member
				.builder()
				.email("testmember@gmail.com")
				.nickName("nickname321")
				.career(Career.JUNIOR)
				.field(Field.ANDROID)
				.githubUrl("github321.com")
				.blogUrl("blog321.com")
				.mbti(Mbti.ENFJ)
				.profileImageUrl("www.s3.com.321123")
				.memberRole(MemberRole.ROLE_MEMBER)
				.build()
		);
	}
}
