package co.edu.unicauca.sgph.gestionplanificacion.manual.infrastructure.input.DTOResponse;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class CursoPlanificacionOutDTO implements Serializable {

	private static final long serialVersionUID = 7792736941477578003L;

	private Long idCurso;

	private Long idAsignatura;

	private String nombrePrograma;

	private Integer semestre;

	private String nombreAsignatura;

	private String grupo;

	private List<String> horarios;
	
	private List<String> horariosSecundarios;

	private List<String> docentes;

	private Long horasAsignadas;

	private Integer horasSemana;

	private Integer cupo;

	private Boolean aplicaEspacioSecundario;

	public CursoPlanificacionOutDTO() {
	}

	public CursoPlanificacionOutDTO(Long idCurso, String nombrePrograma, Integer semestre, String nombreAsignatura,
			String grupo, Long horasAsignadas, Integer horasSemana, Integer cupo, Long idAsignatura, Boolean aplicaEspacioSecundario) {
		this.idCurso = idCurso;
		this.nombrePrograma = nombrePrograma;
		this.semestre = semestre;
		this.nombreAsignatura = nombreAsignatura;
		this.grupo = grupo;
		if (Objects.isNull(horasAsignadas)) {
			this.horasAsignadas = 0L;
		} else {
			this.horasAsignadas = horasAsignadas;
		}
		this.horasSemana = horasSemana;
		this.cupo = cupo;
		this.idAsignatura = idAsignatura;
		this.aplicaEspacioSecundario = aplicaEspacioSecundario;
	}

	public String getNombrePrograma() {
		return nombrePrograma;
	}

	public void setNombrePrograma(String nombrePrograma) {
		this.nombrePrograma = nombrePrograma;
	}

	public Integer getSemestre() {
		return semestre;
	}

	public void setSemestre(Integer semestre) {
		this.semestre = semestre;
	}

	public String getNombreAsignatura() {
		return nombreAsignatura;
	}

	public void setNombreAsignatura(String nombreAsignatura) {
		this.nombreAsignatura = nombreAsignatura;
	}

	public String getGrupo() {
		return grupo;
	}

	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}

	public Long getIdCurso() {
		return idCurso;
	}

	public void setIdCurso(Long idCurso) {
		this.idCurso = idCurso;
	}

	public List<String> getHorarios() {
		return horarios;
	}

	public void setHorarios(List<String> horarios) {
		this.horarios = horarios;
	}

	public List<String> getDocentes() {
		return docentes;
	}

	public void setDocentes(List<String> docentes) {
		this.docentes = docentes;
	}

	public Long getHorasAsignadas() {
		return horasAsignadas;
	}

	public void setHorasAsignadas(Long horasAsignadas) {
		this.horasAsignadas = horasAsignadas;
	}

	public Integer getHorasSemana() {
		return horasSemana;
	}

	public void setHorasSemana(Integer horasSemana) {
		this.horasSemana = horasSemana;
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

	public List<String> getHorariosSecundarios() {
		return horariosSecundarios;
	}

	public void setHorariosSecundarios(List<String> horariosSecundarios) {
		this.horariosSecundarios = horariosSecundarios;
	}

	public Boolean getAplicaEspacioSecundario() {
		return aplicaEspacioSecundario;
	}

	public void setAplicaEspacioSecundario(Boolean aplicaEspacioSecundario) {
		this.aplicaEspacioSecundario = aplicaEspacioSecundario;
	}
}