package co.edu.unicauca.sgph.docente.infrastructure.output.persistence.gateway;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import co.edu.unicauca.sgph.common.infrastructure.output.persistence.entities.TipoIdentificacionEntity;
import co.edu.unicauca.sgph.docente.aplication.output.GestionarDocenteGatewayIntPort;
import co.edu.unicauca.sgph.docente.domain.model.Docente;
import co.edu.unicauca.sgph.docente.infrastructure.input.DTORequest.FiltroDocenteDTO;
import co.edu.unicauca.sgph.docente.infrastructure.input.DTOResponse.DocenteOutDTO;
import co.edu.unicauca.sgph.docente.infrastructure.output.persistence.entity.DocenteEntity;
import co.edu.unicauca.sgph.docente.infrastructure.output.persistence.repository.DocenteRepositoryInt;

@Service
public class GestionarDocenteGatewayImplAdapter implements GestionarDocenteGatewayIntPort {

	@PersistenceContext
	private EntityManager entityManager;

	private DocenteRepositoryInt docenteRepositoryInt;
	private ModelMapper modelMapper;

	public GestionarDocenteGatewayImplAdapter(DocenteRepositoryInt docenteRepository, ModelMapper modelMapper) {
		this.docenteRepositoryInt = docenteRepository;
		this.modelMapper = modelMapper;
	}

	@Override
	public Docente guardarDocente(Docente docente) {
		return this.modelMapper.map(this.docenteRepositoryInt.save(this.modelMapper.map(docente, DocenteEntity.class)),
				Docente.class);
	}

	/* Javier Arias */
	@Override
	public Docente consultarDocentePorIdentificacion(Long idTipoIdentificacion, String numeroIdentificacion) {
		return this.modelMapper.map(this.docenteRepositoryInt.findByTipoIdentificacionAndNumeroIdentificacion(
				new TipoIdentificacionEntity(idTipoIdentificacion), numeroIdentificacion), Docente.class);
	}

	@Override
	public Docente consultarDocentePorIdPersona(Long idPersona) {
		DocenteEntity docenteEntity = this.docenteRepositoryInt.findByIdPersona(idPersona);
		if (Objects.isNull(docenteEntity)) {
			return null;
		} else {
			return this.modelMapper.map(docenteEntity, Docente.class);
		}
	}

	@Override
	public List<String> consultarNombresDocentesPorIdCurso(Long idCurso) {
		return this.docenteRepositoryInt.consultarNombresDocentesPorIdCurso(idCurso);
	}

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
		queryBuilder.append(" doc.idPersona, ti.idTipoIdentificacion, doc.numeroIdentificacion, ");
		queryBuilder.append(" ti.codigoTipoIdentificacion, doc.primerNombre, doc.segundoNombre, doc.primerApellido, ");
		queryBuilder.append(" doc.segundoApellido, doc.email, doc.codigo, doc.estado)");
		queryBuilder.append(" FROM DocenteEntity doc ");
		queryBuilder.append(" JOIN doc.tipoIdentificacion ti ");
		queryBuilder.append(" WHERE 1=1");

		Map<String, Object> parametros = new HashMap<>();

		if (Objects.nonNull(filtroDocenteDTO.getNombre())) {
			queryBuilder.append(" AND UPPER(TRIM(BOTH ' ' FROM CONCAT( ");
			queryBuilder.append(" doc.primerNombre,");
			queryBuilder.append(" CASE WHEN doc.segundoNombre IS NOT NULL THEN CONCAT(' ', doc.segundoNombre) ELSE '' END,");
			queryBuilder.append(" CONCAT(' ',doc.primerApellido),");
			queryBuilder.append(" CASE WHEN doc.segundoApellido IS NOT NULL THEN CONCAT(' ', doc.segundoApellido) ELSE '' END )))");
			queryBuilder.append(" LIKE UPPER(:nombre)");
			parametros.put("nombre", "%"+filtroDocenteDTO.getNombre().replaceAll("\\s+", " ").trim()+"%");
		}		
		if (Objects.nonNull(filtroDocenteDTO.getNumeroIdentificacion())) {
			queryBuilder.append(" AND doc.numeroIdentificacion = :numeroIdentificacion");
			parametros.put("numeroIdentificacion", filtroDocenteDTO.getNumeroIdentificacion().trim());
		}
		if (Objects.nonNull(filtroDocenteDTO.getCodigo())) {
			queryBuilder.append(" AND doc.codigo = :codigo");
			parametros.put("codigo", filtroDocenteDTO.getCodigo().trim());
		}
		if (Objects.nonNull(filtroDocenteDTO.getEstado())) {
			queryBuilder.append(" AND doc.estado = :estado");
			parametros.put("estado", filtroDocenteDTO.getEstado());
		}
		
		queryBuilder.append(" ORDER BY doc.primerNombre asc");

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

	private Long contarDocentesConsultados(FiltroDocenteDTO filtroDocenteDTO) {

		// Construcción de la consulta con StringBuilder
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append(" SELECT COUNT(doc)");
		queryBuilder.append(" FROM DocenteEntity doc ");
		queryBuilder.append(" JOIN doc.tipoIdentificacion ti ");
		queryBuilder.append(" WHERE 1=1");

		Map<String, Object> parametros = new HashMap<>();

		if (Objects.nonNull(filtroDocenteDTO.getNombre())) {
			queryBuilder.append(" AND UPPER(TRIM(BOTH ' ' FROM CONCAT( ");
			queryBuilder.append(" doc.primerNombre,");
			queryBuilder.append(" CASE WHEN doc.segundoNombre IS NOT NULL THEN CONCAT(' ', doc.segundoNombre) ELSE '' END,");
			queryBuilder.append(" doc.primerApellido,");
			queryBuilder.append(" CASE WHEN doc.segundoApellido IS NOT NULL THEN CONCAT(' ', doc.segundoApellido) ELSE '' END )))");
			queryBuilder.append(" LIKE UPPER(:nombre)");
			parametros.put("nombre", "%"+filtroDocenteDTO.getNombre().replaceAll("\\s+", " ").trim()+"%");
		}
		if (Objects.nonNull(filtroDocenteDTO.getNumeroIdentificacion())) {
			queryBuilder.append(" AND doc.numeroIdentificacion = :numeroIdentificacion");
			parametros.put("numeroIdentificacion", filtroDocenteDTO.getNumeroIdentificacion().trim());
		}
		if (Objects.nonNull(filtroDocenteDTO.getCodigo())) {
			queryBuilder.append(" AND doc.codigo = :codigo");
			parametros.put("codigo", filtroDocenteDTO.getCodigo().trim());
		}
		if (Objects.nonNull(filtroDocenteDTO.getEstado())) {
			queryBuilder.append(" AND doc.estado = :estado");
			parametros.put("estado", filtroDocenteDTO.getEstado());
		}
		
		queryBuilder.append(" ORDER BY doc.primerNombre asc");

		// Realiza la consulta para contar
		TypedQuery<Long> countQuery = entityManager.createQuery(queryBuilder.toString(), Long.class);

		// Establece parámetros en la consulta
		for (Map.Entry<String, Object> entry : parametros.entrySet()) {
			countQuery.setParameter(entry.getKey(), entry.getValue());
		}

		return countQuery.getSingleResult();
	}

	@Override
	public List<Docente> consultarDocentePorIdCurso(Long idCurso) {
		List<DocenteEntity> docenteEntities = this.docenteRepositoryInt.consultarDocentePorIdCurso(idCurso);
		return this.modelMapper.map(docenteEntities, new TypeToken<List<Docente>>() {
		}.getType());
	}
}