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
import prgrms.project.stuti.domain.studygroup.repository.dto.StudyGroupQueryDto;
import prgrms.project.stuti.domain.studygroup.repository.studygroup.StudyGroupRepository;
import prgrms.project.stuti.domain.studygroup.repository.studymember.StudyGroupMemberRepository;
import prgrms.project.stuti.domain.studygroup.service.dto.StudyGroupDto;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupDetailResponse;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupIdResponse;
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
		StudyGroupQueryDto.StudyGroupsDto studyGroupsDto =
			studyGroupRepository.findAllWithCursorPaginationByConditions(conditionDto);

		return StudyGroupConverter.toStudyGroupsCursorPageResponse(studyGroupsDto);
	}

	@Transactional(readOnly = true)
	public CursorPageResponse<StudyGroupsResponse> getMemberStudyGroups(StudyGroupDto.FindCondition conditionDto) {
		StudyGroupQueryDto.StudyGroupsDto studyGroupsDto =
			studyGroupRepository.findMembersAllWithCursorPaginationByConditions(conditionDto);

		return StudyGroupConverter.toStudyGroupsCursorPageResponse(studyGroupsDto);
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
		StudyGroup studyGroup = findStudyGroupById(updateDto.studyGroupId());
		validateStudyLeader(updateDto.memberId(), updateDto.studyGroupId());

		updateStudyGroupImage(studyGroup, updateDto.imageFile());
		updateTitleAndDescription(studyGroup, updateDto.title(), updateDto.description());

		return StudyGroupConverter.toStudyGroupIdResponse(studyGroup.getId());
	}

	@Transactional
	public void deleteStudyGroup(StudyGroupDto.DeleteDto deleteDto) {
		validateStudyLeader(deleteDto.memberId(), deleteDto.studyGroupId());
		updateToDeleted(deleteDto.studyGroupId());
	}

	private StudyGroup saveStudyGroup(StudyGroupDto.CreateDto createDto, String imageUrl) {
		StudyGroup studyGroup = StudyGroupConverter.toStudyGroup(createDto, imageUrl);

		return studyGroupRepository.save(studyGroup);
	}

	private void saveStudyGroupLeader(Long memberId, StudyGroup studyGroup) {
		Member member = findMemberById(memberId);

		studyGroupMemberRepository.save(new StudyGroupMember(StudyGroupMemberRole.STUDY_LEADER, member, studyGroup));
	}

	private void updateStudyGroupImage(StudyGroup studyGroup, MultipartFile imageFile) {
		if (imageFile == null || imageFile.isEmpty()) {
			return;
		}

		String imageUrl = imageUploader.upload(imageFile, ImageDirectory.STUDY_GROUP);
		studyGroup.updateImage(imageUrl);
	}

	private void updateTitleAndDescription(StudyGroup studyGroup, String title, String description) {
		studyGroup.updateTitle(title);
		studyGroup.updateDescription(description);
	}

	private void updateToDeleted(Long studyGroupId) {
		StudyGroup studyGroup = findStudyGroupById(studyGroupId);
		studyGroup.delete();
	}

	private void validateStudyLeader(Long memberId, Long studyGroupId) {
		boolean isStudyLeader = studyGroupMemberRepository.isStudyLeader(memberId, studyGroupId);

		if (!isStudyLeader) {
			throw StudyGroupException.notStudyLeader(memberId, studyGroupId);
		}
	}

	private Member findMemberById(Long memberId) {
		return memberRepository.findMemberById(memberId).orElseThrow(() -> MemberException.notFoundMember(memberId));
	}

	private StudyGroup findStudyGroupById(Long studyGroupId) {
		return studyGroupRepository.findStudyGroupById(studyGroupId)
			.orElseThrow(() -> StudyGroupException.notFoundStudyGroup(studyGroupId));
	}
}
