package prgrms.project.stuti.domain.studygroup.model;

import lombok.Getter;

@Getter
public enum Region {
	ONLINE("온라인"), SEOUL("서울"), BUSAN("부산"),
	DAEGU("대구"), INCHEON("인천"), GWANGJU("광주"),
	DAEJEON("대전"), ULSAN("울산"), JEJU("제주");

	private final String value;

	Region(String value) {
		this.value = value;
	}
}
