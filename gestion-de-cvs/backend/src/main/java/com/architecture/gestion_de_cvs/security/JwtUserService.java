package com.architecture.gestion_de_cvs.security;

import java.util.HashSet;
import java.util.Optional;

import com.architecture.gestion_de_cvs.model.Person;
import com.architecture.gestion_de_cvs.repository.PersonRepository;
import com.architecture.gestion_de_cvs.repository.XUserRepository;
import com.architecture.gestion_de_cvs.dto.SignupDTO;
import com.architecture.gestion_de_cvs.dto.SignupWithInvitationDTO;
import com.architecture.gestion_de_cvs.service.InvitationService;
import com.architecture.gestion_de_cvs.service.EmailService;
import com.architecture.gestion_de_cvs.model.Invitation;
import com.architecture.gestion_de_cvs.security.XUser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JwtUserService {

	protected final Log logger = LogFactory.getLog(getClass());

	private final XUserRepository userRepository;
	private final PersonRepository personRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtProvider jwtTokenProvider;
	private final AuthenticationManager authenticationManager;
	private final InvitationService invitationService;
	private final EmailService emailService;

	public JwtUserService(XUserRepository userRepository, PersonRepository personRepository,
			PasswordEncoder passwordEncoder, JwtProvider jwtTokenProvider,
			AuthenticationManager authenticationManager, InvitationService invitationService,
			EmailService emailService) {
		this.userRepository = userRepository;
		this.personRepository = personRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtTokenProvider = jwtTokenProvider;
		this.authenticationManager = authenticationManager;
		this.invitationService = invitationService;
		this.emailService = emailService;
	}

	public String login(String username, String password) {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
			var user = userRepository.findById(username).get();
			return jwtTokenProvider.createToken(user);
		} catch (AuthenticationException e) {
			throw new JwtException("Invalid username/password supplied");
		}
	}

	// Créer un compte utilisateur avec CV
	@Transactional
	public String signup(SignupDTO signupData) {
		// Check if username is already taken
		if (userRepository.existsById(signupData.username())) {
			throw new JwtException("Username is already in use");
		}

		// Check if email is already taken
		if (personRepository.existsByEmail(signupData.email())) {
			throw new JwtException("Email is already in use");
		}

		// Create XUser (authentication entity)
		XUser xUser = new XUser();
		xUser.setUserName(signupData.username());
		xUser.setPassword(passwordEncoder.encode(signupData.password()));

		// Set roles - default to USER if none provided
		if (signupData.roles() == null || signupData.roles().isEmpty()) {
			xUser.setRoles(new HashSet<>(java.util.List.of("USER")));
		} else {
			xUser.setRoles(new HashSet<>(signupData.roles()));
		}

		// Save XUser first (required for foreign key relationship)
		userRepository.save(xUser);

		// Create Person (CV entity) with personal information
		Person person = new Person();
		person.setFirstName(signupData.firstName());
		person.setLastName(signupData.lastName());
		person.setEmail(signupData.email());
		person.setBirthDate(signupData.birthDate());
		person.setWebsite(signupData.website());

		// Associate Person with XUser
		person.setUser(xUser);

		// Save Person
		personRepository.save(person);

		logger.info("Created user account and CV for: " + xUser.getUserName());

		// Return JWT token for immediate authentication
		return jwtTokenProvider.createToken(xUser);
	}

	// Créer un compte depuis une invitation
	@Transactional
	public String signupWithInvitation(SignupWithInvitationDTO signupData) {
		// Validate invitation
		Invitation invitation = invitationService.getValidInvitation(signupData.invitationToken());

		// Verify email matches invitation
		if (!invitation.getEmail().equalsIgnoreCase(signupData.email())) {
			throw new JwtException("Email does not match invitation");
		}

		// Check if username is already taken
		if (userRepository.existsById(signupData.username())) {
			throw new JwtException("Username is already in use");
		}

		// Create XUser (authentication entity)
		XUser xUser = new XUser();
		xUser.setUserName(signupData.username());
		xUser.setPassword(passwordEncoder.encode(signupData.password()));
		xUser.setRoles(new HashSet<>(java.util.List.of("USER")));

		// Save XUser first
		userRepository.save(xUser);

		// Create Person (CV entity) with data from invitation
		Person person = new Person();
		person.setFirstName(invitation.getFirstName());
		person.setLastName(invitation.getLastName());
		person.setEmail(invitation.getEmail());
		person.setBirthDate(invitation.getBirthDate());
		person.setWebsite(invitation.getWebsite());
		person.setUser(xUser);

		// Save Person
		personRepository.save(person);

		// Mark invitation as used
		invitationService.markInvitationAsUsed(signupData.invitationToken());

		// Send welcome email
		try {
			emailService.sendWelcomeEmail(invitation.getEmail(), invitation.getFirstName(), invitation.getLastName());
		} catch (Exception e) {
			logger.warn("Could not send welcome email: " + e.getMessage());
		}

		logger.info("Created user account from invitation for: " + xUser.getUserName());

		// Return JWT token for immediate authentication
		return jwtTokenProvider.createToken(xUser);
	}

	public void delete(String username) {
		userRepository.deleteById(username);
	}

	public Optional<XUser> search(String username) {
		return userRepository.findById(username);
	}

	public String refresh(String username) {
		return jwtTokenProvider.createToken(userRepository.findById(username).get());
	}

}
