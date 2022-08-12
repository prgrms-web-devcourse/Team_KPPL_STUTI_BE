package prgrms.project.stuti.domain.feed.service;

import java.util.List;

import prgrms.project.stuti.domain.feed.model.Post;
import prgrms.project.stuti.domain.feed.model.PostComment;
import prgrms.project.stuti.domain.feed.service.dto.CommentParentContents;
import prgrms.project.stuti.domain.feed.service.dto.PostCommentChildContents;
import prgrms.project.stuti.domain.feed.service.response.PostCommentContentsResponse;
import prgrms.project.stuti.domain.feed.service.dto.PostCommentGetDto;
import prgrms.project.stuti.domain.feed.service.response.PostCommentResponse;
import prgrms.project.stuti.domain.member.model.Member;
import prgrms.project.stuti.global.page.PageResponse;

public class PostCommentConverter {

	public static PostComment toPostComment(String contents, Post post, PostComment parentPostComment,
		Member commentWriteMember) {
		return new PostComment(contents, parentPostComment, commentWriteMember, post);
	}

	public static PostCommentResponse toPostCommentResponse(PostComment savedPostComment) {
		Long parentId = null;
		if (savedPostComment.getParent() != null) {
			parentId = savedPostComment.getParent().getId();
		}
		return PostCommentResponse.builder()
			.postCommentId(savedPostComment.getId())
			.parentId(parentId)
			.profileImageUrl(savedPostComment.getMember().getProfileImageUrl())
			.memberId(savedPostComment.getMember().getId())
			.nickname(savedPostComment.getMember().getNickName())
			.contents(savedPostComment.getContent())
			.updatedAt(savedPostComment.getUpdatedAt())
			.build();
	}

	public static PostCommentGetDto toPostCommentGetDto(Long postId, Long lastCommentId, int size) {
		return new PostCommentGetDto(postId, lastCommentId, size);
	}

	public static PageResponse<CommentParentContents> toCommentPageResponse(List<PostComment> postComments, boolean hasNext,
		Long totalParentComments) {
		List<CommentParentContents> contents = createContents(postComments);

		return new PageResponse<>(contents, hasNext, totalParentComments);
	}

	private static List<CommentParentContents> createContents(List<PostComment> postComments) {
		return postComments.stream().map(
			comment -> CommentParentContents.builder()
				.postCommentId(comment.getId())
				.parentId(null)
				.profileImageUrl(comment.getMember().getProfileImageUrl())
				.memberId(comment.getMember().getId())
				.nickname(comment.getMember().getNickName())
				.contents(comment.getContent())
				.updatedAt(comment.getUpdatedAt())
				.children(comment.getChildren().stream().map(
					childComment -> PostCommentChildContents.builder()
						.parentId(childComment.getParent().getId())
						.postCommentId(childComment.getId())
						.profileImageUrl(childComment.getMember().getProfileImageUrl())
						.memberId(childComment.getMember().getId())
						.nickname(childComment.getMember().getNickName())
						.contents(childComment.getContent())
						.updatedAt(childComment.getUpdatedAt())
						.build()
				).toList()).build()
		).toList();
	}

	public static PostCommentContentsResponse toPostCommentContentsResponse(PostComment postComment) {
		Long parentId = null;
		if (postComment.getParent() != null) {
			parentId = postComment.getParent().getId();
		}
		return new PostCommentContentsResponse(postComment.getId(), parentId, postComment.getContent());
	}
}
