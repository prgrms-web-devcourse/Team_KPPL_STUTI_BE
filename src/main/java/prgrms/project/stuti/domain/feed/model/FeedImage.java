package prgrms.project.stuti.domain.feed.model;

import javax.persistence.Column;
import javax.persistence.Entity;
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
import prgrms.project.stuti.global.base.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedImage extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "feed_image_id", unique = true, nullable = false, updatable = false)
	private Long id;

	@Column(name = "image_url", length = 150, unique = true)
	private String imageUrl;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "feed_id")
	private Feed feed;

	public FeedImage(String imageUrl, Feed feed) {
		this.imageUrl = imageUrl;
		this.feed = feed;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this,
			ToStringStyle.SHORT_PREFIX_STYLE)
			.append("id", id)
			.append("imageUrl", imageUrl)
			.append("feed", feed)
			.toString();
	}
}
