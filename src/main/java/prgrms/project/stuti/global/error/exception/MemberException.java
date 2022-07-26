package prgrms.project.stuti.global.error.exception;

import java.text.MessageFormat;

import prgrms.project.stuti.global.error.dto.ErrorCode;

public class MemberException extends BusinessException {

	protected MemberException(ErrorCode errorCode, String message) {
		super(errorCode, message);
	}

	protected MemberException(ErrorCode errorCode, String message, Throwable cause) {
		super(errorCode, message, cause);
	}

	public static MemberException invalidSignup() {
		return new MemberException(ErrorCode.INVALID_SIGNUP,"회원가입 시간이 지났습니다");
	}

	public static MemberException invalidEmail(String email) {
		return new MemberException(ErrorCode.INVALID_EMAIL,
			MessageFormat.format("이메일이 유효하지 않습니다. (email: {0})", email));
	}

	public static MemberException notFoundMember(Long memberId) {
		return new MemberException(ErrorCode.NOT_FOUND_MEMBER,
			MessageFormat.format("회원을 찾을 수 없습니다. (id: {0})", memberId));
	}

	public static MemberException blacklistDetection() {
		return new MemberException(ErrorCode.BLACKLIST_DETECTION, "이미 logout 된 accessToken 으로의 접근을 감지합니다.");
	}

	public static MemberException nicknameDuplication(String nickname) {
		return new MemberException(ErrorCode.NICKNAME_DUPLICATION,
			MessageFormat.format("이미 존재하는 닉네임 입니다. (nickname: {0})", nickname));
	}

	public static MemberException emailDuplication(String email) {
		return new MemberException(ErrorCode.REGISTERED_MEMBER,
			MessageFormat.format("이미 등록된 회원 입니다. (email: {0})", email));
	}

	public static MemberException notValidAuthType(String provider) {
		return new MemberException(ErrorCode.INVALID_AUTH_TYPE,
			MessageFormat.format("유효하지 않은 Auth 타입입니다. (provider: {0})", provider));
	}
}
