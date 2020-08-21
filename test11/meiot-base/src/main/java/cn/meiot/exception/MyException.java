//package cn.meiot.exception;
//
//import cn.meiot.entity.vo.Result;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//import org.springframework.web.bind.MethodArgumentNotValidException;
///**
// * @Package cn.meiot.exception
// * @Description:
// * @author: 武有
// * @date: 2019/11/22 18:36
// * @Copyright: www.spacecg.cn
// */
//
//@RestControllerAdvice
//@Slf4j
//public class MyException {
//
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public Object parameterValidationFailed(MethodArgumentNotValidException  exception) {
//        BindingResult bindingResult = exception.getBindingResult();
//        if (bindingResult.hasErrors()) {
//            Result result = Result.getDefaultFalse();
//            result.setMsg(bindingResult.getFieldError().getDefaultMessage());
//            return result;
//        }
//
//        return Result.getDefaultFalse();
//    }
//
//}
