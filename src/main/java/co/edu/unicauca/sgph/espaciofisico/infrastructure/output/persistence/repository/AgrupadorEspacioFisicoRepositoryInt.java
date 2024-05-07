package co.edu.unicauca.sgph.espaciofisico.infrastructure.output.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import co.edu.unicauca.sgph.espaciofisico.infrastructure.output.persistence.entity.AgrupadorEspacioFisicoEntity;

public interface AgrupadorEspacioFisicoRepositoryInt extends JpaRepository<AgrupadorEspacioFisicoEntity, Long> {

	/**
	 * Método encargado de consultar los agrupadores de espacios físicos dado una
	 * lista de identificadores únicos<br>
	 * 
	 * @author Pedro Javier Arias Lasso <apedro@unicauca.edu.co>
	 * 
	 * @param idAgrupadorEspacioFisico
	 * @return Lista de instancias de AgrupadorEspacioFisico
	 */
	@Query("SELECT agrupador" 
			+ " FROM AgrupadorEspacioFisicoEntity agrupador "
			+ " WHERE agrupador.idAgrupadorEspacioFisico IN (:idAgrupadorEspacioFisico)")
	public List<AgrupadorEspacioFisicoEntity> consultarAgrupadoresEspaciosFisicosPorIdAgrupadorEspacioFisico(
			List<Long> idAgrupadorEspacioFisico);
	
	
	/**
	 * Método encargado de consultar los agrupadores de espacios físicos dado una
	 * lista de identificadores únicos de facultades<br>
	 * 
	 * @author Pedro Javier Arias Lasso <apedro@unicauca.edu.co>
	 * 
	 * @param idFacultad
	 * @return Lista de instancias de AgrupadorEspacioFisico
	 */
	@Query("SELECT agrupador" 
			+ " FROM AgrupadorEspacioFisicoEntity agrupador "
			+ " WHERE agrupador.facultad.idFacultad IN (:idFacultad)")
	public List<AgrupadorEspacioFisicoEntity> consultarAgrupadoresEspaciosFisicosPorIdFacultad(List<Long> idFacultad);
}