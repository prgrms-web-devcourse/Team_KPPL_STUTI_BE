package prgrms.project.stuti.domain.studygroup.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.math.NumberUtils;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import prgrms.project.stuti.global.base.BaseEntity;
import prgrms.project.stuti.global.error.exception.StudyGroupException;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyGroup extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "study_group_id", unique = true, nullable = false, updatable = false)
	private Long id;

	@Column(name = "image_url", length = 150)
	private String imageUrl;

	@Column(name = "title", length = 100, nullable = false)
	private String title;

	@Enumerated(value = EnumType.STRING)
	@Column(name = "topic", length = 20, nullable = false)
	private Topic topic;

	@Column(name = "is_online", nullable = false)
	private boolean isOnline;

	@Enumerated(value = EnumType.STRING)
	@Column(name = "region", nullable = false, length = 16)
	private Region region;

	@Column(name = "number_of_members", nullable = false)
	private int numberOfMembers;

	@Column(name = "number_of_recruits", nullable = false)
	private int numberOfRecruits;

	@Column(name = "number_of_applicants", nullable = false)
	private int numberOfApplicants;

	@Embedded
	private StudyPeriod studyPeriod;

	@Column(name = "description", length = 1000, nullable = false)
	private String description;

	@Column(name = "is_deleted", nullable = false)
	private boolean isDeleted;

	@OneToMany(mappedBy = "studyGroup", cascade = CascadeType.PERSIST)
	private final Set<PreferredMbti> preferredMBTIs = new HashSet<>();

	@Builder
	public StudyGroup(
		String imageUrl, String title, Topic topic, boolean isOnline, Region region,
		int numberOfRecruits, StudyPeriod studyPeriod, String description, Set<PreferredMbti> preferredMBTIs
	) {
		this.imageUrl = imageUrl;
		this.title = title;
		this.topic = topic;
		this.isOnline = isOnline;
		this.region = region;
		this.numberOfMembers = NumberUtils.INTEGER_ONE;
		this.numberOfRecruits = numberOfRecruits;
		this.numberOfApplicants = NumberUtils.INTEGER_ZERO;
		this.studyPeriod = studyPeriod;
		this.description = description;
		this.isDeleted = false;
		preferredMBTIs.forEach(preferredMBTI -> preferredMBTI.setStudyGroup(this));
	}

	public void updateImage(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public void updateTitle(String title) {
		this.title = title;
	}

	public void updateDescription(String description) {
		this.description = description;
	}

	public void delete() {
		this.isDeleted = true;
	}

	public void increaseNumberOfMembers() {
		if (this.numberOfMembers >= this.numberOfRecruits) {
			throw StudyGroupException.recruitmentIsClosed(this.numberOfMembers, this.numberOfRecruits);
		}

		this.numberOfMembers++;
	}

	public void decreaseNumberOfMembers() {
		if (this.numberOfMembers == NumberUtils.INTEGER_ZERO) {
			return;
		}

		this.numberOfMembers--;
	}

	public void increaseNumberOfApplicants() {
		this.numberOfApplicants++;
	}

	public void decreaseNumberOfApplicants() {
		if (this.numberOfApplicants == NumberUtils.INTEGER_ZERO) {
			return;
		}

		this.numberOfApplicants--;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this,
			ToStringStyle.SHORT_PREFIX_STYLE)
			.append("id", id)
			.append("imageUrl", imageUrl)
			.append("title", title)
			.append("topic", topic)
			.append("isOnline", isOnline)
			.append("region", region)
			.append("numberOfMembers", numberOfMembers)
			.append("numberOfRecruits", numberOfRecruits)
			.append("numberOfApplicants", numberOfApplicants)
			.append("studyPeriod", studyPeriod)
			.append("description", description)
			.append("isDeleted", isDeleted)
			.append("preferredMBTIs", preferredMBTIs)
			.toString();
	}
}
