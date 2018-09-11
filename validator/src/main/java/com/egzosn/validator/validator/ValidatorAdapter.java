/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.egzosn.validator.validator;

import org.springframework.beans.NotReadablePropertyException;
import org.springframework.validation.*;
import org.springframework.validation.beanvalidation.CustomValidatorBean;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import javax.validation.ConstraintViolation;
import javax.validation.metadata.ConstraintDescriptor;
import java.util.Set;

/**
 * Adapter that takes a JSR-303 {@code javax.validator.Validator}
 * and exposes it as a Spring {@link Validator}
 * while also exposing the original JSR-303 Validator interface itself.
 *
 * <p>Can be used as a programmatic wrapper. Also serves as base class for
 * {@link CustomValidatorBean} and {@link LocalValidatorFactoryBean}.
 *
 * @author Juergen Hoeller
 * @since 3.0
 */
public class ValidatorAdapter extends SpringValidatorAdapter {



	/**
	 * Create a new SpringValidatorAdapter for the given JSR-303 Validator.
	 * @param targetValidator the JSR-303 Validator to wrap
	 */
	public ValidatorAdapter(javax.validation.Validator targetValidator) {
		super(targetValidator);
	}


	public void validateProperty(Object object, String propertyName, Errors errors) {
		Set<ConstraintViolation<Object>> violations = super.validateProperty(object, propertyName);
		processConstraintViolations(violations, errors);
	}

	/**
	 * Process the given JSR-303 ConstraintViolations, adding corresponding errors to
	 * the provided Spring {@link Errors} object.
	 * @param violations the JSR-303 ConstraintViolation results
	 * @param errors the Spring errors object to register to
	 */
	protected void processConstraintViolations(Set<ConstraintViolation<Object>> violations, Errors errors) {
		for (ConstraintViolation<Object> violation : violations) {
			String field = determineField(violation);
			FieldError fieldError = errors.getFieldError(field);
			if (fieldError == null || !fieldError.isBindingFailure()) {
				try {
					ConstraintDescriptor<?> cd = violation.getConstraintDescriptor();
					String errorCode = determineErrorCode(cd);
					Object[] errorArgs = getArgumentsForConstraint(errors.getObjectName(), field, cd);
					if (errors instanceof BindingResult) {
						// Can do custom FieldError registration with invalid value from ConstraintViolation,
						// as necessary for Hibernate Validator compatibility (non-indexed set path in field)
						BindingResult bindingResult = (BindingResult) errors;
						String nestedField = bindingResult.getNestedPath() + field;
						if ("".equals(nestedField)) {
							String[] errorCodes = bindingResult.resolveMessageCodes(errorCode, bindingResult.getObjectName());
							bindingResult.addError(new ObjectError(
									errors.getObjectName(), errorCodes, errorArgs, violation.getMessage()));
						}
						else {
							Object rejectedValue = getRejectedValue(field, violation, bindingResult);
							String[] errorCodes = bindingResult.resolveMessageCodes(errorCode, field);
							bindingResult.addError(new FieldError(
									errors.getObjectName(), nestedField, rejectedValue, false,
									errorCodes, errorArgs, violation.getMessage()));
						}
					}
					else {
						// got no BindingResult - can only do standard rejectValue call
						// with automatic extraction of the current field value
						errors.rejectValue(field, errorCode, errorArgs, violation.getMessage());
					}
				}
				catch (NotReadablePropertyException ex) {
					throw new IllegalStateException("JSR-303 validated property '" + field +
							"' does not have a corresponding accessor for Spring data binding - " +
							"check your DataBinder's configuration (bean property versus direct field access)", ex);
				}
			}
		}
	}
	/**
	 * Determine a field for the given constraint violation.
	 * <p>The default implementation returns the stringified property path.
	 * @param violation the current JSR-303 ConstraintViolation
	 * @return the Spring-reported field (for use with {@link Errors})
	 * @since 4.2
	 * @see ConstraintViolation#getPropertyPath()
	 * @see FieldError#getField()
	 */
	protected String determineField(ConstraintViolation<Object> violation) {
		String path = violation.getPropertyPath().toString();
		int elementIndex = path.indexOf(".<");
		return (elementIndex >= 0 ? path.substring(0, elementIndex) : path);
	}
	/**
	 * Determine a Spring-reported error code for the given constraint descriptor.
	 * <p>The default implementation returns the simple class name of the descriptor's
	 * annotation type. Note that the configured
	 * {@link MessageCodesResolver} will automatically
	 * generate error code variations which include the object name and the field name.
	 * @param descriptor the JSR-303 ConstraintDescriptor for the current violation
	 * @return a corresponding error code (for use with {@link Errors})
	 * @since 4.2
	 * @see ConstraintDescriptor#getAnnotation()
	 * @see MessageCodesResolver
	 */
	protected String determineErrorCode(ConstraintDescriptor<?> descriptor) {
		return descriptor.getAnnotation().annotationType().getSimpleName();
	}

    /**
     * Extract the rejected value behind the given constraint violation,
     * for exposure through the Spring errors representation.
     * @param field the field that caused the binding error
     * @param violation the corresponding JSR-303 ConstraintViolation
     * @param bindingResult a Spring BindingResult for the backing object
     * which contains the current field's value
     * @return the invalid value to expose as part of the field error
     * @since 4.2
     * @see ConstraintViolation#getInvalidValue()
     * @see FieldError#getRejectedValue()
     */
    protected Object getRejectedValue(String field, ConstraintViolation<Object> violation, BindingResult bindingResult) {
        Object invalidValue = violation.getInvalidValue();
        if (!"".equals(field) && (invalidValue == violation.getLeafBean() ||
                (!field.contains("[]") && (field.contains("[") || field.contains("."))))) {
            // Possibly a bean constraint with property path: retrieve the actual property value.
            // However, explicitly avoid this for "address[]" style paths that we can't handle.
            invalidValue = bindingResult.getRawFieldValue(field);
        }
        return invalidValue;
    }
}
