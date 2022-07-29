package prgrms.project.stuti.global.base;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import lombok.Getter;

@Getter
@MappedSuperclass
public abstract class BaseEntity extends BaseTimeEntity {

	@CreatedBy
	@Column(name = "created_by", length = 40, nullable = false, updatable = false)
	private Long createdBy;

	@LastModifiedBy
	@Column(name = "updated_by", length = 40, nullable = false)
	private Long updatedBy;
}
