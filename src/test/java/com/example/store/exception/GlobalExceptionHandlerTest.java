package com.example.store.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    private WebRequest webRequest() {
        return new ServletWebRequest(new MockHttpServletRequest());
    }

    @Test
    void handleNotFound_returns404ProblemDetail() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Order", 42L);

        ProblemDetail problem = handler.handleNotFound(ex, webRequest());

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(problem.getTitle()).isEqualTo("Resource Not Found");
        assertThat(problem.getDetail()).contains("Order").contains("42");
    }

    @Test
    void handleUnexpected_returns500ProblemDetail() {
        Exception ex = new RuntimeException("something broke");

        ProblemDetail problem = handler.handleUnexpected(ex, webRequest());

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(problem.getTitle()).isEqualTo("Internal Server Error");
    }
}
