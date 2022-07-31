package prgrms.project.stuti.global.error.exception;

import java.text.MessageFormat;
import java.time.LocalDateTime;

import prgrms.project.stuti.global.error.dto.ErrorCode;

public class StudyGroupException extends BusinessException {

	protected StudyGroupException(ErrorCode errorCode, String message) {
		super(errorCode, message);
	}

	public static StudyGroupException invalidStudyPeriod(LocalDateTime startDateTime, LocalDateTime endDateTime) {
		return new StudyGroupException(ErrorCode.INVALID_STUDY_PERIOD,
			MessageFormat.format(
				"유효하지 않은 스터디 기간입니다. (startDateTime: {0}, endDateTime: {1}", startDateTime, endDateTime));
	}
}
