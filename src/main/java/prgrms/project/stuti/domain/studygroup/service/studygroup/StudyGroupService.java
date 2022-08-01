package prgrms.project.stuti.domain.studygroup.service.studygroup;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.member.model.Mbti;
import prgrms.project.stuti.domain.member.model.Member;
import prgrms.project.stuti.domain.member.repository.MemberRepository;
import prgrms.project.stuti.domain.studygroup.model.PreferredMbti;
import prgrms.project.stuti.domain.studygroup.model.StudyGroup;
import prgrms.project.stuti.domain.studygroup.model.StudyMember;
import prgrms.project.stuti.domain.studygroup.model.StudyMemberRole;
import prgrms.project.stuti.domain.studygroup.repository.PreferredMbtiRepository;
import prgrms.project.stuti.domain.studygroup.repository.studygroup.StudyGroupRepository;
import prgrms.project.stuti.domain.studygroup.repository.studymember.StudyMemberRepository;
import prgrms.project.stuti.domain.studygroup.service.dto.StudyGroupApplyDto;
import prgrms.project.stuti.domain.studygroup.service.dto.StudyGroupCreateDto;
import prgrms.project.stuti.domain.studygroup.service.dto.StudyGroupDeleteDto;
import prgrms.project.stuti.domain.studygroup.service.dto.StudyGroupIdResponse;
import prgrms.project.stuti.domain.studygroup.service.dto.StudyGroupUpdateDto;
import prgrms.project.stuti.global.error.exception.MemberException;
import prgrms.project.stuti.global.error.exception.StudyGroupException;
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
		saveStudyGroupLeader(createDto.memberId(), studyGroup);

		return StudyGroupConverter.toStudyGroupIdResponse(studyGroup.getId());
	}

	@Transactional
	public StudyGroupIdResponse updateStudyGroup(StudyGroupUpdateDto updateDto) {
		StudyGroup studyGroup = getStudyGroup(updateDto.studyGroupId());

		validateLeader(updateDto.memberId(), updateDto.studyGroupId());

		updateStudyGroupImage(updateDto.imageFile(), studyGroup);
		updateTitleAndDescription(updateDto.title(), updateDto.description(), studyGroup);

		return StudyGroupConverter.toStudyGroupIdResponse(studyGroup.getId());
	}

	@Transactional
	public StudyGroupIdResponse applyStudyGroup(StudyGroupApplyDto applyDto) {
		Long memberId = applyDto.memberId();
		Long studyGroupId = applyDto.studyGroupId();

		validateExistingStudyMember(memberId, studyGroupId);
		saveStudyGroupApplicant(memberId, studyGroupId);

		return StudyGroupConverter.toStudyGroupIdResponse(studyGroupId);
	}

	@Transactional
	public StudyGroupIdResponse deleteStudyGroup(StudyGroupDeleteDto deleteDto) {
		validateLeader(deleteDto.memberId(), deleteDto.studyGroupId());
		updateIsDeleted(deleteDto.studyGroupId());

		return StudyGroupConverter.toStudyGroupIdResponse(deleteDto.studyGroupId());
	}

	private StudyGroup saveStudyGroup(StudyGroupCreateDto createDto, String imageUrl, String thumbnailUrl) {
		StudyGroup studyGroup = StudyGroupConverter.toStudyGroup(createDto, imageUrl, thumbnailUrl);

		return studyGroupRepository.save(studyGroup);
	}

	private void savePreferredMbtis(List<Mbti> preferredMbtis, StudyGroup studyGroup) {
		preferredMbtiRepository.saveAll(
			preferredMbtis.stream().map(mbti -> new PreferredMbti(mbti, studyGroup)).toList());
	}

	private void saveStudyGroupLeader(Long memberId, StudyGroup studyGroup) {
		Member member = getMember(memberId);

		studyMemberRepository.save(new StudyMember(StudyMemberRole.LEADER, member, studyGroup));
	}

	private void saveStudyGroupApplicant(Long memberId, Long studyGroupId) {
		Member member = getMember(memberId);
		StudyGroup studyGroup = getStudyGroup(studyGroupId);

		studyMemberRepository.save(new StudyMember(StudyMemberRole.APPLICANT, member, studyGroup));
	}

	private void updateStudyGroupImage(MultipartFile imageFile, StudyGroup studyGroup) {
		if (imageFile == null || imageFile.isEmpty()) {
			return;
		}

		String imageUrl = imageUploader.upload(imageFile, ImageDirectory.STUDY_GROUP);
		String thumbnailUrl = imageUploader.createThumbnail(imageUrl);

		studyGroup.updateImage(imageUrl, thumbnailUrl);
	}

	private void updateTitleAndDescription(String title, String description, StudyGroup studyGroup) {
		if (!title.equals(studyGroup.getTitle()) && !description.equals(studyGroup.getDescription())) {
			studyGroup.updateTitle(title);
			studyGroup.updateDescription(description);
		}
	}

	private void updateIsDeleted(Long studyGroupId) {
		StudyGroup studyGroup = getStudyGroup(studyGroupId);
		studyGroup.delete();
	}

	private void validateLeader(Long memberId, Long studyGroupId) {
		boolean isLeader = studyMemberRepository.isLeader(memberId, studyGroupId);

		if (!isLeader) {
			throw StudyGroupException.notLeader(memberId, studyGroupId);
		}
	}

	private void validateExistingStudyMember(Long memberId, Long studyGroupId) {
		boolean isExists = studyMemberRepository.existsByMemberIdAndStudyGroupId(memberId, studyGroupId);

		if (isExists) {
			throw StudyGroupException.existingStudyMember(memberId, studyGroupId);
		}
	}

	private Member getMember(Long memberId) {
		return memberRepository.findMemberById(memberId).orElseThrow(() -> MemberException.notFoundMember(memberId));
	}

	private StudyGroup getStudyGroup(Long studyGroupId) {
		return studyGroupRepository.findStudyGroupById(studyGroupId)
			.orElseThrow(() -> StudyGroupException.notFoundStudyGroup(studyGroupId));
	}
}
