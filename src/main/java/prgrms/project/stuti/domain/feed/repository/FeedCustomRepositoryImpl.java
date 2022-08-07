package prgrms.project.stuti.domain.feed.repository;

import static prgrms.project.stuti.domain.feed.model.QComment.*;
import static prgrms.project.stuti.domain.feed.model.QFeed.*;
import static prgrms.project.stuti.domain.feed.model.QFeedImage.*;
import static prgrms.project.stuti.domain.feed.model.QFeedLike.*;
import static prgrms.project.stuti.domain.member.model.QMember.*;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.feed.service.dto.PostDto;

@Repository
@RequiredArgsConstructor
public class FeedCustomRepositoryImpl implements FeedCustomRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public List<PostDto> findAllWithNoOffset(Long lastPostId, int size, Long memberId) {

		BooleanBuilder dynamicLtId = new BooleanBuilder();

		if (lastPostId != null) {
			dynamicLtId.and(feed.id.lt(lastPostId));
		}

		if(memberId != null) {
			dynamicLtId.and(feed.member.id.eq(memberId));
		}

		List<Tuple> fetch = jpaQueryFactory
			.select(feed, feedImage)
			.from(feed)
			.leftJoin(feedImage).on(feed.id.eq(feedImage.feed.id))
			.leftJoin(member).on(feed.member.id.eq(member.id))
			.where(dynamicLtId)
			.orderBy(feedImage.id.desc())
			.limit(size)
			.fetch();

		List<PostDto> postsDtos = new ArrayList<>();
		for (Tuple tuple : fetch) {
			PostDto postsDto = PostDto.builder()
				.postId(tuple.get(feed).getId())
				.memberId(tuple.get(feed).getMember().getId())
				.nickname(tuple.get(feed).getMember().getNickName())
				.mbti(tuple.get(feed).getMember().getMbti())
				.profileImageUrl(tuple.get(feed).getMember().getProfileImageUrl())
				.contents(tuple.get(feed).getContent())
				.postImageUrl(tuple.get(feedImage.imageUrl))
				.createdAt(tuple.get(feed).getCreatedAt())
				.totalLikes(getTotalLikes(tuple.get(feed).getId()))
				.totalComments(getTotalComments(tuple.get(feed).getId()))
				.isliked(isLiked(memberId, tuple.get(feed).getId()))
				.build();
			postsDtos.add(postsDto);
		}

		return postsDtos;
	}

	private Long getTotalLikes(Long postId) {
		return jpaQueryFactory
			.select(feedLike.count())
			.from(feedLike)
			.where(feedLike.feed.id.eq(postId))
			.fetchOne();
	}

	private Long getTotalComments(Long postId) {
		return jpaQueryFactory
			.select(comment.count())
			.from(comment)
			.where(comment.feed.id.eq(postId), comment.parent.isNull())
			.fetchOne();
	}

	private boolean isLiked(Long memberId, Long postId) {
		List<Long> likedResult = jpaQueryFactory
			.select(feedLike.id)
			.from(feedLike)
			.where(eqMemberId(memberId), feedLike.feed.id.eq(postId))
			.fetch();

		return !likedResult.isEmpty();
	}

	private BooleanExpression eqMemberId(Long memberId) {
		if(ObjectUtils.isEmpty(memberId)) {
			return null;
		}

		return feedLike.member.id.eq(memberId);
	}
}
