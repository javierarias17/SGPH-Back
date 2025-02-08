package co.edu.unicauca.sgph.asignatura.infrastructure.input.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import co.edu.unicauca.sgph.asignatura.aplication.output.GestionarAsignaturaGatewayIntPort;
import co.edu.unicauca.sgph.asignatura.infrastructure.input.DTORequest.AsignaturaInDTO;
import co.edu.unicauca.sgph.asignatura.infrastructure.input.DTOResponse.AsignaturaOutDTO;

public class ExisteOidAsignaturaValidation implements ConstraintValidator<ExisteOidAsignatura, AsignaturaInDTO>{

	@Autowired
	private GestionarAsignaturaGatewayIntPort gestionarAsignaturaGatewayIntPort;
	
	@Override
	public boolean isValid(AsignaturaInDTO value, ConstraintValidatorContext context) {
		// Si el OID es nulo o está vacío, dejamos que otros validadores se encarguen de ello.
        if (value.getOID() == null || value.getOID().trim().isEmpty()) {
            return true;
        }
        
        // Modo actualización: si el DTO tiene un id, consultamos la asignatura original
        if (value.getIdAsignatura() != null) {
            AsignaturaOutDTO asignaturaOriginal = gestionarAsignaturaGatewayIntPort
                    .obtenerAsignaturaPorId(value.getIdAsignatura());
            if (asignaturaOriginal != null) {
                // Si el OID no ha cambiado, se permite la validación
                if (asignaturaOriginal.getOID().equalsIgnoreCase(value.getOID())) {
                    return true;
                } else {
                    // Si se modificó el OID, se verifica que no exista otro registro con ese OID
                    return !gestionarAsignaturaGatewayIntPort.existeOidAsignatura(value.getOID());
                }
            } else {
                // Si no se encuentra el registro original, se procede a validar la unicidad
                return !gestionarAsignaturaGatewayIntPort.existeOidAsignatura(value.getOID());
            }
        } else {
            // Modo creación: se verifica que no exista ningún registro con ese OID
            return !gestionarAsignaturaGatewayIntPort.existeOidAsignatura(value.getOID());
        }
    }

}
