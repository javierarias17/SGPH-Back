package co.edu.unicauca.sgph.espaciofisico.infrastructura.input.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import co.edu.unicauca.sgph.espaciofisico.aplication.output.GestionarEspacioFisicoGatewayIntPort;
import co.edu.unicauca.sgph.espaciofisico.domain.model.EspacioFisico;
import co.edu.unicauca.sgph.espaciofisico.infrastructure.input.DTORequest.EspacioFisicoInDTO;

@Component
public class ExisteOidEspacioFisicoValidation implements ConstraintValidator<ExisteOidEspacioFisico, EspacioFisicoInDTO>{

	@Autowired
	private GestionarEspacioFisicoGatewayIntPort gestionarEspacioFisicoGatewayIntPort;
	
	@Override
	public boolean isValid(EspacioFisicoInDTO value, ConstraintValidatorContext context) {
		// Si el OID es nulo o vacío, dejamos que otros validadores se encarguen de ello.
        if (value.getOID() == null || value.getOID().trim().isEmpty()) {
            return true;
        }
        
        // Si estamos en modo actualización (el DTO tiene un id)
        if (value.getIdEspacioFisico() != null) {
            // Se consulta el espacio físico original usando el id
            EspacioFisico espacioOriginal = gestionarEspacioFisicoGatewayIntPort
                    .consultarEspacioFisicoPorIdEspacioFisico(value.getIdEspacioFisico());
            if (espacioOriginal != null) {
                // Si el OID no ha cambiado, se permite la validación
                if (espacioOriginal.getOID().equalsIgnoreCase(value.getOID())) {
                    return true;
                } else {
                    // Si se modificó el OID, se verifica que no exista otro espacio con ese OID
                    return !gestionarEspacioFisicoGatewayIntPort.existsEspacioFisicoByOid(value.getOID());
                }
            } else {
                // Si no se encuentra el registro original, se procede a validar la unicidad
                return !gestionarEspacioFisicoGatewayIntPort.existsEspacioFisicoByOid(value.getOID());
            }
        } else {
            // Modo creación: se verifica que no exista ningún espacio con ese OID
            return !gestionarEspacioFisicoGatewayIntPort.existsEspacioFisicoByOid(value.getOID());
        }
	}
}
