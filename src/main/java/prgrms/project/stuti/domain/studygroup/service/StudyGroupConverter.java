package prgrms.project.stuti.domain.studygroup.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import prgrms.project.stuti.domain.member.model.Member;
import prgrms.project.stuti.domain.studygroup.model.StudyGroup;
import prgrms.project.stuti.domain.studygroup.model.StudyGroupMember;
import prgrms.project.stuti.domain.studygroup.model.StudyGroupQuestion;
import prgrms.project.stuti.domain.studygroup.model.StudyPeriod;
import prgrms.project.stuti.domain.studygroup.service.dto.StudyGroupCreateDto;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupDetailResponse;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupIdResponse;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupMemberIdResponse;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupMemberResponse;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupQuestionListResponse;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupQuestionResponse;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupResponse;
import prgrms.project.stuti.global.page.CursorPageResponse;
import prgrms.project.stuti.global.page.PageResponse;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StudyGroupConverter {

	public static StudyGroup toStudyGroup(StudyGroupCreateDto createDto, String imageUrl, String thumbnailUrl) {
		return StudyGroup
			.builder()
			.imageUrl(imageUrl)
			.thumbnailUrl(thumbnailUrl)
			.title(createDto.title())
			.topic(createDto.topic())
			.isOnline(createDto.isOnline())
			.region(createDto.region())
			.numberOfRecruits(createDto.numberOfRecruits())
			.studyPeriod(new StudyPeriod(createDto.startDateTime(), createDto.endDateTime()))
			.preferredMBTIs(new HashSet<>(createDto.preferredMBTIs()))
			.description(createDto.description())
			.build();
	}

	public static StudyGroupIdResponse toStudyGroupIdResponse(Long studyGroupId) {
		return new StudyGroupIdResponse(studyGroupId);
	}

	public static StudyGroupMemberIdResponse toStudyGroupMemberIdResponse(Long studyGroupMemberId) {
		return new StudyGroupMemberIdResponse(studyGroupMemberId);
	}

	public static StudyGroupQuestionResponse toStudyGroupQuestionResponse(StudyGroupQuestion studyGroupQuestion) {
		Member member = studyGroupQuestion.getMember();

		return StudyGroupQuestionResponse
			.builder()
			.studyGroupQuestionId(studyGroupQuestion.getId())
			.parentId(studyGroupQuestion.getParent() == null ? null : studyGroupQuestion.getParent().getId())
			.profileImageUrl(member.getProfileImageUrl())
			.memberId(member.getId())
			.nickname(member.getNickName())
			.contents(studyGroupQuestion.getContents())
			.updatedAt(studyGroupQuestion.getUpdatedAt())
			.build();
	}

	public static StudyGroupDetailResponse toStudyGroupDetailResponse(StudyGroupMember studyGroupDetail) {
		StudyGroup studyGroup = studyGroupDetail.getStudyGroup();
		StudyPeriod studyPeriod = studyGroup.getStudyPeriod();
		Member member = studyGroupDetail.getMember();

		return StudyGroupDetailResponse
			.builder()
			.studyGroupId(studyGroup.getId())
			.topic(studyGroup.getTopic().getValue())
			.title(studyGroup.getTitle())
			.imageUrl(studyGroup.getImageUrl())
			.leader(toStudyGroupMemberResponse(member))
			.preferredMBTIs(studyGroup.getPreferredMBTIs())
			.isOnline(studyGroup.isOnline())
			.region(studyGroup.getRegion().getValue())
			.startDateTime(studyPeriod.getStartDateTime())
			.endDateTime(studyPeriod.getEndDateTime())
			.numberOfMembers(studyGroup.getNumberOfMembers())
			.numberOfRecruits(studyGroup.getNumberOfRecruits())
			.description(studyGroup.getDescription())
			.build();
	}

	public static CursorPageResponse<StudyGroupResponse> toStudyGroupPageResponse(
		List<StudyGroupMember> studyGroupMembers, boolean hasNext) {
		return new CursorPageResponse<>(toStudyGroupResponse(studyGroupMembers), hasNext);
	}

	public static PageResponse<StudyGroupQuestionListResponse> toStudyGroupQuestionPageResponse(
		List<StudyGroupQuestion> questions, boolean hasNext, Long totalElements) {
		return new PageResponse<>(toStudyGroupQuestionListResponse(questions), hasNext, totalElements);
	}

	private static StudyGroupMemberResponse toStudyGroupMemberResponse(Member member) {
		return StudyGroupMemberResponse
			.builder()
			.memberId(member.getId())
			.profileImageUrl(member.getProfileImageUrl())
			.nickname(member.getNickName())
			.field(member.getField().getFieldValue())
			.career(member.getCareer().getCareerValue())
			.mbti(member.getMbti())
			.build();
	}

	private static List<StudyGroupResponse> toStudyGroupResponse(List<StudyGroupMember> studyGroupMembers) {
		return studyGroupMembers
			.stream()
			.map(studyGroupMember -> {
				Member member = studyGroupMember.getMember();
				StudyGroup studyGroup = studyGroupMember.getStudyGroup();
				StudyPeriod studyPeriod = studyGroup.getStudyPeriod();

				return StudyGroupResponse
					.builder()
					.studyGroupId(studyGroup.getId())
					.memberId(member.getId())
					.thumbnailUrl(studyGroup.getThumbnailUrl())
					.topic(studyGroup.getTopic().getValue())
					.title(studyGroup.getTitle())
					.preferredMBTIs(studyGroup.getPreferredMBTIs())
					.region(studyGroup.getRegion().getValue())
					.startDateTime(studyPeriod.getStartDateTime())
					.endDateTime(studyPeriod.getEndDateTime())
					.numberOfMembers(studyGroup.getNumberOfMembers())
					.numberOfRecruits(studyGroup.getNumberOfRecruits())
					.build();
			})
			.toList();

	}

	private static List<StudyGroupQuestionListResponse> toStudyGroupQuestionListResponse(
		List<StudyGroupQuestion> studyGroupQuestions) {
		return studyGroupQuestions
			.stream()
			.map(parentQuestion -> {
				Member parentMember = parentQuestion.getMember();

				return StudyGroupQuestionListResponse
					.builder()
					.studyGroupQuestionId(parentQuestion.getId())
					.parentId(null)
					.profileImageUrl(parentMember.getProfileImageUrl())
					.memberId(parentMember.getId())
					.nickname(parentMember.getNickName())
					.contents(parentQuestion.getContents())
					.updatedAt(parentQuestion.getUpdatedAt())
					.children(toStudyGroupQuestionChildren(parentQuestion.getChildren()))
					.build();
			})
			.toList();
	}

	private static List<StudyGroupQuestionResponse> toStudyGroupQuestionChildren(
		List<StudyGroupQuestion> children) {
		return children.isEmpty()
			? Collections.emptyList()
			: children
			.stream()
			.map(child -> {
				Member member = child.getMember();
				StudyGroupQuestion childrenParent = child.getParent();

				return StudyGroupQuestionResponse.builder()
					.studyGroupQuestionId(child.getId())
					.parentId(childrenParent.getId())
					.profileImageUrl(member.getProfileImageUrl())
					.memberId(member.getId())
					.nickname(member.getNickName())
					.contents(child.getContents())
					.updatedAt(child.getUpdatedAt())
					.build();
			})
			.toList();
	}
}
