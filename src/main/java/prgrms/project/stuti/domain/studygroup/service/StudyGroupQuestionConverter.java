package prgrms.project.stuti.domain.studygroup.service;

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

	public static PageResponse<StudyGroupQuestionsResponse> toStudyGroupQuestionsPageResponse(
		List<StudyGroupQuestionResponse> parentQuestions, List<StudyGroupQuestionResponse> childrenQuestions,
		boolean hasNext, Long totalElements
	) {
		return new PageResponse<>
			(toStudyGroupQuestionsResponse(parentQuestions, childrenQuestions), hasNext, totalElements);
	}

	private static List<StudyGroupQuestionsResponse> toStudyGroupQuestionsResponse(
		List<StudyGroupQuestionResponse> parentQuestions, List<StudyGroupQuestionResponse> childrenQuestions
	) {
		return parentQuestions
			.stream()
			.map(parentQuestion -> StudyGroupQuestionsResponse
				.builder()
				.studyGroupQuestionId(parentQuestion.studyGroupQuestionId())
				.parentId(parentQuestion.parentId())
				.profileImageUrl(parentQuestion.profileImageUrl())
				.memberId(parentQuestion.memberId())
				.nickname(parentQuestion.nickname())
				.contents(parentQuestion.contents())
				.updatedAt(parentQuestion.updatedAt())
				.children(
					childrenQuestions
						.stream()
						.filter(c -> c.parentId().equals(parentQuestion.studyGroupQuestionId()))
						.toList())
				.build())
			.toList();
	}
}
