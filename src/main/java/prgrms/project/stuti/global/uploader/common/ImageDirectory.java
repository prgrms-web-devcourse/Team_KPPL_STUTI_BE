package prgrms.project.stuti.global.uploader.common;

import lombok.Getter;

@Getter
public enum ImageDirectory {
	MEMBER("images/members"), FEED("images/feeds"), STUDY_GROUP("images/study-groups"), BASIC("images/basic");

	private final String directory;

	ImageDirectory(String directory) {
		this.directory = directory;
	}
}
