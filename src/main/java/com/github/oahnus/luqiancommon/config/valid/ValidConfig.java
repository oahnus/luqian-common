package com.github.oahnus.luqiancommon.config.valid;

import org.hibernate.validator.HibernateValidator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;


/**
 * Created by oahnus on 2020-05-05
 * 15:47.
 */
@ConditionalOnClass(HibernateValidator.class)
@Configuration
public class ValidConfig {
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        MethodValidationPostProcessor processor = new MethodValidationPostProcessor();
        processor.setValidator(validator());
        return processor;
    }

    public Validator validator() {
        ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
                .configure()
                .failFast(true)
                .buildValidatorFactory();
        return validatorFactory.getValidator();
    }
}
