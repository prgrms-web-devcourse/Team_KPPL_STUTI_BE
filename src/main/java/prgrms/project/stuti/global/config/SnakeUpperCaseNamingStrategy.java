package prgrms.project.stuti.global.config;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

public class SnakeUpperCaseNamingStrategy implements PhysicalNamingStrategy {

	private static final String REGEX = "([a-z])([A-Z])";
	private static final String REPLACEMENT = "$1_$2";

	@Override
	public Identifier toPhysicalCatalogName(Identifier name, JdbcEnvironment jdbcEnvironment) {
		return name != null ? toSnakeUpperCase(name) : null;
	}

	@Override
	public Identifier toPhysicalSchemaName(Identifier name, JdbcEnvironment jdbcEnvironment) {
		return name != null ? toSnakeUpperCase(name) : null;
	}

	@Override
	public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment jdbcEnvironment) {
		return toSnakeUpperCase(name);
	}

	@Override
	public Identifier toPhysicalSequenceName(Identifier name, JdbcEnvironment jdbcEnvironment) {
		return toSnakeUpperCase(name);
	}

	@Override
	public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment jdbcEnvironment) {
		return toSnakeUpperCase(name);
	}

	private Identifier toSnakeUpperCase(Identifier identifier) {
		String snakeUpperCase = identifier.getText().replaceAll(REGEX, REPLACEMENT).toUpperCase();

		return Identifier.toIdentifier(snakeUpperCase);
	}
}
