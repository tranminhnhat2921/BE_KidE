package com.payload.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
	
	private String id;
	private String username;
	private String exp;
	private String avatar;
	private String createdAt;
	private List<String> roles;
	private List<UnitScoreResponse> listScore;
	
}
