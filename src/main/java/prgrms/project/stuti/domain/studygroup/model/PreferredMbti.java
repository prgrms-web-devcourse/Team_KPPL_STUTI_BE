package prgrms.project.stuti.domain.studygroup.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import prgrms.project.stuti.domain.member.model.Mbti;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PreferredMbti {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "preferred_mbti_id", unique = true, nullable = false, updatable = false)
	private Long id;

	@Enumerated(value = EnumType.STRING)
	@Column(name = "mbti", length = 4, nullable = false)
	private Mbti mbti;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "study_group_id")
	private StudyGroup studyGroup;

	public PreferredMbti(Mbti mbti, StudyGroup studyGroup) {
		this.mbti = mbti;
		this.studyGroup = studyGroup;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this,
			ToStringStyle.SHORT_PREFIX_STYLE)
			.append("id", id)
			.append("mbti", mbti)
			.append("studyGroup", studyGroup)
			.toString();
	}
}
