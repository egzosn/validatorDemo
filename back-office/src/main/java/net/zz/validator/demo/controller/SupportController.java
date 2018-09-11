package net.zz.validator.demo.controller;

import com.egzosn.validator.error.MsgCode;
import org.hibernate.validator.method.MethodConstraintViolation;
import org.hibernate.validator.method.MethodConstraintViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.annotation.Annotation;
import java.util.*;

public abstract class SupportController {


    protected Map<String, Object> successData() {
        Map<String, Object> data = newData();
        data.put("MZCode", 0);
        return data;
    }

    /**
     * @return
     */
    protected Map<String, Object> successData(String key, Object result) {
        Map<String, Object> data = newData();
        data.put("MZCode", 0);
        data.put(key, result);
        return data;
    }


    protected Map<String, Object> newData() {
        Map<String, Object> data = new HashMap<String, Object>();
        return data;
    }




}
