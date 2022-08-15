package prgrms.project.stuti.domain.feed.model;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import prgrms.project.stuti.domain.member.model.Member;

class PostTest {

	@Test
	@DisplayName("softDelete로 deleted 필드를 true로 변경한다")
	void testDelete() {
		Member member = Member.builder()
			.email("test@gmail.com")
			.nickName("닉네임")
			.build();
		Post post = new Post("새로운 게시글", member);
		post.softDelete();

		assertThat(post.isDeleted()).isTrue();
	}

	@Test
	@DisplayName("게시글의 내용을 바꾼다")
	void testChangeContents() {
		Member member = Member.builder()
			.email("test@gmail.com")
			.nickName("닉네임")
			.build();
		Post post = new Post("새로운 게시글", member);
		post.changeContents("변경된 게시글 내용");

		assertThat(post.getContent()).isEqualTo("변경된 게시글 내용");
	}
}