package co.edu.unicauca.sgph.asignatura.infrastructure.input.DTORequest;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import co.edu.unicauca.sgph.asignatura.infrastructure.input.validation.ExisteCodigoAsignatura;
import co.edu.unicauca.sgph.asignatura.infrastructure.input.validation.ExisteOidAsignatura;
import co.edu.unicauca.sgph.espaciofisico.infrastructura.input.validation.ValidationGroups;

@ExisteCodigoAsignatura(groups = {ValidationGroups.OnCreate.class, ValidationGroups.OnUpdate.class})
@ExisteOidAsignatura(groups = {ValidationGroups.OnCreate.class, ValidationGroups.OnUpdate.class})
public class AsignaturaInDTO {

	private Long idAsignatura;

	@NotEmpty
	private String nombre;

	@NotEmpty(groups = ValidationGroups.OnCreate.class)
	private String codigoAsignatura;

	private String OID;

	@NotNull
	private Integer semestre;

	
	private String pensum;

	@NotNull
	private Integer horasSemana;
	
	@NotNull
	private Long idPrograma;
	
	private String estado;
	
	private String base64;

	@NotNull
	private List<Long> lstIdAgrupadorEspacioFisico;
	
	private Boolean aplicaEspacioSecundario;
	
	private Boolean esValidar;
	

	public AsignaturaInDTO() {
		super();
	}



	public AsignaturaInDTO(String nombre,
						   String codigoAsignatura,
						   String OID,
						   Integer semestre,
						   String pensum,
						   Integer horasSemana,
						   Long idPrograma,
						   String estado) {
		this.nombre = nombre;
		this.codigoAsignatura = codigoAsignatura;
		this.OID = OID;
		this.semestre = semestre;
		this.pensum = pensum;
		this.horasSemana = horasSemana;
		this.idPrograma = idPrograma;
		this.estado = estado;
	}

	
	
	public Long getIdAsignatura() {
		return idAsignatura;
	}

	public void setIdAsignatura(Long idAsignatura) {
		this.idAsignatura = idAsignatura;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getCodigoAsignatura() {
		return codigoAsignatura;
	}

	public void setCodigoAsignatura(String codigoAsignatura) {
		this.codigoAsignatura = codigoAsignatura;
	}

	public String getOID() {
		return OID;
	}

	public void setOID(String oID) {
		OID = oID;
	}

	public Integer getSemestre() {
		return semestre;
	}

	public void setSemestre(Integer semestre) {
		this.semestre = semestre;
	}

	public String getPensum() {
		return pensum;
	}

	public void setPensum(String pensum) {
		this.pensum = pensum;
	}

	public Integer getHorasSemana() {
		return horasSemana;
	}

	public void setHorasSemana(Integer horasSemana) {
		this.horasSemana = horasSemana;
	}

	public Long getIdPrograma() {
		return idPrograma;
	}

	public void setIdPrograma(Long idPrograma) {
		this.idPrograma = idPrograma;
	}

	public List<Long> getLstIdAgrupadorEspacioFisico() {
		return lstIdAgrupadorEspacioFisico;
	}

	public void setLstIdAgrupadorEspacioFisico(List<Long> lstIdAgrupadorEspacioFisico) {
		this.lstIdAgrupadorEspacioFisico = lstIdAgrupadorEspacioFisico;
	}

	public String getBase64() {
		return base64;
	}

	public void setBase64(String base64) {
		this.base64 = base64;
	}
	
	public Boolean getAplicaEspacioSecundario() {
		return aplicaEspacioSecundario;
	}

	public void setAplicaEspacioSecundario(Boolean aplicaEspacioSecundario) {
		this.aplicaEspacioSecundario = aplicaEspacioSecundario;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public Boolean getEsValidar() {
		return esValidar;
	}

	public void setEsValidar(Boolean esValidar) {
		this.esValidar = esValidar;
	}
	
	
}