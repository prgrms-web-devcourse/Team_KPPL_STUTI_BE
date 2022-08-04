package prgrms.project.stuti.domain.feed.repository;

import static prgrms.project.stuti.domain.feed.model.QComment.*;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.feed.model.Comment;

@Repository
@RequiredArgsConstructor
public class CommentCustomRepositoryImpl implements CommentCustomRepository{

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public List<Comment> findAllByFeedIdAndParentIdIsNUllWithNoOffset(Long postId, Long lastCommentId, int size) {

		BooleanBuilder dynamicLtId = new BooleanBuilder();

		if(lastCommentId != null) {
			dynamicLtId.and(comment.id.lt(lastCommentId));
		}

		List<Comment> comments = jpaQueryFactory.selectFrom(comment)
			.where(comment.parent.id.isNull(), dynamicLtId)
			.orderBy(comment.id.desc())
			.limit(size)
			.fetch(); // 잘 못가져옴

		comments.stream().map(Comment::getChildren);

		return comments;
	}
}
