package co.edu.unicauca.sgph.docente.infrastructure.input.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.edu.unicauca.sgph.common.domain.model.CommonEJB;
import co.edu.unicauca.sgph.docente.aplication.input.GestionarDocenteCUIntPort;
import co.edu.unicauca.sgph.docente.infrastructure.input.DTORequest.DocenteInDTO;
import co.edu.unicauca.sgph.docente.infrastructure.input.DTORequest.DocenteLaborDTO;
import co.edu.unicauca.sgph.docente.infrastructure.input.DTORequest.FiltroDocenteDTO;
import co.edu.unicauca.sgph.docente.infrastructure.input.DTOResponse.DocenteOutDTO;
import co.edu.unicauca.sgph.docente.infrastructure.input.mapper.DocenteRestMapper;
import co.edu.unicauca.sgph.docente.infrastructure.output.persistence.entity.EstadoDocenteEnum;
import co.edu.unicauca.sgph.espaciofisico.infrastructura.input.validation.ValidationGroups;
import co.edu.unicauca.sgph.espaciofisico.infrastructure.input.DTOResponse.MensajeOutDTO;
import co.edu.unicauca.sgph.periodoacademico.aplication.input.GestionarPeriodoAcademicoCUIntPort;
import co.edu.unicauca.sgph.periodoacademico.domain.model.PeriodoAcademico;
import co.edu.unicauca.sgph.reporte.infraestructure.input.DTO.ReporteDocenteDTO;

@RestController
@RequestMapping("/AdministrarDocente")
public class DocenteController extends CommonEJB{

	// private static final Logger log =
	// LoggerFactory.getLogger(DocenteController.class);

	private GestionarDocenteCUIntPort gestionarDocenteCUIntPort;
	private DocenteRestMapper docenteRestMapper;
	private GestionarPeriodoAcademicoCUIntPort periodoAcademicoCUIntPort;

	public DocenteController(GestionarDocenteCUIntPort gestionarDocenteCUIntPort, DocenteRestMapper docenteRestMapper, GestionarPeriodoAcademicoCUIntPort periodoAcademicoCUIntPort) {
		this.gestionarDocenteCUIntPort = gestionarDocenteCUIntPort;
		this.docenteRestMapper = docenteRestMapper;
		this.periodoAcademicoCUIntPort = periodoAcademicoCUIntPort;
	}

	@PostMapping("/guardarDocente")
	public ResponseEntity<?> guardarDocente(@RequestBody DocenteInDTO docenteInDTO, BindingResult result) {
		Class<?> validationGroup = (docenteInDTO.getIdDocente() == null) 
                ? ValidationGroups.OnCreate.class 
                : ValidationGroups.OnUpdate.class;

		// Realizar la validación con el grupo correspondiente
		Set<ConstraintViolation<DocenteInDTO>> violations = validator.validate(docenteInDTO, validationGroup);
		
		// Convertir las violaciones en errores del BindingResult
		for (ConstraintViolation<DocenteInDTO> violation : violations) {
			String fieldName = violation.getPropertyPath().toString();
			String errorMessage = violation.getMessage();
			result.addError(new FieldError("docenteInDTO", fieldName, errorMessage));
		}
		
		// Si hay errores, retornar los mensajes de error
		if (result.hasErrors()) {
			return ResponseEntity.badRequest().body(result.getAllErrors());
		}
		
		if (Boolean.FALSE.equals(docenteInDTO.getEsValidar())) {
			DocenteOutDTO docenteOutDTO =  this.docenteRestMapper.toDocenteOutDTO(
					this.gestionarDocenteCUIntPort.guardarDocente(this.docenteRestMapper.toDocente(docenteInDTO)));

			return new ResponseEntity<>(docenteOutDTO, HttpStatus.OK);
	        
	    } else {
	        return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
	    }
		
	}

