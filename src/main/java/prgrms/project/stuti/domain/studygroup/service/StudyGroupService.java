package prgrms.project.stuti.domain.studygroup.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.member.model.Mbti;
import prgrms.project.stuti.domain.member.model.Member;
import prgrms.project.stuti.domain.member.repository.MemberRepository;
import prgrms.project.stuti.domain.studygroup.model.PreferredMbti;
import prgrms.project.stuti.domain.studygroup.model.StudyGroup;
import prgrms.project.stuti.domain.studygroup.model.StudyMember;
import prgrms.project.stuti.domain.studygroup.model.StudyMemberRole;
import prgrms.project.stuti.domain.studygroup.repository.PreferredMbtiRepository;
import prgrms.project.stuti.domain.studygroup.repository.StudyGroupRepository;
import prgrms.project.stuti.domain.studygroup.repository.StudyMemberRepository;
import prgrms.project.stuti.domain.studygroup.service.dto.StudyGroupCreateDto;
import prgrms.project.stuti.domain.studygroup.service.dto.StudyGroupIdResponse;
import prgrms.project.stuti.global.error.exception.MemberException;
import prgrms.project.stuti.global.uploader.ImageUploader;
import prgrms.project.stuti.global.uploader.common.ImageDirectory;

@Service
@RequiredArgsConstructor
public class StudyGroupService {

	private final ImageUploader imageUploader;
	private final MemberRepository memberRepository;
	private final StudyGroupRepository studyGroupRepository;
	private final StudyMemberRepository studyMemberRepository;
	private final PreferredMbtiRepository preferredMbtiRepository;

	@Transactional
	public StudyGroupIdResponse createStudyGroup(StudyGroupCreateDto createDto) {
		String imageUrl = imageUploader.upload(createDto.imageFile(), ImageDirectory.STUDY_GROUP);
		String thumbnailUrl = imageUploader.createThumbnail(imageUrl);

		StudyGroup studyGroup = saveStudyGroup(createDto, imageUrl, thumbnailUrl);
		savePreferredMbtis(createDto.preferredMBTIs(), studyGroup);
		saveStudyLeader(createDto.memberId(), studyGroup);

		return StudyGroupConverter.toStudyGroupIdResponse(studyGroup.getId());
	}

	private StudyGroup saveStudyGroup(StudyGroupCreateDto createDto, String imageUrl, String thumbnailUrl) {
		StudyGroup studyGroup = StudyGroupConverter.toStudyGroup(createDto, imageUrl, thumbnailUrl);

		return studyGroupRepository.save(studyGroup);
	}

	private void savePreferredMbtis(List<Mbti> preferredMbtis, StudyGroup studyGroup) {
		preferredMbtiRepository.saveAll(
			preferredMbtis.stream().map(mbti -> new PreferredMbti(mbti, studyGroup)).toList());
	}

	private void saveStudyLeader(Long memberId, StudyGroup studyGroup) {
		Member member = getMember(memberId);

		studyMemberRepository.save(new StudyMember(StudyMemberRole.LEADER, member, studyGroup));
	}

	private Member getMember(Long memberId) {
		return memberRepository.findById(memberId).orElseThrow(() -> MemberException.notFoundMember(memberId));
	}
}
