package prgrms.project.stuti.domain.feed.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import prgrms.project.stuti.domain.feed.model.Comment;
import prgrms.project.stuti.domain.feed.model.Feed;
import prgrms.project.stuti.domain.feed.model.FeedImage;
import prgrms.project.stuti.domain.feed.repository.CommentRepository;
import prgrms.project.stuti.domain.feed.repository.FeedImageRepository;
import prgrms.project.stuti.domain.feed.repository.FeedRepository;
import prgrms.project.stuti.domain.feed.service.dto.FeedResponse;
import prgrms.project.stuti.domain.feed.service.dto.PostChangeDto;
import prgrms.project.stuti.domain.feed.service.dto.PostCreateDto;
import prgrms.project.stuti.domain.feed.service.dto.PostIdResponse;
import prgrms.project.stuti.domain.member.model.Career;
import prgrms.project.stuti.domain.member.model.Field;
import prgrms.project.stuti.domain.member.model.Mbti;
import prgrms.project.stuti.domain.member.model.Member;
import prgrms.project.stuti.domain.member.model.MemberRole;
import prgrms.project.stuti.domain.member.repository.MemberRepository;
import prgrms.project.stuti.global.error.exception.FeedException;
import prgrms.project.stuti.global.error.exception.MemberException;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
class FeedServiceTest {

	@Autowired
	private FeedRepository feedRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private FeedImageRepository feedImageRepository;

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private FeedService feedService;

	private static Member savedMember;

	@BeforeAll
	void memberSetup() {
		Member testMember = Member.builder()
			.email("testMember@gmail.com")
			.nickName("테스트멤버")
			.career(Career.JUNIOR)
			.profileImageUrl("www.test.com")
			.githubUrl("www.test.com")
			.blogUrl("www.blog.com")
			.memberRole(MemberRole.ROLE_MEMBER)
			.field(Field.BACKEND)
			.mbti(Mbti.ENFJ)
			.build();
		savedMember = memberRepository.save(testMember);
	}

	@AfterEach
	void deleteAllPost() {
		feedImageRepository.deleteAll();
		feedRepository.deleteAll();
	}

	@Test
	@DisplayName("포스트를 정상적으로 등록한다")
	void testRegisterPost() throws IOException {
		String testFilePath = Paths.get("src", "test", "resources").toString();
		File testImageFile = new File(testFilePath + File.separator + "test.png");

		MultipartFile testMultipartFile = getMockMultipartFile(testImageFile);

		PostCreateDto postDto = PostCreateDto.builder()
			.memberId(savedMember.getId())
			.contents("새로운 게시글의 내용입니다.")
			.imageFile(testMultipartFile)
			.build();

		PostIdResponse postIdResponse = feedService.registerPost(postDto);
		Optional<Feed> foundFeed = feedRepository.findById(postIdResponse.postId());

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

		assertThrows(MemberException.class, () -> feedService.registerPost(postDto));
	}

	@Test
	@DisplayName("전체 포스트리스트를 커서방식으로 페이징하여 가져온다")
	void testGetAllPosts() {
		Long lastPostId = null;
		for (int i = 0; i < 10; i++) {
			Feed feed = new Feed("게시글" + i, savedMember);
			FeedImage feedImage = new FeedImage(i + "test.jpg", feed);
			Feed savedFeed = feedRepository.save(feed);
			feedImageRepository.save(feedImage);
			if (i == 9)
				lastPostId = savedFeed.getId();
		}

		FeedResponse allPosts = feedService.getAllPosts(lastPostId, 2);

		assertThat(allPosts.posts()).hasSize(2);
		assertThat(allPosts.posts().get(0).contents()).isEqualTo("게시글8");
		assertThat(allPosts.hasNext()).isTrue();
	}

	@Test
	@DisplayName("전체 포스트리스트 첫 조회시(lastPostId가 null일 때) 페이징 조회한다")
	void testGetAllPostsWhenLastPostIdIsNull() {
		for (int i = 0; i < 10; i++) {
			Feed feed = new Feed("게시글" + i, savedMember);
			FeedImage feedImage = new FeedImage(i + "test.jpg", feed);
			feedRepository.save(feed);
			feedImageRepository.save(feedImage);
		}

		FeedResponse allPosts = feedService.getAllPosts(null, 2);

		assertThat(allPosts.posts()).hasSize(2);
		assertThat(allPosts.posts().get(0).contents()).isEqualTo("게시글9");
		assertThat(allPosts.hasNext()).isTrue();
	}

