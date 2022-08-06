package prgrms.project.stuti.domain.studygroup.service;

import java.util.Collections;
import java.util.List;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import prgrms.project.stuti.domain.member.model.Member;
import prgrms.project.stuti.domain.studygroup.model.StudyGroupQuestion;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupQuestionResponse;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupQuestionsResponse;
import prgrms.project.stuti.global.page.PageResponse;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StudyGroupQuestionConverter {

	public static StudyGroupQuestionResponse toStudyGroupQuestionResponse(
		StudyGroupQuestion studyGroupQuestion) {
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

	public static PageResponse<StudyGroupQuestionsResponse> toStudyGroupQuestionsPageResponse(
		List<StudyGroupQuestion> questions, boolean hasNext, Long totalElements) {
		return new PageResponse<>(toStudyGroupQuestionListResponse(questions), hasNext, totalElements);
	}

	private static List<StudyGroupQuestionsResponse> toStudyGroupQuestionListResponse(
		List<StudyGroupQuestion> studyGroupQuestions) {
		return studyGroupQuestions
			.stream()
			.map(parentQuestion -> {
				Member parentMember = parentQuestion.getMember();

				return StudyGroupQuestionsResponse
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
			}).toList();
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

				return StudyGroupQuestionResponse
					.builder()
					.studyGroupQuestionId(child.getId())
					.parentId(childrenParent.getId())
					.profileImageUrl(member.getProfileImageUrl())
					.memberId(member.getId())
					.nickname(member.getNickName())
					.contents(child.getContents())
					.updatedAt(child.getUpdatedAt())
					.build();
			}).toList();
	}
}
