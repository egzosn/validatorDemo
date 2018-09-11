package com.egzosn.validator.validator.constraintvalidators;



import com.egzosn.validator.validator.constraints.RepositoryData;
import com.egzosn.validator.validator.plug.Restriction;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *  Created by ZaoSheng on 2018/06/13.
 *  使用这个校验 仓储数据(执行指定方法)，对规则进行匹配
 */
public class RepositoryDataValdator implements ConstraintValidator<RepositoryData, Object> {

    @Autowired
    private AutowireCapableBeanFactory spring;
    /**
     * 指定的类
     */
    private Class<?> action;
    /**
     * 指定类的方法
     */
    private String method;

    /**
     * 约束
     */
    private Restriction restriction;
    /**
     * 预期值
     */
    private String[] expect;

    @Override
    public void initialize(RepositoryData ann) {
        this.action = ann.clazz();
        this.method = ann.method();
        this.restriction = ann.restriction();
        this.expect = ann.expect();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (null == value){
            return true;
        }

        Method method = BeanUtils.findMethod(action, this.method, value.getClass());

        try {
            Object invoke = method.invoke(spring.getBean(action), value);
            return restriction.match(invoke, expect);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }

}