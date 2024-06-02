package co.edu.unicauca.sgph.reporte.infraestructure.output;

import co.edu.unicauca.sgph.asignatura.infrastructure.output.persistence.entity.AsignaturaEntity;
import co.edu.unicauca.sgph.common.enums.DiaSemanaEnum;
import co.edu.unicauca.sgph.curso.infrastructure.output.persistence.entity.CursoEntity;
import co.edu.unicauca.sgph.docente.domain.model.Docente;
import co.edu.unicauca.sgph.docente.infrastructure.output.persistence.entity.DocenteEntity;
import co.edu.unicauca.sgph.facultad.infrastructure.output.persistence.entity.FacultadEntity;
import co.edu.unicauca.sgph.horario.infrastructure.output.persistence.entity.HorarioEntity;
import co.edu.unicauca.sgph.periodoacademico.infrastructure.output.persistence.entity.PeriodoAcademicoEntity;
import co.edu.unicauca.sgph.programa.aplication.output.GestionarProgramaGatewayIntPort;
import co.edu.unicauca.sgph.programa.domain.model.Programa;
import co.edu.unicauca.sgph.programa.infrastructure.output.persistence.entity.ProgramaEntity;
import co.edu.unicauca.sgph.programa.infrastructure.output.persistence.repository.ProgramaRepositoryInt;
import co.edu.unicauca.sgph.reporte.aplication.output.GestionarReporteGatewayIntPort;
import co.edu.unicauca.sgph.reporte.infraestructure.input.DTO.HorarioDTO;
import co.edu.unicauca.sgph.reporte.infraestructure.input.DTO.ReporteSimcaDTO;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GestionarReporteGatewayImplAdapter implements GestionarReporteGatewayIntPort {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public ReporteSimcaDTO generarReporteSimca(ReporteSimcaDTO reporteSimcaDTO) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<CursoEntity> criteriaQuery = cb.createQuery(CursoEntity.class);
		Root<CursoEntity> root = criteriaQuery.from(CursoEntity.class);
		Join<CursoEntity,AsignaturaEntity> joinCurso = root.join("asignatura");
		Join<AsignaturaEntity, ProgramaEntity> joinPrograma = joinCurso.join("programa");
		Join<ProgramaEntity, FacultadEntity> joinFacultad = joinPrograma.join("facultad");
		Join<CursoEntity, HorarioEntity> joinHorario = root.join("horarios");
		Join<CursoEntity, PeriodoAcademicoEntity> joinPeriodo = root.join("periodoAcademico");
		Join<CursoEntity, DocenteEntity> joinDocente =  root.join("docentes");

		Predicate predicate = cb.conjunction();
		if (reporteSimcaDTO.getIdPeriodo() != null) {
			Predicate predicatePeriodo = cb.and(predicate, cb.equal(joinPeriodo.get("idPeriodoAcademico"), reporteSimcaDTO.getIdPeriodo()));
			predicate = predicatePeriodo;
		}
		if (reporteSimcaDTO.getIdPrograma() != null) {
			Predicate predicatePrograma = cb.and(predicate, cb.equal(joinPrograma.get("idPrograma"), reporteSimcaDTO.getIdPrograma()));
			predicate = predicatePrograma;
		}
		if (reporteSimcaDTO.getIdFacultad() != null) {
			Predicate predicateFacultad = cb.and(predicate, cb.equal(joinFacultad.get("idFacultad"), reporteSimcaDTO.getIdFacultad()));
			predicate = predicateFacultad;
		}

		criteriaQuery.where(predicate);

		List<CursoEntity> resultList = entityManager.createQuery(criteriaQuery).getResultList();
		List<ReporteSimcaDTO> datos = resultList.stream().map(this::mapCursoEntityReporteDTO).collect(Collectors.toList());
		try {
			return this.crearExcel(datos);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	private ReporteSimcaDTO mapCursoEntityReporteDTO(CursoEntity curso) {
		ReporteSimcaDTO dto = new ReporteSimcaDTO();
		dto.setCupo(curso.getCupo());
		dto.setMatriculados(1L); // TODO MATRICULADOS;
		dto.setAbreviaturaPrograma(curso.getAsignatura().getPrograma().getAbreviatura());
		dto.setCodigoAsignatura(curso.getAsignatura().getCodigoAsignatura());
		dto.setNombreAsignatura(curso.getAsignatura().getNombre());
		dto.setNombreGrupo(curso.getGrupo());
		dto.setSemestre(curso.getAsignatura().getSemestre());
		dto.setOIDAsignatura(curso.getAsignatura().getOid());
		dto.setPeriodo(curso.getPeriodoAcademico().getAnio().toString() + "-" + curso.getPeriodoAcademico().getPeriodo().toString());
		dto.setNombreDocente(this.nombreDocentes(curso.getDocentes()));
		List<HorarioEntity> horario = curso.getHorarios();
		if (horario != null) {
			dto.setHorarios(curso.getHorarios().stream().map(this::mapHorarioEnityToDTO).collect(Collectors.toList()));
		}
		return dto;
	}
	private String nombreDocentes(List<DocenteEntity> docentes) {
		List<String> nombres = docentes.stream().map(this::nombreCompletoDocente).collect(Collectors.toList());
		return String.join(", ", nombres);
	}
	private String nombreCompletoDocente(DocenteEntity docente) {
		String nombreCompleto = docente.getPrimerNombre() + " " + docente.getSegundoNombre() + " " + docente.getPrimerApellido() + " " + docente.getSegundoApellido();
		return nombreCompleto;
	}
	private HorarioDTO mapHorarioEnityToDTO(HorarioEntity entidad) {
		HorarioDTO dto = new HorarioDTO();
		dto.setDia(entidad.getDia());
		dto.setHorarioInicio(entidad.getHoraInicio());
		dto.setHoraFin(entidad.getHoraFin());
		return dto;
	}
	public ReporteSimcaDTO crearExcel(List<ReporteSimcaDTO> reporteSimcaList) throws IOException {
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("Reporte Simca");
		CellStyle headerCellStyle = workbook.createCellStyle();
		Font headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerCellStyle.setFont(headerFont);
		headerCellStyle.setBorderBottom(BorderStyle.THIN);
		headerCellStyle.setBorderTop(BorderStyle.THIN);
		headerCellStyle.setBorderLeft(BorderStyle.THIN);
		headerCellStyle.setBorderRight(BorderStyle.THIN);
		String[] columnHeaders = {
				"Periodo", "Abreviatura Programa", "Semestre", "OID Asignatura", "Codigo Asignatura",
				"Nombre Asignatura", "Nombre Grupo", "Cupo", "Matriculados", "Lunes", "Martes",
				"Miércoles", "Jueves", "Viernes", "Sábado", "Docente"
		};
		Row headerRow = sheet.createRow(0);
		for (int i = 0; i < columnHeaders.length; i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(columnHeaders[i]);
			cell.setCellStyle(headerCellStyle);
		}
		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
		int rowNum = 1;
		for (ReporteSimcaDTO reporteSimca : reporteSimcaList) {
			Row dataRow = sheet.createRow(rowNum++);
			dataRow.createCell(0).setCellValue(reporteSimca.getPeriodo() != null ? reporteSimca.getPeriodo() : "");
			dataRow.createCell(1).setCellValue(reporteSimca.getAbreviaturaPrograma() != null ? reporteSimca.getAbreviaturaPrograma() : "");
			dataRow.createCell(2).setCellValue(reporteSimca.getSemestre() != null ? reporteSimca.getSemestre() : 0);
			dataRow.createCell(3).setCellValue(reporteSimca.getOIDAsignatura() != null ? reporteSimca.getOIDAsignatura() : "");
			dataRow.createCell(4).setCellValue(reporteSimca.getCodigoAsignatura() != null ? reporteSimca.getCodigoAsignatura() : "");
			dataRow.createCell(5).setCellValue(reporteSimca.getNombreAsignatura() != null ? reporteSimca.getNombreAsignatura() : "");
			dataRow.createCell(6).setCellValue(reporteSimca.getNombreGrupo() != null ? reporteSimca.getNombreGrupo() : "");
			dataRow.createCell(7).setCellValue(reporteSimca.getCupo() != null ? reporteSimca.getCupo() : 0);
			dataRow.createCell(8).setCellValue(reporteSimca.getMatriculados() != null ? reporteSimca.getMatriculados() : 0L);
			Map<DiaSemanaEnum, StringBuilder> horariosPorDia = new EnumMap<>(DiaSemanaEnum.class);
			for (DiaSemanaEnum dia : DiaSemanaEnum.values()) {
				horariosPorDia.put(dia, new StringBuilder());
			}
			List<HorarioDTO> horarios = reporteSimca.getHorarios();
			if (horarios != null) {
				for (HorarioDTO horario : horarios) {
					DiaSemanaEnum dia = horario.getDia();
					String horarioStr = horario.getHorarioInicio().format(timeFormatter) + "-" + horario.getHoraFin().format(timeFormatter);
					horariosPorDia.get(dia).append(horarioStr).append(", ");
				}
			}
			int colIndex = 9;
			for (DiaSemanaEnum dia : DiaSemanaEnum.values()) {
				StringBuilder horariosStr = horariosPorDia.get(dia);
				if (horariosStr.length() > 0) {
					horariosStr.setLength(horariosStr.length() - 2);
				}
				dataRow.createCell(colIndex++).setCellValue(horariosStr.toString());
			}
			dataRow.createCell(15).setCellValue(reporteSimca.getNombreDocente() != null ? reporteSimca.getNombreDocente() : "");
		}

		for (int i = 0; i < columnHeaders.length; i++) {
			sheet.autoSizeColumn(i);
		}
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		workbook.write(byteArrayOutputStream);

		String base64EncodedExcel = Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
		byteArrayOutputStream.close();
		workbook.close();
		ReporteSimcaDTO reporte = new ReporteSimcaDTO();
		reporte.setArchivoBase64(base64EncodedExcel);
		return reporte;
	}
}