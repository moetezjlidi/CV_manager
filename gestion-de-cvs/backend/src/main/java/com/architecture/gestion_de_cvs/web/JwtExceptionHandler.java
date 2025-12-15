package com.architecture.gestion_de_cvs.web;

import java.io.IOException;

import com.architecture.gestion_de_cvs.security.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletResponse;

@RestControllerAdvice
public class JwtExceptionHandler {

	@ExceptionHandler(JwtException.class)
	public void handleCustomException(HttpServletResponse res, JwtException ex) throws IOException {
		if (!res.isCommitted()) {
			res.sendError(ex.getStatusCode().value(), ex.getMessage());
		}
	}

	@ExceptionHandler(AccessDeniedException.class)
	public void handleAccessDeniedException(HttpServletResponse res) throws IOException {
		if (!res.isCommitted()) {
			res.sendError(HttpStatus.FORBIDDEN.value(), "Access denied");
		}
	}

	@ExceptionHandler(Exception.class)
	public void handleException(HttpServletResponse res) throws IOException {
		if (!res.isCommitted()) {
			res.sendError(HttpStatus.BAD_REQUEST.value(), "Something went wrong");
		}
	}

}
