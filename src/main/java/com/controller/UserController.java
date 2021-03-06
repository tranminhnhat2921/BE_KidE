package com.controller;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.models.EAvatar;
import com.models.ERole;
import com.models.EUnit;
import com.models.Role;
import com.models.UnitScore;
import com.models.User;
import com.payload.request.UpdateScoreRequest;
import com.payload.request.UpdateUserDetailRequest;
import com.payload.response.ChangeAvatarResponse;
import com.payload.response.MessageResponse;
import com.payload.response.UpdateScoreResponse;
import com.repositories.AvatarRepository;
import com.repositories.RoleRepository;
import com.repositories.UserRepository;

@RestController
@RequestMapping("/api")
public class UserController {

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;
	
	@Autowired
	AvatarRepository avatarRepository;
	
	@PutMapping("/user/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> updateDetail(@PathVariable("id") String id,
			@RequestBody UpdateUserDetailRequest updateUserDetailRequest) {
		Optional<User> userData = userRepository.findById(id);
		if (userData.isPresent()) {
			User _user = userData.get();
			_user.setExp(updateUserDetailRequest.getExp());
			
			switch (updateUserDetailRequest.getAvatar()) {
			case "cat":
				_user.setAvatar(avatarRepository.findByName(EAvatar.AVATAR_CAT).get());
				break;
			case "dinosaur":
				_user.setAvatar(avatarRepository.findByName(EAvatar.AVATAR_DINOSAUR).get());
			case "dolphin":
				_user.setAvatar(avatarRepository.findByName(EAvatar.AVATAR_DOLPHIN).get());
			default:
				System.out.println("Not found avatar");
			}
			
			Set<Role> roles = new HashSet<>();
			Role userRole = roleRepository.findByName(ERole.ROLE_USER).get();
			Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN).get();
			roles.add(userRole);
			if(updateUserDetailRequest.isAdmin()) {
				roles.add(adminRole);
			}
			
			_user.setRoles(roles);
			userRepository.save(_user);
			return ResponseEntity.ok().body(new MessageResponse("User data has been changed!"));
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@PutMapping("/user/score/{id}")
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	public ResponseEntity<?> updateScore(@PathVariable("id") String id,
			@RequestBody UpdateScoreRequest updateScoreRequest) {
		Optional<User> userData = userRepository.findById(id);
		if (userData.isPresent()) {
			User user = userData.get();
			String updateUnitName;
			int newExp = user.getExp() + 2;
			int currentScore = 0;
			int increaseScore = 0;

			switch (updateScoreRequest.getName()) {
			case "aphabet":
				updateUnitName = EUnit.UNIT_APHABET.name();
				break;
			case "number":
				updateUnitName = EUnit.UNIT_NUMBER.name();
				break;
			case "color":
				updateUnitName = EUnit.UNIT_COLOR.name();
				break;
			case "animal":
				updateUnitName = EUnit.UNIT_ANIMAL.name();
				break;
			default:
				return ResponseEntity.accepted().body(new MessageResponse("Unit does not exist!"));
			}

			for (UnitScore unitScore : user.getListScore()) {
				if (unitScore.getName().equals(updateUnitName)) {
					currentScore = unitScore.getScore();
					increaseScore = updateScoreRequest.getScore() - currentScore;
					if (unitScore.getScore() < updateScoreRequest.getScore()) {
						unitScore.setScore(updateScoreRequest.getScore());
						user.setExp(newExp);
						userRepository.save(user);
						return ResponseEntity.ok().body(new UpdateScoreResponse("Update score success!",
								updateScoreRequest.getName(), unitScore.getScore(), newExp + increaseScore / 10));
					}
					break;
				}
			}
			user.setExp(newExp);
			userRepository.save(user);
			return ResponseEntity.ok().body(new UpdateScoreResponse("Score less than current score!",
					updateScoreRequest.getName(), currentScore, newExp));
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@PutMapping("/user/avatar/{id}/{avatar}")
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	public ResponseEntity<?> updateAvatar(@PathVariable("id") String id, @PathVariable("avatar") String avatar) {
		Optional<User> userData = userRepository.findById(id);
		if (userData.isPresent()) {
			User _user = userData.get();
			switch (avatar) {
			case "cat":
				_user.setAvatar(avatarRepository.findByName(EAvatar.AVATAR_CAT).get());
				break;
			case "dinosaur":
				if (_user.getExp() >= 300) {
					_user.setAvatar(avatarRepository.findByName(EAvatar.AVATAR_DINOSAUR).get());
				} else
					return ResponseEntity.ok()
							.body(new ChangeAvatarResponse("Avatar dinosaur ch??a ???????c m??? kh??a!", false));
				break;
			case "dolphin":
				if (_user.getExp() >= 500) {
					_user.setAvatar(avatarRepository.findByName(EAvatar.AVATAR_DOLPHIN).get());
				} else
					return ResponseEntity.ok()
							.body(new ChangeAvatarResponse("Avatar dolphin ch??a ???????c m??? kh??a!", false));
				break;
			default:
				return ResponseEntity.notFound().build();
			}
			userRepository.save(_user);
			return ResponseEntity.ok().body(new ChangeAvatarResponse("Thay ?????i avatar th??nh c??ng!", true));
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@DeleteMapping("/user/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> deleteUser(@PathVariable("id") String id) {
		Optional<User> userData = userRepository.findById(id);
		if (userData.isPresent()) {
			userRepository.deleteById(id);
			return ResponseEntity.ok().body(new MessageResponse("Account has been deleted!"));
		} else {
			return ResponseEntity.notFound().build();
		}
	}

}
