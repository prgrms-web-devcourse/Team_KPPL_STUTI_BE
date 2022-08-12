package prgrms.project.stuti.domain.feed.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import prgrms.project.stuti.config.ServiceTestConfig;
import prgrms.project.stuti.domain.feed.model.Post;
import prgrms.project.stuti.domain.feed.model.PostComment;
import prgrms.project.stuti.domain.feed.repository.postcomment.PostCommentRepository;
import prgrms.project.stuti.domain.feed.repository.post.PostRepository;
import prgrms.project.stuti.domain.feed.service.dto.CommentParentContents;
import prgrms.project.stuti.domain.feed.service.response.PostCommentContentsResponse;
import prgrms.project.stuti.domain.feed.service.dto.PostCommentCreateDto;
import prgrms.project.stuti.domain.feed.service.dto.PostCommentGetDto;
import prgrms.project.stuti.domain.feed.service.response.PostCommentResponse;
import prgrms.project.stuti.domain.feed.service.dto.PostCommentUpdateDto;
import prgrms.project.stuti.domain.member.model.Member;
import prgrms.project.stuti.global.error.exception.CommentException;
import prgrms.project.stuti.global.error.exception.PostException;
import prgrms.project.stuti.global.page.PageResponse;

@SpringBootTest
class PostCommentServiceTest extends ServiceTestConfig {

	@Autowired
	private PostCommentService postCommentService;

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private PostCommentRepository postCommentRepository;

	@AfterEach
	void deleteAllComment() {
		postCommentRepository.deleteAll();
		postRepository.deleteAll();
	}

	@Test
	@DisplayName("댓글을 등록한다")
	void testCreateParentComment() {
		Post post = createPost(member);

		PostCommentCreateDto postCommentCreateDto = PostCommentCreateDto.builder()
			.memberId(member.getId())
			.postId(post.getId())
			.parentId(null)
			.contents("테스트 게시글 1번에 대한 댓글 1번")
			.build();

		PostCommentResponse postCommentResponse = postCommentService.createComment(postCommentCreateDto);
		PostComment savedPostComment = postCommentRepository.findById(postCommentResponse.postCommentId()).get();

		assertThat(savedPostComment.getContent()).isEqualTo(postCommentCreateDto.contents());
		assertThat(savedPostComment.getParent()).isNull();
	}

	@Test
	@DisplayName("대댓글을 등록한다")
	void testCreateChildComment() {
		Post post = createPost(member);
		PostCommentCreateDto parentPostCommentCreateDto = PostCommentCreateDto.builder()
			.memberId(member.getId())
			.postId(post.getId())
			.parentId(null)
			.contents("테스트 게시글 1번에 대한 댓글")
			.build();
		PostCommentResponse parentPostCommentResponse = postCommentService.createComment(parentPostCommentCreateDto);

		PostCommentCreateDto childPostCommentCreateDto = PostCommentCreateDto.builder()
			.memberId(member.getId())
			.postId(post.getId())
			.parentId(parentPostCommentResponse.postCommentId())
			.contents("테스트 게시글 1번에 대한 대댓글")
			.build();
		PostCommentResponse childPostCommentResponse = postCommentService.createComment(childPostCommentCreateDto);

		PostComment childPostComment = postCommentRepository.findById(childPostCommentResponse.postCommentId()).get();

		assertThat(childPostComment.getContent()).isEqualTo(childPostCommentCreateDto.contents());
		assertThat(childPostComment.getParent().getId()).isEqualTo(parentPostCommentResponse.postCommentId());
	}

	@Test
	@DisplayName("존재하지 않는 포스트에는 댓글을 달 수 없다")
	void testCreateCommentWithNotExistPost() {
		PostCommentCreateDto postCommentCreateDto = PostCommentCreateDto.builder()
			.memberId(member.getId())
			.postId(0L)
			.parentId(null)
			.contents("존재하지 않는 게시글의 댓글")
			.build();

		assertThrows(PostException.class, () -> postCommentService.createComment(postCommentCreateDto));
	}

	@Test
	@DisplayName("댓글을 정상적으로 수정한다")
	void testChangeComment() {
		Post post = createPost(member);
		PostComment postComment = new PostComment("댓글입니다.", null, member, post);
		PostComment savedPostComment = postCommentRepository.save(postComment);
		PostCommentUpdateDto postCommentUpdateDto = PostCommentUpdateDto.builder()
			.memberId(member.getId())
			.postId(post.getId())
			.postCommentId(savedPostComment.getId())
			.parentId(null)
			.contents("수정된 댓글입니다.")
			.build();

		PostCommentResponse postCommentResponse = postCommentService.changeComment(postCommentUpdateDto);

		assertThat(postCommentResponse.contents()).isEqualTo(postCommentUpdateDto.contents());
	}

