package com.models;

import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "users")
public class User {
	@Id
	private String id;

	@NotBlank
	@Size(max = 20)
	private String username;

	@NotBlank
	@Size(max = 120)
	private String password;
	
	private int exp;

	@DBRef
	private Avatar avatar;
	
	@DBRef
	private Set<Role> roles = new HashSet<>();
	
	@DBRef
	private Set<UnitScore> listScore;

	public User(String username, String password) {
		this.username = username;
		this.password = password;
		this.exp = 0;	
	}

}
