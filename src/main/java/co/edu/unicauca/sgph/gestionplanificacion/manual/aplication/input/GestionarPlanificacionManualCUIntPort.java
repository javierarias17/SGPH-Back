package co.edu.unicauca.sgph.gestionplanificacion.manual.aplication.input;

import java.util.List;

import org.springframework.data.domain.Page;

import co.edu.unicauca.sgph.gestionplanificacion.manual.infrastructure.input.DTORequest.FiltroCursoPlanificacionDTO;
import co.edu.unicauca.sgph.gestionplanificacion.manual.infrastructure.input.DTOResponse.CursoPlanificacionOutDTO;
import co.edu.unicauca.sgph.gestionplanificacion.manual.infrastructure.input.DTOResponse.FormatoPresentacionFranjaHorariaCursoDTO;
import co.edu.unicauca.sgph.gestionplanificacion.manual.infrastructure.input.DTOResponse.FranjaHorariaEspacioFisicoDTO;
import co.edu.unicauca.sgph.gestionplanificacion.manual.infrastructure.input.DTOResponse.FranjaHorariaCursoDTO;
import co.edu.unicauca.sgph.gestionplanificacion.manual.infrastructure.input.DTOResponse.FranjaHorariaDocenteDTO;
import co.edu.unicauca.sgph.gestionplanificacion.manual.infrastructure.input.DTOResponse.InfoGeneralCursosPorProgramaDTO;
import co.edu.unicauca.sgph.horario.infrastructure.input.DTORequest.CrearActualizarDocentesCursoInDTO;
import co.edu.unicauca.sgph.horario.infrastructure.input.DTORequest.CrearActualizarHorarioCursoInDTO;
import co.edu.unicauca.sgph.horario.infrastructure.input.DTORequest.FiltroFranjaHorariaDisponibleCursoDTO;
import co.edu.unicauca.sgph.horario.infrastructure.input.DTOResponse.CrearActualizarDocentesCursoOutDTO;
import co.edu.unicauca.sgph.horario.infrastructure.input.DTOResponse.CrearActualizarHorarioCursoOutDTO;

public interface GestionarPlanificacionManualCUIntPort {

	/**
	 * Método encargado de obtener los cursos dado un conjunto de criterios de
	 * busqueda.
	 * 
	 * @author Pedro Javier Arias Lasso <apedro@unicauca.com.co>
	 * 
	 * @param filtroCursoPlanificacionDTO
	 * @return
	 */
	public Page<CursoPlanificacionOutDTO> consultarCursosPlanificacionPorFiltro(
			FiltroCursoPlanificacionDTO filtroCursoPlanificacionDTO);

	/**
	 * Método encargado de consultar la información gneral de los cursos de un
	 * programa dado el identificador del programa
	 * 
	 * @author Pedro Javier Arias Lasso <apedro@unicauca.com.co>
	 * 
	 * @param idPrograma Identificador del programa
	 * @return
	 */
	public InfoGeneralCursosPorProgramaDTO consultarInfoGeneralCursosPorPrograma(Long idPrograma);

	/**
	 * Método encargado de crear y/o actualizar los horarios de un curso.
	 * 
	 * @author Pedro Javier Arias Lasso <apedro@unicauca.edu.co>
	 * 
	 * @param crearActualizarHorarioCursoInDTO
	 * @return
	 */
	public CrearActualizarHorarioCursoOutDTO crearActualizarHorarioCurso(
			CrearActualizarHorarioCursoInDTO crearActualizarHorarioCursoInDTO);

	/**
	 * Método encargado de crear y/o actualizar los docentes de un curso.
	 * 
	 * @author Pedro Javier Arias Lasso <apedro@unicauca.edu.co>
	 * 
	 * @param crearActualizarDocentesCursoInDTO
	 * @return
	 */
	public CrearActualizarDocentesCursoOutDTO crearActualizarDocentesCursoDTO(
			CrearActualizarDocentesCursoInDTO crearActualizarDocentesCursoInDTO);

	/**
	 * Método encargado de obtener las franjas disponibles de un curso dado un
	 * conjunto de criterios de busqueda; este método considera los horarios de los
	 * docentes y espacios físicos.
	 * 
	 * @author Pedro Javier Arias Lasso <apedro@unicauca.edu.co>
	 * 
	 * @param filtroFranjaHorariaDisponibleCursoDTO
	 * @return
	 */
	public List<FranjaHorariaCursoDTO> consultarFranjasHorariasDisponiblesPorCurso(
			FiltroFranjaHorariaDisponibleCursoDTO filtroFranjaHorariaDisponibleCursoDTO);

	/**
	 * Método encargado de obtener los nombres completos de cada espacio físico.
	 * Ejemplo del formato: 'Salón 204-Edificio nuevo'
	 * 
	 * @author Pedro Javier Arias Lasso <apedro@unicauca.edu.co>
	 * 
	 * @return
	 */
	public List<FormatoPresentacionFranjaHorariaCursoDTO> consultarFormatoPresentacionFranjaHorariaCurso();

	public List<FranjaHorariaCursoDTO> consultarFranjasHorariaCursoPorIdCurso(Long idCurso);

	/**
	 * Método encargado de obtener todas las franjas horarias de un docente
	 * 
	 * @author Pedro Javier Arias Lasso <apedro@unicauca.edu.co>
	 * 
	 * @param idPersona
	 * @return
	 */
	public List<FranjaHorariaDocenteDTO> consultarFranjasDocentePorIdPersona(Long idPersona);

	/**
	 * Método encargado de obtener todas las franjas horarias de un espacio físico
	 * 
	 * @author Pedro Javier Arias Lasso <apedro@unicauca.edu.co>
	 * 
	 * @param idEspacioFisico
	 * @return
	 */
	public List<FranjaHorariaEspacioFisicoDTO> consultarFranjasEspacioFisicoPorIdEspacioFisico(Long idEspacioFisico);

}