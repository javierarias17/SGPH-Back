package co.edu.unicauca.sgph.curso.aplication.output;

import java.util.List;
import java.util.Optional;

import co.edu.unicauca.sgph.curso.domain.model.Curso;
import co.edu.unicauca.sgph.curso.infrastructure.output.persistence.entity.CursoEntity;
import co.edu.unicauca.sgph.periodoacademico.domain.model.PeriodoAcademico;
import co.edu.unicauca.sgph.periodoacademico.infrastructure.output.persistence.entity.PeriodoAcademicoEntity;

public interface GestionarCursoGatewayIntPort {

	/**
	 * Método encargado de consultar un curso por grupo y asignatura <br>
	 * 
	 * @author apedro
	 * 
	 * @param grupo
	 * @param idAsignatura
	 * @return
	 */
	public Curso consultarCursoPorGrupoYAsignatura(String grupo, Long idAsignatura);

	/**
	 * Método encargado de guardar o actualizar un curso <br>
	 * 
	 * @author apedro
	 * 
	 * @param curso
	 * @return
	 */
	public Curso guardarCurso(Curso curso);

	/**
	 * Método encargado de obtener el curso por su identificador, si no existe
	 * retorna nulo.
	 * 
	 * @author apedro
	 * 
	 * @param idCurso
	 * @return Retorna el curso si existe, caso contrario retorna nulo
	 */
	public Curso consultarCursoPorIdCurso(Long idCurso);

	/**
	 * Método encargado de consultar los agrupadores de espacios físicos asociados
	 * al curso<br>
	 * 
	 * @author apedro
	 * 
	 * @param cursoInDTO
	 * @return Lista de idAgrupadorEspacioFisico
	 */
	public List<Long> consultarAgrupadoresEspaciosFisicosPorCurso(Long idCurso);
	
	/**
	 * Método encargado de consultar todos los cursos de un programa dado el
	 * programa y periodo académico </br>
	 * 
	 * @author apedro
	 * 
	 * @param idPrograma         Identificador único del programa
	 * @param idPeriodoAcademico Identificador único del periodo académico
	 * @return
	 */
	public List<Curso> consultarCursosPorProgramaYPeriodoAcademico(Long idPrograma, Long idPeriodoAcademico);

	Boolean eliminarCurso(Long id);
	
	public Boolean existsCursoByAsignaturaActiva(Long idAsignatura);
	
	public Boolean existsCursoByGrupoYAsignatura(String Grupo, Long idAsignatura);
	
	public Boolean existsCursoByDocente(Long idCurso);
	
	public Boolean existsCursoByGrupoYCupoYPeriodoYAsignatura(String grupo, Integer cupo, Long idPeriodo,
			Long idAsignatura);
	
	public List<Curso> findCursoByGrupoYCupoYPeriodoYAsignatura(String grupo, Long idAsignatura);
	
	public int actualizarCurso(PeriodoAcademicoEntity periodoAcademico, Integer cupo, String grupo, Long asignaturaId) ;
}
