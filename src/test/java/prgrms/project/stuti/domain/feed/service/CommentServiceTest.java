package prgrms.project.stuti.domain.feed.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

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
import prgrms.project.stuti.domain.feed.service.dto.CommentCreateDto;
import prgrms.project.stuti.domain.feed.service.dto.CommentGetDto;
import prgrms.project.stuti.domain.feed.service.dto.CommentIdResponse;
import prgrms.project.stuti.domain.feed.service.dto.CommentParentContents;
import prgrms.project.stuti.domain.member.model.Member;
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

		CommentIdResponse commentIdResponse = commentService.createComment(commentCreateDto);
		Comment savedComment = commentRepository.findById(commentIdResponse.commentId()).get();

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
		CommentIdResponse parentCommentIdResponse = commentService.createComment(parentCommentCreateDto);

		CommentCreateDto childCommentCreateDto = CommentCreateDto.builder()
			.memberId(member.getId())
			.postId(post.getId())
			.parentId(parentCommentIdResponse.commentId())
			.contents("테스트 게시글 1번에 대한 대댓글")
			.build();
		CommentIdResponse childCommentIdResponse = commentService.createComment(childCommentCreateDto);

		Comment childComment = commentRepository.findById(childCommentIdResponse.commentId()).get();

		assertThat(childComment.getContent()).isEqualTo(childCommentCreateDto.contents());
		assertThat(childComment.getParent().getId()).isEqualTo(parentCommentIdResponse.commentId());

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
	@DisplayName("댓글과 대댓글을 페이징해서 가져온다.")
	void testGetAllCommentByPostId() {
		//게시글 등록
		Feed post = createPost(member);
		//댓글 등록
		Comment firstParent = null;
		for(int i = 1; i <= 10; i++) {
			Comment parentComment = new Comment("댓글" + i, null, member, post);
			Comment savedComment = commentRepository.save(parentComment);
			if(i == 10) {
				firstParent = savedComment;
			}
		}
		//대댓글 등록
		for(int i = 1; i <= 10; i++) {
			Comment childComment = new Comment("대댓글" + i, firstParent, member2, post);
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

	private Feed createPost(Member member) {
		Feed feed = new Feed("테스트 게시글 1번", member);
		return feedRepository.save(feed);
	}
}