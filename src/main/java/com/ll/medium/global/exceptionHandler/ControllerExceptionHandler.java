package com.ll.medium.global.exceptionHandler;

import com.ll.medium.global.rq.Rq;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(annotations = Controller.class)
@RequiredArgsConstructor
public class ControllerExceptionHandler {
    private final Rq rq;

    @ExceptionHandler(RuntimeException.class)
    public String handleException(RuntimeException ex) {
        return rq.historyBack(ex.getMessage());
    }

    // 로그인 필요한 요청에 대한 예외 처리 (스프링 시큐리티가 처리하던 것)
    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDeniedException(AccessDeniedException ex) {
        return "redirect:/member/login";
    }
}
