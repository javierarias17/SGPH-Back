package co.edu.unicauca.sgph.curso.infrastructura.input.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import co.edu.unicauca.sgph.curso.aplication.output.GestionarCursoGatewayIntPort;
import co.edu.unicauca.sgph.curso.domain.model.Curso;
import co.edu.unicauca.sgph.curso.infrastructure.input.DTORequest.CursoInDTO;
import co.edu.unicauca.sgph.curso.infrastructure.input.DTOResponse.CursoOutDTO;

@Component
public class ExisteCursoConMismoGrupoValidation implements ConstraintValidator<ExisteCursoConMismoGrupo, CursoInDTO>{
	@Autowired
	private GestionarCursoGatewayIntPort gestionarCursoGatewayIntPort;

	@Override
	public boolean isValid(CursoInDTO value, ConstraintValidatorContext context) {
		// Si el campo "grupo" es nulo o vacío, se permite que otros validadores se encarguen de esa validación.
        if (value.getGrupo() == null || value.getGrupo().trim().isEmpty()) {
            return true;
        }
        
        // Modo actualización: si el DTO tiene un ID, se consulta el curso original.
        if (value.getIdCurso() != null) {
            Curso cursoOriginal = gestionarCursoGatewayIntPort.consultarCursoPorIdCurso(value.getIdCurso());
            if (cursoOriginal != null) {
                // Si el grupo no ha cambiado, se permite la validación.
                if (cursoOriginal.getGrupo().equalsIgnoreCase(value.getGrupo())) {
                    return true;
                } else {
                    // Si se modificó, se verifica que no exista otro curso con el mismo grupo para la misma asignatura.
                    return !gestionarCursoGatewayIntPort.existsCursoByGrupoYAsignatura(value.getGrupo(), value.getIdAsignatura());
                }
            } else {
                // Si no se encuentra el curso original, se procede a validar la unicidad.
                return !gestionarCursoGatewayIntPort.existsCursoByGrupoYAsignatura(value.getGrupo(), value.getIdAsignatura());
            }
        } else {
            // Modo creación: se verifica que no exista ningún curso con ese grupo para la asignatura.
            return !gestionarCursoGatewayIntPort.existsCursoByGrupoYAsignatura(value.getGrupo(), value.getIdAsignatura());
        }
	}
}
