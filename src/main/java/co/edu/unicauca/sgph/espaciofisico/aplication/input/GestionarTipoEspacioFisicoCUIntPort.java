package co.edu.unicauca.sgph.espaciofisico.aplication.input;

import java.util.List;

import co.edu.unicauca.sgph.espaciofisico.domain.model.TipoEspacioFisico;

public interface GestionarTipoEspacioFisicoCUIntPort {

	/**
	 * Método encargado de consultar los tipos de espacios físicos por facultad
	 * </br>
	 * 
	 * @author Pedro Javier Arias Lasso <apedro@unicauca.edu.co>
	 * 
	 * @param lstIdFacultad
	 * @return
	 */
	public List<TipoEspacioFisico> consultarTiposEspaciosFisicosPorIdFacultad(List<Long> lstIdFacultad);
}
