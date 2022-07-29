package prgrms.project.stuti.domain.member.model;

public enum Career {
	JUNIOR("0년차~3년차"),
	INTERMEDIATE("3년차~5년차"),
	SENIOR("5년차~10년차"),
	MASTER("10년차~");

	private final String careerValue;

	Career(String careerValue) {
		this.careerValue = careerValue;
	}
}
