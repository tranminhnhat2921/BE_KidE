package com.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.models.EAvatar;
import com.models.ERole;
import com.models.EUnit;
import com.models.Role;
import com.models.Unit;
import com.models.UnitScore;
import com.models.User;
import com.payload.request.LoginRequest;
import com.payload.request.SignupRequest;
import com.payload.response.JwtResponse;
import com.payload.response.MessageResponse;
import com.repositories.AvatarRepository;
import com.repositories.RoleRepository;
import com.repositories.UserRepository;
import com.repositories.UnitRepository;
import com.repositories.UnitScoreRepository;
import com.security.JwtUtils;
import com.services.UserDetailsImpl;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class AuthController {
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;
	
	@Autowired
	UnitRepository unitRepository;
	
	@Autowired
	UnitScoreRepository unitScoreRepository;

	@Autowired
	AvatarRepository avatarRepository;
	
	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
				.collect(Collectors.toList());
		User user = userRepository.findByUsername(loginRequest.getUsername()).get();
		return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), String.valueOf(user.getExp()), user.getAvatar().getName().name(), roles, user.getListScore()));
	}

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
		}
		
		User user = new User(signUpRequest.getUsername(), encoder.encode(signUpRequest.getPassword()));
		Set<UnitScore> listScores = new HashSet<>();
		for (EUnit unitName : EUnit.values()) {
			Unit userUnit = unitRepository.findByName(unitName).get();
			UnitScore unitScore = new UnitScore(userUnit, 0);
			unitScoreRepository.save(unitScore);
			listScores.add(unitScore);
		}
		
		Set<String> strRoles = signUpRequest.getRoles();
		Set<Role> roles = new HashSet<>();
		
		if (strRoles == null) {
			Role userRole = roleRepository.findByName(ERole.ROLE_USER)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			roles.add(userRole);
		} else {
			strRoles.forEach(role -> {
				switch (role) {
				case "admin":
					Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(adminRole);

					break;
				default:
					Role userRole = roleRepository.findByName(ERole.ROLE_USER)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(userRole);
				}
			});
		}
		
		user.setAvatar(avatarRepository.findByName(EAvatar.AVATAR_CAT).get());
		user.setRoles(roles);
		user.setListScore(listScores);
		
		userRepository.save(user);
		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
	}
}
