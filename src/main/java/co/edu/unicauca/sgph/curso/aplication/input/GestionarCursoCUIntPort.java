package co.edu.unicauca.sgph.curso.aplication.input;

import java.util.List;

import co.edu.unicauca.sgph.curso.domain.model.Curso;

public interface GestionarCursoCUIntPort {

	/**
	 * Método encargado de consultar un curso por grupo y asignatura <br>
	 * 
	 * @author apedro
	 * 
	 * @param grupo
	 * @param idAsignatura
	 * @return
	 */
	@Deprecated
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
	 * Método encargado de obtener el curso por su identificador<br>
	 * 
	 * @author apedro
	 * 
	 * @param idCurso
	 * @return
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

	Curso obtenerCurso(Long id);

	Boolean eliminarCurso(Long id);
	List<Curso> consultarCursosPorIdPeriodoYIdPrograma(Long idPeriodo, Long idPrograma);
}
