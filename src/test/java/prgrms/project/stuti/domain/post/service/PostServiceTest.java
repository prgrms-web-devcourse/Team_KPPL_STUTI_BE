package prgrms.project.stuti.domain.post.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import prgrms.project.stuti.config.ServiceTestConfig;
import prgrms.project.stuti.domain.post.model.Post;
import prgrms.project.stuti.domain.post.model.PostComment;
import prgrms.project.stuti.domain.post.model.PostImage;
import prgrms.project.stuti.domain.post.model.PostLike;
import prgrms.project.stuti.domain.post.repository.PostImageRepository;
import prgrms.project.stuti.domain.post.repository.PostLikeRepository;
import prgrms.project.stuti.domain.post.repository.post.PostRepository;
import prgrms.project.stuti.domain.post.repository.postcomment.PostCommentRepository;
import prgrms.project.stuti.domain.post.service.dto.PostChangeDto;
import prgrms.project.stuti.domain.post.service.dto.PostCreateDto;
import prgrms.project.stuti.domain.post.service.response.PostsResponse;
import prgrms.project.stuti.domain.post.service.response.PostDetailResponse;
import prgrms.project.stuti.domain.member.model.Career;
import prgrms.project.stuti.domain.member.model.Field;
import prgrms.project.stuti.domain.member.model.Mbti;
import prgrms.project.stuti.domain.member.model.Member;
import prgrms.project.stuti.domain.member.model.MemberRole;
import prgrms.project.stuti.global.error.exception.MemberException;
import prgrms.project.stuti.global.error.exception.PostException;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PostServiceTest extends ServiceTestConfig {

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private PostImageRepository postImageRepository;

	@Autowired
	private PostCommentRepository postCommentRepository;

	@Autowired
	private PostLikeRepository postLikeRepository;

	@Autowired
	private PostService postService;

	@AfterEach
	void deleteAllPost() {
		postImageRepository.deleteAll();
		postCommentRepository.deleteAll();
		postLikeRepository.deleteAll();
		postRepository.deleteAll();
	}

	@Test
	@DisplayName("포스트를 정상적으로 등록한다")
	void testRegisterPost() throws IOException {
		String testFilePath = Paths.get("src", "test", "resources").toString();
		File testImageFile = new File(testFilePath + File.separator + "test.png");

		MultipartFile testMultipartFile = getMockMultipartFile(testImageFile);

		PostCreateDto postDto = PostCreateDto.builder()
			.memberId(member.getId())
			.contents("새로운 게시글의 내용입니다.")
			.imageFile(testMultipartFile)
			.build();

		PostDetailResponse postDetailResponse = postService.registerPost(postDto);
		Optional<Post> foundFeed = postRepository.findById(postDetailResponse.postId());

		assertThat(foundFeed).isNotEmpty();
		assertThat(foundFeed.get().getContent()).isEqualTo(postDto.contents());
	}

	@Test
	@DisplayName("등록되지 않은 멤버가 포스트를 등록할시에 예외가 발생한다.")
	void testRegisterPostByUnknownMember() {
		PostCreateDto postDto = PostCreateDto.builder()
			.memberId(2L)
			.contents("UnknownMember가 작성한 게시글 입니다.")
			.build();

		assertThrows(MemberException.class, () -> postService.registerPost(postDto));
	}

	@Test
	@DisplayName("전체 포스트리스트를 커서방식으로 페이징하여 가져온다")
	void testGetAllPosts() {
		Long lastPostId = null;
		Post likedAndCommentPost = null;
		for (int i = 0; i < 10; i++) {
			Post post = new Post("게시글" + i, member);
			PostImage postImage = new PostImage(i + "test.jpg", post);
			Post savedPost = postRepository.save(post);
			postImageRepository.save(postImage);
			if (i == 9)
				lastPostId = savedPost.getId();
			if (i == 8) {
				likedAndCommentPost = post;
			}
		}
		PostLike postLike = new PostLike(member, likedAndCommentPost);
		PostComment postComment = new PostComment("게시글 8에 대한 댓글입니다.", null, member, likedAndCommentPost);
		postCommentRepository.save(postComment);
		postLikeRepository.save(postLike);

		PostsResponse allPosts = postService.getAllPosts(lastPostId, 2);

		assertThat(allPosts.posts()).hasSize(2);
		assertThat(allPosts.posts().get(0).contents()).isEqualTo("게시글8");
		assertThat(allPosts.hasNext()).isTrue();
		assertThat(allPosts.posts().get(0).totalPostComments()).isEqualTo(1L);
		assertThat(allPosts.posts().get(0).likedMembers()).hasSize(1);
		assertThat(allPosts.posts().get(0).likedMembers().get(0)).isEqualTo(member.getId());
	}

	@Test
	@DisplayName("전체 포스트리스트 첫 조회시(lastPostId가 null일 때) 페이징 조회한다- 이미지 조회 추가")
	void testGetAllPostsWhenLastPostIdIsNull() {
		for (int i = 0; i < 10; i++) {
			Post post = new Post("게시글" + i, member);
			PostImage postImage = new PostImage(i + "test.jpg", post);
			postRepository.save(post);
			postImageRepository.save(postImage);
		}

		PostsResponse allPosts = postService.getAllPosts(null, 2);

		assertThat(allPosts.posts()).hasSize(2);
		assertThat(allPosts.posts().get(0).contents()).isEqualTo("게시글9");
		assertThat(allPosts.hasNext()).isTrue();
		assertThat(allPosts.posts().get(0).postImageUrl()).isEqualTo("9test.jpg");
	}

	@Test
	@DisplayName("게시글 내용과 업로드 이미지가 둘다 정상 변경된다.")
	void testChangePost() throws IOException {
		PostDetailResponse postDetailResponse = savePost();
		List<PostImage> originImages = postImageRepository.findByPostId(postDetailResponse.postId());

		File changeImageFile = new File(Paths.get("src", "test", "resources")
			+ File.separator + "change.jpg");
		MultipartFile testChangeMultipartFile = getMockMultipartFile(changeImageFile);
		PostChangeDto postChangeDto = new PostChangeDto(postDetailResponse.postId(), "게시글 내용이 변경되었습니다.",
			testChangeMultipartFile);
		PostDetailResponse changedPostDetailResponse = postService.changePost(postChangeDto);

		List<PostImage> changedImages = postImageRepository.findByPostId(changedPostDetailResponse.postId());
		Post changedPost = postRepository.findById(changedPostDetailResponse.postId()).get();

		assertThat(changedPost.getContent()).isEqualTo("게시글 내용이 변경되었습니다.");
		assertThat(changedImages).hasSize(1);
		assertThat(changedImages.get(0).getImageUrl()).isNotEqualTo(originImages.get(0).getImageUrl());
	}

	@Test
	@DisplayName("업로드 이미지를 보내주지않으면 삭제하지 않는다. - 원래 이미지를 가지고있는다")
	void testChangePostWithOutImages() throws IOException {
		PostDetailResponse postDetailResponse = savePost();
		List<PostImage> originImages = postImageRepository.findByPostId(postDetailResponse.postId());
		PostChangeDto postChangeDto = new PostChangeDto(postDetailResponse.postId(), "게시글 내용이 변경되었습니다.", null);
		PostDetailResponse changedPostDetailResponse = postService.changePost(postChangeDto);

		List<PostImage> changedImages = postImageRepository.findByPostId(changedPostDetailResponse.postId());
		Post changedPost = postRepository.findById(changedPostDetailResponse.postId()).get();

		assertThat(changedPost.getContent()).isEqualTo("게시글 내용이 변경되었습니다.");
		assertThat(changedImages.get(0).getImageUrl()).isEqualTo(originImages.get(0).getImageUrl());
	}

	@Test
	@DisplayName("게시글과 게시글 이미지를 삭제한다")
	void testDeletePost() throws IOException {
		PostDetailResponse postDetailResponse = savePost();

		postService.deletePost(postDetailResponse.postId());
		Optional<Post> foundPost = postRepository.findByIdAndDeletedFalse(postDetailResponse.postId());

		assertThat(foundPost).isEmpty();
	}

	@Test
	@DisplayName("없는게시글을 삭제하려고하면 에러를 리턴한다")
	void testDeletePostWithUnknownPostId() {
		assertThrows(PostException.class, () -> postService.deletePost(1L));
	}

	private PostDetailResponse savePost() throws IOException {
		String testFilePath = Paths.get("src", "test", "resources").toString();
		File originalImageFile = new File(testFilePath + File.separator + "test.png");
		MultipartFile testOriginalMultipartFile = getMockMultipartFile(originalImageFile);

		PostCreateDto postDto = PostCreateDto.builder()
			.memberId(member.getId())
			.contents("새로운 게시글의 내용입니다.")
			.imageFile(testOriginalMultipartFile)
			.build();

		return postService.registerPost(postDto);
	}

	@Test
	@DisplayName("게시글 삭제시 게시글에 붙은 댓글도 전부 삭제처리한다.")
	void testDeletePostWithComments() {
		Post post = new Post("울랄라 테스트 게시글", member);
		postRepository.save(post);
		PostComment postComment = new PostComment("댓글", null, member, post);
		postCommentRepository.save(postComment);

		postService.deletePost(post.getId());
		Optional<PostComment> foundComment = postCommentRepository.findById(postComment.getId());

		assertThat(foundComment).isEmpty();
	}

	@Test
	@DisplayName("게시글 삭제시 게시글에 붙은 댓글도 전부 삭제처리한다.")
	void testDeletePostWithLikes() {
		Post post = new Post("울랄라 테스트 게시글", member);
		postRepository.save(post);
		PostLike postLike = new PostLike(member, post);
		postLikeRepository.save(postLike);

		postService.deletePost(post.getId());
		Optional<PostComment> foundComment = postCommentRepository.findById(postLike.getId());

		assertThat(foundComment).isEmpty();
	}

	@Test
	@DisplayName("내 게시글을 정상 조회힌다")
	void testGetMyPosts() {
		Member differentMember = Member.builder()
			.email("differentMember@gmail.com")
			.nickName("다른멤버")
			.career(Career.JUNIOR)
			.profileImageUrl("www.differentMember.com")
			.githubUrl("www.differentMember.com")
			.blogUrl("www.differentMember.com")
			.memberRole(MemberRole.ROLE_MEMBER)
			.field(Field.FRONTEND)
			.mbti(Mbti.INTP)
			.build();
		memberRepository.save(differentMember);
		Post savedPost = null;
		for (int i = 0; i < 10; i++) {
			Post post;
			if (i % 2 == 0) {
				post = new Post("게시글" + i, differentMember);
			} else {
				post = new Post("게시글" + i, member);
			}
			PostImage postImage = new PostImage(i + "test.jpg", post);
			savedPost = postRepository.save(post);
			postImageRepository.save(postImage);
		}

		PostsResponse myPosts = postService.getMemberPosts(member.getId(), savedPost.getId(), 3);

		assertThat(myPosts.hasNext()).isTrue();
		assertThat(myPosts.posts().get(0).contents()).isEqualTo("게시글7");
	}

	@Test
	@DisplayName("삭제된 게시글은 수정 할 수 없다.")
	void testChangeSoftDeletedPost() {
		Post post = new Post("울랄라 테스트 게시글", member);
		postRepository.save(post);

		postService.deletePost(post.getId());
		PostChangeDto changeDto = PostChangeDto.builder()
			.postId(post.getId())
			.contents("변경된 테스트 게시글")
			.build();

		assertThrows(PostException.class, () -> postService.changePost(changeDto));
	}

	@Test
	@DisplayName("게시글 조회시 삭제된 데이터는 가져오지 않는다")
	void testGetAllPostsWithOutDeletedPost() {
		Post post = new Post("울랄라 테스트 게시글", member);
		postRepository.save(post);
		postService.deletePost(post.getId());

		PostsResponse response = postService.getAllPosts(null, 10);

		assertThat(response.posts()).isEmpty();
		assertThat(response.hasNext()).isFalse();
	}

	private MultipartFile getMockMultipartFile(File testFile) throws IOException {
		FileInputStream inputStream = new FileInputStream(testFile);
		String[] split = testFile.getName().split("\\.");

		return new MockMultipartFile(split[0], testFile.getName(), "image/" + split[1], inputStream);
	}
}