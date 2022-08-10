package prgrms.project.stuti.domain.studygroup.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.member.model.Member;
import prgrms.project.stuti.domain.member.repository.MemberRepository;
import prgrms.project.stuti.domain.studygroup.model.StudyGroup;
import prgrms.project.stuti.domain.studygroup.model.StudyGroupMember;
import prgrms.project.stuti.domain.studygroup.model.StudyGroupMemberRole;
import prgrms.project.stuti.domain.studygroup.repository.StudyGroupQueryDto;
import prgrms.project.stuti.domain.studygroup.repository.studygroup.StudyGroupRepository;
import prgrms.project.stuti.domain.studygroup.repository.studymember.StudyGroupMemberRepository;
import prgrms.project.stuti.domain.studygroup.service.dto.StudyGroupDto;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupIdResponse;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupDetailResponse;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupsResponse;
import prgrms.project.stuti.global.error.exception.MemberException;
import prgrms.project.stuti.global.error.exception.StudyGroupException;
import prgrms.project.stuti.global.page.CursorPageResponse;
import prgrms.project.stuti.global.uploader.ImageUploader;
import prgrms.project.stuti.global.uploader.common.ImageDirectory;

@Service
@RequiredArgsConstructor
public class StudyGroupService {

	private final ImageUploader imageUploader;
	private final MemberRepository memberRepository;
	private final StudyGroupRepository studyGroupRepository;
	private final StudyGroupMemberRepository studyGroupMemberRepository;

	@Transactional
	public StudyGroupIdResponse createStudyGroup(StudyGroupDto.CreateDto createDto) {
		String imageUrl = imageUploader.upload(createDto.imageFile(), ImageDirectory.STUDY_GROUP);
		StudyGroup studyGroup = saveStudyGroup(createDto, imageUrl);
		saveStudyGroupLeader(createDto.memberId(), studyGroup);

		return StudyGroupConverter.toStudyGroupIdResponse(studyGroup.getId());
	}

	@Transactional(readOnly = true)
	public CursorPageResponse<StudyGroupsResponse> getStudyGroups(StudyGroupDto.FindCondition conditionDto) {
		return studyGroupRepository.dynamicFindStudyGroupsWithCursorPagination(conditionDto);
	}

	@Transactional(readOnly = true)
	public CursorPageResponse<StudyGroupsResponse> getMemberStudyGroups(StudyGroupDto.FindCondition conditionDto) {
		return studyGroupRepository.findMemberStudyGroupsWithCursorPagination(conditionDto);
	}

	@Transactional(readOnly = true)
	public StudyGroupDetailResponse getStudyGroupDetail(StudyGroupDto.ReadDto readDto) {
		List<StudyGroupQueryDto.StudyGroupDetailDto> detailDtos =
			studyGroupRepository.findStudyGroupDetailById(readDto.studyGroupId());

		if (detailDtos.isEmpty()) {
			throw StudyGroupException.notFoundStudyGroup(readDto.studyGroupId());
		}

		return StudyGroupConverter.toStudyGroupDetailResponse(detailDtos);
	}

	@Transactional
	public StudyGroupIdResponse updateStudyGroup(StudyGroupDto.UpdateDto updateDto) {
		StudyGroup studyGroup = findStudyGroup(updateDto.studyGroupId());

		validateLeader(updateDto.memberId(), updateDto.studyGroupId());

		updateStudyGroupImage(updateDto.imageFile(), studyGroup);
		updateTitleAndDescription(updateDto.title(), updateDto.description(), studyGroup);

		return StudyGroupConverter.toStudyGroupIdResponse(studyGroup.getId());
	}

	@Transactional
	public void deleteStudyGroup(StudyGroupDto.DeleteDto deleteDto) {
		validateLeader(deleteDto.memberId(), deleteDto.studyGroupId());
		updateToDeleted(deleteDto.studyGroupId());
	}

	private StudyGroup saveStudyGroup(StudyGroupDto.CreateDto createDto, String imageUrl) {
		StudyGroup studyGroup = StudyGroupConverter.toStudyGroup(createDto, imageUrl);

		return studyGroupRepository.save(studyGroup);
	}

	private void saveStudyGroupLeader(Long memberId, StudyGroup studyGroup) {
		Member member = findMember(memberId);

		studyGroupMemberRepository.save(new StudyGroupMember(StudyGroupMemberRole.STUDY_LEADER, member, studyGroup));
	}

	private void updateStudyGroupImage(MultipartFile imageFile, StudyGroup studyGroup) {
		if (imageFile == null || imageFile.isEmpty()) {
			return;
		}

		String imageUrl = imageUploader.upload(imageFile, ImageDirectory.STUDY_GROUP);

		studyGroup.updateImage(imageUrl);
	}

	private void updateTitleAndDescription(String title, String description, StudyGroup studyGroup) {
		if (title.equals(studyGroup.getTitle()) && description.equals(studyGroup.getDescription())) {
			return;
		}

		studyGroup.updateTitle(title);
		studyGroup.updateDescription(description);
	}

	private void updateToDeleted(Long studyGroupId) {
		StudyGroup studyGroup = findStudyGroup(studyGroupId);
		studyGroup.delete();
	}

	private void validateLeader(Long memberId, Long studyGroupId) {
		boolean isLeader = studyGroupMemberRepository.isStudyLeader(memberId, studyGroupId);

		if (!isLeader) {
			throw StudyGroupException.notStudyLeader(memberId, studyGroupId);
		}
	}

	private Member findMember(Long memberId) {
		return memberRepository.findMemberById(memberId).orElseThrow(() -> MemberException.notFoundMember(memberId));
	}

	private StudyGroup findStudyGroup(Long studyGroupId) {
		return studyGroupRepository.findStudyGroupById(studyGroupId)
			.orElseThrow(() -> StudyGroupException.notFoundStudyGroup(studyGroupId));
	}
}
