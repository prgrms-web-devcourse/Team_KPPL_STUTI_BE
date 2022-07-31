package prgrms.project.stuti.domain.feed.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import prgrms.project.stuti.domain.feed.model.Feed;
import prgrms.project.stuti.domain.feed.repository.FeedRepository;
import prgrms.project.stuti.domain.feed.service.dto.PostDto;
import prgrms.project.stuti.domain.member.model.Career;
import prgrms.project.stuti.domain.member.model.Field;
import prgrms.project.stuti.domain.member.model.Mbti;
import prgrms.project.stuti.domain.member.model.Member;
import prgrms.project.stuti.domain.member.model.MemberRole;
import prgrms.project.stuti.domain.member.repository.MemberRepository;
import prgrms.project.stuti.global.error.exception.NotFoundException;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
class FeedServiceTest {

	@Autowired
	private FeedRepository feedRepository;

	@Autowired
	private MemberRepository memberRepository;

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

	@Test
	@DisplayName("포스트를 정상적으로 등록한다")
	void TestRegisterPost() throws IOException {
		String testFilePath = Paths.get("src", "test", "resources").toString();
		File testImageFile = new File(testFilePath + File.separator + "test.png");

		MultipartFile testMultipartFile = getMockMultipartFile(testImageFile);

		PostDto postDto = PostDto.builder()
			.memberId(savedMember.getId())
			.contents("새로운 게시글의 내용입니다.")
			.imageFile(testMultipartFile)
			.build();

		Long feedId = feedService.registerPost(postDto);
		Optional<Feed> foundFeed = feedRepository.findById(feedId);

		assertThat(foundFeed).isNotEmpty();
		assertThat(foundFeed.get().getContent()).isEqualTo(postDto.contents());
	}

	@Test
	@DisplayName("등록되지 않은 멤버가 포스트를 등록할시에 예외가 발생한다.")
	void TestRegisterPostByUnknownMember() {
		PostDto postDto = PostDto.builder()
			.memberId(2L)
			.contents("UnknownMember가 작성한 게시글 입니다.")
			.build();

		assertThrows(NotFoundException.class, () -> feedService.registerPost(postDto));
	}

	private MultipartFile getMockMultipartFile(File testFile) throws IOException {
		FileInputStream inputStream = new FileInputStream(testFile);
		String[] split = testFile.getName().split("\\.");

		return new MockMultipartFile(split[0], testFile.getName(), "image/" + split[1], inputStream);
	}
}