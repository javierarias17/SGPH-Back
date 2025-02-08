package co.edu.unicauca.sgph.docente.infrastructure.output.persistence.gateway;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.commons.codec.binary.Base64;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import co.edu.unicauca.sgph.asignatura.infrastructure.output.persistence.repository.AsignaturaRepositoryInt;
import co.edu.unicauca.sgph.curso.infrastructure.output.persistence.repository.CursoRepositoryInt;
import co.edu.unicauca.sgph.docente.aplication.output.GestionarDocenteGatewayIntPort;
import co.edu.unicauca.sgph.docente.domain.model.Docente;
import co.edu.unicauca.sgph.docente.infrastructure.input.DTORequest.DocenteLaborDTO;
import co.edu.unicauca.sgph.docente.infrastructure.input.DTORequest.FiltroDocenteDTO;
import co.edu.unicauca.sgph.docente.infrastructure.input.DTOResponse.DocenteOutDTO;
import co.edu.unicauca.sgph.docente.infrastructure.output.persistence.entity.DocenteEntity;
import co.edu.unicauca.sgph.docente.infrastructure.output.persistence.repository.DocenteRepositoryInt;
import co.edu.unicauca.sgph.persona.infrastructure.output.persistence.repository.PersonaRepositoryInt;

@Service
public class GestionarDocenteGatewayImplAdapter implements GestionarDocenteGatewayIntPort {

	@PersistenceContext
	private EntityManager entityManager;

	private final PersonaRepositoryInt personaRepository;
    private final AsignaturaRepositoryInt asignaturaRepository;
    private final CursoRepositoryInt cursoRepository;	
	private DocenteRepositoryInt docenteRepositoryInt;
	private ModelMapper modelMapper;
	private final RestTemplate restTemplate;

	public GestionarDocenteGatewayImplAdapter(PersonaRepositoryInt personaRepository, DocenteRepositoryInt docenteRepository, AsignaturaRepositoryInt asignaturaRepository, CursoRepositoryInt cursoRepository, ModelMapper modelMapper, RestTemplate restTemplate) {
		this.docenteRepositoryInt = docenteRepository;
		this.personaRepository = personaRepository;
        this.asignaturaRepository = asignaturaRepository;
        this.cursoRepository = cursoRepository;
		this.modelMapper = modelMapper;
		this.restTemplate = restTemplate;
	}

	@Override
	public Docente guardarDocente(Docente docente) {
		DocenteEntity entidadGuardada = this.docenteRepositoryInt.save(
                this.modelMapper.map(docente, DocenteEntity.class));
        return this.modelMapper.map(entidadGuardada, Docente.class);
	}

	/** 
	 * @see co.edu.unicauca.sgph.docente.aplication.output.GestionarDocenteGatewayIntPort#consultarDocentePorIdentificacion(java.lang.Long, java.lang.String)
	 */
	@Override
	public Docente consultarDocentePorIdentificacion(Long idTipoIdentificacion, String numeroIdentificacion) {		
		DocenteEntity docenteEntity = this.docenteRepositoryInt
				.consultarDocentePorIdentificacion(idTipoIdentificacion, numeroIdentificacion);
		if (Objects.isNull(docenteEntity)) {
			return null;
		}	
		return this.modelMapper.map(docenteEntity, Docente.class);
	}

	/** 
	 * @see co.edu.unicauca.sgph.docente.aplication.output.GestionarDocenteGatewayIntPort#consultarDocentePorIdPersona(java.lang.Long)
	 */
	@Override
	public Docente consultarDocentePorIdPersona(Long idPersona) {
		DocenteEntity docenteEntity = this.docenteRepositoryInt.consultarDocentePorIdPersona(idPersona);
		if (Objects.isNull(docenteEntity)) {
			return null;
		} else {
			return this.modelMapper.map(docenteEntity, Docente.class);
		}
	}

	/** 
	 * @see co.edu.unicauca.sgph.docente.aplication.output.GestionarDocenteGatewayIntPort#consultarNombresDocentesPorIdCurso(java.lang.Long)
	 */
	@Override
	public List<String> consultarNombresDocentesPorIdCurso(Long idCurso) {
		return this.docenteRepositoryInt.consultarNombresDocentesPorIdCurso(idCurso);
	}

