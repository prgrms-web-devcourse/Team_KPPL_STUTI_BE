package prgrms.project.stuti.global.error.dto;


public record TokenExpirationResponse(String errorCode, String newToken) {

	public static TokenExpirationResponse of(ErrorCode errorCode) {
		return new TokenExpirationResponse(errorCode.getCode(), errorCode.getMessage());
	}

}
