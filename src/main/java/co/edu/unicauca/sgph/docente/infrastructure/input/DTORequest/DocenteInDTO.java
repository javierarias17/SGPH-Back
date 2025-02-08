package co.edu.unicauca.sgph.docente.infrastructure.input.DTORequest;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import co.edu.unicauca.sgph.docente.infrastructure.input.validation.ExisteCodigoDocente;
import co.edu.unicauca.sgph.docente.infrastructure.output.persistence.entity.EstadoDocenteEnum;
import co.edu.unicauca.sgph.espaciofisico.infrastructura.input.validation.ValidationGroups;
import co.edu.unicauca.sgph.docente.infrastructure.input.validation.ExisteIdPersonaDocente;

@ExisteCodigoDocente(groups = {ValidationGroups.OnCreate.class, ValidationGroups.OnUpdate.class})
@ExisteIdPersonaDocente(groups = {ValidationGroups.OnCreate.class, ValidationGroups.OnUpdate.class})
public class DocenteInDTO {

	private Long idDocente;
	
	@NotEmpty(groups = ValidationGroups.OnCreate.class)
	private String codigo;

	@NotNull
	private EstadoDocenteEnum estado;

	@NotNull
	private Long idDepartamento;

	@NotNull(groups = ValidationGroups.OnCreate.class)
	private Long idPersona;

	/**
	 * Atributo que determina si la invocación es para validar la información o
	 * persistirla
	 */
	private Boolean esValidar;

	public Long getIdDocente() {
		return idDocente;
	}

	public void setIdDocente(Long idDocente) {
		this.idDocente = idDocente;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public EstadoDocenteEnum getEstado() {
		return estado;
	}

	public void setEstado(EstadoDocenteEnum estado) {
		this.estado = estado;
	}

	public Long getIdDepartamento() {
		return idDepartamento;
	}

	public void setIdDepartamento(Long idDepartamento) {
		this.idDepartamento = idDepartamento;
	}

	public Long getIdPersona() {
		return idPersona;
	}

	public void setIdPersona(Long idPersona) {
		this.idPersona = idPersona;
	}

	public Boolean getEsValidar() {
		return esValidar;
	}

	public void setEsValidar(Boolean esValidar) {
		this.esValidar = esValidar;
	}
}