	@Test
	@DisplayName("게시글 내용과 업로드 이미지가 둘다 정상 변경된다.")
	void testChangePost() throws IOException {
		PostIdResponse postIdResponse = savePost();
		List<FeedImage> originImages = feedImageRepository.findByFeedId(postIdResponse.postId());

		File changeImageFile = new File(Paths.get("src", "test", "resources")
			+ File.separator + "change.jpg");
		MultipartFile testChangeMultipartFile = getMockMultipartFile(changeImageFile);
		PostChangeDto postChangeDto = new PostChangeDto(postIdResponse.postId(), "게시글 내용이 변경되었습니다.",
			testChangeMultipartFile);
		PostIdResponse changePostIdResponse = feedService.changePost(postChangeDto);

		List<FeedImage> changedImages = feedImageRepository.findByFeedId(changePostIdResponse.postId());
		Feed changedFeed = feedRepository.findById(changePostIdResponse.postId()).get();

		assertThat(changedFeed.getContent()).isEqualTo("게시글 내용이 변경되었습니다.");
		assertThat(changedImages).hasSize(1);
		assertThat(changedImages.get(0).getImageUrl()).isNotEqualTo(originImages.get(0).getImageUrl());
	}

	@Test
	@DisplayName("업로드 이미지를 보내주지않으면 삭제로 인지하여 삭제한다")
	void testChangePostWithOutImages() throws IOException {
		PostIdResponse postIdResponse = savePost();
		PostChangeDto postChangeDto = new PostChangeDto(postIdResponse.postId(), "게시글 내용이 변경되었습니다.", null);
		PostIdResponse changePostIdResponse = feedService.changePost(postChangeDto);

		List<FeedImage> changedImages = feedImageRepository.findByFeedId(changePostIdResponse.postId());
		Feed changedFeed = feedRepository.findById(changePostIdResponse.postId()).get();

		assertThat(changedFeed.getContent()).isEqualTo("게시글 내용이 변경되었습니다.");
		assertThat(changedImages).isEmpty();
	}

	@Test
	@DisplayName("게시글과 게시글 이미지를 삭제한다")
	void TestDeletePost() throws IOException {
		PostIdResponse postIdResponse = savePost();

		feedService.deletePost(postIdResponse.postId());
		List<Feed> allPosts = feedRepository.findAll();

		assertThat(allPosts).isEmpty();
	}

	@Test
	@DisplayName("없는게시글을 삭제하려고하면 에러를 리턴한다")
	void TestDeletePostWithUnknownPostId() {
		assertThrows(FeedException.class, () -> feedService.deletePost(1L));
	}

	private PostIdResponse savePost() throws IOException {
		String testFilePath = Paths.get("src", "test", "resources").toString();
		File originalImageFile = new File(testFilePath + File.separator + "test.png");
		MultipartFile testOriginalMultipartFile = getMockMultipartFile(originalImageFile);

		PostCreateDto postDto = PostCreateDto.builder()
			.memberId(savedMember.getId())
			.contents("새로운 게시글의 내용입니다.")
			.imageFile(testOriginalMultipartFile)
			.build();
		return feedService.registerPost(postDto);
	}

	@Test
	@DisplayName("게시글 삭제시 게시글에 붙은 댓글도 전부 삭제처리한다.")
	void testDeletePostWithComments() {
		Feed feed = new Feed("울랄라 테스트 게시글", savedMember);
		feedRepository.save(feed);
		Comment comment = new Comment("댓글", null, savedMember, feed);
		commentRepository.save(comment);

		feedService.deletePost(feed.getId());
		Optional<Comment> foundComment = commentRepository.findById(comment.getId());

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
		Feed savedFeed = null;
		for (int i = 0; i < 10; i++) {
			Feed feed;
			if (i % 2 == 0) {
				feed = new Feed("게시글" + i, differentMember);
			} else {
				feed = new Feed("게시글" + i, savedMember);
			}
			FeedImage feedImage = new FeedImage(i + "test.jpg", feed);
			savedFeed = feedRepository.save(feed);
			feedImageRepository.save(feedImage);
		}

		FeedResponse myPosts = feedService.getMyPosts(savedMember.getId(), savedFeed.getId(), 3);

		assertThat(myPosts.hasNext()).isTrue();
		assertThat(myPosts.posts().get(0).contents()).isEqualTo("게시글7");
	}

	private MultipartFile getMockMultipartFile(File testFile) throws IOException {
		FileInputStream inputStream = new FileInputStream(testFile);
		String[] split = testFile.getName().split("\\.");

		return new MockMultipartFile(split[0], testFile.getName(), "image/" + split[1], inputStream);
	}
}