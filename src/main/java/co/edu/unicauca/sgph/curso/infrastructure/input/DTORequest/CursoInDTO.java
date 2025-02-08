package co.edu.unicauca.sgph.curso.infrastructure.input.DTORequest;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import co.edu.unicauca.sgph.curso.infrastructura.input.validation.ExisteCursoConMismoGrupo;
import co.edu.unicauca.sgph.curso.infrastructura.input.validation.ExisteCursoPorAsignaturaActiva;
import co.edu.unicauca.sgph.espaciofisico.infrastructura.input.validation.ValidationGroups;

@ExisteCursoConMismoGrupo(groups = {ValidationGroups.OnCreate.class, ValidationGroups.OnUpdate.class})
public class CursoInDTO {
	private Long idCurso;

	@NotEmpty(groups = ValidationGroups.OnCreate.class)
	private String grupo;
	
	private Integer cupo;

	@NotNull(groups = ValidationGroups.OnCreate.class)
	private Long idAsignatura;

	private Boolean esValidar;
	
	public Long getIdCurso() {
		return idCurso;
	}

	public void setIdCurso(Long idCurso) {
		this.idCurso = idCurso;
	}

	public String getGrupo() {
		return grupo;
	}

	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}

	public Integer getCupo() {
		return cupo;
	}

	public void setCupo(Integer cupo) {
		this.cupo = cupo;
	}

	public Long getIdAsignatura() {
		return idAsignatura;
	}

	public void setIdAsignatura(Long idAsignatura) {
		this.idAsignatura = idAsignatura;
	}
	public Boolean getEsValidar() {
		return esValidar;
	}

	public void setEsValidar(Boolean esValidar) {
		this.esValidar = esValidar;
	}
	
}