	/** 
	 * @see co.edu.unicauca.sgph.docente.aplication.output.GestionarDocenteGatewayIntPort#consultarDocentes(co.edu.unicauca.sgph.docente.infrastructure.input.DTORequest.FiltroDocenteDTO)
	 */
	@Override
	public Page<DocenteOutDTO> consultarDocentes(FiltroDocenteDTO filtroDocenteDTO) {
		PageRequest pageable = null;
		if (Objects.nonNull(filtroDocenteDTO.getPagina())
				&& Objects.nonNull(filtroDocenteDTO.getRegistrosPorPagina())) {
			// Configuración de la paginación
			pageable = PageRequest.of(filtroDocenteDTO.getPagina(), filtroDocenteDTO.getRegistrosPorPagina());
		}

		// Construcción de la consulta con StringBuilder
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append(" SELECT NEW co.edu.unicauca.sgph.docente.infrastructure.input.DTOResponse.DocenteOutDTO(");
		queryBuilder.append(" doc.idDocente, per.idPersona, ti.idTipoIdentificacion, per.numeroIdentificacion, ");
		queryBuilder.append(" ti.codigoTipoIdentificacion, per.primerNombre, per.segundoNombre, per.primerApellido, ");
		queryBuilder.append(" per.segundoApellido, per.email, doc.codigo, doc.estado, doc.departamento.idDepartamento)");
		queryBuilder.append(" FROM DocenteEntity doc ");
		queryBuilder.append(" JOIN doc.persona per ");
		queryBuilder.append(" JOIN per.tipoIdentificacion ti ");
		queryBuilder.append(" WHERE 1=1");

		Map<String, Object> parametros = new HashMap<>();

		if (Objects.nonNull(filtroDocenteDTO.getNombre())) {
			queryBuilder.append(" AND UPPER(TRIM(BOTH ' ' FROM CONCAT( ");
			queryBuilder.append(" per.primerNombre,");
			queryBuilder.append(" CASE WHEN per.segundoNombre IS NOT NULL THEN CONCAT(' ', per.segundoNombre) ELSE '' END,");
			queryBuilder.append(" CONCAT(' ',per.primerApellido),");
			queryBuilder.append(" CASE WHEN per.segundoApellido IS NOT NULL THEN CONCAT(' ', per.segundoApellido) ELSE '' END )))");
			queryBuilder.append(" LIKE UPPER(:nombre)");
			parametros.put("nombre", "%"+filtroDocenteDTO.getNombre().replaceAll("\\s+", " ").trim()+"%");
		}		
		if (Objects.nonNull(filtroDocenteDTO.getNumeroIdentificacion())
				&& !filtroDocenteDTO.getNumeroIdentificacion().isEmpty()) {
			queryBuilder.append(" AND per.numeroIdentificacion LIKE :numeroIdentificacion");
			parametros.put("numeroIdentificacion",
					"%" + filtroDocenteDTO.getNumeroIdentificacion().replaceAll("\\s+", " ").trim() + "%");
		}
		if (Objects.nonNull(filtroDocenteDTO.getCodigo()) && !filtroDocenteDTO.getCodigo().isEmpty()) {
			queryBuilder.append(" AND doc.codigo LIKE :codigo");
			parametros.put("codigo", "%" + filtroDocenteDTO.getCodigo().replaceAll("\\s+", " ").trim() + "%");
		}
		if (Objects.nonNull(filtroDocenteDTO.getEstado())) {
			queryBuilder.append(" AND doc.estado = :estado");
			parametros.put("estado", filtroDocenteDTO.getEstado());
		}

		queryBuilder.append(" ORDER BY per.primerNombre asc");

		// Realiza la consulta paginada
		TypedQuery<DocenteOutDTO> typedQuery = entityManager.createQuery(queryBuilder.toString(), DocenteOutDTO.class);
		
		if (Objects.nonNull(pageable)) {
	        typedQuery.setFirstResult((int) pageable.getOffset());
	        typedQuery.setMaxResults(pageable.getPageSize());
	    }

		// Establece parámetros en la consulta
		for (Map.Entry<String, Object> entry : parametros.entrySet()) {
			typedQuery.setParameter(entry.getKey(), entry.getValue());
		}

		 // Si pageable es nulo, entonces retornar todos los resultados sin paginación
	    if (Objects.isNull(pageable)) {
	        return new PageImpl<>(typedQuery.getResultList());
	    }else {
	    	return new PageImpl<>(typedQuery.getResultList(), pageable, contarDocentesConsultados(filtroDocenteDTO));
	    }
	}

