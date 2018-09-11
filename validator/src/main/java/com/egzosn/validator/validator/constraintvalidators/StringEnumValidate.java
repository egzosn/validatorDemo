package com.egzosn.validator.validator.constraintvalidators;




import com.egzosn.validator.validator.constraints.StringEnum;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;


/**
 * Created by ZaoSheng on 15-1-26.
 */
public class StringEnumValidate implements ConstraintValidator<StringEnum, String> {
    private String[] type;

    private boolean allowNull = false;

    @Override
    public void initialize(StringEnum stringEnumValidate) {
        this.type = stringEnumValidate.type();
        Arrays.sort(this.type);
        this.allowNull = stringEnumValidate.allowNull();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (type == null || type.length == 0 || (value == null && allowNull)) {
            return true;
        }
        return Arrays.binarySearch(type, value) >= 0;
    }


}
