package prgrms.project.stuti.domain.studygroup.model;

import lombok.Getter;

@Getter
public enum Topic {
	FRONTEND("프론트엔드"), BACKEND("백엔드"), IOS("IOS"), ANDROID("안드로이드"),
	AI("인공지능"), CS("컴퓨터 사이언스"), INFRA("인프라"), DEV_OPS("데브옵스"),
	BIG_DATA("빅 데이터"), EMBEDDED("임베디드"), SECURITY("보안"), NETWORK("네트워크");

	private final String value;

	Topic(String value) {
		this.value = value;
	}
}