	/**
	 * Método encargado de contabilizar los docentes por filtro<br>
	 * 
	 * @author apedro
	 * 
	 * @param filtroDocenteDTO
	 * @return
	 */
	private Long contarDocentesConsultados(FiltroDocenteDTO filtroDocenteDTO) {

		// Construcción de la consulta con StringBuilder
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append(" SELECT COUNT(doc)");
		queryBuilder.append(" FROM DocenteEntity doc ");
		queryBuilder.append(" JOIN doc.persona per ");
		queryBuilder.append(" JOIN per.tipoIdentificacion ti ");
		queryBuilder.append(" WHERE 1=1");

		Map<String, Object> parametros = new HashMap<>();

		if (Objects.nonNull(filtroDocenteDTO.getNombre())) {
			queryBuilder.append(" AND UPPER(TRIM(BOTH ' ' FROM CONCAT( ");
			queryBuilder.append(" per.primerNombre,");
			queryBuilder.append(" CASE WHEN per.segundoNombre IS NOT NULL THEN CONCAT(' ', per.segundoNombre) ELSE '' END,");
			queryBuilder.append(" per.primerApellido,");
			queryBuilder.append(" CASE WHEN per.segundoApellido IS NOT NULL THEN CONCAT(' ', per.segundoApellido) ELSE '' END )))");
			queryBuilder.append(" LIKE UPPER(:nombre)");
			parametros.put("nombre", "%"+filtroDocenteDTO.getNombre().replaceAll("\\s+", " ").trim()+"%");
		}
		if (Objects.nonNull(filtroDocenteDTO.getNumeroIdentificacion())
				&& !filtroDocenteDTO.getNumeroIdentificacion().isEmpty()) {
			queryBuilder.append(" AND per.numeroIdentificacion LIKE :numeroIdentificacion");
			parametros.put("numeroIdentificacion",
					"%" + filtroDocenteDTO.getNumeroIdentificacion().replaceAll("\\s+", " ").trim() + "%");
		}
		if (Objects.nonNull(filtroDocenteDTO.getCodigo()) && !filtroDocenteDTO.getCodigo().isEmpty()) {
			queryBuilder.append(" AND doc.codigo LIKE :codigo");
			parametros.put("codigo", "%" + filtroDocenteDTO.getCodigo().replaceAll("\\s+", " ").trim() + "%");
		}
		if (Objects.nonNull(filtroDocenteDTO.getEstado())) {
			queryBuilder.append(" AND doc.estado = :estado");
			parametros.put("estado", filtroDocenteDTO.getEstado());
		}

		queryBuilder.append(" ORDER BY per.primerNombre asc");

		// Realiza la consulta para contar
		TypedQuery<Long> countQuery = entityManager.createQuery(queryBuilder.toString(), Long.class);

		// Establece parámetros en la consulta
		for (Map.Entry<String, Object> entry : parametros.entrySet()) {
			countQuery.setParameter(entry.getKey(), entry.getValue());
		}

		return countQuery.getSingleResult();
	}

	/** 
	 * @see co.edu.unicauca.sgph.docente.aplication.output.GestionarDocenteGatewayIntPort#consultarDocentePorIdCurso(java.lang.Long)
	 */
	@Override
	public List<Docente> consultarDocentePorIdCurso(Long idCurso) {
		List<DocenteEntity> docenteEntities = this.docenteRepositoryInt.consultarDocentePorIdCurso(idCurso);
		return this.modelMapper.map(docenteEntities, new TypeToken<List<Docente>>() {
		}.getType());
	}

	/** 
	 * @see co.edu.unicauca.sgph.docente.aplication.output.GestionarDocenteGatewayIntPort#existsDocenteByCodigo(java.lang.String, java.lang.Long)
	 */
	@Override
	public Boolean existsDocenteByCodigo(String codigo, Long idDocente) {
		DocenteEntity docenteEntity = this.docenteRepositoryInt.consultarDocentePorCodigo(codigo, idDocente);
		if (Objects.nonNull(docenteEntity)) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}

	/** 
	 * @see co.edu.unicauca.sgph.docente.aplication.output.GestionarDocenteGatewayIntPort#existeDocentePorIdPersona(java.lang.Long, java.lang.Long)
	 */
	@Override
	public Boolean existeDocentePorIdPersona(Long idPersona, Long idDocente) {
		DocenteEntity docenteEntity = this.docenteRepositoryInt.consultarUsuarioPorIdPersona(idPersona, idDocente);
		if (Objects.nonNull(docenteEntity)) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}

	@Override
	public List<DocenteLaborDTO> cargarLaborDocente(String nombrePrograma, String periodoVigente) throws IOException {
		return null;
		

	}

	@Override
	public String obtenerBase64ArchivoFiltrado(String nombrePrograma, String periodoVigente) throws IOException {
		return this.generarExcelBase64(cargarLaborDocente(nombrePrograma, periodoVigente));
	}

