package prgrms.project.stuti.domain.studygroup.model;

import static lombok.AccessLevel.*;

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

import lombok.Getter;
import lombok.NoArgsConstructor;
import prgrms.project.stuti.domain.member.model.Mbti;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class PreferredMbti {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "preferred_mbti_id", unique = true, nullable = false, updatable = false)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "mbti", length = 4, nullable = false)
	private Mbti mbti;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "study_group_id")
	private StudyGroup studyGroup;

	public PreferredMbti(Mbti mbti) {
		this.mbti = mbti;
	}

	public void setStudyGroup(StudyGroup studyGroup) {
		this.studyGroup = studyGroup;
		studyGroup.getPreferredMBTIs().add(this);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this,
			ToStringStyle.SHORT_PREFIX_STYLE)
			.append("id", id)
			.append("mbti", mbti)
			.toString();
	}
}
