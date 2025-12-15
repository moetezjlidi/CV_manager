package com.architecture.gestion_de_cvs.web;

import java.security.Principal;

import com.architecture.gestion_de_cvs.security.JwtUserService;
import com.architecture.gestion_de_cvs.security.XUser;
import com.architecture.gestion_de_cvs.dto.XUserDTO;
import com.architecture.gestion_de_cvs.dto.SignupDTO;
import com.architecture.gestion_de_cvs.dto.SignupWithInvitationDTO;

import jakarta.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.modelmapper.ModelMapper;

/**
 * L'API d'authentification
 */
@RestController
@RequestMapping("/secu-users")
public class UserController {

	protected final Log logger = LogFactory.getLog(getClass());

	private final JwtUserService userService;

	public UserController(JwtUserService userService) {
		this.userService = userService;
	}

	@RequestMapping(path = "/login", method = { RequestMethod.GET, RequestMethod.POST })
	public String login(
			@RequestParam String username,
			@RequestParam String password) {
		return userService.login(username, password);
	}


	@PostMapping("/signup")
	public String signup(@Valid @RequestBody SignupDTO signupData) {
		return userService.signup(signupData);
	}


	@PostMapping("/signup-with-invitation")
	public String signupWithInvitation(@Valid @RequestBody SignupWithInvitationDTO signupData) {
		return userService.signupWithInvitation(signupData);
	}

	@DeleteMapping("/{username}")
	@PreAuthorize("hasAuthority('USER')")
	public String delete(@PathVariable String username) {
		userService.delete(username);
		return username;
	}

	@GetMapping("/me")
	@PreAuthorize("hasAuthority('USER')")
	public ResponseEntity<XUser> whoami(Principal user) {
		return ResponseEntity.of(userService.search(user.getName()));
	}

	@GetMapping("/{username}")
	@PreAuthorize("hasAuthority('USER')")
	public ResponseEntity<XUser> search(@PathVariable String username) {
		return ResponseEntity.of(userService.search(username));
	}

	@GetMapping("/refresh")
	@PreAuthorize("hasAuthority('USER')")
	public String refresh(Principal user) {
		return userService.refresh(user.getName());
	}

}
