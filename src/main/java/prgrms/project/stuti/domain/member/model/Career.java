package prgrms.project.stuti.domain.member.model;

public enum Career {
	CODER("0년차~1년차"),
	JUNIOR("1년차~3년차"),
	INTERMEDIATE("3년차~5년차"),
	SENIOR("5년차~10년차"),
	MASTER("10년차~");

	private final String careerValue;

	Career(String careerValue) {
		this.careerValue = careerValue;
	}

	public static Career toCareer(String value) {
		return Career.valueOf(value);
	}
}
