package co.edu.unicauca.sgph.usuario.aplication.input;

import java.util.List;

import org.springframework.data.domain.Page;

import co.edu.unicauca.sgph.usuario.domain.model.Rol;
import co.edu.unicauca.sgph.usuario.domain.model.Usuario;
import co.edu.unicauca.sgph.usuario.infrastructure.input.DTORequest.FiltroUsuarioDTO;
import co.edu.unicauca.sgph.usuario.infrastructure.input.DTOResponse.UsuarioOutDTO;
import co.edu.unicauca.sgph.usuario.infrastructure.input.DTOResponse.UsuarioReservasDTO;

public interface GestionarUsuarioCUIntPort {

	/**
	 * Método encargado de guardar o actualizar un usuario <br>
	 * 
	 * @author apedro
	 * 
	 * @param usuario
	 * @return
	 */
	public Usuario guardarUsuario(Usuario usuario);

	/**
	 * Método encargado de consultar los usuarios por diferentes criterios de
	 * busqueda y retornarlos de manera paginada<br>
	 * 
	 * @author apedro
	 * 
	 * @param filtroUsuarioDTO DTO con los criterios de busqueda
	 * @return
	 */
	public Page<UsuarioOutDTO> consultarUsuariosPorFiltro(FiltroUsuarioDTO filtroUsuarioDTO);

	/**
	 * Método encargado de consultar todos los roles de usuario<br>
	 * 
	 * @author apedro
	 * 
	 * @return
	 */
	public List<Rol> consultarRoles();

	/**
	 * Método encargado de consultar todos los estados de usuario<br>
	 * 
	 * @author apedro
	 * 
	 * @return
	 */
	public List<String> consultarEstadosUsuario();	
	
	/**
	 * Método encargado de cambiar el estado al usuario dado su identificador único</br>
	 * 
	 * @author apedro
	 * 
	 * @param idUsuario
	 * @return
	 */
	public Usuario cambiarEstadoUsuarioPorIdUsuario(Long idUsuario);

	Usuario consultarUsuarioPorIdPersona(Long idPersona);
	
	public UsuarioReservasDTO obtenerDatosUsuarioExterno(String username);
	
	Usuario consultarUsuarioAutenticado(String nombreUsuario);
}