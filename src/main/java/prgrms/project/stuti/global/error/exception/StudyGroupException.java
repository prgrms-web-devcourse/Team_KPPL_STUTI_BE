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
				"유효하지 않은 스터디 기간입니다. (startDateTime: {0}, endDateTime: {1})", startDateTime, endDateTime));
	}

	public static StudyGroupException notFoundStudyGroup(Long studyGroupId) {
		return new StudyGroupException(ErrorCode.NOT_FOUND_STUDY_GROUP,
			MessageFormat.format(
				"스터디 그룹을 찾을 수 없습니다. (studyGroupId: {0})", studyGroupId));
	}

	public static StudyGroupException notStudyLeader(Long memberId, Long studyGroupId) {
		return new StudyGroupException(ErrorCode.NOT_STUDY_LEADER,
			MessageFormat.format(
				"스터디 그룹의 리더가 아닙니다. (memberId: {0}, studyGroupId: {1})", memberId, studyGroupId));
	}

	public static StudyGroupException existingStudyMember(Long memberId, Long studyGroupId) {
		return new StudyGroupException(ErrorCode.EXISTING_STUDY_GROUP_MEMBER,
			MessageFormat.format(
				"이미 존재하는 스터디 그룹 멤버입니다. (memberId: {0}, studyGroupId: {1})", memberId, studyGroupId));
	}

	public static StudyGroupException notFoundStudyGroupMember(Long studyGroupMemberId) {
		return new StudyGroupException(ErrorCode.EXISTING_STUDY_GROUP_MEMBER,
			MessageFormat.format(
				"스터디 그룹 멤버를 찾을 수 없습니다. (studyGroupMemberId: {0})", studyGroupMemberId));
	}

	public static StudyGroupException notFoundStudyGroupQuestion(Long studyGroupQuestionId) {
		return new StudyGroupException(ErrorCode.NOT_FOUND_STUDY_GROUP_QUESTION,
			MessageFormat.format(
				"스터디 그룹 문의 댓글을 찾을 수 없습니다. (studyGroupQuestionId: {0})", studyGroupQuestionId));
	}
}
