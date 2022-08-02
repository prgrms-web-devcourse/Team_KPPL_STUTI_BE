package prgrms.project.stuti.domain.studygroup.model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.math.NumberUtils;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import prgrms.project.stuti.global.error.exception.StudyGroupException;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyPeriod {

	@Column(name = "start_date_time", nullable = false)
	private LocalDateTime startDateTime;

	@Column(name = "end_date_time", nullable = false)
	private LocalDateTime endDateTime;

	public StudyPeriod(LocalDateTime startDateTime, LocalDateTime endDateTime) {
		validateStudyPeriod(startDateTime, endDateTime);

		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime;
	}

	private void validateStudyPeriod(LocalDateTime startDateTime, LocalDateTime endDateTime) {
		boolean isStartAfterEndDateTime = isStartAfterEndDateTime(startDateTime, endDateTime);
		boolean isLessThanOneDay = isLessThanOneDay(startDateTime, endDateTime);

		if (isStartAfterEndDateTime || isLessThanOneDay) {
			throw StudyGroupException.invalidStudyPeriod(startDateTime, endDateTime);
		}
	}

	private boolean isLessThanOneDay(LocalDateTime startDateTime, LocalDateTime endDateTime) {
		long diffOfDays = ChronoUnit.DAYS.between(startDateTime, endDateTime);

		return diffOfDays < NumberUtils.INTEGER_ONE;
	}

	private boolean isStartAfterEndDateTime(LocalDateTime startDateTime, LocalDateTime endDateTime) {
		return startDateTime.isAfter(endDateTime);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this,
			ToStringStyle.SHORT_PREFIX_STYLE)
			.append("startDateTime", startDateTime)
			.append("endDateTime", endDateTime)
			.toString();
	}
}
