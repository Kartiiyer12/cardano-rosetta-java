package org.cardanofoundation.rosetta.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = Word64TypeValidator.class)
@Documented
public @interface Word64Type {

  String message() default "The value must be Word64Type";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
