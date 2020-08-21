package cn.meiot.exception;

import cn.meiot.common.ErrorCode;
import cn.meiot.entity.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.List;
import java.util.Set;

@Slf4j
@RestControllerAdvice
public class CtrlExceptionHandler {

    private static final Result result = Result.getDefaultFalse();

    @ExceptionHandler(value = Exception.class)
    public Object errorHandler(HttpServletRequest request, HttpServletResponse response, Exception e) {
        log.error("系统错误==========>{}",e);
        //ErrorCode.SYSTEM_ERROR
        result.setMsg(ErrorCode.SYSTEM_ERROR);
        return result;
    }

    /**
     *  校验错误拦截处理
     *
     * @param exception 错误信息集合
     * @return 错误信息
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object validationBodyException(MethodArgumentNotValidException exception){
        BindingResult bindingResult = exception.getBindingResult();
        if (bindingResult.hasErrors()) {
            return Result.faild(bindingResult.getFieldError().getDefaultMessage());
        }
        return result;
    }

    /**
     * 400 - Bad Request
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Object handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        e.printStackTrace();
        log.error("缺少请求参数==========>{}",e);
        result.setMsg(ErrorCode.MISSING_REQUEST_PARAMETERS);
        return result;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public Object handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        e.printStackTrace();
        log.error("参数解析失败==========>{}",e);
        result.setMsg(ErrorCode.PARAMETER_RESOLUTION_FAILED);
        return result;
    }


    /**
     * 400 - Bad Request
     */
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public Object handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
//        e.printStackTrace();
//        log.info("参数验证失败==========>{}",e.getMessage());
//        result.setMsg(ErrorCode.PARAMETER_VALIDATION_FAILED);
//        return result;
//    }

    /**
     * 400 - Bad Request
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public Object handleBindException(BindException e) {
        e.printStackTrace();
        log.error("参数绑定失败===========>{}",e);
        BindingResult r = e.getBindingResult();
        FieldError error = r.getFieldError();
        String field = error.getField();
        String code = error.getDefaultMessage();
        String message = String.format("%s:%s", field, code);
        log.info("参数:{}",message);
        result.setMsg(ErrorCode.PARAMETER_BINDING_FAILED);
        return result;
    }


    /**
     * 400 - Bad Request
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public Object handleServiceException(ConstraintViolationException e) {
        e.printStackTrace();
        log.error("参数验证失败==========>{}",e);
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        ConstraintViolation<?> violation = violations.iterator().next();
        String message = violation.getMessage();
        log.info("参数:{}",message);
        result.setMsg(ErrorCode.PARAMETER_VAILD_FAILED);
        return result;
    }

    /**
     * 400 - Bad Request
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidationException.class)
    public Object handleValidationException(ValidationException e) {
        e.printStackTrace();
        log.error("参数验证失败==========>{}",e);
        result.setMsg(ErrorCode.PARAMETER_VAILD_FAILED );
        return result;
    }

    /**
     * 404 - Not Found
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoHandlerFoundException.class)
    public Object noHandlerFoundException(NoHandlerFoundException e) {
        e.printStackTrace();
        log.error("Not Found==========>{}",e);
        result.setMsg(ErrorCode.NOT_FOUND);
        return result;
    }


    /**
     * 405 - Method Not Allowed
     */
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Object handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        e.printStackTrace();
        log.error("不支持当前请求方法==========>{}",e);
        result.setMsg(ErrorCode.THE_CURRENT_REQUEST_METHOD_IS_NOT_SUPPORTED);
        return result;

    }

    /**
     * 415 - Unsupported Media Type
     */
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public Object handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        e.printStackTrace();
        log.error("不支持当前参数类型==========>{}",e);
        result.setMsg(ErrorCode.CURRENT_PARAMETER_TYPES_ARE_NOT_SUPPORTED);
        return  result;
    }

    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler(DuplicateKeyException.class)
    public Object handleDuplicateKeyException(DuplicateKeyException e) {
        e.printStackTrace();
        log.error("违反数据库约束==========>{}",e);
        result.setMsg(ErrorCode.VIOLATION_OF_DATABASE_CONSTRAINTS);
        return  result;
    }
    /**
     * 业务层需要自己声明异常的情况
     */
    @ExceptionHandler(MyServiceException.class)
    public Object handleServiceException(MyServiceException e) {
        e.printStackTrace();
        log.error("==========>{}",e);
        if(StringUtils.isEmpty(e.getCode())){
            e.setCode("-1");
        }
        return Result.faild(e.getCode(),e.getExceptMsg(),e.getArgs());
    }


    /**
     * 业务层需要自己声明异常的情况
     */
    @ExceptionHandler(MyTokenExcption.class)
    public Object handleMyTokenExcption(MyTokenExcption e) {
        e.printStackTrace();
        log.error("==========>{}",e);
        result.setCode("-1");
        result.setMsg(e.getCode());
        return result;
    }

    /**
     * 操作数据或库出现异常
     */
    /*@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(DataDoException.class)
    public String handleException(DataDoException e) {
        logger.error("操作数据库出现异常:", e);
        return "操作数据库出现异常：字段重复、有外键关联等";
    }*/
}
