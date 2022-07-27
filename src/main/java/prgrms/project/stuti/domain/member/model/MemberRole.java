package prgrms.project.stuti.domain.member.model;

public enum MemberRole {
	ROLE_NOTHING("ROLE_NOTHING"),
	ROLE_USER("ROLE_USER"),
	ROLE_ADMIN("ROLE_ADMIN"),
	ROLE_MANAGER("ROLE_MANAGER");

	public final String stringValue;

	private MemberRole(String label) {
		this.stringValue = label;
	}
}
