package co.edu.unicauca.sgph.espaciofisico.infrastructure.output.persistence.gateway;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import co.edu.unicauca.sgph.agrupador.infrastructure.output.persistence.entity.AgrupadorEspacioFisicoEntity;
import co.edu.unicauca.sgph.espaciofisico.aplication.output.GestionarEspacioFisicoGatewayIntPort;
import co.edu.unicauca.sgph.espaciofisico.domain.model.Edificio;
import co.edu.unicauca.sgph.espaciofisico.domain.model.EspacioFisico;
import co.edu.unicauca.sgph.espaciofisico.domain.model.RecursoEspacioFisico;
import co.edu.unicauca.sgph.espaciofisico.domain.model.TipoEspacioFisico;
import co.edu.unicauca.sgph.espaciofisico.domain.model.Ubicacion;
import co.edu.unicauca.sgph.espaciofisico.infrastructure.input.DTORequest.EspacioFisicoInDTO;
import co.edu.unicauca.sgph.espaciofisico.infrastructure.input.DTORequest.FiltroEspacioFisicoAgrupadorDTO;
import co.edu.unicauca.sgph.espaciofisico.infrastructure.input.DTORequest.FiltroEspacioFisicoDTO;
import co.edu.unicauca.sgph.espaciofisico.infrastructure.input.DTOResponse.EspacioFisicoDTO;
import co.edu.unicauca.sgph.espaciofisico.infrastructure.input.DTOResponse.MensajeOutDTO;
import co.edu.unicauca.sgph.espaciofisico.infrastructure.input.DTOResponse.RecursoOutDTO;
import co.edu.unicauca.sgph.espaciofisico.infrastructure.input.mapper.EspacioFisicoRestMapper;
import co.edu.unicauca.sgph.espaciofisico.infrastructure.output.persistence.entity.EspacioFisicoEntity;
import co.edu.unicauca.sgph.espaciofisico.infrastructure.output.persistence.entity.EstadoEspacioFisicoEnum;
import co.edu.unicauca.sgph.espaciofisico.infrastructure.output.persistence.entity.RecursoEspacioFisicoEntity;
import co.edu.unicauca.sgph.espaciofisico.infrastructure.output.persistence.entity.RecursoFisicoEntity;
import co.edu.unicauca.sgph.espaciofisico.infrastructure.output.persistence.entity.TipoEspacioFisicoEntity;
import co.edu.unicauca.sgph.espaciofisico.infrastructure.output.persistence.entity.UbicacionEntity;
import co.edu.unicauca.sgph.espaciofisico.infrastructure.output.persistence.repository.EspacioFisicoRepositoryInt;
import co.edu.unicauca.sgph.espaciofisico.infrastructure.output.persistence.repository.RecursoEspacioFisicoRepositoryInt;
import co.edu.unicauca.sgph.espaciofisico.infrastructure.output.persistence.repository.RecursoFisicoRepositoryInt;
import co.edu.unicauca.sgph.espaciofisico.infrastructure.output.persistence.repository.TipoEspacioFisicoRepositoryInt;
import co.edu.unicauca.sgph.espaciofisico.infrastructure.output.persistence.repository.UbicacionRepositoryInt;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;


@Service
public class GestionarEspacioFisicoGatewayImplAdapter implements GestionarEspacioFisicoGatewayIntPort {

	@PersistenceContext
	private EntityManager entityManager;

	private EspacioFisicoRepositoryInt espacioFisicoRepositoryInt;
	private TipoEspacioFisicoRepositoryInt tipoEspacioFisicoRepositoryInt;
	private RecursoFisicoRepositoryInt recursoFisicoRepositoryInt;
	private EspacioFisicoRestMapper espacioFisicoRestMapper;
	private ModelMapper modelMapper;
	@Autowired
	private RecursoEspacioFisicoRepositoryInt recursoEspacioFisicoRepositoryInt;
	@Autowired
	private UbicacionRepositoryInt ubicacionRepositoryInt;

	public GestionarEspacioFisicoGatewayImplAdapter(EspacioFisicoRepositoryInt espacioFisicoRepositoryInt,
													TipoEspacioFisicoRepositoryInt tipoEspacioFisicoRepositoryInt, ModelMapper modelMapper, RecursoFisicoRepositoryInt recursoFisicoRepositoryInt,
													UbicacionRepositoryInt ubicacionRepositoryInt) {
		this.espacioFisicoRepositoryInt = espacioFisicoRepositoryInt;
		this.tipoEspacioFisicoRepositoryInt = tipoEspacioFisicoRepositoryInt;
		this.modelMapper = modelMapper;
		this.recursoFisicoRepositoryInt = recursoFisicoRepositoryInt;
		this.ubicacionRepositoryInt = ubicacionRepositoryInt;
	}

