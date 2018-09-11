package com.egzosn.validator.validator.constraints;


import com.egzosn.validator.validator.constraintvalidators.StringArrayEnumValidateImpl;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by ZaoSheng on 15-1-26.
 * 使用注解这个注解约束字段时，字段是数组，可以有多个值，但每个值都必须约束，
 * 如： type = {"wx","qq","wb"} ，那么被约束的 String[] filed; 可以是这三个值里面的多个
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = StringArrayEnumValidateImpl.class)
public @interface StringArrayEnumValidate {

    String[] type() default {};

    int arrLength() default  0;

    boolean allowEmpty() default false;

    String message() default "validate failure";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
