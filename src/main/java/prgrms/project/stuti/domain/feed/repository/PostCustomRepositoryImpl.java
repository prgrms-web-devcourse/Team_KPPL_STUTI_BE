package prgrms.project.stuti.domain.feed.repository;

import static prgrms.project.stuti.domain.feed.model.QPost.*;
import static prgrms.project.stuti.domain.feed.model.QPostComment.*;
import static prgrms.project.stuti.domain.feed.model.QPostImage.*;
import static prgrms.project.stuti.domain.feed.model.QPostLike.*;
import static prgrms.project.stuti.domain.member.model.QMember.*;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.feed.service.dto.PostResponse;

@Repository
@RequiredArgsConstructor
public class PostCustomRepositoryImpl implements PostCustomRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public List<PostResponse> findAllWithNoOffset(Long lastPostId, int size, Long memberId) {

		BooleanBuilder dynamicLtId = new BooleanBuilder();

		if (lastPostId != null) {
			dynamicLtId.and(post.id.lt(lastPostId));
		}

		if (memberId != null) {
			dynamicLtId.and(post.member.id.eq(memberId));
		}

		List<Tuple> fetch = jpaQueryFactory
			.select(post, postImage)
			.from(post)
			.leftJoin(postImage).on(post.id.eq(postImage.post.id))
			.leftJoin(member).on(post.member.id.eq(member.id))
			.where(dynamicLtId)
			.orderBy(postImage.id.desc())
			.limit(size)
			.fetch();

		List<PostResponse> postsDtos = new ArrayList<>();
		for (Tuple tuple : fetch) {
			PostResponse postsDto = PostResponse.builder()
				.postId(tuple.get(post).getId())
				.memberId(tuple.get(post).getMember().getId())
				.nickname(tuple.get(post).getMember().getNickName())
				.mbti(tuple.get(post).getMember().getMbti())
				.profileImageUrl(tuple.get(post).getMember().getProfileImageUrl())
				.contents(tuple.get(post).getContent())
				.postImageUrl(tuple.get(postImage.imageUrl))
				.updatedAt(tuple.get(post).getUpdatedAt())
				.likedMembers(findAllLikedMembers(tuple.get(post).getId()))
				.totalPostComments(getTotalPostComments(tuple.get(post).getId()))
				.build();
			postsDtos.add(postsDto);
		}

		return postsDtos;
	}

	@Override
	public List<Long> findAllLikedMembers(Long postId) {
		return jpaQueryFactory
			.select(postLike.member.id)
			.from(postLike)
			.where(post.id.eq(postId))
			.fetch();
	}

	private Long getTotalPostComments(Long postId) {
		return jpaQueryFactory
			.select(postComment.count())
			.from(postComment)
			.where(postComment.post.id.eq(postId), postComment.parent.isNull())
			.fetchOne();
	}
}
