package prgrms.project.stuti.global.token;

public enum TokenType {
	JWT_TYPE("Bearer "),
	JWT_BLACKLIST("BL ");

	private final String typeValue;

	TokenType(String typeValue) {
		this.typeValue = typeValue;
	}

	public String getTypeValue() {
		return typeValue;
	}
}
