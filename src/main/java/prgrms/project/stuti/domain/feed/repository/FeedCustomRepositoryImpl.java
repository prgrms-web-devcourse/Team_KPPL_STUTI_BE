package prgrms.project.stuti.domain.feed.repository;

import static prgrms.project.stuti.domain.feed.model.QFeed.*;
import static prgrms.project.stuti.domain.feed.model.QFeedImage.*;
import static prgrms.project.stuti.domain.member.model.QMember.*;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.feed.service.dto.PostDto;

@Repository
@RequiredArgsConstructor
public class FeedCustomRepositoryImpl implements FeedCustomRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public List<PostDto> findAllWithNoOffset(Long lastPostId, int size) {

		BooleanBuilder dynamicLtId = new BooleanBuilder();

		if (lastPostId != null) {
			dynamicLtId.and(feed.id.lt(lastPostId));
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
				.postImageUrl(tuple.get(feedImage).getImageUrl())
				.createdAt(tuple.get(feed).getCreatedAt())
				.build();
			postsDtos.add(postsDto);
		}

		return postsDtos;
	}
}
