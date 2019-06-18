package com.siemens.krawal.krawalcloudmanager.aspect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.siemens.krawal.krawalcloudmanager.exception.DBException;
import com.siemens.krawal.krawalcloudmanager.exception.NetworkException;
import com.siemens.krawal.krawalcloudmanager.exception.RequestModificationException;
import com.siemens.krawal.krawalcloudmanager.exception.RequestNotCompleteException;
import com.siemens.krawal.krawalcloudmanager.exception.ValidationException;
import com.siemens.krawal.krawalcloudmanager.model.response.ResultDTO;

@ControllerAdvice
public class ExceptionAdvise {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionAdvise.class);

	@ExceptionHandler(ValidationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ResponseEntity<ResultDTO> handleValidationException(final ValidationException exception) {

		ResultDTO result = constructResultDTO(exception);
		result.setCode(HttpStatus.BAD_REQUEST.toString());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);

	}

	@ExceptionHandler(DBException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public ResponseEntity<ResultDTO> handleValidationException(final DBException exception) {

		ResultDTO result = constructResultDTO(exception);
		result.setCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);

	}

	@ExceptionHandler(RequestNotCompleteException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ResponseEntity<ResultDTO> handleValidationException(final RequestNotCompleteException exception) {

		ResultDTO result = constructResultDTO(exception);
		result.setCode(HttpStatus.BAD_REQUEST.toString());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);

	}

	@ExceptionHandler(RequestModificationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ResponseEntity<ResultDTO> handleValidationException(final RequestModificationException exception) {

		ResultDTO result = constructResultDTO(exception);
		result.setCode(HttpStatus.BAD_REQUEST.toString());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);

	}

	@ExceptionHandler(NetworkException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public ResponseEntity<ResultDTO> handleValidationException(final NetworkException exception) {

		ResultDTO result = constructResultDTO(exception);
		result.setCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
		LOGGER.error("error:" + exception.getCause());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public ResponseEntity<ResultDTO> handleValidationException(final Exception exception) {

		ResultDTO result = constructResultDTO(exception);
		result.setCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
		if (null == exception.getMessage()) {
			result.setMessage("Unknown Error!!");
		}
		LOGGER.error("error:" + exception.getCause());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
	}

	private ResultDTO constructResultDTO(Throwable t) {

		ResultDTO result = new ResultDTO();
		result.setMessage(t.getMessage());
		return result;
	}

}
