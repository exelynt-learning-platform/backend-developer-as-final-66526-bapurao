package com.example.booking.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
	private ResponseEntity<ApiError> error(HttpStatus s, String m, HttpServletRequest r) {
		return ResponseEntity.status(s)
				.body(new ApiError(LocalDateTime.now(), s.value(), s.getReasonPhrase(), m, r.getRequestURI()));
	}

	@ExceptionHandler({ ResourceNotFoundException.class, ReservationNotFoundException.class,
			UserNotFoundException.class })
	ResponseEntity<ApiError> notFound(RuntimeException e, HttpServletRequest r) {
		return error(HttpStatus.NOT_FOUND, e.getMessage(), r);
	}

	@ExceptionHandler({ InvalidReservationException.class, MethodArgumentNotValidException.class })
	ResponseEntity<ApiError> bad(Exception e, HttpServletRequest r) {
		String m = e instanceof MethodArgumentNotValidException v
				? v.getBindingResult().getFieldErrors().stream().map(x -> x.getField() + ": " + x.getDefaultMessage())
						.findFirst().orElse("Validation failed")
				: e.getMessage();
		return error(HttpStatus.BAD_REQUEST, m, r);
	}

	@ExceptionHandler({ ResourceUnavailableException.class, ReservationConflictException.class })
	ResponseEntity<ApiError> conflict(RuntimeException e, HttpServletRequest r) {
		return error(HttpStatus.CONFLICT, e.getMessage(), r);
	}

	@ExceptionHandler(UnauthorizedException.class)
	ResponseEntity<ApiError> unauthorized(RuntimeException e, HttpServletRequest r) {
		return error(HttpStatus.FORBIDDEN, e.getMessage(), r);
	}

	@ExceptionHandler(AccessDeniedException.class)
	ResponseEntity<ApiError> denied(AccessDeniedException e, HttpServletRequest r) {
		return error(HttpStatus.FORBIDDEN, "Access denied", r);
	}

	@ExceptionHandler(AuthenticationException.class)
	ResponseEntity<ApiError> auth(AuthenticationException e, HttpServletRequest r) {
		return error(HttpStatus.UNAUTHORIZED, "Invalid authentication credentials", r);
	}

	@ExceptionHandler(Exception.class)
	ResponseEntity<ApiError> generic(Exception e, HttpServletRequest r) {

	    log.error("Unhandled exception while processing request: {}", 
	            r.getRequestURI(), e);

	    return error(
	            HttpStatus.INTERNAL_SERVER_ERROR,
	            "Internal server error",
	            r
	    );
	}
}
