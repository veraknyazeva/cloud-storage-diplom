package ru.netology.cloudstoragediplom.controller.advice;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.netology.cloudstoragediplom.dto.CommonErrorResponse;
import ru.netology.cloudstoragediplom.exeption.FileConstraintException;
import ru.netology.cloudstoragediplom.exeption.FileNotFoundException;
import ru.netology.cloudstoragediplom.exeption.FileNotReadableException;
import ru.netology.cloudstoragediplom.exeption.LoginFailedException;

@RestControllerAdvice
public class FileControllerAdvice {

    private static final int USER_NOT_FOUND_ERROR_ID = 888;

    @ExceptionHandler(FileNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonErrorResponse handleFileNotReadableException(FileNotReadableException exception) {
        String message = exception.getMessage();
        int id = exception.getId();
        CommonErrorResponse response = new CommonErrorResponse();
        response.setId(id);
        response.setMessage(message);
        return response;
    }

    @ExceptionHandler(FileConstraintException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonErrorResponse handleFileConstraintException(FileConstraintException ex) {
        String message = ex.getMessage();
        int id = ex.getId();
        CommonErrorResponse response = new CommonErrorResponse();
        response.setMessage(message);
        response.setId(id);
        return response;
    }

    @ExceptionHandler(FileNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonErrorResponse handleFileNotFoundException(FileNotFoundException ex) {
        String message = ex.getMessage();
        int id = ex.getId();
        CommonErrorResponse response = new CommonErrorResponse();
        response.setMessage(message);
        response.setId(id);
        return response;
    }

    @ExceptionHandler(LoginFailedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonErrorResponse handleLoginFailedException(LoginFailedException ex) {
        String message = ex.getMessage();
        int id = ex.getId();
        CommonErrorResponse response = new CommonErrorResponse();
        response.setMessage(message);
        response.setId(id);
        return response;
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonErrorResponse handleUsernameNotFoundException(UsernameNotFoundException ex) {
        String message = ex.getMessage();
        CommonErrorResponse response = new CommonErrorResponse();
        response.setMessage(message);
        response.setId(USER_NOT_FOUND_ERROR_ID);
        return response;
    }
}
