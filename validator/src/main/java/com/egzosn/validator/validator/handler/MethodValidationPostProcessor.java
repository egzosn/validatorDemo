//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.egzosn.validator.validator.handler;

import java.lang.annotation.Annotation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import com.egzosn.validator.validator.annotation.Validated;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

public class MethodValidationPostProcessor extends AbstractAdvisingBeanPostProcessor implements InitializingBean {
    private Class<? extends Annotation> validatedAnnotationType = Validated.class;
    private Validator validator ;//= ValidateUtils.validator;

    public MethodValidationPostProcessor() {
    }

    public void setValidatedAnnotationType(Class<? extends Annotation> validatedAnnotationType) {
        Assert.notNull(validatedAnnotationType, "\'validatedAnnotationType\' must not be null");
        this.validatedAnnotationType = validatedAnnotationType;
    }

    public void setValidator(Validator validator) {
        if(validator instanceof LocalValidatorFactoryBean) {
            this.validator = ((LocalValidatorFactoryBean)validator).getValidator();
        } else {
            this.validator = validator;
        }

    }

    public void setValidatorFactory(ValidatorFactory validatorFactory) {
        this.validator = validatorFactory.getValidator();
    }

    public void afterPropertiesSet() {
        AnnotationMatchingPointcut pointcut = new AnnotationMatchingPointcut(this.validatedAnnotationType, true);
        MethodValidationInterceptor advice = this.validator != null?new MethodValidationInterceptor(this.validator):new MethodValidationInterceptor();
        this.advisor = new DefaultPointcutAdvisor(pointcut, advice);
    }
}
