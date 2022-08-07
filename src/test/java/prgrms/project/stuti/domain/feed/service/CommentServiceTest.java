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
import prgrms.project.stuti.domain.feed.model.Comment;
import prgrms.project.stuti.domain.feed.model.Feed;
import prgrms.project.stuti.domain.feed.repository.CommentRepository;
import prgrms.project.stuti.domain.feed.repository.FeedRepository;
import prgrms.project.stuti.domain.feed.service.dto.CommentContentsResponse;
import prgrms.project.stuti.domain.feed.service.dto.CommentCreateDto;
import prgrms.project.stuti.domain.feed.service.dto.CommentGetDto;
import prgrms.project.stuti.domain.feed.service.dto.CommentParentContents;
import prgrms.project.stuti.domain.feed.service.dto.CommentResponse;
import prgrms.project.stuti.domain.feed.service.dto.CommentUpdateDto;
import prgrms.project.stuti.domain.member.model.Member;
import prgrms.project.stuti.global.error.exception.CommentException;
import prgrms.project.stuti.global.error.exception.FeedException;
import prgrms.project.stuti.global.page.offset.PageResponse;

@SpringBootTest
class CommentServiceTest extends ServiceTestConfig {

	@Autowired
	private CommentService commentService;

	@Autowired
	private FeedRepository feedRepository;

	@Autowired
	private CommentRepository commentRepository;

	@AfterEach
	void deleteAllComment() {
		commentRepository.deleteAll();
		feedRepository.deleteAll();
	}

	@Test
	@DisplayName("댓글을 등록한다")
	void testCreateParentComment() {
		Feed post = createPost(member);

		CommentCreateDto commentCreateDto = CommentCreateDto.builder()
			.memberId(member.getId())
			.postId(post.getId())
			.parentId(null)
			.contents("테스트 게시글 1번에 대한 댓글 1번")
			.build();

		CommentResponse commentResponse = commentService.createComment(commentCreateDto);
		Comment savedComment = commentRepository.findById(commentResponse.postCommentId()).get();

		assertThat(savedComment.getContent()).isEqualTo(commentCreateDto.contents());
		assertThat(savedComment.getParent()).isNull();
	}

	@Test
	@DisplayName("대댓글을 등록한다")
	void testCreateChildComment() {
		Feed post = createPost(member);
		CommentCreateDto parentCommentCreateDto = CommentCreateDto.builder()
			.memberId(member.getId())
			.postId(post.getId())
			.parentId(null)
			.contents("테스트 게시글 1번에 대한 댓글")
			.build();
		CommentResponse parentCommentResponse = commentService.createComment(parentCommentCreateDto);

		CommentCreateDto childCommentCreateDto = CommentCreateDto.builder()
			.memberId(member.getId())
			.postId(post.getId())
			.parentId(parentCommentResponse.postCommentId())
			.contents("테스트 게시글 1번에 대한 대댓글")
			.build();
		CommentResponse childCommentResponse = commentService.createComment(childCommentCreateDto);

		Comment childComment = commentRepository.findById(childCommentResponse.postCommentId()).get();

		assertThat(childComment.getContent()).isEqualTo(childCommentCreateDto.contents());
		assertThat(childComment.getParent().getId()).isEqualTo(parentCommentResponse.postCommentId());
	}

	@Test
	@DisplayName("존재하지 않는 포스트에는 댓글을 달 수 없다")
	void testCreateCommentWithNotExistPost() {
		CommentCreateDto commentCreateDto = CommentCreateDto.builder()
			.memberId(member.getId())
			.postId(0L)
			.parentId(null)
			.contents("존재하지 않는 게시글의 댓글")
			.build();

		assertThrows(FeedException.class, () -> commentService.createComment(commentCreateDto));
	}

	@Test
	@DisplayName("댓글을 정상적으로 수정한다")
	void testChangeComment() {
		Feed post = createPost(member);
		Comment comment = new Comment("댓글입니다.", null, member, post);
		Comment savedComment = commentRepository.save(comment);
		CommentUpdateDto commentUpdateDto = CommentUpdateDto.builder()
			.memberId(member.getId())
			.postId(post.getId())
			.postCommentId(savedComment.getId())
			.parentId(null)
			.contents("수정된 댓글입니다.")
			.build();

		CommentResponse commentResponse = commentService.changeComment(commentUpdateDto);

		assertThat(commentResponse.contents()).isEqualTo(commentUpdateDto.contents());
	}

