package com.breads.minds.exception;

import com.breads.minds.dto.response.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleNotFound_returns404WithMessage() {
        ResponseEntity<ApiResponse<Void>> response =
                handler.handleNotFound(new ResourceNotFoundException("Not found"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).isEqualTo("Not found");
    }

    @Test
    void handleUnauthorized_returns401WithMessage() {
        ResponseEntity<ApiResponse<Void>> response =
                handler.handleUnauthorized(new UnauthorizedException("Unauthorized"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).isEqualTo("Unauthorized");
    }

    @Test
    void handleBadCredentials_returns401WithFixedMessage() {
        ResponseEntity<ApiResponse<Void>> response =
                handler.handleBadCredentials(new BadCredentialsException("bad creds"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().getMessage()).isEqualTo("Invalid username or password");
    }

    @Test
    void handleAccessDenied_returns403WithPermissionMessage() {
        ResponseEntity<ApiResponse<Void>> response =
                handler.handleAccessDenied(new AccessDeniedException("denied"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody().getMessage()).contains("permission");
    }

    @Test
    void handleIllegalArg_returns400WithMessage() {
        ResponseEntity<ApiResponse<Void>> response =
                handler.handleIllegalArg(new IllegalArgumentException("bad argument"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getMessage()).isEqualTo("bad argument");
    }

    @Test
    void handleIllegalState_returns409WithMessage() {
        ResponseEntity<ApiResponse<Void>> response =
                handler.handleIllegalState(new IllegalStateException("conflict state"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().getMessage()).isEqualTo("conflict state");
    }

    @Test
    void handleValidation_returns400WithFieldErrors() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("obj", "username", "Username is required");
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<ApiResponse<Map<String, String>>> response = handler.handleValidation(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).isEqualTo("Validation failed");
        assertThat(response.getBody().getData()).containsKey("username");
        assertThat(response.getBody().getData().get("username")).isEqualTo("Username is required");
    }

    @Test
    void handleGeneric_returns500WithGenericMessage() {
        ResponseEntity<ApiResponse<Void>> response =
                handler.handleGeneric(new RuntimeException("something unexpected"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).contains("unexpected error");
    }
}