	@Test
	@DisplayName("존재하지 않는 댓글은 수정 할 수 없다")
	void testChangeCommentWithNotExist() {
		Post post = createPost(member);
		PostCommentUpdateDto postCommentUpdateDto = PostCommentUpdateDto.builder()
			.memberId(member.getId())
			.postId(post.getId())
			.postCommentId(0L)
			.parentId(null)
			.contents("존재하지 않는 댓글에 대한 수정댓글입니다.")
			.build();

		assertThrows(CommentException.class, () -> postCommentService.changeComment(postCommentUpdateDto));
	}

	@Test
	@DisplayName("댓글을 삭제한다.")
	void testDeleteComment() {
		Post post = createPost(member);
		PostComment postComment = new PostComment("댓글입니다.", null, member, post);
		postCommentRepository.save(postComment);

		postCommentService.deleteComment(post.getId(), postComment.getId(), member.getId());
		Optional<PostComment> foundComment = postCommentRepository.findById(postComment.getId());

		assertThat(foundComment).isEmpty();
	}

	@Test
	@DisplayName("댓글삭제시 대댓글도 전부 삭제한다.")
	void testDeleteCommentWithChildComment() {
		Post post = createPost(member);
		PostComment parentPostComment = new PostComment("댓글입니다.", null, member, post);
		postCommentRepository.save(parentPostComment);
		PostComment childPostComment = new PostComment("대댓글입니다.", parentPostComment, member, post);
		postCommentRepository.save(childPostComment);

		postCommentService.deleteComment(post.getId(), parentPostComment.getId(), member.getId());
		Optional<PostComment> foundChildComment = postCommentRepository.findById(childPostComment.getId());

		assertThat(foundChildComment).isEmpty();
	}

	@Test
	@DisplayName("댓글이 없는 경우 삭제 할 수 없다.")
	void testDuplicateDeleteTest() {
		Post post = createPost(member);
		PostComment postComment = new PostComment("댓글입니다.", null, member, post);
		postCommentRepository.save(postComment);

		postCommentService.deleteComment(post.getId(), postComment.getId(), member.getId());

		assertThrows(CommentException.class, () -> postCommentService.deleteComment(post.getId(), postComment.getId(),
			member.getId()));
	}

	@Test
	@DisplayName("원 게시글이 없는경우 댓글 삭제를 할 수 없다")
		// 보류
	void testDeleteCommentWithUnknownPostId() {
		Post post = createPost(member);
		PostComment postComment = new PostComment("댓글입니다.", null, member, post);
		postCommentRepository.save(postComment);
	}

	@Test
	@DisplayName("댓글과 대댓글을 페이징해서 가져온다.") // 페이징 오류 - 수정필요
	void testGetAllCommentByPostId() {
		//게시글 등록
		Post post = createPost(member);
		//댓글 등록
		for (int i = 1; i <= 10; i++) {
			PostComment parentPostComment = new PostComment("댓글" + i, null, member, post);
			PostComment savedPostComment = postCommentRepository.save(parentPostComment);
			//대댓글 등록
			for (int j = 1; j <= 10; j++) {
				PostComment childPostComment = new PostComment("대댓글" + j, savedPostComment, member, post);
				postCommentRepository.save(childPostComment);
			}
		}

		PageResponse<CommentParentContents> postComments = postCommentService.getPostComments(
			new PostCommentGetDto(post.getId(), null, 10));
		System.out.println(postComments + "가져와 ");
		System.out.println(postComments.contents().get(0).children().size() + "자식 사이즈");
		// System.out.println(pageResponse.contents().get(0).postCommentId() + "가져온부모아이디");
		// assertThat(pageResponse.hasNext()).isTrue();
		// assertThat(pageResponse.totalElements()).isEqualTo(10);
		// assertThat(pageResponse.contents()).isNotEmpty();
		// assertThat(pageResponse.contents()).hasSize(4);
		// assertThat(pageResponse.contents().get(0).contents()).isEqualTo("댓글10");
		// assertThat(pageResponse.contents().get(0).children());
		// System.out.println(pageResponse);
	}

	@Test
	@DisplayName("댓글의 내용을 반환한다.")
	void testGetCommentContents() {
		Post post = createPost(member);
		PostComment postComment = new PostComment("댓글입니다.", null, member, post);
		postCommentRepository.save(postComment);

		PostCommentContentsResponse commentContents = postCommentService.getCommentContents(post.getId(),
			postComment.getId());

		assertThat(commentContents.contents()).isEqualTo(postComment.getContent());
	}

	@Test
	@DisplayName("존재하지 않는 게시글이라면 댓글의 내용을 반환할 수 없다.")
	void testGetCommentContentsWithUnknownPostId() {
		assertThrows(PostException.class, () -> postCommentService.getCommentContents(1L, 2L));
	}

	@Test
	@DisplayName("존재하지 않는 게시글이라면 댓글의 내용을 반환할 수 없다.")
	void testGetCommentContentsWithUnknownCommentId() {
		Post post = createPost(member);

		assertThrows(CommentException.class, () -> postCommentService.getCommentContents(post.getId(), 1L));
	}

	private Post createPost(Member member) {
		Post post = new Post("테스트 게시글 1번", member);
		return postRepository.save(post);
	}
}