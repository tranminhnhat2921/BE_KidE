package com.payload.response;

import lombok.AllArgsConstructor;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateScoreResponse {
	private String message;
	private String unit;
	private int score;
	private int exp;
}
