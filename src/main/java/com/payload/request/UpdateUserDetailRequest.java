package com.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserDetailRequest {
	private int exp;
	private String avatar;
	private boolean admin;
}
