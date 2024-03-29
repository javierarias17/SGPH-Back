package co.edu.unicauca.sgph.seguridad.usuario.infrastructure.input.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unicauca.sgph.seguridad.usuario.aplication.input.GestionarUsuarioCUIntPort;
import co.edu.unicauca.sgph.seguridad.usuario.infrastructure.input.DTORequest.FiltroUsuarioDTO;
import co.edu.unicauca.sgph.seguridad.usuario.infrastructure.input.DTORequest.UsuarioInDTO;
import co.edu.unicauca.sgph.seguridad.usuario.infrastructure.input.DTOResponse.RolOutDTO;
import co.edu.unicauca.sgph.seguridad.usuario.infrastructure.input.DTOResponse.TipoIdentificacionOutDTO;
import co.edu.unicauca.sgph.seguridad.usuario.infrastructure.input.DTOResponse.UsuarioOutDTO;
import co.edu.unicauca.sgph.seguridad.usuario.infrastructure.input.mapper.UsuarioRestMapper;

@CrossOrigin(origins = { "http://localhost:4200" })
@RestController
@RequestMapping("/AdministrarUsuario")
public class UsuarioController {

	@Autowired
    private MessageSource messageSource;
	
	private GestionarUsuarioCUIntPort gestionarUsuarioCUIntPort;
	private UsuarioRestMapper usuarioRestMapper;

	public UsuarioController(GestionarUsuarioCUIntPort gestionarUsuarioCUIntPort, UsuarioRestMapper usuarioRestMapper) {
		this.gestionarUsuarioCUIntPort = gestionarUsuarioCUIntPort;
		this.usuarioRestMapper = usuarioRestMapper;
	}

	/**
	 * Método encargado de guardar o actualizar un usuario </br>
	 * 
	 * @author Pedro Javier Arias Lasso <apedro@unicauca.edu.co>
	 * 
	 * @param usuarioInDTO
	 * @return
	 */
	@PostMapping("/guardarUsuario")
	public ResponseEntity<?> guardarUsuario(@Valid @RequestBody UsuarioInDTO usuarioInDTO, BindingResult result) {
		HashMap<String, Object> respuestas = new HashMap<>();

		if (result.hasErrors()) {
			List<String> listaErrores = new ArrayList<>();
			for (FieldError error : result.getFieldErrors()) {
				listaErrores.add(error.getField() + ": " + error.getDefaultMessage());
			}
			respuestas.put("errors", listaErrores);
			return new ResponseEntity<Map<String, Object>>(respuestas, HttpStatus.BAD_REQUEST);
		}
		
		UsuarioOutDTO usuarioOutDTO = this.usuarioRestMapper.toUsuarioOutDTO(
				this.gestionarUsuarioCUIntPort.guardarUsuario(this.usuarioRestMapper.toUsuario(usuarioInDTO)));

		if (Objects.equals(usuarioInDTO.getIdPersona(), usuarioOutDTO.getIdPersona())) {
			return new ResponseEntity<UsuarioOutDTO>(usuarioOutDTO,	HttpStatus.OK);
		}else {
			return new ResponseEntity<UsuarioOutDTO>(usuarioOutDTO,	HttpStatus.CREATED);
		}
	}

	/**
	 * Método encargado de consultar los usuarios por diferentes criterios de
	 * busqueda y retornarlos de manera paginada
	 * 
	 * 
	 * @param filtroUsuarioDTO DTO con los criterios de busqueda
	 * @return
	 */
	@PostMapping("/consultarUsuariosPorFiltro")
	public Page<UsuarioOutDTO> consultarUsuariosPorFiltro(@RequestBody FiltroUsuarioDTO filtroUsuarioDTO) {
		Page<UsuarioOutDTO>  respuesta = this.gestionarUsuarioCUIntPort.consultarUsuariosPorFiltro(filtroUsuarioDTO);
		
		String mensajito = messageSource.getMessage("correo.de.prueba", null, LocaleContextHolder.getLocale());
		respuesta.getContent().get(0).setEmail(mensajito);
		return respuesta;
	}

	/**
	 * Método encargado de consultar todos los tipos de identificación de
	 * persona</br>
	 * 
	 * @author Pedro Javier Arias Lasso <apedro@unicauca.edu.co>
	 * 
	 * @return
	 */
	@GetMapping("/consultarTiposIdentificacion")
	public List<TipoIdentificacionOutDTO> consultarTiposIdentificacion() {
		return this.usuarioRestMapper
				.toLstTipoIdentificacionOutDTO(this.gestionarUsuarioCUIntPort.consultarTiposIdentificacion());
	}

	/**
	 * Método encargado de consultar todos los roles de usuario</br>
	 * 
	 * @author Pedro Javier Arias Lasso <apedro@unicauca.edu.co>
	 * 
	 * @return
	 */
	@GetMapping("/consultarRoles")
	public List<RolOutDTO> consultarRoles() {
		return this.usuarioRestMapper.toLstRolOutDTO(this.gestionarUsuarioCUIntPort.consultarRoles());
	}

	/**
	 * Método encargado de consultar todos los estados de usuario</br>
	 * 
	 * @author Pedro Javier Arias Lasso <apedro@unicauca.edu.co>
	 * 
	 * @return
	 */
	@GetMapping("/consultarEstadosUsuario")
	public List<String> consultarEstadosUsuario() {
		return this.gestionarUsuarioCUIntPort.consultarEstadosUsuario();
	}

}