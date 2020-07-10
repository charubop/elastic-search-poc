package com.example.demo.controller;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.example.demo.constants.ExceptionConstants;
import com.example.demo.dto.ErrorDetailsDto;
import com.example.demo.exception.ResourceConflictException;
import com.example.demo.exception.ServiceException;

@ControllerAdvice
@RestController
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		List<String> validationErrorList = ex.getBindingResult().getFieldErrors().stream()
				.map(FieldError::getDefaultMessage).collect(Collectors.toList());

		ErrorDetailsDto detailsDto = new ErrorDetailsDto(new Date(), validationErrorList.toString(),
				HttpStatus.BAD_REQUEST.value());

		return new ResponseEntity<Object>(detailsDto, HttpStatus.BAD_REQUEST);
	}

	@Override
	protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		ErrorDetailsDto errorDetails = new ErrorDetailsDto(new Date(), ExceptionConstants.PARAMETER_MISSING,
				HttpStatus.BAD_REQUEST.value());

		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ServiceException.class)
	public final ResponseEntity<ErrorDetailsDto> handleServiceException(ServiceException ex, WebRequest request) {
		ErrorDetailsDto errorDetailsDto = new ErrorDetailsDto(new Date(), ex.getMessage(),
				HttpStatus.INTERNAL_SERVER_ERROR.value());
		return new ResponseEntity<>(errorDetailsDto, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(ResourceConflictException.class)
	public final ResponseEntity<ErrorDetailsDto> handleResourceConflictException(ResourceConflictException ex,
			WebRequest request) {
		ErrorDetailsDto errorDetailsDto = new ErrorDetailsDto(new Date(), ex.getMessage(), HttpStatus.CONFLICT.value());
		return new ResponseEntity<>(errorDetailsDto, HttpStatus.CONFLICT);
	}

}
