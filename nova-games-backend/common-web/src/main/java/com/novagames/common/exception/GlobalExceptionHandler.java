package com.novagames.common.exception;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.dao.DataIntegrityViolationException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.*;
import java.time.Instant;
import org.slf4j.*;
@RestControllerAdvice public class GlobalExceptionHandler {
    private static final Logger log=LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(BusinessException.class) ResponseEntity<ApiError> business(BusinessException e,HttpServletRequest r) {
        return response(e.status,e.code,e.getMessage(),r);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<ApiError> validation(MethodArgumentNotValidException e,HttpServletRequest r) {
        Map<String,String> fields=new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors().forEach(f->fields.putIfAbsent(f.getField(),f.getDefaultMessage()));
        return ResponseEntity.badRequest().body(new ApiError(Instant.now(),400,"VALIDATION_ERROR","Revisa los campos ingresados",r.getRequestURI(),fields));
    }
    @ExceptionHandler({HttpMessageNotReadableException.class,MethodArgumentTypeMismatchException.class,jakarta.validation.ConstraintViolationException.class,org.springframework.web.bind.MissingRequestHeaderException.class,org.springframework.web.bind.MissingServletRequestParameterException.class}) ResponseEntity<ApiError> bad(Exception e,HttpServletRequest r) {
        return response(400,"INVALID_REQUEST","Solicitud inválida",r);
    }
    @ExceptionHandler(DataIntegrityViolationException.class) ResponseEntity<ApiError> conflict(Exception e,HttpServletRequest r) {
        return response(409,"DATA_CONFLICT","Los datos entran en conflicto con un registro existente",r);
    }
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class) ResponseEntity<ApiError> denied(Exception e,HttpServletRequest r) {
        return response(403,"ACCESS_DENIED","No tienes permisos para esta acción",r);
    }
    @ExceptionHandler(org.springframework.web.servlet.resource.NoResourceFoundException.class) ResponseEntity<ApiError> notFound(Exception e,HttpServletRequest r) {
        return response(404,"NOT_FOUND","Recurso no encontrado",r);
    }
    @ExceptionHandler(org.springframework.web.HttpRequestMethodNotSupportedException.class) ResponseEntity<ApiError> method(Exception e,HttpServletRequest r) {
        return response(405,"METHOD_NOT_ALLOWED","Método HTTP no permitido",r);
    }
    @ExceptionHandler(org.springframework.web.HttpMediaTypeNotSupportedException.class) ResponseEntity<ApiError> media(Exception e,HttpServletRequest r) {
        return response(415,"UNSUPPORTED_MEDIA_TYPE","Formato de contenido no admitido",r);
    }
    @ExceptionHandler(Exception.class) ResponseEntity<ApiError> unexpected(Exception e,HttpServletRequest r) {
        log.error("Error interno {}",r.getRequestURI(),e);
        return response(500,"INTERNAL_ERROR","Ocurrió un error inesperado",r);
    }
    private ResponseEntity<ApiError> response(int status,String code,String msg,HttpServletRequest r) {
        return ResponseEntity.status(status).body(ApiError.of(status,code,msg,r.getRequestURI()));
    }
}
