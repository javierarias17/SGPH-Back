package co.edu.unicauca.sgph.asignatura.infrastructure.input.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import co.edu.unicauca.sgph.asignatura.aplication.output.GestionarAsignaturaGatewayIntPort;
import co.edu.unicauca.sgph.asignatura.domain.model.Asignatura;
import co.edu.unicauca.sgph.asignatura.infrastructure.input.DTORequest.AsignaturaInDTO;
import co.edu.unicauca.sgph.asignatura.infrastructure.input.DTOResponse.AsignaturaOutDTO;

@Component
public class ExisteCodigoAsignaturaValidation implements ConstraintValidator<ExisteCodigoAsignatura, AsignaturaInDTO>{

	@Autowired
	private GestionarAsignaturaGatewayIntPort gestionarAsignaturaGatewayIntPort;
	
	@Override
	public boolean isValid(AsignaturaInDTO value, ConstraintValidatorContext context) {
		// Si el código está nulo o vacío, se delega a otros validadores.
        if (value.getCodigoAsignatura() == null || value.getCodigoAsignatura().trim().isEmpty()) {
            return true;
        }
        
        if (value.getIdAsignatura() != null) {
            AsignaturaOutDTO asignaturaOriginal = gestionarAsignaturaGatewayIntPort.obtenerAsignaturaPorId(value.getIdAsignatura());
            if (asignaturaOriginal != null) {
                if (asignaturaOriginal.getCodigoAsignatura().equalsIgnoreCase(value.getCodigoAsignatura())) {
                    return true;
                } else {
                    return !gestionarAsignaturaGatewayIntPort.existeCodigoAsignatura(value.getCodigoAsignatura());
                }
            } else {
                return !gestionarAsignaturaGatewayIntPort.existeCodigoAsignatura(value.getCodigoAsignatura());
            }
        } else {
            return !gestionarAsignaturaGatewayIntPort.existeCodigoAsignatura(value.getCodigoAsignatura());
        }

	}
}
