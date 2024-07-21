package co.edu.unicauca.sgph.persona.infrastructure.input.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ExistePersonaPorIdentificacionValidation.class)
public @interface ExistePersonaPorIdentificacion {
	String message() default "{persona.existe.persona.por.identificacion}";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}