package prgrms.project.stuti.global.config;

import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

@Configuration
public class JsonDateTimeFormatConfig {

	private static final String DATE_FORMAT = "yyyy-MM-dd";
	private static final DateTimeFormatter LOCAL_DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);
	private static final DateTimeFormatter LOCAL_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	@Bean
	public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
		return jacksonObjectMapperBuilder -> jacksonObjectMapperBuilder
			.timeZone(TimeZone.getTimeZone("Asia/Seoul"))
			.simpleDateFormat(DATE_FORMAT)
			.serializers(new LocalDateSerializer(LOCAL_DATE_FORMATTER))
			.serializers(new LocalDateTimeSerializer(LOCAL_DATE_TIME_FORMATTER))
			.build();
	}
}
