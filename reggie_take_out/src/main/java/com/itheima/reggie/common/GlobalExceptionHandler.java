package com.itheima.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;

//全局异常处理
@Slf4j
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
public class GlobalExceptionHandler {

    /**
     * sql唯一索引，异常处理
     *
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException exception) {
        log.error(exception.getMessage());

        if (exception.getMessage().contains("Duplicate entry")) {
            String[] split = exception.getMessage().split(" ");
            String msg = split[2]+"已存在";
            return R.error(msg);
        }
        return R.error("未知错误");
    }
}