	@Test
	@DisplayName("존재하지 않는 댓글은 수정 할 수 없다")
	void testChangeCommentWithNotExist() {
		Feed post = createPost(member);
		CommentUpdateDto commentUpdateDto = CommentUpdateDto.builder()
			.memberId(member.getId())
			.postId(post.getId())
			.postCommentId(0L)
			.parentId(null)
			.contents("존재하지 않는 댓글에 대한 수정댓글입니다.")
			.build();

		assertThrows(CommentException.class, () -> commentService.changeComment(commentUpdateDto));
	}

	@Test
	@DisplayName("댓글을 삭제한다.")
	void testDeleteComment() {
		Feed post = createPost(member);
		Comment comment = new Comment("댓글입니다.", null, member, post);
		commentRepository.save(comment);

		commentService.deleteComment(post.getId(), comment.getId(), member.getId());
		Optional<Comment> foundComment = commentRepository.findById(comment.getId());

		assertThat(foundComment).isEmpty();
	}

	@Test
	@DisplayName("댓글삭제시 대댓글도 전부 삭제한다.")
	void testDeleteCommentWithChildComment() {
		Feed post = createPost(member);
		Comment parentComment = new Comment("댓글입니다.", null, member, post);
		commentRepository.save(parentComment);
		Comment childComment = new Comment("대댓글입니다.", parentComment, member, post);
		commentRepository.save(childComment);

		commentService.deleteComment(post.getId(), parentComment.getId(), member.getId());
		Optional<Comment> foundChildComment = commentRepository.findById(childComment.getId());

		assertThat(foundChildComment).isEmpty();
	}

	@Test
	@DisplayName("댓글이 없는 경우 삭제 할 수 없다.")
	void testDuplicateDeleteTest() {
		Feed post = createPost(member);
		Comment comment = new Comment("댓글입니다.", null, member, post);
		commentRepository.save(comment);

		commentService.deleteComment(post.getId(), comment.getId(), member.getId());

		assertThrows(CommentException.class, () -> commentService.deleteComment(post.getId(), comment.getId(),
			member.getId()));
	}

	@Test
	@DisplayName("원 게시글이 없는경우 댓글 삭제를 할 수 없다")
		// 보류
	void testDeleteCommentWithUnknownPostId() {
		Feed post = createPost(member);
		Comment comment = new Comment("댓글입니다.", null, member, post);
		commentRepository.save(comment);
	}

	@Test
	@DisplayName("댓글과 대댓글을 페이징해서 가져온다.")
	void testGetAllCommentByPostId() {
		//게시글 등록
		Feed post = createPost(member);
		//댓글 등록
		Comment firstParent = null;
		for (int i = 1; i <= 10; i++) {
			Comment parentComment = new Comment("댓글" + i, null, member, post);
			Comment savedComment = commentRepository.save(parentComment);
			if (i == 10) {
				firstParent = savedComment;
			}
		}
		//대댓글 등록
		for (int i = 1; i <= 10; i++) {
			Comment childComment = new Comment("대댓글" + i, firstParent, member, post);
			commentRepository.save(childComment);
		}

		CommentGetDto commentGetDto = new CommentGetDto(post.getId(), null, 4);
		PageResponse<CommentParentContents> pageResponse = commentService.getPostComments(commentGetDto);

		assertThat(pageResponse.hasNext()).isTrue();
		assertThat(pageResponse.totalElements()).isEqualTo(10);
		assertThat(pageResponse.contents()).isNotEmpty();
		assertThat(pageResponse.contents()).hasSize(4);
		assertThat(pageResponse.contents().get(0).contents()).isEqualTo("댓글10");
		assertThat(pageResponse.contents().get(0).children()).hasSize(10);
		System.out.println(pageResponse);
	}

	@Test
	@DisplayName("댓글의 내용을 반환한다.")
	void testGetCommentContents() {
		Feed post = createPost(member);
		Comment comment = new Comment("댓글입니다.", null, member, post);
		commentRepository.save(comment);

		CommentContentsResponse commentContents = commentService.getCommentContents(post.getId(), comment.getId());

		assertThat(commentContents.contents()).isEqualTo(comment.getContent());
	}

	@Test
	@DisplayName("존재하지 않는 게시글이라면 댓글의 내용을 반환할 수 없다.")
	void testGetCommentContentsWithUnknownPostId() {
		assertThrows(FeedException.class, () -> commentService.getCommentContents(1L, 2L));
	}

	@Test
	@DisplayName("존재하지 않는 게시글이라면 댓글의 내용을 반환할 수 없다.")
	void testGetCommentContentsWithUnknownCommentId() {
		Feed post = createPost(member);

		assertThrows(CommentException.class, () -> commentService.getCommentContents(post.getId(), 1L));
	}

	private Feed createPost(Member member) {
		Feed feed = new Feed("테스트 게시글 1번", member);
		return feedRepository.save(feed);
	}
}