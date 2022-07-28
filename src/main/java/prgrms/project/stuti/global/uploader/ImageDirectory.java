package prgrms.project.stuti.global.uploader;

import lombok.Getter;

@Getter
public enum ImageDirectory {
	MEMBER("images/members"), FEED("images/feeds"), STUDY_GROUP("images/study-groups");

	private final String directory;

	ImageDirectory(String directory) {
		this.directory = directory;
	}
}
