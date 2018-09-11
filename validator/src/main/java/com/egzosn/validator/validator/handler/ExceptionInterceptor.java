package com.egzosn.validator.validator.handler;

import com.egzosn.validator.error.ErrorCodeProperties;
import com.egzosn.validator.error.MsgCode;
import com.egzosn.validator.exception.CommonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.validator.internal.engine.NodeImpl;
import org.hibernate.validator.internal.engine.PathImpl;
import org.hibernate.validator.method.MethodConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

import static com.egzosn.validator.error.ErrorCode.ILLEGAL_PARAMETER;
import static com.egzosn.validator.error.ErrorCode.SERVER_ERROR;


/**
 * WEB异常处理器
 *
 * @author: egan
 * @email egzosn@gmail.com
 * @date 2016/11/30 10:31
 */
public class ExceptionInterceptor implements HandlerExceptionResolver {

    public final static Map<String, MsgCode> errorFields = new WeakHashMap();
    public final static ModelAndView NULL_VIEW =  new ModelAndView();

    public final static String CODE_KEY = "result";
    public final static String MESSAGE_KEY = "message";

    @Autowired
    private ErrorCodeProperties errorCode;

    ObjectMapper mapper = new ObjectMapper();

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

        String msgText = null;
        OutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();

            msgText = mapper.writeValueAsString(handleException(ex));
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            response.setHeader("cache-control", "no-cache");

