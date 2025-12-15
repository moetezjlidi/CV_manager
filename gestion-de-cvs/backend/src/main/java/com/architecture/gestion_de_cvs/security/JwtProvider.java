package com.architecture.gestion_de_cvs.security;

import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

/**
 * Création/vérification/gestion d'un JWT
 */
@Component
public class JwtProvider {

	protected final Log logger = LogFactory.getLog(getClass());

	/**
	 * Par simplicité, nous stockons la clef de manière statique. il est sans doute
	 * préférable d'avoir un autre API (sur un serveur de configuration) qui nous
	 * fournisse la clé.
	 */
	@Value("${security.jwt.token.secret-key:my-very-big-secret-phrase-for-signature}")
	private String secretText;

	@Value("${security.jwt.token.expire-length:3600000}")
	private long validityInMilliseconds = 3600000; // 1h

	private SecretKey secretKey;

	@Autowired
	private JwtUserDetails myUserDetails;

	@PostConstruct
	protected void init() {
		secretKey = Keys.hmacShaKeyFor(secretText.getBytes());
	}

	public String createToken(XUser user) {

		var rolesAsString = user.getRoles().stream()//
				.filter(Objects::nonNull)//
				.collect(Collectors.toList());

		Date now = new Date();
		Date validity = new Date(now.getTime() + validityInMilliseconds);

		logger.info("+++ create token for " + user.getUserName());
		logger.info("+++ date now = " + now);
		logger.info("+++ date validity = " + validity);

		return Jwts.builder()//
				.subject(user.getUserName())//
				.claim("auth", rolesAsString)//
				.issuedAt(now)//
				.expiration(validity)//
				.signWith(secretKey).compact();
	}

	public Authentication getAuthentication(String token) {
		UserDetails userDetails = myUserDetails.loadUserByUsername(getUsername(token));
		return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
	}

	public String getUsername(String token) {
		return Jwts.parser()//
				.verifyWith(secretKey).build()//
				.parseSignedClaims(token).getPayload().getSubject();
	}

	public boolean validateToken(String token) {
		logger.info("+++ Before validate token " + token);
		try {
			Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			throw new JwtException("Expired or invalid JWT token");
		}
	}

}
