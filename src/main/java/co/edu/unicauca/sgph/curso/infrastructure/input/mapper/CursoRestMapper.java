package co.edu.unicauca.sgph.curso.infrastructure.input.mapper;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import co.edu.unicauca.sgph.agrupador.domain.model.AgrupadorEspacioFisico;
import co.edu.unicauca.sgph.agrupador.infrastructure.output.persistence.entity.AgrupadorEspacioFisicoEntity;
import co.edu.unicauca.sgph.curso.domain.model.Curso;
import co.edu.unicauca.sgph.curso.infrastructure.input.DTORequest.CursoInDTO;
import co.edu.unicauca.sgph.curso.infrastructure.input.DTOResponse.CursoOutDTO;
import co.edu.unicauca.sgph.curso.infrastructure.output.persistence.entity.CursoEntity;

@Mapper(componentModel = "spring")
public interface CursoRestMapper {

	@Mapping(target = "idAsignatura", source = "curso.asignatura.idAsignatura")
	@Mapping(target = "idPeriodoAcademico", source = "curso.periodoAcademico.idPeriodoAcademico")
	@Mapping(target = "idPrograma", source = "curso.asignatura.programa.idPrograma")
	@Mapping(target = "idFacultad", source = "curso.asignatura.programa.facultad.idFacultad")
	@Mapping(target = "nombreCurso", source = "curso.asignatura.nombre")
	@Mapping(target = "nombreFacultad", source = "curso.asignatura.programa.facultad.nombre")
	@Mapping(target = "nombrePrograma", source = "curso.asignatura.programa.nombre")
	@Mapping(target = "grupo", source = "curso.grupo")
	@Mapping(target = "periodoAcademicoAnio", source = "curso.periodoAcademico.anio")
	@Mapping(target = "OIDAsignatura", source = "curso.asignatura.OID")
	@Mapping(target = "semestre", source = "curso.asignatura.semestre")
	@Mapping(target = "horas", source = "curso.asignatura.horasSemana")
	CursoOutDTO toCursoOutDTO(Curso curso);

	@Mapping(target = "idAsignatura", source = "asignatura.idAsignatura")
    @Mapping(target = "idPeriodoAcademico", source = "periodoAcademico.idPeriodoAcademico")
    @Mapping(target = "idPrograma", source = "asignatura.programa.idPrograma")
    @Mapping(target = "idFacultad", source = "asignatura.programa.facultad.idFacultad")
    @Mapping(target = "nombreCurso", source = "asignatura.nombre")
    @Mapping(target = "nombreFacultad", source = "asignatura.programa.facultad.nombre")
    @Mapping(target = "nombrePrograma", source = "asignatura.programa.nombre")
    @Mapping(target = "grupo", source = "grupo")
    @Mapping(target = "periodoAcademicoAnio", source = "periodoAcademico.anio")
	@Mapping(target = "OIDAsignatura", source = "asignatura.oid")
    @Mapping(target = "semestre", source = "asignatura.semestre")
    @Mapping(target = "horas", source = "asignatura.horasSemana")
    @Mapping(target = "cupo", source = "cupo")
    CursoOutDTO toCursoOutDTOFromEntity(CursoEntity entity);

    // 3. Mapea de CursoInDTO (entrada) a objeto de dominio Curso.
    @Mapping(target = "asignatura", expression = "java(new Asignatura(cursoInDTO.getIdAsignatura()))")
    @Mapping(target = "docentes", ignore = true)
    @Mapping(target = "horarios", ignore = true)
    // Puedes mapear otros campos según corresponda.
    Curso toCurso(CursoInDTO cursoInDTO);

    // 4. Mapea de dominio Curso a entidad CursoEntity (para creación)
    @Mapping(target = "idCurso", ignore = true)
    CursoEntity toCursoEntity(Curso curso);

    Curso toCursoFromEntity(CursoEntity entity);
    
    // 5. Método para actualizar una entidad existente a partir de un objeto de dominio.
    // Se ignoran campos que no deben actualizarse (por ejemplo, el id y, si corresponde, el periodo académico).
    @Mapping(target = "idCurso", ignore = true)
    @Mapping(target = "periodoAcademico", ignore = true)
    void updateEntityFromCurso(Curso curso, @MappingTarget CursoEntity entity);

    List<CursoOutDTO> toLstCursoOutDTO(List<Curso> lstCurso);

    // Si se requieren métodos auxiliares para convertir listas de IDs a objetos de dominio o viceversa, se pueden agregar:
    @Named("toAgrupadorEspacioFisico")
    default List<AgrupadorEspacioFisico> toAgrupadorEspacioFisico(List<Long> lstIdAgrupadorEspacioFisico) {
        if (lstIdAgrupadorEspacioFisico == null) {
            return new ArrayList<>();
        }
        List<AgrupadorEspacioFisico> agrupadores = new ArrayList<>();
        for (Long id : lstIdAgrupadorEspacioFisico) {
            AgrupadorEspacioFisico a = new AgrupadorEspacioFisico();
            a.setIdAgrupadorEspacioFisico(id);
            agrupadores.add(a);
        }
        return agrupadores;
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