	@GetMapping("/consultarDocentePorIdentificacion")
	public DocenteOutDTO consultarDocentePorIdentificacion(@RequestParam Long idTipoIdentificacion,
			@RequestParam String numeroIdentificacion) {
		return this.docenteRestMapper.toDocenteOutDTO(this.gestionarDocenteCUIntPort
				.consultarDocentePorIdentificacion(idTipoIdentificacion, numeroIdentificacion));
	}

	/**
	 * Método encargado de consultar los nombres de docentes por curso<br>
	 * 
	 * @author apedro
	 * 
	 * @param idCurso
	 * @return
	 */
	@GetMapping("/consultarNombresDocentesPorIdCurso")
	public List<String> consultarNombresDocentesPorIdCurso(@RequestParam Long idCurso) {
		return this.gestionarDocenteCUIntPort.consultarNombresDocentesPorIdCurso(idCurso);
	}

	/**
	 * Método encargado de consultar los docentes por diferentes criterios de
	 * busqueda y retornarlos de manera paginada
	 * 
	 * @author apedro
	 * 
	 * @param filtroDocenteDTO DTO con los filtros de busqueda
	 * @return
	 */
	@GetMapping("/consultarDocentes")
	public Page<DocenteOutDTO> consultarDocentes(
		    @RequestParam(required = false) String nombre,
		    @RequestParam(required = false) String numeroIdentificacion,
		    @RequestParam(required = false) String codigo,
		    @RequestParam(required = false) EstadoDocenteEnum estado,
		    @RequestParam(defaultValue = "0") Integer pagina,
		    @RequestParam(defaultValue = "10") Integer registrosPorPagina) {

	    FiltroDocenteDTO filtroDocenteDTO = new FiltroDocenteDTO();
	    filtroDocenteDTO.setNombre(nombre);
	    filtroDocenteDTO.setNumeroIdentificacion(numeroIdentificacion);
	    filtroDocenteDTO.setCodigo(codigo);
	    filtroDocenteDTO.setEstado(estado);
	    filtroDocenteDTO.setPagina(pagina);
	    filtroDocenteDTO.setRegistrosPorPagina(registrosPorPagina);

	    return this.gestionarDocenteCUIntPort.consultarDocentes(filtroDocenteDTO);
	}

	/**
	 * Método encargado de obtener los docentes asociadas a un curso.
	 * 
	 * @author apedro
	 * 
	 * @param idCurso
	 * @return
	 */
	@GetMapping("/consultarDocentePorIdCurso")
	public List<DocenteOutDTO> consultarDocentePorIdCurso(@RequestParam Long idCurso) {
		return this.docenteRestMapper
				.toLstDocenteOutDTO(this.gestionarDocenteCUIntPort.consultarDocentePorIdCurso(idCurso));
	}

	/**
	 * Método que se ejecuta para cargar labor docente
	 *
	 *
	 * @author
	 *
	 *
	 *
	 */
	@PostMapping("/cargarLaborDocente")
	public MensajeOutDTO cargarLaborDocente(@RequestBody ReporteDocenteDTO archivoDocente) {
		return this.gestionarDocenteCUIntPort.cargarLaborDocente(archivoDocente);
	}
	@PostMapping("/consultaLaborDocente")
	public ReporteDocenteDTO consultaLaborDocente(@RequestBody ReporteDocenteDTO filtro) {
		return this.gestionarDocenteCUIntPort.consultaLaborDocente(filtro);
	}

	@Autowired
	private Validator validator;
	

	@PostMapping("/importar/{idFacultad}/{idPrograma}")
	public ResponseEntity<?> importarLaborDocente(
	        @PathVariable Long idFacultad,
	        @PathVariable Long idPrograma) {
	    List<DocenteLaborDTO> docenteLaborDTOList = new ArrayList<>();
	    List<String> erroresConContexto = new ArrayList<>();

	    PeriodoAcademico periodo = periodoAcademicoCUIntPort.consultarPeriodoAcademicoVigente();
	    
	    try {
	        // 1. Configuración del cliente WebClient
	        WebClient webClient = WebClient.builder()
	                .baseUrl("http://10.200.1.181:8081/api/v1")
	                .exchangeStrategies(ExchangeStrategies.builder()
	                        .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024)) // 10 MB
	                        .build())
	                .build();

