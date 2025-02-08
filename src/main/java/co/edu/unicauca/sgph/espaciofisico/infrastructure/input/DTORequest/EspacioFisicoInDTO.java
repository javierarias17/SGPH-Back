package co.edu.unicauca.sgph.espaciofisico.infrastructure.input.DTORequest;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import co.edu.unicauca.sgph.espaciofisico.infrastructura.input.validation.ExisteNombreEspacioFisico;
import co.edu.unicauca.sgph.espaciofisico.infrastructura.input.validation.ExisteOidEspacioFisico;
import co.edu.unicauca.sgph.espaciofisico.infrastructura.input.validation.ValidationGroups;
import co.edu.unicauca.sgph.espaciofisico.infrastructure.output.persistence.entity.EstadoEspacioFisicoEnum;

@ExisteOidEspacioFisico(groups = {ValidationGroups.OnCreate.class, ValidationGroups.OnUpdate.class})
@ExisteNombreEspacioFisico(groups = {ValidationGroups.OnCreate.class, ValidationGroups.OnUpdate.class})
public class EspacioFisicoInDTO {

	private Long idEspacioFisico;

	@JsonProperty("OID")
	private String OID;

	@NotNull
	private Long capacidad;

	@NotNull
	private EstadoEspacioFisicoEnum estado;

	@NotEmpty(groups = ValidationGroups.OnCreate.class)
	private String salon;

	private Long idEdificio;

	@NotNull
	private Long idUbicacion;
	
	@NotNull
	private Long idTipoEspacioFisico;

	private List<Long> lstIdAgrupadorEspacioFisico;
	private List<Long> recursos;

	private List<Long> saveIdAgrupadores;
	
	private Boolean esValidar;

	private String base64;
	
	
	
	public EspacioFisicoInDTO(@NotEmpty String oID, @NotNull Long capacidad, @NotNull EstadoEspacioFisicoEnum estado,
			@NotEmpty String salon, @NotNull Long idTipoEspacioFisico) {
		OID = oID;
		this.capacidad = capacidad;
		this.estado = estado;
		this.salon = salon;
		this.idTipoEspacioFisico = idTipoEspacioFisico;
	}

	public Long getIdEspacioFisico() {
		return idEspacioFisico;
	}

	public void setIdEspacioFisico(Long idEspacioFisico) {
		this.idEspacioFisico = idEspacioFisico;
	}

	public Long getCapacidad() {
		return capacidad;
	}

	public void setCapacidad(Long capacidad) {
		this.capacidad = capacidad;
	}

	public EstadoEspacioFisicoEnum getEstado() {
		return estado;
	}

	public void setEstado(EstadoEspacioFisicoEnum estado) {
		this.estado = estado;
	}

	public String getSalon() {
		return salon;
	}

	public void setSalon(String salon) {
		this.salon = salon;
	}

	public Long getIdTipoEspacioFisico() {
		return idTipoEspacioFisico;
	}

	public void setIdTipoEspacioFisico(Long idTipoEspacioFisico) {
		this.idTipoEspacioFisico = idTipoEspacioFisico;
	}

	public List<Long> getLstIdAgrupadorEspacioFisico() {
		return lstIdAgrupadorEspacioFisico;
	}

	public void setLstIdAgrupadorEspacioFisico(List<Long> lstIdAgrupadorEspacioFisico) {
		this.lstIdAgrupadorEspacioFisico = lstIdAgrupadorEspacioFisico;
	}

	public String getOID() {
		return OID;
	}

	public void setOID(String oID) {
		OID = oID;
	}

	public Long getIdEdificio() {
		return idEdificio;
	}

	public void setIdEdificio(Long idEdificio) {
		this.idEdificio = idEdificio;
	}

	public Long getIdUbicacion() {
		return idUbicacion;
	}

	public void setIdUbicacion(Long idUbicacion) {
		this.idUbicacion = idUbicacion;
	}

	public List<Long> getRecursos() {
		return recursos;
	}

	public void setRecursos(List<Long> recursos) {
		this.recursos = recursos;
	}

	public List<Long> getSaveIdAgrupadores() {
		return saveIdAgrupadores;
	}

	public void setSaveIdAgrupadores(List<Long> saveIdAgrupadores) {
		this.saveIdAgrupadores = saveIdAgrupadores;
	}

	public Boolean getEsValidar() {
		return esValidar;
	}

	public void setEsValidar(Boolean esValidar) {
		this.esValidar = esValidar;
	}

	public String getBase64() {
		return base64;
	}

	public void setBase64(String base64) {
		this.base64 = base64;
	}
	
}