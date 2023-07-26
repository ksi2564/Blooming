package com.ssafy.backend.domain.common;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public abstract class CreatedAndUpdatedBaseEntity {
	@CreatedDate
	@Column(name = "CREATED_AT")
	private LocalDateTime createdDate;

	@LastModifiedDate
	@Column(name = "UPDATED_AT")
	private LocalDateTime updatedDate;

	public CreatedAndUpdatedBaseEntity(LocalDateTime createdDate, LocalDateTime updatedDate) {
		this.createdDate = createdDate;
		this.updatedDate = updatedDate;
	}
}
