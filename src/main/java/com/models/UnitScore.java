package com.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "unit_score")
public class UnitScore {
	@Id
	private String id;
	@DBRef
	private Unit unit;
	private int score;
	
	public UnitScore(Unit unit, int score) {
		super();
		this.unit = unit;
		this.score = score;
	}
}