	        String periodoActual = periodo.getAnio() + "-" + periodo.getPeriodo();// Cambiar dinámicamente según corresponda

	        // 2. Realizar la llamada al servicio externo
	        String jsonResponse = webClient.post()
	                .uri(uriBuilder -> uriBuilder
	                        .path("/labor/materias/{periodo}")
	                        .build(periodoActual))
	                .bodyValue(List.of(idPrograma)) // Enviar idPrograma como body
	                .retrieve()
	                .bodyToMono(String.class) // Obtener la respuesta como JSON crudo
	                .block();

	        // Registrar el JSON recibido para depuración
	        System.out.println("Respuesta del servicio externo: " + jsonResponse);

	        // 3. Mapear el JSON al DTO
	        ObjectMapper objectMapper = new ObjectMapper();
	        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // Ignorar propiedades desconocidas

	        docenteLaborDTOList = objectMapper.readValue(
	                jsonResponse,
	                new TypeReference<List<DocenteLaborDTO>>() {});

	        // Depurar datos deserializados
	        System.out.println("Datos mapeados correctamente: " + docenteLaborDTOList);

	        // 4. Validar si se obtuvieron datos
	        if (docenteLaborDTOList == null || docenteLaborDTOList.isEmpty()) {
	            return ResponseEntity.badRequest()
	                    .body("No se obtuvieron datos desde el servicio externo para el periodo y programa especificados.");
	        }

	        // 5. Validar los datos obtenidos
	        for (int i = 0; i < docenteLaborDTOList.size(); i++) {
	            DocenteLaborDTO dto = docenteLaborDTOList.get(i);
	            Set<ConstraintViolation<DocenteLaborDTO>> violaciones = validator.validate(dto);

	            if (!violaciones.isEmpty()) {
	                for (ConstraintViolation<DocenteLaborDTO> violacion : violaciones) {
	                    String mensajeError = String.format(
	                            "Error en el registro %d (Asignatura: %s, Grupo: %s, Identificación: %s): %s - %s",
	                            i + 1,
	                            dto.getNombreMateria() != null ? dto.getNombreMateria() : "N/A",
	                            dto.getGrupo() != null ? dto.getGrupo() : "N/A",
	                            dto.getIdentificacion() != null ? dto.getIdentificacion() : "N/A",
	                            violacion.getPropertyPath(),
	                            violacion.getMessage()
	                    );
	                    erroresConContexto.add(mensajeError);
	                }
	            }
	        }

	        // Si hay errores de validación, devolver la respuesta con los errores
	        if (!erroresConContexto.isEmpty()) {
	            return ResponseEntity.badRequest().body(erroresConContexto);
	        }

	        // 6. Procesar los datos obtenidos
	        List<String> mensajes = this.gestionarDocenteCUIntPort.procesarLaborDocenteDesdeJson(docenteLaborDTOList, idFacultad, idPrograma);
	        return ResponseEntity.ok(mensajes);

	    } catch (WebClientResponseException e) {
	        // Manejo de errores específicos de WebClient
	        return ResponseEntity.status(e.getStatusCode())
	                .body("Error en la llamada al servicio externo: " + e.getResponseBodyAsString());
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("Error al procesar los datos: " + e.getMessage());
	    }
	}

	
	@DeleteMapping("/eliminar")
	public ResponseEntity<?> eliminarCargueLaborDocente(@RequestParam Long idPrograma, @RequestParam Long idPeriodo) {
	    try {
	        // Lógica para eliminar el cargue
	    	 this.gestionarDocenteCUIntPort.eliminarCargue(idPrograma, idPeriodo);
	        return ResponseEntity.ok("El cargue de la labor docente se eliminó correctamente.");
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("Error al eliminar el cargue de la labor docente: " + e.getMessage());
	    }
	}
}

	