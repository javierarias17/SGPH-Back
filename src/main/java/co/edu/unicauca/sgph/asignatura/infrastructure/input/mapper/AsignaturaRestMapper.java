package co.edu.unicauca.sgph.asignatura.infrastructure.input.mapper;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import co.edu.unicauca.sgph.agrupador.domain.model.AgrupadorEspacioFisico;
import co.edu.unicauca.sgph.agrupador.infrastructure.output.persistence.entity.AgrupadorEspacioFisicoEntity;
import co.edu.unicauca.sgph.asignatura.domain.model.Asignatura;
import co.edu.unicauca.sgph.asignatura.infrastructure.input.DTORequest.AsignaturaInDTO;
import co.edu.unicauca.sgph.asignatura.infrastructure.input.DTOResponse.AsignaturaOutDTO;
import co.edu.unicauca.sgph.asignatura.infrastructure.output.persistence.entity.AsignaturaEntity;

import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface AsignaturaRestMapper {

	@Mapping(target = "idPrograma", source = "asignatura.programa.idPrograma")
	@Mapping(target = "lstIdAgrupadorEspacioFisico", source = "asignatura.agrupadores", qualifiedByName = "toLstIdAgrupadorEspacioFisico")
	@Mapping(target = "nombrePrograma", source = "asignatura.programa.nombre")
    @Mapping(target = "nombreFacultad", source = "asignatura.programa.facultad.nombre")
    @Mapping(target = "idFacultad", source = "asignatura.programa.facultad.idFacultad")
	AsignaturaOutDTO toAsignaturaOutDTO(Asignatura asignatura);

	@Mapping(target = "idPrograma", source = "programa.idPrograma")
    @Mapping(target = "lstIdAgrupadorEspacioFisico", source = "agrupadores", qualifiedByName = "toLstIdAgrupadorEspacioFisicoEntity")
    @Mapping(target = "nombrePrograma", source = "programa.nombre")
    @Mapping(target = "nombreFacultad", source = "programa.facultad.nombre")
    @Mapping(target = "idFacultad", source = "programa.facultad.idFacultad")
	@Mapping(target = "programa.facultad.programas", ignore = true)
    AsignaturaOutDTO toAsignaturaOutDTOFromEntity(AsignaturaEntity asignaturaEntity);
	
	// Mapear de AsignaturaInDTO a Asignatura (para guardar/actualizar)
    @Mapping(target = "idAsignatura", source = "idAsignatura") // No se modifica el ID
    @Mapping(target = "programa", expression = "java(new Programa(asignaturaInDTO.getIdPrograma()))")
    @Mapping(target = "agrupadores", source = "asignaturaInDTO.lstIdAgrupadorEspacioFisico", qualifiedByName = "toAgrupadorEspacioFisico")
    @Mapping(target = "nombre", source = "nombre")
    @Mapping(target = "codigoAsignatura", source = "codigoAsignatura")
    @Mapping(target = "OID", source = "OID")
    @Mapping(target = "semestre", source = "semestre")
    @Mapping(target = "pensum", source = "pensum")
    @Mapping(target = "horasSemana", source = "horasSemana")
    @Mapping(target = "aplicaEspacioSecundario", source = "aplicaEspacioSecundario")
	Asignatura toAsignatura(AsignaturaInDTO asignaturaInDTO);

    @Mapping(target = "idAsignatura", ignore = true) // No mapea el ID para que se genere en persistencia
    AsignaturaEntity toAsignaturaEntity(Asignatura asignatura);
    
    @Mapping(target = "programa.asignaturas", ignore = true)
    Asignatura toAsignaturaFromEntity(AsignaturaEntity entity);
    
    @Mapping(target = "idAsignatura", ignore = true)
    @Mapping(target = "programa", ignore = true) // suponiendo que no deseas actualizar el programa a través de este DTO
    void updateEntityFromAsignatura(Asignatura asignatura, @MappingTarget AsignaturaEntity entity);
    
	@Named("handleNullList")
	static List<Long> handleNullList(List<Long> lstIdAgrupadorEspacioFisico) {
		return lstIdAgrupadorEspacioFisico != null ? lstIdAgrupadorEspacioFisico : new ArrayList<>();
	}
	List<AsignaturaOutDTO> toLstAsignaturaOutDTO(List<Asignatura> lstAsignatura);
	
	@Named("toAgrupadorEspacioFisico")
	default List<AgrupadorEspacioFisico> toAgrupadorEspacioFisico(List<Long> lstIdAgrupadorEspacioFisico) {
		if (lstIdAgrupadorEspacioFisico == null) {
            return new ArrayList<>();
        }
		List<AgrupadorEspacioFisico> agrupadores = new ArrayList<>();
		for (Long idAgrupadorEspacioFisico : lstIdAgrupadorEspacioFisico) {
			AgrupadorEspacioFisico agrupadorEspacioFisico = new AgrupadorEspacioFisico();
			agrupadorEspacioFisico.setIdAgrupadorEspacioFisico(idAgrupadorEspacioFisico);
			agrupadores.add(agrupadorEspacioFisico);
		}
		return agrupadores;
	}

	@Named("toLstIdAgrupadorEspacioFisico")
	default List<Long> toLstIdAgrupadorEspacioFisico(List<AgrupadorEspacioFisico> agrupadores) {
		if (agrupadores == null) {
            return new ArrayList<>();
        }
		List<Long> lstIdAgrupadorEspacioFisico = new ArrayList<>();
		for (AgrupadorEspacioFisico agrupadorEspacioFisico : agrupadores) {
			lstIdAgrupadorEspacioFisico.add(agrupadorEspacioFisico.getIdAgrupadorEspacioFisico());
		}
		return lstIdAgrupadorEspacioFisico;
	}
	
	@Named("toLstIdAgrupadorEspacioFisicoEntity")
	default List<Long> toLstIdAgrupadorEspacioFisicoEntity(List<AgrupadorEspacioFisicoEntity> agrupadores) {
	    if (agrupadores == null) {
	        return new ArrayList<>();
	    }
	    List<Long> lst = new ArrayList<>();
	    for (AgrupadorEspacioFisicoEntity ae : agrupadores) {
	        lst.add(ae.getIdAgrupadorEspacioFisico());
	    }
	    return lst;
	}

}