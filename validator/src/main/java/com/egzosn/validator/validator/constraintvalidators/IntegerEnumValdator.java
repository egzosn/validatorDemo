package com.egzosn.validator.validator.constraintvalidators;



import com.egzosn.validator.validator.constraints.IntegerEnum;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import java.util.Arrays;


/**
 * Created by ZaoSheng on 2016/11/16.
 */
public class IntegerEnumValdator implements ConstraintValidator<IntegerEnum, Integer> {
    private int[] type;
    @Override
    public void initialize(IntegerEnum constraintAnnotation) {
        this.type = constraintAnnotation.type();
        Arrays.sort(this.type);
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if(value == null  || type == null || type.length == 0){
            return true;
        }
        return Arrays.binarySearch(type, value) >= 0;
    }

}