package com.egzosn.validator.validator.constraints;



import com.egzosn.validator.validator.constraintvalidators.RepositoryDataValdator;
import com.egzosn.validator.validator.plug.Restriction;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by ZaoSheng on 2018/06/13.
 *  使用这个注解进行校验仓储数据(执行指定方法)，对规则进行匹配
 */
@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = RepositoryDataValdator.class)
public @interface RepositoryData {



    /**
     * 指定的类
     * @return 类
     */
    Class<?> clazz();

    /**
     * 指定类的方法
     * @return 方法
     */
    String method();

    /**
     * 约束
     * @return 约束
     */
    Restriction restriction() default Restriction.NOT_NULL;

    /**
     * 预期值
     * @return 预期值
     */
    String[] expect() default "";



    String message() default "validate failure";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
