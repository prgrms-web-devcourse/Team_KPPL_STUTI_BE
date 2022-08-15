package prgrms.project.stuti.domain.feed.service;

import java.util.List;

import prgrms.project.stuti.domain.feed.model.Post;
import prgrms.project.stuti.domain.feed.model.PostImage;
import prgrms.project.stuti.domain.feed.service.dto.PostCreateDto;
import prgrms.project.stuti.domain.feed.service.response.PostsResponse;
import prgrms.project.stuti.domain.feed.service.response.PostDetailResponse;
import prgrms.project.stuti.domain.member.model.Member;

public class PostConverter {

	public static Post toPost(PostCreateDto postDto, Member member) {
		return new Post(postDto.contents(), member);
	}


	public static PostsResponse toPostsResponse(List<PostDetailResponse> postDetailRespons, boolean hasNext) {
		return new PostsResponse(postDetailRespons, hasNext);
	}

	public static PostDetailResponse toPostDetailResponse(Post post, Member member, PostImage postImage, Long totalPostComments,
		List<Long> likedMembers) {
		return PostDetailResponse.builder()
			.postId(post.getId())
			.memberId(member.getId())
			.nickname(member.getNickName())
			.mbti(member.getMbti())
			.profileImageUrl(insertEmptyStringIfImageIsNull(member.getProfileImageUrl()))
			.contents(post.getContent())
			.postImageUrl(postImage.getImageUrl())
			.updatedAt(post.getUpdatedAt())
			.totalPostComments(totalPostComments)
			.likedMembers(likedMembers)
			.build();
	}

	private static String insertEmptyStringIfImageIsNull(String imageUrl) {
		return imageUrl == null ? "" : imageUrl;
	}
}