	@Override
	public Docente consultarDocentePorNumeroIdentificacion(String numeroIdentificacion) {
		DocenteEntity docenteEntity = this.docenteRepositoryInt.consultarDocentePorIdentificacion(numeroIdentificacion);
		if (Objects.isNull(docenteEntity)) {
			return null;
		}
		return this.modelMapper.map(docenteEntity, Docente.class);
	}

	public static List<DocenteLaborDTO> mockLeerExcelBase64(String base64Excel) throws IOException {
		return null;
		
		
	}
	
	private static String getStringCellValue(Row row, int cellIndex) {
		Cell cell = row.getCell(cellIndex);
		return cell != null ? cell.getStringCellValue() : "";
	}

	private static int getIntCellValue(Row row, int cellIndex) {
		Cell cell = row.getCell(cellIndex);
		return cell != null ? (int) cell.getNumericCellValue() : 0;
	}
	private static boolean validarGrupoYDocente(DocenteLaborDTO docenteLaborDTO) {
		// TODO   DADO que el excel compartido tiene casillas con grupos faltantes
		if (docenteLaborDTO.getGrupo() == null || docenteLaborDTO.getIdentificacion() == null
		|| docenteLaborDTO.getPrimerNombre() == null || docenteLaborDTO.getPrimerApellido() == null) {
			return false;
		}
		return true;
	}
	public static String generarExcelBase64(List<DocenteLaborDTO> docenteLaborDTOList) throws IOException {
		return null;
		
	}
	
	public Long obtenerIdDepartamentoPorNombre(String nombreDepartamento) {
        // URL del servicio que devuelve el ID del departamento basado en el nombre
        String url = "http://localhost:8080/AdministrarDepartamento/obtenerIdPorNombre?nombre=" + nombreDepartamento;
        
        // Realiza la llamada al servicio REST
        ResponseEntity<Long> response = restTemplate.getForEntity(url, Long.class);
        
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return response.getBody();
        } else {
            throw new RuntimeException("Error al obtener el ID del departamento: " + nombreDepartamento);
        }
    }
	
	@Override
	public void guardarLaborDocente(DocenteLaborDTO docenteLaborDTO) {
		/*
		// 1. Persistir Persona
        PersonaEntity persona = new PersonaEntity();
        persona.setNumeroIdentificacion(docenteLaborDTO.getIdentificacion());
        persona.setPrimerNombre(docenteLaborDTO.getPrimerNombre());
        persona.setSegundoNombre(docenteLaborDTO.getSegundoNombre());
        persona.setPrimerApellido(docenteLaborDTO.getPrimerApellido());
        persona.setSegundoApellido(docenteLaborDTO.getSegundoApellido());
        persona.setEmail(docenteLaborDTO.getCorreo());
        persona = personaRepository.save(persona);
        
        // 2. Persistir Docente asociado a la Persona
        DocenteEntity docente = new DocenteEntity();
        docente.setPersona(persona);
        docente.getDepartamento().setNombre(docenteLaborDTO.getDepartamento());
        docente.setIdDocente(Long.parseLong(docenteLaborDTO.getIdentificacion()));
        docente = docenteRepositoryInt.save(docente);

        // 3. Persistir Asignatura
        AsignaturaEntity asignatura = new AsignaturaEntity();
        asignatura.setCodigoMateria(docenteLaborDTO.getCodigoMateria());
        asignatura.setNombreMateria(docenteLaborDTO.getNombreMateria());
        asignatura.setTipoAsignatura(docenteLaborDTO.getTipoAsignatura());
        asignatura.setHorasTeoricas(docenteLaborDTO.getHorasTeoricas());
        asignatura = asignaturaRepository.save(asignatura);

        // 4. Persistir Curso asociado a la Asignatura
        CursoEntity curso = new CursoEntity();
        curso.setAsignatura(asignatura);
        curso.setGrupo(docenteLaborDTO.getGrupo());
        curso.setHorasSemanales(docenteLaborDTO.getHorasSemanales());
        curso.setSemestre(docenteLaborDTO.getSemestre());
        cursoRepository.save(curso);
    }
		*/
	}

	@Override
	public Long contarDocente() {
		return this.docenteRepositoryInt.contarDocentes();
	}

	@Override
	public Boolean eliminarCargue(Long idPrograma, Long idPeriodo) {
		return true;
		//return this.docenteRepositoryInt.eliminarCargue(idPrograma, idPeriodo);
	}
}