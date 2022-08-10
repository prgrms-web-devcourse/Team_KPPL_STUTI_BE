package prgrms.project.stuti.domain.feed.repository;

import static prgrms.project.stuti.domain.feed.model.QPostComment.*;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.feed.model.PostComment;
import prgrms.project.stuti.domain.feed.service.PostCommentConverter;
import prgrms.project.stuti.domain.feed.service.dto.CommentParentContents;
import prgrms.project.stuti.global.page.offset.PageResponse;

@Repository
@RequiredArgsConstructor
public class PostCommentCustomRepositoryImpl implements PostCommentCustomRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public PageResponse<CommentParentContents> findAllByPostIdAndParentIdIsNUllWithNoOffset(Long postId,
		Long lastCommentId, int size) {

		BooleanBuilder dynamicLtId = new BooleanBuilder();

		if (lastCommentId != null) {
			dynamicLtId.and(postComment.id.lt(lastCommentId));
		}

		List<PostComment> postComments = jpaQueryFactory.selectFrom(postComment)
			.where(postComment.post.id.eq(postId), postComment.parent.isNull(), dynamicLtId)
			.orderBy(postComment.id.desc())
			.limit(size)
			.fetch();

		Long totalParentComments = totalParentComments(postId);

		boolean hasNext = false;
		if (postComments.size() == size) {
			Long lastCalledComment = postComments.get(postComments.size() - 1).getId();
			hasNext = hasNext(postId, lastCalledComment);
		}

		for (PostComment postComment : postComments) {
			List<PostComment> childPostComments = findByParentId(postComment.getId());
			postComment.findChildren(childPostComments);
		}

		return PostCommentConverter.toCommentResponse(postComments, hasNext, totalParentComments);
	}

	@Override
	public Long totalParentComments(Long postId) {
		return jpaQueryFactory.select(postComment.count())
			.from(postComment)
			.where(postComment.post.id.eq(postId), postComment.parent.isNull())
			.fetchOne();
	}

	private boolean hasNext(Long postId, Long lastCommentId) {
		List<PostComment> postComments = jpaQueryFactory.selectFrom(postComment)
			.where(postComment.post.id.eq(postId), postComment.parent.isNull(), postComment.id.lt(lastCommentId))
			.orderBy(postComment.id.desc())
			.fetch();

		return !postComments.isEmpty();
	}

	private List<PostComment> findByParentId(Long parentId) {
		return jpaQueryFactory.selectFrom(postComment)
			.where(postComment.parent.id.eq(parentId))
			.fetch();
	}
}
