package com.egzosn.validator.validator.constraintvalidators;





import com.egzosn.validator.validator.constraints.QQ;
import com.egzosn.validator.validator.plug.Regx;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


/**
 * Created by ZaoSheng on 2015/5/9.
 */
public class QQValidator implements ConstraintValidator<QQ, String> {
    private String message;
    private int errorCode;

    public void initialize(QQ annotation) {
        this.message = annotation.message();
        this.errorCode = annotation.erroCode();

    }

    public boolean isValid(String value, ConstraintValidatorContext context) {
        if ( value == null || value.length() == 0 ) {
            return true;
        }

        return value.matches(Regx.QQ) || value.matches(Regx.PHONE);


    }

}