	@Override
	@Transactional
	public EspacioFisico guardarEspacioFisico(EspacioFisico espacioFisico) {
		EspacioFisicoEntity espacioFisicoEntity;

	    if (espacioFisico.getIdEspacioFisico() == null) {
	        // Modo creación: mapear y persistir la nueva entidad
	        espacioFisicoEntity = modelMapper.map(espacioFisico, EspacioFisicoEntity.class);
	        espacioFisicoEntity = espacioFisicoRepositoryInt.save(espacioFisicoEntity);
	    } else {
	        // Modo actualización:
	        // 1. Recuperar la entidad existente por su ID
	        EspacioFisicoEntity espacioFisicoEncontrado = espacioFisicoRepositoryInt
	            .findById(espacioFisico.getIdEspacioFisico())
	            .orElseThrow(() -> new EntityNotFoundException(
	                    "Espacio Físico no encontrado con ID: " + espacioFisico.getIdEspacioFisico()));

	        // 2. Configurar modelMapper para que omita:
	        //    - El id del espacio físico
	        //    - La propiedad 'ubicacion'
	        //    - La colección de recursos (por ejemplo, recursosEspacioFisico)
	        modelMapper.getConfiguration().setPropertyCondition(context -> context.getSource() != null);
	        modelMapper.typeMap(EspacioFisico.class, EspacioFisicoEntity.class)
	            .addMappings(mapper -> {
	                mapper.skip(EspacioFisicoEntity::setIdEspacioFisico);
	                mapper.skip(EspacioFisicoEntity::setUbicacion);
	                mapper.skip(EspacioFisicoEntity::setRecursosEspacioFisico);
	            });

	        // 3. Mapear los nuevos valores sobre la entidad existente (sin modificar el id, la ubicación ni la colección de recursos)
	        modelMapper.map(espacioFisico, espacioFisicoEncontrado);

	        // 4. Guardar la entidad actualizada
	        espacioFisicoEntity = espacioFisicoRepositoryInt.save(espacioFisicoEncontrado);
	    }

	    // Procesar los recursos asociados al espacio físico de forma manual
	    List<RecursoEspacioFisicoEntity> recursosProcesados = new ArrayList<>();

	    if (espacioFisico.getRecursosEspacioFisico() != null) {
	        for (RecursoEspacioFisico recurso : espacioFisico.getRecursosEspacioFisico()) {
	            if (recurso.getIdRecursoEspacioFisico() == null) {
	                throw new IllegalArgumentException("El recurso físico enviado es inválido o no tiene ID.");
	            }

	            RecursoFisicoEntity recursoFisicoEntity = recursoFisicoRepositoryInt
	                    .findById(recurso.getIdRecursoEspacioFisico())
	                    .orElseThrow(() -> new EntityNotFoundException(
	                            "Recurso Físico no encontrado con ID: " + recurso.getIdRecursoEspacioFisico()));

	            Optional<RecursoEspacioFisicoEntity> recursoExistente = recursoEspacioFisicoRepositoryInt
	                    .findByEspacioFisicoAndRecursoFisico(
	                            espacioFisicoEntity.getIdEspacioFisico(),
	                            recurso.getIdRecursoEspacioFisico()
	                    );

	            if (recursoExistente.isPresent()) {
	                RecursoEspacioFisicoEntity recursoActual = recursoExistente.get();
	                recursoActual.setCantidad(recurso.getCantidad());
	                recursosProcesados.add(recursoActual);
	            } else {
	                RecursoEspacioFisicoEntity recursoNuevo = new RecursoEspacioFisicoEntity();
	                recursoNuevo.setEspacioFisico(espacioFisicoEntity);
	                recursoNuevo.setRecursoFisico(recursoFisicoEntity);
	                recursoNuevo.setCantidad(recurso.getCantidad());
	                recursosProcesados.add(recursoNuevo);
	            }
	        }
	    }

	    recursoEspacioFisicoRepositoryInt.saveAll(recursosProcesados);

	    // Actualizar el objeto de dominio con el id persistido (opcional)
	    espacioFisico.setIdEspacioFisico(espacioFisicoEntity.getIdEspacioFisico());
	    return espacioFisico;
	}

	/**
	 * @see co.edu.unicauca.sgph.espaciofisico.aplication.output.GestionarEspacioFisicoGatewayIntPort#consultarEspacioFisicoPorIdEspacioFisico(java.lang.Long)
	 */
	@Override
	public EspacioFisico consultarEspacioFisicoPorIdEspacioFisico(Long idEspacioFisico) {
		return this.modelMapper.map(this.espacioFisicoRepositoryInt.findByIdEspacioFisico(idEspacioFisico), EspacioFisico.class);
	}

	/** 
	 * @see co.edu.unicauca.sgph.espaciofisico.aplication.output.GestionarEspacioFisicoGatewayIntPort#consultarEspacioFisicoHorarioPorIdCurso(java.lang.Long, java.lang.Boolean)
	 */
	@Override
	public List<String> consultarEspacioFisicoHorarioPorIdCurso(Long idCurso, Boolean esPrincipal) {
		return this.espacioFisicoRepositoryInt.consultarEspacioFisicoHorarioPorIdCurso(idCurso, esPrincipal);
	}

