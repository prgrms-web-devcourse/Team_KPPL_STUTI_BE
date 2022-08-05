package prgrms.project.stuti.domain.studygroup.service.question;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import prgrms.project.stuti.domain.studygroup.service.response.QuestionIdResponse;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QuestionConverter {

	public static QuestionIdResponse toQuestionIdResponse(Long questionId) {
		return new QuestionIdResponse(questionId);
	}
}
