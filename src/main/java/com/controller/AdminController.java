package com.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.models.ERole;
import com.models.Role;
import com.models.UnitScore;
import com.models.User;
import com.payload.request.UpdateRoleRequest;
import com.payload.response.UnitScoreResponse;
import com.payload.response.UserResponse;
import com.repositories.AvatarRepository;
import com.repositories.RoleRepository;
import com.repositories.UserRepository;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	RoleRepository roleRepository;
	
	@Autowired
	AvatarRepository avatarRepository;

	@GetMapping("/users")
	public ResponseEntity<?> getAllUsers() {
		List<User> listUsers = userRepository.findAll();
		List<UserResponse> listUserResponse = new ArrayList<UserResponse>();
		UserResponse userResponse;
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		for(User user : listUsers) {
			userResponse = new UserResponse();
			userResponse.setId(user.getId());
			userResponse.setUsername(user.getUsername());
			userResponse.setAvatar(user.getAvatar().getName().name());
			userResponse.setExp(String.valueOf(user.getExp()));
			userResponse.setCreatedAt(sdf.format(user.getCreatedAt()));
			
			List<String> listRoles = new ArrayList<String>();
			for (Role role : user.getRoles()) {
				listRoles.add(role.getName().toString());
			}
			userResponse.setRoles(listRoles);
			
			List<UnitScoreResponse> listScore = new ArrayList<UnitScoreResponse>();
			for (UnitScore unitScore : user.getListScore()) {
				UnitScoreResponse unitScoreResponse = new UnitScoreResponse();
				unitScoreResponse.setName(unitScore.getName());
				unitScoreResponse.setScore(String.valueOf(unitScore.getScore()));
				listScore.add(unitScoreResponse);
			}
			userResponse.setListScore(listScore);
			
			listUserResponse.add(userResponse);
		}
		return ResponseEntity.ok(listUserResponse);
	}

	@GetMapping("/users/{id}")
	public ResponseEntity<?> getUserById(@PathVariable("id") String id) {
		Optional<User> userData = userRepository.findById(id);
		UserResponse userResponse = new UserResponse();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		if (userData.isPresent()) {
				User user = userData.get();
				userResponse.setId(user.getId());
				userResponse.setUsername(user.getUsername());
				userResponse.setAvatar(user.getAvatar().getName().name());
				userResponse.setExp(String.valueOf(user.getExp()));
				userResponse.setCreatedAt(sdf.format(user.getCreatedAt()));
				
				List<String> listRoles = new ArrayList<String>();
				for (Role role : user.getRoles()) {
					listRoles.add(role.getName().toString());
				}
				userResponse.setRoles(listRoles);
				
				List<UnitScoreResponse> listScore = new ArrayList<UnitScoreResponse>();
				for (UnitScore unitScore : user.getListScore()) {
					UnitScoreResponse unitScoreResponse = new UnitScoreResponse();
					unitScoreResponse.setName(unitScore.getName());
					unitScoreResponse.setScore(String.valueOf(unitScore.getScore()));
					listScore.add(unitScoreResponse);
				}
				userResponse.setListScore(listScore);
		}
		return ResponseEntity.ok(userResponse);
	}
	
	@PutMapping("/role/{id}")
	public ResponseEntity<?> changeUserRole(@PathVariable("id") String id, @RequestBody UpdateRoleRequest updateRoleRequest) {
		Optional<User> userData = userRepository.findById(id);
		Set<Role> roles = new HashSet<>();
		if (userData.isPresent()) {
			User user = userData.get();
			Role userRole = roleRepository.findByName(ERole.ROLE_USER).get();
			Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN).get();
			roles.add(userRole);
			if(updateRoleRequest.isAdmin()) {
				roles.add(adminRole);
			}
			user.setRoles(roles);
		} else {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(userRepository.save(userData.get()));
	}
}
