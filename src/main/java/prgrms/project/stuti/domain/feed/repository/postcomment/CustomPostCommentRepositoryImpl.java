package prgrms.project.stuti.domain.feed.repository.postcomment;

import static prgrms.project.stuti.domain.feed.model.QPostComment.*;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.feed.model.PostComment;
import prgrms.project.stuti.domain.feed.service.PostCommentConverter;
import prgrms.project.stuti.domain.feed.service.dto.PostCommentParentDto;
import prgrms.project.stuti.global.page.PageResponse;

@Repository
@RequiredArgsConstructor
public class CustomPostCommentRepositoryImpl implements CustomPostCommentRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public PageResponse<PostCommentParentDto> findAllByPostIdAndParentIdIsNUllWithNoOffset(Long postId,
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

		long totalParentComments = totalParentComments(postId);
		boolean hasNext = false;
		if (postComments.size() == size) {
			Long lastCalledComment = postComments.get(postComments.size() - 1).getId();
			hasNext = hasNext(postId, lastCalledComment);
		}

		return PostCommentConverter.toCommentPageResponse(postComments, hasNext, totalParentComments);
	}

	@Override
	public long totalParentComments(Long postId) {
		Long totalParentComments = jpaQueryFactory.select(postComment.count())
			.from(postComment)
			.where(postComment.post.id.eq(postId), postComment.parent.isNull())
			.fetchOne();

		return totalParentComments == null ? 0 : totalParentComments;
	}

	private boolean hasNext(Long postId, Long lastCommentId) {
		List<PostComment> postComments = jpaQueryFactory.selectFrom(postComment)
			.where(postComment.post.id.eq(postId), postComment.parent.isNull(), postComment.id.lt(lastCommentId))
			.orderBy(postComment.id.desc())
			.fetch();

		return !postComments.isEmpty();
	}
}
