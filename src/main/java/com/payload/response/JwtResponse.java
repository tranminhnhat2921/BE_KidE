package com.payload.response;

import java.util.List;
import java.util.Set;

import com.models.UnitScore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {
	private String accessToken;
	private String type = "Bearer";
	private String id;
	private String username;
	private String exp;
	private String avatar;
	private List<String> roles;
	private Set<UnitScore> listScore;
	public JwtResponse(String accessToken, String id, String username, String exp, String avatar, List<String> roles,
			Set<UnitScore> listScore) {
		super();
		this.accessToken = accessToken;
		this.id = id;
		this.username = username;
		this.exp = exp;
		this.avatar = avatar;
		this.roles = roles;
		this.listScore = listScore;
	}
}
