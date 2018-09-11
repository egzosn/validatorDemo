package com.egzosn.validator.validator.constraintvalidators;



import com.egzosn.validator.validator.constraints.Phone;
import com.egzosn.validator.validator.plug.Regx;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created by ZaoSheng on 2015/6/25.
 */
public class PhoneValidator implements ConstraintValidator<Phone, String> {
    @Override
    public void initialize(Phone constraintAnnotation) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if ( value == null || value.length() == 0 ) {
            return true;
        }

        return value.matches(Regx.PHONE);

    }
}
