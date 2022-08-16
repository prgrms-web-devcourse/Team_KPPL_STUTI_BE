package prgrms.project.stuti.domain.post.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import prgrms.project.stuti.domain.post.model.PostImage;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {

	List<PostImage> findByPostId(Long postId);

	void deleteByPostId(Long postId);
}
