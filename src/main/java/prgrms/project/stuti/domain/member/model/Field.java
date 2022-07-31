package prgrms.project.stuti.domain.member.model;

public enum Field {
	FRONTEND("프론트엔드"),
	BACKEND("백엔드"),
	INFRA("인프라"),
	IOS("IOS"),
	ANDROID("안드로이드"),
	DATA_ANALYST("데이터 분석가"),
	DEVELOPER("개발자");

	private final String fieldValue;

	Field(String fieldValue) {
		this.fieldValue = fieldValue;
	}
}
