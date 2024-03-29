package co.edu.unicauca.sgph.seguridad.usuario.aplication.input;

import java.util.List;

import org.springframework.data.domain.Page;

import co.edu.unicauca.sgph.common.domain.model.TipoIdentificacion;
import co.edu.unicauca.sgph.seguridad.usuario.domain.model.Rol;
import co.edu.unicauca.sgph.seguridad.usuario.domain.model.Usuario;
import co.edu.unicauca.sgph.seguridad.usuario.infrastructure.input.DTORequest.FiltroUsuarioDTO;
import co.edu.unicauca.sgph.seguridad.usuario.infrastructure.input.DTOResponse.UsuarioOutDTO;

public interface GestionarUsuarioCUIntPort {

	/**
	 * Método encargado de guardar o actualizar un usuario </br>
	 * 
	 * @author Pedro Javier Arias Lasso <apedro@unicauca.edu.co>
	 * 
	 * @param usuario
	 * @return
	 */
	public Usuario guardarUsuario(Usuario usuario);

	/**
	 * Método encargado de consultar los usuarios por diferentes criterios de
	 * busqueda y retornarlos de manera paginada
	 * 
	 * 
	 * @param filtroUsuarioDTO DTO con los criterios de busqueda
	 * @return
	 */
	public Page<UsuarioOutDTO> consultarUsuariosPorFiltro(FiltroUsuarioDTO filtroUsuarioDTO);

	/**
	 * Método encargado de consultar todos los tipos de identificación de
	 * persona</br>
	 * 
	 * @author Pedro Javier Arias Lasso <apedro@unicauca.edu.co>
	 * 
	 * @return
	 */
	public List<TipoIdentificacion> consultarTiposIdentificacion();

	/**
	 * Método encargado de consultar todos los roles de usuario</br>
	 * 
	 * @author Pedro Javier Arias Lasso <apedro@unicauca.edu.co>
	 * 
	 * @return
	 */
	public List<Rol> consultarRoles();

	/**
	 * Método encargado de consultar todos los estados de usuario</br>
	 * 
	 * @author Pedro Javier Arias Lasso <apedro@unicauca.edu.co>
	 * 
	 * @return
	 */
	public List<String> consultarEstadosUsuario();

}