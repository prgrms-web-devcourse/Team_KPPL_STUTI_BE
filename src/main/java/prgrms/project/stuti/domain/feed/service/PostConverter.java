package prgrms.project.stuti.domain.feed.service;

import java.util.List;

import prgrms.project.stuti.domain.feed.model.Post;
import prgrms.project.stuti.domain.feed.model.PostImage;
import prgrms.project.stuti.domain.feed.service.dto.PostCreateDto;
import prgrms.project.stuti.domain.feed.service.response.PostListResponse;
import prgrms.project.stuti.domain.feed.service.response.PostResponse;
import prgrms.project.stuti.domain.member.model.Member;

public class PostConverter {

	public static Post toPost(PostCreateDto postDto, Member member) {
		return new Post(postDto.contents(), member);
	}


	public static PostListResponse toPostListResponse(List<PostResponse> postResponses, boolean hasNext) {
		return new PostListResponse(postResponses, hasNext);
	}

	public static PostResponse toPostResponse(Post post, Member member, PostImage postImage, Long totalPostComments,
		List<Long> likedMembers) {
		return PostResponse.builder()
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
