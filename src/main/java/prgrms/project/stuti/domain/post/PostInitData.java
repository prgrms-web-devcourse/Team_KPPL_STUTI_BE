package prgrms.project.stuti.domain.post;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.post.model.Post;
import prgrms.project.stuti.domain.post.model.PostComment;
import prgrms.project.stuti.domain.post.model.PostLike;
import prgrms.project.stuti.domain.post.repository.postcomment.PostCommentRepository;
import prgrms.project.stuti.domain.post.repository.PostImageRepository;
import prgrms.project.stuti.domain.post.repository.PostLikeRepository;
import prgrms.project.stuti.domain.post.repository.post.PostRepository;
import prgrms.project.stuti.domain.member.model.Career;
import prgrms.project.stuti.domain.member.model.Field;
import prgrms.project.stuti.domain.member.model.Mbti;
import prgrms.project.stuti.domain.member.model.Member;
import prgrms.project.stuti.domain.member.model.MemberRole;
import prgrms.project.stuti.domain.member.repository.MemberRepository;

@Component
@RequiredArgsConstructor
public class PostInitData {

	private final PostRepository postRepository;
	private final PostLikeRepository postLikeRepository;
	private final PostCommentRepository postCommentRepository;
	private final PostImageRepository postImageRepository;
	private final MemberRepository memberRepository;


	public void creatMember() {
		String email = "test@gmail.com";
		String nickname = "테스트";
		String profileImageUrl = "www.prgrms.com/test.jpg";
		String githubUrl = "www.github.com/";
		String blogUrl = "www.naver.blog.com/";
		Member member = new Member(email, nickname, Field.BACKEND, Career.JUNIOR, profileImageUrl, githubUrl,
			Mbti.ENFJ, blogUrl, MemberRole.ROLE_MEMBER);
		memberRepository.save(member);
	}
	//@PostConstruct
	public void createInitData() {
		//member
		List<Member> memberList = new ArrayList<>();
		for (int i = 1; i <= 5; i++) {
			String email = "test" + i + "@gmail.com";
			String nickname = "테스트" + i;
			String profileImageUrl = "www.prgrms.com/test" + i + ".jpg";
			String githubUrl = "www.github.com/" + i;
			String blogUrl = "www.naver.blog.com/" + i;
			// Member member = new Member(email, nickname, Field.BACKEND, Career.JUNIOR, profileImageUrl, githubUrl,
			// 	Mbti.ENFJ, blogUrl, MemberRole.ROLE_MEMBER);
			//
			Member member = Member.builder()
				.email(email)
				.nickName(nickname)
				.field(Field.BACKEND)
				.career(Career.JUNIOR)
				.githubUrl(githubUrl)
				.mbti(Mbti.ENFJ)
				.blogUrl(blogUrl)
				.memberRole(MemberRole.ROLE_MEMBER)
				.build();
			Member save = memberRepository.save(member);
			memberList.add(save);
		}

		//post
		List<Post> postList = new ArrayList<>();
		for (int i = 1; i <= 31; i++) {
			String contents = "님은 갔습니다. 아아 사랑하는 나의 님은 갔습니다.\n"
				+ "푸른 산빛을 깨치고, 단풍나무 숲을 향하여 난, 작은 길을 걸어서 차마 떨치고 갔습니다.\n"
				+ "황금의 꽃같이 굳고 빛나던 옛 맹서는 차디찬 티끌이 되어서, 한숨의 미풍에 날아갔습니다.\n"
				+ "날카로운 첫 「키스」의 추억은 나의 운명의 지침을 돌려놓고, 뒷걸음쳐서 사라졌습니다.\n"
				+ "나는 향기로운 님의 말소리에 귀먹고, 꽃다운 님의 얼굴에 눈멀었습니다.\n"
				+ "사랑도 사람의 일이라, 만날 때에 미리 떠날 것을 염려하고 경계하지 아니한 것은 아니지만, 이별은 뜻밖의 일이 되고 놀란 가슴은 새로운 슬픔에 터집니다.\n"
				+ "그러나 이별을 쓸데없는 눈물의 원천을 만들고 마는 것은 스스로 사랑을 깨뜨리는 것인 줄 아는 까닭에, 걷잡을 수 없는 슬픔의 힘을 옮겨서 새 희망의 정수박이에 들이부었습니다.\n"
				+ "우리는 만날 때에 떠날 것을 염려하는 것과 같이, 떠날 때에 다시 만날 것을 믿습니다.\n"
				+ "아아 님은 갔지마는 나는 님을 보내지 아니하였습니다.\n"
				+ "제 곡조를 못 이기는 사랑의 노래는 님의 침묵을 휩싸고 돕니다.";
			Post post = new Post(contents, memberList.get(i % 5));
			Post save = postRepository.save(post);
			postList.add(save);
		}

		//postLike
		List<PostLike> postLikeList = new ArrayList<>();
		for (int i = 1; i <= 31; i++) {
			for(int j = 1; j <= 5; j++) {
				PostLike postLike = new PostLike(memberList.get(j-1), postList.get(i-1));
				PostLike save = postLikeRepository.save(postLike);
				postLikeList.add(save);
			}
		}

		//postImage
		// for(int i = 1; i <= 31; i++) {
		// 	String postImageUrl = "www.stuti.prgrms.com/" + i;
		// 	PostImage postImage = new PostImage(postImageUrl, postList.get(i-1));
		// 	postImageRepository.save(postImage);
		// }

		//comment
		List<PostComment> parentComments = new ArrayList<>();
		List<PostComment> childComments = new ArrayList<>();
		for(int i = 1; i <= 31; i++) {
			for(int j = 1; j <= 23; j++) {
				String contents = "게시글" + i + "에대한 " + "댓글" + j;
				PostComment postComment = new PostComment(contents, null, memberList.get(j % 5), postList.get(i-1));
				PostComment save = postCommentRepository.save(postComment);
				parentComments.add(save);
				for (int k = 1; k <=4; k++) {
					String contents2 = "게시글" + i + "에대한 댓글" + j + "에 대한 대댓글" + k;
					PostComment postComment2 = new PostComment(contents2, save, memberList.get(k-1), postList.get(i-1));
					postCommentRepository.save(postComment2);
				}
			}
		}

	}

}
