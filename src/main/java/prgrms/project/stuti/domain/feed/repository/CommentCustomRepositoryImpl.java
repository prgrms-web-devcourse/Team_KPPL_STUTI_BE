package prgrms.project.stuti.domain.feed.repository;

import static prgrms.project.stuti.domain.feed.model.QComment.*;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.feed.model.Comment;
import prgrms.project.stuti.domain.feed.service.CommentConverter;
import prgrms.project.stuti.domain.feed.service.dto.CommentParentContents;
import prgrms.project.stuti.global.page.offset.PageResponse;

@Repository
@RequiredArgsConstructor
public class CommentCustomRepositoryImpl implements CommentCustomRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public PageResponse<CommentParentContents> findAllByFeedIdAndParentIdIsNUllWithNoOffset(Long postId,
		Long lastCommentId, int size) {

		BooleanBuilder dynamicLtId = new BooleanBuilder();

		if (lastCommentId != null) {
			dynamicLtId.and(comment.id.lt(lastCommentId));
		}

		List<Comment> comments = jpaQueryFactory.selectFrom(comment)
			.where(comment.feed.id.eq(postId), comment.parent.isNull(), dynamicLtId)
			.orderBy(comment.id.desc())
			.limit(size)
			.fetch();

		Long totalParentComments = jpaQueryFactory.select(comment.count())
			.from(comment)
			.where(comment.feed.id.eq(postId), comment.parent.isNull())
			.fetchOne();

		Long lastCalledComment = comments.get(comments.size() - 1).getId();
		boolean hasNext = hasNext(postId, lastCalledComment);
		for(Comment comment : comments) {
			List<Comment> childComments = findByParentId(comment.getId());
			comment.findChildren(childComments);
		}

		return CommentConverter.toCommentResponse(comments, hasNext, totalParentComments);
	}

	private boolean hasNext(Long postId, Long lastCommentId) {
		List<Comment> comments = jpaQueryFactory.selectFrom(comment)
			.where(comment.feed.id.eq(postId), comment.parent.isNull(), comment.id.lt(lastCommentId))
			.orderBy(comment.id.desc())
			.fetch();

		return !comments.isEmpty();
	}

	private List<Comment> findByParentId(Long parentId) {
		return jpaQueryFactory.selectFrom(comment)
			.where(comment.parent.id.eq(parentId))
			.fetch();
	}
}