	/**
	 * @see co.edu.unicauca.sgph.espaciofisico.aplication.output.GestionarEspacioFisicoGatewayIntPort#consultarEspaciosFisicos(co.edu.unicauca.sgph.espaciofisico.infrastructure.input.DTORequest.FiltroEspacioFisicoDTO)
	 */
	@Override
	public Page<EspacioFisicoDTO> consultarEspaciosFisicos(FiltroEspacioFisicoDTO filtroEspacioFisicoDTO) {
		PageRequest pageable = null;
		if (Objects.nonNull(filtroEspacioFisicoDTO.getPagina())
				&& Objects.nonNull(filtroEspacioFisicoDTO.getRegistrosPorPagina())) {
			// Configuración de la paginación
			pageable = PageRequest.of(filtroEspacioFisicoDTO.getPagina(),
					filtroEspacioFisicoDTO.getRegistrosPorPagina());
		}

		// Construcción de la consulta con StringBuilder
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append(
				" SELECT NEW co.edu.unicauca.sgph.espaciofisico.infrastructure.input.DTOResponse.EspacioFisicoDTO(");
		queryBuilder.append(" espacioFisico.ubicacion.idUbicacion, espacioFisico.edificio.idEdificio, ");
		queryBuilder.append(" tipoEspacioFisico.tipo, espacioFisico.salon,");
		queryBuilder.append(" espacioFisico.capacidad, espacioFisico.estado, espacioFisico.idEspacioFisico, ");
		queryBuilder.append(" ubicacion.nombre, edificio.nombre )");
		queryBuilder.append(" FROM EspacioFisicoEntity espacioFisico");
		queryBuilder.append(" LEFT JOIN espacioFisico.tipoEspacioFisico tipoEspacioFisico");
		queryBuilder.append(" LEFT JOIN espacioFisico.ubicacion ubicacion");
		queryBuilder.append(" LEFT JOIN espacioFisico.edificio edificio");
		queryBuilder.append(" WHERE 1=1");

		Map<String, Object> parametros = new HashMap<>();

		if (Objects.nonNull(filtroEspacioFisicoDTO.getListaIdUbicacion())
				&& !filtroEspacioFisicoDTO.getListaIdUbicacion().isEmpty()) {
			queryBuilder.append(" AND espacioFisico.ubicacion.idUbicacion IN (:listaIdUbicacion)");
			parametros.put("listaIdUbicacion", filtroEspacioFisicoDTO.getListaIdUbicacion());
		}
		if (Objects.nonNull(filtroEspacioFisicoDTO.getListaIdEdificio())
				&& !filtroEspacioFisicoDTO.getListaIdEdificio().isEmpty()) {
			queryBuilder.append(" AND espacioFisico.edificio.idEdificio IN (:listaIdEdificio)");
			parametros.put("listaIdEdificio", filtroEspacioFisicoDTO.getListaIdEdificio());
		}
		if (Objects.nonNull(filtroEspacioFisicoDTO.getListaIdTipoEspacioFisico())
				&& !filtroEspacioFisicoDTO.getListaIdTipoEspacioFisico().isEmpty()) {
			queryBuilder.append(" AND tipoEspacioFisico.idTipoEspacioFisico IN (:listaIdTipoEspacioFisico)");
			parametros.put("listaIdTipoEspacioFisico", filtroEspacioFisicoDTO.getListaIdTipoEspacioFisico());
		}
		if (Objects.nonNull(filtroEspacioFisicoDTO.getSalon()) && !filtroEspacioFisicoDTO.getSalon().isEmpty()) {
			queryBuilder.append("AND espacioFisico.salon LIKE :salon ");
			parametros.put("salon", "%" + filtroEspacioFisicoDTO.getSalon().replaceAll("\\s+", " ").trim() + "%");
		}
		if (Objects.nonNull(filtroEspacioFisicoDTO.getEstado())) {
			queryBuilder.append(" AND espacioFisico.estado =:estado");
			parametros.put("estado", filtroEspacioFisicoDTO.getEstado());
		}
		if (Objects.nonNull(filtroEspacioFisicoDTO.getCapacidad())) {
			queryBuilder.append(" AND espacioFisico.capacidad =:capacidad");
			parametros.put("capacidad", filtroEspacioFisicoDTO.getCapacidad());
		}
		queryBuilder.append(" ORDER BY espacioFisico.ubicacion asc, espacioFisico.edificio asc ");

		// Realiza la consulta paginada
		TypedQuery<EspacioFisicoDTO> typedQuery = entityManager.createQuery(queryBuilder.toString(),
				EspacioFisicoDTO.class);

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
		} else {
			return new PageImpl<>(typedQuery.getResultList(), pageable,
					contarEspaciosFisicosConsultadas(filtroEspacioFisicoDTO));
		}
	}

	private Long contarEspaciosFisicosConsultadas(FiltroEspacioFisicoDTO filtroEspacioFisicoDTO) {

		// Construcción de la consulta con StringBuilder
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append(" SELECT COUNT(espacioFisico)");
		queryBuilder.append(" FROM EspacioFisicoEntity espacioFisico");
		queryBuilder.append(" LEFT JOIN espacioFisico.tipoEspacioFisico tipoEspacioFisico");
		queryBuilder.append(" WHERE 1=1");

		Map<String, Object> parametros = new HashMap<>();

		if (Objects.nonNull(filtroEspacioFisicoDTO.getListaIdUbicacion())
				&& !filtroEspacioFisicoDTO.getListaIdUbicacion().isEmpty()) {
			queryBuilder.append(" AND espacioFisico.ubicacion.idUbicacion IN (:listaIdUbicacion)");
			parametros.put("listaIdUbicacion", filtroEspacioFisicoDTO.getListaIdUbicacion());
		}
		if (Objects.nonNull(filtroEspacioFisicoDTO.getListaIdEdificio())
				&& !filtroEspacioFisicoDTO.getListaIdEdificio().isEmpty()) {
			queryBuilder.append(" AND espacioFisico.edificio.idEdificio IN (:listaIdEdificio)");
			parametros.put("listaIdEdificio", filtroEspacioFisicoDTO.getListaIdEdificio());
		}
		if (Objects.nonNull(filtroEspacioFisicoDTO.getListaIdTipoEspacioFisico())
				&& !filtroEspacioFisicoDTO.getListaIdTipoEspacioFisico().isEmpty()) {
			queryBuilder.append(" AND tipoEspacioFisico.idTipoEspacioFisico IN (:listaIdTipoEspacioFisico)");
			parametros.put("listaIdTipoEspacioFisico", filtroEspacioFisicoDTO.getListaIdTipoEspacioFisico());
		}
		if (Objects.nonNull(filtroEspacioFisicoDTO.getSalon()) && !filtroEspacioFisicoDTO.getSalon().isEmpty()) {
			queryBuilder.append("AND espacioFisico.salon LIKE :salon ");
			parametros.put("salon", "%" + filtroEspacioFisicoDTO.getSalon().replaceAll("\\s+", " ").trim() + "%");
		}
		if (Objects.nonNull(filtroEspacioFisicoDTO.getEstado())) {
			queryBuilder.append(" AND espacioFisico.estado =:estado");
			parametros.put("estado", filtroEspacioFisicoDTO.getEstado());
		}
		if (Objects.nonNull(filtroEspacioFisicoDTO.getCapacidad())) {
			queryBuilder.append(" AND espacioFisico.capacidad =:capacidad");
			parametros.put("capacidad", filtroEspacioFisicoDTO.getCapacidad());
		}
		queryBuilder.append(" ORDER BY espacioFisico.ubicacion asc, espacioFisico.edificio asc ");

		// Realiza la consulta para contar
		TypedQuery<Long> countQuery = entityManager.createQuery(queryBuilder.toString(), Long.class);

		// Establece parámetros en la consulta
		for (Map.Entry<String, Object> entry : parametros.entrySet()) {
			countQuery.setParameter(entry.getKey(), entry.getValue());
		}

		return countQuery.getSingleResult();
	}

	/**
	 * @see co.edu.unicauca.sgph.espaciofisico.aplication.output.GestionarEspacioFisicoGatewayIntPort#consultarTiposEspaciosFisicosPorEdificio(java.util.List)
	 */
	@Override
	public List<TipoEspacioFisico> consultarTiposEspaciosFisicosPorEdificio(List<Long> lstIdEdificio) {

		List<TipoEspacioFisicoEntity> lstTipoEspacioFisicoEntity = this.tipoEspacioFisicoRepositoryInt
				.consultarTiposEspaciosFisicosPorEdificio(lstIdEdificio);
		return this.modelMapper.map(lstTipoEspacioFisicoEntity, new TypeToken<List<TipoEspacioFisico>>() {
		}.getType());

	}

	/**
	 * @see co.edu.unicauca.sgph.espaciofisico.aplication.output.GestionarEspacioFisicoGatewayIntPort#consultarEdificios()
	 */
	@Override
	public List<String> consultarEdificios() {
		return this.espacioFisicoRepositoryInt.consultarEdificios();
	}

	/**
	 * @see co.edu.unicauca.sgph.espaciofisico.aplication.output.GestionarEspacioFisicoGatewayIntPort#consultarUbicaciones()
	 */
	@Override
	public List<String> consultarUbicaciones() {
		return this.espacioFisicoRepositoryInt.consultarUbicaciones();

	}

	/**
	 * @see co.edu.unicauca.sgph.espaciofisico.aplication.output.GestionarEspacioFisicoGatewayIntPort#consultarEdificiosPorUbicacion(java.util.List)
	 */
	@Override
	public List<Edificio> consultarEdificiosPorUbicacion(List<Long> lstIdUbicacion) {
		if (!lstIdUbicacion.isEmpty()) {
			return this.modelMapper.map(this.espacioFisicoRepositoryInt.consultarEdificiosPorUbicacion(lstIdUbicacion), new TypeToken<List<EspacioFisico>>() {
			}.getType());
		} else {
			return new ArrayList<>();
		}
	}

	/**
	 * @see co.edu.unicauca.sgph.espaciofisico.aplication.output.GestionarEspacioFisicoGatewayIntPort#consultarCapacidadEstadoYSalonPorListaIdEspacioFisico(java.util.List)
	 */
	@Override
	public List<EspacioFisico> consultarCapacidadEstadoYSalonPorListaIdEspacioFisico(List<Long> lstIdEspacioFisico) {
		List<EspacioFisicoEntity> lstEspacioFisicoEntity = this.espacioFisicoRepositoryInt
				.consultarCapacidadEstadoYSalonPorListaIdEspacioFisico(lstIdEspacioFisico);

		return this.modelMapper.map(lstEspacioFisicoEntity, new TypeToken<List<EspacioFisico>>() {
		}.getType());
	}
			
	/**
	 * @see co.edu.unicauca.sgph.espaciofisico.aplication.output.GestionarEspacioFisicoGatewayIntPort#consultarEspacioFisicoCursoPorIdHorario(java.lang.Long,
	 *      java.lang.Boolean)
	 */
	@Override
	public EspacioFisico consultarEspacioFisicoCursoPorIdHorario(Long idHorario, Boolean esPrincipal) {			
		EspacioFisicoEntity espacioFisicoEntity = this.espacioFisicoRepositoryInt.consultarEspacioFisicoCursoPorIdHorario(idHorario, esPrincipal);
		if(Objects.isNull(espacioFisicoEntity)) {
			return new EspacioFisico();
		}		
		return this.modelMapper.map(espacioFisicoEntity,EspacioFisico.class);
	}

	@Override
	public List<EspacioFisicoDTO> obtenerEspaciosFisicosPorAgrupadorId(Long idAgrupador) {
		List<EspacioFisicoEntity> espaciosFisicos = this.espacioFisicoRepositoryInt.findAll();
		List<EspacioFisicoEntity> espaciosFisicosAsignados = espaciosFisicos.stream()
				.filter(espacio -> espacio.getAgrupadores().stream()
						.anyMatch(agrupador -> agrupador.getIdAgrupadorEspacioFisico().equals(idAgrupador)))
				.collect(Collectors.toList());
		return espaciosFisicosAsignados.stream().map(this::mapEspacioFisico).collect(Collectors.toList());
	}

	@Override
	public List<EspacioFisicoDTO> obtenerEspaciosFisicosSinAsignarAAgrupadorId(Long idAgrupador) {
		List<EspacioFisicoEntity> espaciosFisicos = this.espacioFisicoRepositoryInt.findAll();
		List<EspacioFisicoEntity> espaciosFisicosSinAsignar = espaciosFisicos.stream()
				.filter(espacio -> espacio.getAgrupadores().isEmpty()).collect(Collectors.toList());
		return espaciosFisicosSinAsignar.stream().map(this::mapEspacioFisico).collect(Collectors.toList());
	}

	@Override
	public List<EspacioFisicoDTO> consultarEspaciosFisicosConFiltro(FiltroEspacioFisicoAgrupadorDTO filtro) {
		List<EspacioFisicoEntity> lista = this.espacioFisicoRepositoryInt
				.obtenerEspacioFisicoPorFiltro(filtro.getNombre(), filtro.getUbicacion(), filtro.getTipo());
		if (lista != null) {
			lista = filtrarEspaciosFisicosAgrupadosADiferenteAgrupadorId(lista, filtro.getIdAgrupador());
			return lista.stream().map(this::mapEspacioFisico).collect(Collectors.toList());
		} else {
			return null;
		}
	}

	@Override
	public List<RecursoOutDTO> obtenerListaRecursos() {
		return this.recursoFisicoRepositoryInt.findAll().stream().map(e -> new RecursoOutDTO(e.getIdRecursoFisico(), e.getNombre(), e.getDescripcion())).collect(Collectors.toList());
	}

	@Override
	public EspacioFisico guardarEspacioFisico(EspacioFisicoInDTO espacioFisicoInDTO) {
		EspacioFisicoEntity entidad = new EspacioFisicoEntity();
		if (espacioFisicoInDTO.getIdEspacioFisico() != null) {
			entidad.setIdEspacioFisico(espacioFisicoInDTO.getIdEspacioFisico());
		}
		entidad.setCapacidad(espacioFisicoInDTO.getCapacidad());
		entidad.setEstado(espacioFisicoInDTO.getEstado());
		entidad.setSalon(espacioFisicoInDTO.getSalon());
		UbicacionEntity ubicacion = new UbicacionEntity();
		ubicacion.setIdUbicacion(espacioFisicoInDTO.getIdUbicacion());
		entidad.setUbicacion(ubicacion);
		TipoEspacioFisicoEntity tipoEspacioFisico = new TipoEspacioFisicoEntity();
		tipoEspacioFisico.setIdTipoEspacioFisico(espacioFisicoInDTO.getIdTipoEspacioFisico());
		entidad.setTipoEspacioFisico(tipoEspacioFisico);
		entidad = this.espacioFisicoRepositoryInt.save(entidad);
		if (espacioFisicoInDTO.getSaveIdAgrupadores() != null) {
			entidad.setAgrupadores(
					espacioFisicoInDTO.getSaveIdAgrupadores().stream().map(g -> {
						AgrupadorEspacioFisicoEntity entidadG = new AgrupadorEspacioFisicoEntity();
						entidadG.setIdAgrupadorEspacioFisico(g);
						return entidadG;
					}).collect(Collectors.toList()));
		}
		this.eliminarRecursosActuales(entidad);
		this.agregarRecursosAEspacioFisico(espacioFisicoInDTO.getRecursos(), entidad);
		return this.modelMapper.map(entidad, EspacioFisico.class);
	}

	private void eliminarRecursosActuales(EspacioFisicoEntity entidad) {
		if (entidad.getIdEspacioFisico() != null) {
			List<RecursoEspacioFisicoEntity> recursosActuales = recursoEspacioFisicoRepositoryInt.findByEspacioFisicoIdEspacioFisico(entidad.getIdEspacioFisico());
			recursosActuales.forEach(r -> this.recursoEspacioFisicoRepositoryInt.delete(r));
		}

	}

	@Override
	public void activarInactivarEspacioFisico(Long id) {
		Optional<EspacioFisicoEntity> espacioFisicio = this.espacioFisicoRepositoryInt.findById(id);
		if (espacioFisicio.isPresent()) {
			EspacioFisicoEntity entidad = espacioFisicio.get();
			if (entidad.getEstado().equals(EstadoEspacioFisicoEnum.ACTIVO)) {
				entidad.setEstado(EstadoEspacioFisicoEnum.INACTIVO);
			} else {
				entidad.setEstado(EstadoEspacioFisicoEnum.ACTIVO);
			}
			this.espacioFisicoRepositoryInt.save(entidad);
		}
	}

	public void agregarRecursosAEspacioFisico(List<Long> recursos, EspacioFisicoEntity espacioFisico) {
		recursos.forEach(
				r -> {
					RecursoFisicoEntity recursoFisicoEntity = new RecursoFisicoEntity();
					recursoFisicoEntity.setIdRecursoFisico(r);
					RecursoEspacioFisicoEntity recursoEspacioFisicoEntity = new RecursoEspacioFisicoEntity();
					recursoEspacioFisicoEntity.setEspacioFisico(espacioFisico);
					recursoEspacioFisicoEntity.setRecursoFisico(recursoFisicoEntity);
					this.recursoEspacioFisicoRepositoryInt.save(recursoEspacioFisicoEntity);
				}
		);
	}

	private List<EspacioFisicoEntity> filtrarEspaciosFisicosAgrupadosADiferenteAgrupadorId(
			List<EspacioFisicoEntity> lista, Long idAgrupador) {
		return lista.stream().filter(a -> a.getAgrupadores().stream()
						.filter(b -> b.getIdAgrupadorEspacioFisico() == idAgrupador).collect(Collectors.toList()).isEmpty())
				.collect(Collectors.toList());
	}

	private EspacioFisicoDTO mapEspacioFisico(EspacioFisicoEntity entidad) {
		EspacioFisicoDTO dto = new EspacioFisicoDTO();
		dto.setIdEspacioFisico(entidad.getIdEspacioFisico());
		dto.setSalon(entidad.getSalon());
		return dto;
	}

	@Override
	public Boolean existsEspacioFisicoByOid(String OID) {
		EspacioFisicoEntity espacioFisicoEntity = this.espacioFisicoRepositoryInt.consultarEspacioFisicoPorOID(OID);
		if (Objects.nonNull(espacioFisicoEntity)) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}

	@Override
	public Boolean existsEspacioFisicoByNombre(String salon) {
		EspacioFisicoEntity espacioFisicoEntity = this.espacioFisicoRepositoryInt.consultarEspacioFisicoPorNombre(salon);
		if (Objects.nonNull(espacioFisicoEntity)) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}

	@Override
	public MensajeOutDTO cargaMasivaEspaciosFisicos(EspacioFisicoInDTO dto) {
		return null;
		/*String base64Excel = dto.getBase64();
	    byte[] decodedBytes = Base64.getDecoder().decode(base64Excel);
	    List<EspacioFisicoInDTO> espaciosFisicos = new ArrayList<>();
	    
	    try {
	        ByteArrayInputStream inputStream = new ByteArrayInputStream(decodedBytes);
	        Workbook workbook = new XSSFWorkbook(inputStream);
	        Sheet sheet = workbook.getSheetAt(0);
	        Iterator<Row> rowIterator = sheet.iterator();
	        rowIterator.next(); 

	        while (rowIterator.hasNext()) {
	            Row row = rowIterator.next();
	            if (row.getCell(0) == null) {
	                break; 
	            }

	            String oid = getCellValueAsString(row.getCell(0));
	            Long capacidad = Long.parseLong(getCellValueAsString(row.getCell(1)));
	            String salon = getCellValueAsString(row.getCell(2));
	            String estado = getCellValueAsString(row.getCell(3));
	            String tipoEspacio = getCellValueAsString(row.getCell(4));

	            Optional<TipoEspacioFisicoEntity> tipoEspacioFisicoEntityOptional = tipoEspacioFisicoRepositoryInt.findByNombre(tipoEspacio);

	            if (!tipoEspacioFisicoEntityOptional.isPresent()) {
	                MensajeOutDTO mensaje = new MensajeOutDTO();
	                mensaje.setError(Boolean.TRUE);
	                mensaje.setDescripcion("No existe el tipo de espacio físico " + tipoEspacio);
	                return mensaje;
	            }

	            TipoEspacioFisicoEntity tipoEspacioFisicoEntity = tipoEspacioFisicoEntityOptional.get();
	            EspacioFisicoInDTO espacioFisico = new EspacioFisicoInDTO(
	                    oid,
	                    capacidad,	                    
	                    EstadoEspacioFisicoEnum.valueOf(estado), 
	                    salon,
	                    tipoEspacioFisicoEntity.getIdTipoEspacioFisico()
	            );

	            espaciosFisicos.add(espacioFisico);
	        }

	        workbook.close();
	        inputStream.close();
	    } catch (IOException e) {
	        MensajeOutDTO mensaje = new MensajeOutDTO();
	        mensaje.setError(Boolean.TRUE);
	        mensaje.setDescripcion("Error al leer el archivo");
	        return mensaje;
	    } catch (java.io.IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    // Validar los espacios físicos antes de guardar
	    MensajeOutDTO mensaje = this.validarEspaciosFisicos(espaciosFisicos);
	    if (mensaje.getError()) {
	        return mensaje;
	    }

	    // Guardar los espacios físicos
	    for (EspacioFisicoInDTO espacioFisico : espaciosFisicos) {
	        this.guardarEspacioFisico(this.espacioFisicoRestMapper.toEspacioFisico(espacioFisico));
	    }

	    return mensaje;*/
		
	}

	private static String getCellValueAsString(Cell cell) {
	    if (cell == null) {
	        return "";
	    }

	    switch (cell.getCellType()) {
	        case STRING:
	            return cell.getStringCellValue();
	        case NUMERIC:
	            if (DateUtil.isCellDateFormatted(cell)) {
	                return cell.getDateCellValue().toString();
	            } else {
	                return String.valueOf((int) cell.getNumericCellValue());
	            }
	        case BOOLEAN:
	            return String.valueOf(cell.getBooleanCellValue());
	        case FORMULA:
	            return cell.getCellFormula();
	        default:
	            return "";
	    }
	}

	public MensajeOutDTO validarEspaciosFisicos(List<EspacioFisicoInDTO> espaciosFisicos) {
	    Set<String> oids = new HashSet<>();
	    Set<String> salones = new HashSet<>();
	    MensajeOutDTO mensaje = new MensajeOutDTO();
	    mensaje.setError(Boolean.FALSE);

	    for (EspacioFisicoInDTO espacioFisico : espaciosFisicos) {
	        // Validar que ningún atributo sea vacío
	        if (espacioFisico.getOID() == null || espacioFisico.getOID().isEmpty() ||
	            espacioFisico.getCapacidad() == null ||
	            espacioFisico.getSalon() == null || espacioFisico.getSalon().isEmpty() ||
	            espacioFisico.getEstado() == null ||
	            espacioFisico.getIdTipoEspacioFisico() == null) {
	            mensaje.setDescripcion("Todos los campos deben estar llenos y no ser nulos");
	            mensaje.setError(Boolean.TRUE);
	            return mensaje;
	        }

	        // Validar que el estado sea ACTIVO o INACTIVO
	        if (!espacioFisico.getEstado().equals(EstadoEspacioFisicoEnum.ACTIVO.name()) &&
	            !espacioFisico.getEstado().equals(EstadoEspacioFisicoEnum.INACTIVO.name())) {
	            mensaje.setDescripcion("El estado permitido debe ser ACTIVO o INACTIVO");
	            mensaje.setError(Boolean.TRUE);
	            return mensaje;
	        }

	        // Validar si el OID ya existe en la base de datos o está duplicado en la lista
	        Optional<EspacioFisicoEntity> espacioExistenteOid = Optional.of(this.espacioFisicoRepositoryInt.consultarEspacioFisicoPorOID(espacioFisico.getOID()));
	        if (espacioExistenteOid.isPresent() || !oids.add(espacioFisico.getOID())) {
	            mensaje.setDescripcion("El OID " + espacioFisico.getOID() + " está duplicado");
	            mensaje.setError(Boolean.TRUE);
	            return mensaje;
	        }

	        // Validar si el salón ya existe en la base de datos o está duplicado en la lista
	        Optional<EspacioFisicoEntity> espacioExistenteSalon = Optional.of(this.espacioFisicoRepositoryInt.consultarEspacioFisicoPorNombre(espacioFisico.getSalon()));
	        if (espacioExistenteSalon.isPresent() || !salones.add(espacioFisico.getSalon())) {
	            mensaje.setDescripcion("El salón " + espacioFisico.getSalon() + " está duplicado");
	            mensaje.setError(Boolean.TRUE);
	            return mensaje;
	        }
	    }

	    // Si pasa todas las validaciones, el mensaje es satisfactorio
	    mensaje.setDescripcion("Espacios físicos cargados correctamente");
	    return mensaje;
	}

	@Override
	public List<RecursoOutDTO> obtenerRecursosPorEspacioFisico(Long idEspacioFisico) {
		return null;
	}

	@Override
	public Map<String, Object> procesarArchivoExcel(MultipartFile file) throws Exception {
		List<EspacioFisico> espaciosFisicos = new ArrayList<>();
	    List<String> mensajesErrores = new ArrayList<>();

	    try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
	        Sheet sheet = workbook.getSheetAt(0);

	        for (Row row : sheet) {
	            if (row.getRowNum() == 0) continue; // Saltar encabezados

	            try {
	                EspacioFisico espacioFisico = new EspacioFisico();

	                String nombreUbicacion = row.getCell(0).getStringCellValue();
	                String salon = getCellValueAsString(row.getCell(1));
	                String tipo = row.getCell(2).getStringCellValue();
	                String oid = getCellValueAsString(row.getCell(3));
	                Long capacidad = (long) row.getCell(4).getNumericCellValue();
	                String estadoStr = row.getCell(5).getStringCellValue();
	                EstadoEspacioFisicoEnum estado = EstadoEspacioFisicoEnum.valueOf(estadoStr.toUpperCase());

	                // Validar que los campos obligatorios no estén vacíos
	                if (salon == null || salon.isEmpty()) {
	                    mensajesErrores.add("Error en la fila " + (row.getRowNum() + 1) + ": El salón es obligatorio.");
	                    continue;
	                }
	                if (oid == null || oid.isEmpty()) {
	                    mensajesErrores.add("Error en la fila " + (row.getRowNum() + 1) + ": El OID es obligatorio.");
	                    continue;
	                }

	                // Buscar entidades relacionadas
	                UbicacionEntity ubicacion = ubicacionRepositoryInt.findByNombre(nombreUbicacion)
	                        .orElseThrow(() -> new IllegalArgumentException("Ubicación no encontrada: " + nombreUbicacion));
	                Ubicacion ubicacionMap = modelMapper.map(ubicacion, Ubicacion.class);
	                TipoEspacioFisicoEntity tipoEspacio = tipoEspacioFisicoRepositoryInt.findByTipo(tipo)
	                        .orElseThrow(() -> new IllegalArgumentException("Tipo no encontrado: " + tipo));
	                TipoEspacioFisico tipoMap = modelMapper.map(tipoEspacio, TipoEspacioFisico.class);

	                // Verificar si el OID o el salón ya existen
	                EspacioFisicoEntity espacioOID = espacioFisicoRepositoryInt.consultarEspacioFisicoPorOID(oid);
	                
	                EspacioFisicoEntity espacioSalon = espacioFisicoRepositoryInt.consultarEspacioFisicoPorNombre(salon);

	                if (espacioOID!=null) {
	                    mensajesErrores.add("Error en la fila " + (row.getRowNum() + 1) + ": El OID '" + oid + "' ya existe.");
	                    continue;
	                }

	                if (espacioSalon!=null) {
	                    mensajesErrores.add("Error en la fila " + (row.getRowNum() + 1) + ": El salón '" + salon + "' ya existe.");
	                    continue;
	                }

	                // Crear el objeto EspacioFisico
	                espacioFisico.setUbicacion(ubicacionMap);
	                espacioFisico.setSalon(salon);
	                espacioFisico.setOID(oid);
	                espacioFisico.setCapacidad(capacidad);
	                espacioFisico.setEstado(estado);
	                espacioFisico.setTipoEspacioFisico(tipoMap);

	                espaciosFisicos.add(espacioFisico);
	            } catch (Exception e) {
	                mensajesErrores.add("Error en la fila " + (row.getRowNum() + 1) + ": " + e.getMessage());
	            }
	        }
	    } catch (Exception e) {
	        throw new Exception("Error al procesar el archivo: " + e.getMessage());
	    }

	    // Retornar espacios físicos procesados y los mensajes de error
	    Map<String, Object> resultado = new HashMap<>();
	    resultado.put("espaciosFisicos", espaciosFisicos);
	    resultado.put("mensajesErrores", mensajesErrores);

	    return resultado;
	}

}