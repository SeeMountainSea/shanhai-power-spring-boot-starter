package com.wangshanhai.power.examples.config;

import com.wangshanhai.power.exceptions.ShanHaiPowerException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 统一异常处理
 * @author Shmily
 */
@RestControllerAdvice
public class ErrorConfig {

    @ExceptionHandler(value = ShanHaiPowerException.class)
    public ResponseEntity<?> shanHaiPowerErrorHandler(Exception e) {
        Map<String, Object> resp=new HashMap<>();
        resp.put("code",((ShanHaiPowerException)e).getCode());
        resp.put("message",e.getMessage());
        HttpHeaders headers = new HttpHeaders();
        MediaType mediaType = new MediaType("application","json", StandardCharsets.UTF_8);
        headers.setContentType(mediaType);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).headers(headers).body(resp);
    }
}
