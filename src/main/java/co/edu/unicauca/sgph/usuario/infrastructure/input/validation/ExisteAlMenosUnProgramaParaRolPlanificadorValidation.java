package co.edu.unicauca.sgph.usuario.infrastructure.input.validation;

import java.util.Objects;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.stereotype.Component;

import co.edu.unicauca.sgph.usuario.infrastructure.input.DTORequest.UsuarioInDTO;

@Component
public class ExisteAlMenosUnProgramaParaRolPlanificadorValidation implements ConstraintValidator<ExisteAlMenosUnProgramaParaRolPlanificador, UsuarioInDTO> {

	@Override
	public boolean isValid(UsuarioInDTO usuarioInDTO, ConstraintValidatorContext context) {

		Long ROLE_PLANIFICADOR = 2L;
		boolean isValid = true;

		if (usuarioInDTO.getLstIdRol().contains(ROLE_PLANIFICADOR)
				&& (Objects.isNull(usuarioInDTO.getLstIdPrograma()) || usuarioInDTO.getLstIdPrograma().isEmpty())) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("{usuario.existe.al.menos.un.programa.para.rol.planificador}")
					.addConstraintViolation();
			isValid = false;
		} else if (!usuarioInDTO.getLstIdRol().contains(ROLE_PLANIFICADOR)
				&& Objects.nonNull(usuarioInDTO.getLstIdPrograma()) && !usuarioInDTO.getLstIdPrograma().isEmpty()) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("{usuario.existe.programa.para.rol.diferente.de.planificador}")
					.addConstraintViolation();
			isValid = false;
		}

		return isValid;
	}
}
