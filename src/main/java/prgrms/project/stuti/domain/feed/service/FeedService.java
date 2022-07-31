package prgrms.project.stuti.domain.feed.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.feed.model.Feed;
import prgrms.project.stuti.domain.feed.model.FeedImage;
import prgrms.project.stuti.domain.feed.repository.FeedImageRepository;
import prgrms.project.stuti.domain.feed.repository.FeedRepository;
import prgrms.project.stuti.domain.feed.service.dto.PostDto;
import prgrms.project.stuti.domain.member.model.Member;
import prgrms.project.stuti.domain.member.repository.MemberRepository;
import prgrms.project.stuti.global.error.exception.NotFoundException;
import prgrms.project.stuti.global.uploader.LocalImageUploader;
import prgrms.project.stuti.global.uploader.common.ImageDirectory;

@Service
@RequiredArgsConstructor
public class FeedService {

	private final FeedRepository feedRepository;
	private final MemberRepository memberRepository;
	private final LocalImageUploader localImageUploader;
	private final FeedImageRepository feedImageRepository;

	@Transactional
	public Long registerPost(PostDto postDto) {
		Optional<Member> findMember = memberRepository.findById(postDto.memberId());
		if (findMember.isEmpty()) {
			NotFoundException.MEMBER_NOT_FOUND.get();
		}
		Feed feed = FeedConverter.toPost(postDto, findMember.get());
		Feed savedFeed = feedRepository.save(feed);
		String uploadUrl = localImageUploader.upload(postDto.imageFile(), ImageDirectory.FEED);
		FeedImage feedImage = new FeedImage(uploadUrl, savedFeed);
		feedImageRepository.save(feedImage);
		return savedFeed.getId();
	}
}
