package fr._il.MedFall.handler;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import fr._il.MedFall.exceptions.User.ExpiredOtpException;
import fr._il.MedFall.exceptions.User.IncorrectMailException;
import fr._il.MedFall.exceptions.User.IncorrectOtpException;
import static fr._il.MedFall.handler.BusinessErrorCodes.BAD_CREDENTIALS;
import static fr._il.MedFall.handler.BusinessErrorCodes.EXPIRED_OTP;
import static fr._il.MedFall.handler.BusinessErrorCodes.INCORRECT_MAIL;
import static fr._il.MedFall.handler.BusinessErrorCodes.INCORRECT_OTP;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handleException() {
        return ResponseEntity
                .status(UNAUTHORIZED)
                .body(
                        ExceptionResponse.builder()
                                .businessErrorCode(BAD_CREDENTIALS.getCode())
                                .businessErrorDescription(BAD_CREDENTIALS.getDescription())
                                .error("Email et / ou mot de passe sont incorrects.")
                                .build()
                );
    }

    @ExceptionHandler(IncorrectMailException.class)
    public ResponseEntity<ExceptionResponse> handleException(IncorrectMailException exp) {
        return ResponseEntity
                .status(NOT_FOUND)
                .body(
                        ExceptionResponse.builder()
                                .businessErrorCode(INCORRECT_MAIL.getCode())
                                .businessErrorDescription(INCORRECT_MAIL.getDescription())
                                .error(exp.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(IncorrectOtpException.class)
    public ResponseEntity<ExceptionResponse> handleException(IncorrectOtpException exp) {
        return ResponseEntity
                .status(NOT_FOUND)
                .body(
                        ExceptionResponse.builder()
                                .businessErrorCode(INCORRECT_OTP.getCode())
                                .businessErrorDescription(INCORRECT_OTP.getDescription())
                                .error(exp.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(ExpiredOtpException.class)
    public ResponseEntity<ExceptionResponse> handleException(ExpiredOtpException exp) {
        return ResponseEntity
                .status(UNAUTHORIZED)
                .body(
                        ExceptionResponse.builder()
                                .businessErrorCode(EXPIRED_OTP.getCode())
                                .businessErrorDescription(EXPIRED_OTP.getDescription())
                                .error(exp.getMessage())
                                .build()
                );
    }
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An internal server error occurred",
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public record ErrorResponse(int status, String message, LocalDateTime timestamp) {}
}
