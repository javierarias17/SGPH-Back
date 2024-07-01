package co.edu.unicauca.sgph.common.infrastructure.input.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import co.edu.unicauca.sgph.common.aplication.output.GestionarPersonaGatewayIntPort;
import co.edu.unicauca.sgph.common.dto.PersonaInDTOAbstract;

@Component
public class ExistsByEmailValidation implements ConstraintValidator<ExistsByEmail, PersonaInDTOAbstract> {

	@Autowired
	private GestionarPersonaGatewayIntPort gestionarPersonaGatewayIntPort;

	@Override
	public boolean isValid(PersonaInDTOAbstract personaInDTOAbstract, ConstraintValidatorContext context) {
		return !this.gestionarPersonaGatewayIntPort.existsPersonaByEmail(personaInDTOAbstract.getEmail(),
				personaInDTOAbstract.getIdPersona());
	}
}