            outputStreamWrite(outputStream, msgText);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != outputStream){
                try {
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        return NULL_VIEW;
    }

    private boolean outputStreamWrite(OutputStream outputStream, String text) {
        try {
            outputStream.write(text.getBytes("UTF-8"));
            outputStream.flush();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public Map<String, Object> handleException(Exception ex) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        if (ex instanceof BindException) {
            BindException be = (BindException) ex;
            Class<?> targetClass = be.getTarget().getClass();
            //获取所有的对象错误集
            List<FieldError> errors = be.getFieldErrors();
            return processFieldError(result, targetClass, errors);

        }
        if (ex instanceof MethodConstraintViolationException){
            //获取方法参数异常
            Set constraintViolations = ( (MethodConstraintViolationException) ex).getConstraintViolations();
            return constraintViolation(result, constraintViolations);
        }
        //约束违反异常 主要用于校验方法参数
        if (ex instanceof ConstraintViolationException) {
            ConstraintViolationException mcve = (ConstraintViolationException) ex;
            //获取校验不通过的异常集
            Set<ConstraintViolation<?>> constraintViolations = mcve.getConstraintViolations();
           return constraintViolation(result, constraintViolations);
        }
         /* // TODO 2016/11/16 10:47 author: egan  判断是否为字段式绑定异常
       if(ex instanceof MethodArgumentNotValidException){
                MethodArgumentNotValidException me = (MethodArgumentNotValidException) ex;
                BindingResult bindingResult = me.getBindingResult();
                return processFieldError(result, bindingResult.getTarget().getClass(), bindingResult.getFieldErrors());
         }*/

        if (ex instanceof CommonException) { //自定义异常
            Integer code = ((CommonException) ex).getCode();
            result.put(CODE_KEY, errorCode.getCode(code));
            result.put(MESSAGE_KEY, errorCode.getMessage(code));
            return result;
        }
        //非法参数,如：反序列化无法转换异常
        if (ex instanceof HttpMessageNotReadableException) {
            result.put(CODE_KEY, errorCode.getCode(ILLEGAL_PARAMETER));
            result.put(MESSAGE_KEY, errorCode.getMessage(ILLEGAL_PARAMETER));
            return result;
        }
       /*  if (ex instanceof MethodArgumentTypeMismatchException) { //方法参数类型不匹配异常
            result.put(CODE_KEY, errorCode.getCode(ILLEGAL_PARAMETER));
            result.put(MESSAGE_KEY, errorCode.getMessage(ILLEGAL_PARAMETER));
        } */
        throw ex;
    }

    private Map<String, Object> constraintViolation(Map<String, Object> result, Set<ConstraintViolation<?>> constraintViolations) throws NoSuchFieldException {

        for (ConstraintViolation<?> violation : constraintViolations) {
//                Object[] executableParameters = violation.getExecutableParameters();
            // TODO 2016/11/16 10:47 author: egan  violation.getMessage()主要为code(规范书写)
            //如 @NotNull(message = "1001")
            if (violation.getMessage().matches("\\d+")) {
                Integer code = Integer.valueOf(violation.getMessage());
                result.put(CODE_KEY, errorCode.getCode(code));
                String message = errorCode.getMessage(code);
                if (null != message) {
                    result.put(MESSAGE_KEY, message);
                }
                return result;
            }
            MsgCode code = errorFields.get(violation.getPropertyPath().toString());
            if (null != code) {
                result.put(CODE_KEY, errorCode.getCode(code.value()));
                String message = errorCode.getMessage(code.value());
                if (null == message) {
                    message = violation.getMessage();
                }
                result.put(MESSAGE_KEY, message);
                return result;
            }
            result.put(CODE_KEY, SERVER_ERROR);
            String message = null;
            if (violation.getPropertyPath() instanceof PathImpl) {
                PathImpl path = (PathImpl) violation.getPropertyPath();
                NodeImpl currentLeafNode = path.getLeafNode();
                code = violation.getLeafBean().getClass().getDeclaredField(currentLeafNode.getName()).getAnnotation(MsgCode.class);

                if (null != code) {
                    errorFields.put(violation.getPropertyPath().toString(), code);
                    result.put(CODE_KEY, errorCode.getCode(code.value()));
                    message = errorCode.getMessage(code.value());
                }
            }
            if (null == message) {
                message = violation.getMessage();
            }
            result.put(MESSAGE_KEY, message);
            return result;
        }
        result.put(CODE_KEY, ILLEGAL_PARAMETER);
        return result;
    }


    private Map<String, Object> processFieldError(Map<String, Object> result, Class<?> targetClass, List<FieldError> errors) throws NoSuchFieldException {
        for (FieldError error : errors) {
            String field = error.getField();
            String key = field + error.getCode();
            MsgCode msgCode = null;
            if (null == errorFields.get(key)) {
                String[] pfields = field.split("\\.");
                //判断是否为双层对象并进行切割获取字段对象的字段并抓取code
                if (pfields.length >= 2) {
                    String temp = pfields[0];
                    if (temp.contains("[")) {
                        temp = temp.substring(0, temp.indexOf("["));
                    }
                    Field declaredField = targetClass.getDeclaredField(temp);
                    Class clazz = declaredField.getType();
                    if (clazz.isAssignableFrom(List.class) || clazz.isAssignableFrom(Map.class)) {
                        Type genType = declaredField.getGenericType();
                        if (!(genType instanceof ParameterizedType)) {
                            clazz = Object.class;
                        } else {
                            Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
                            try {
                                clazz = (Class) params[0];
                            } catch (Exception e) {
                                try {
                                    clazz = (Class) ((ParameterizedType) params[0]).getRawType();
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                    }
                    msgCode = clazz.getDeclaredField(pfields[1]).getAnnotation(MsgCode.class);
                } else {
                    msgCode = targetClass.getDeclaredField(field).getAnnotation(MsgCode.class);
                }
                errorFields.put(key, msgCode);
            } else {
                msgCode = errorFields.get(key);
            }

            if (null != msgCode) {
                result.put(CODE_KEY, errorCode.getCode(msgCode.value()));
            }
            //根据code 在error_code.yml取值，如果未取到值则去默认的error.getDefaultMessage();
            String message = errorCode.getMessage(msgCode != null ? msgCode.value() : null);
            if (null == message) {
                message = error.getDefaultMessage();
            }
            result.put(MESSAGE_KEY, message);
            return result;
        }
        result.put(CODE_KEY, ILLEGAL_PARAMETER);
        return result;
    }
}