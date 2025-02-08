package co.edu.unicauca.sgph.seguridad.controller;

import java.io.IOException;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import co.edu.unicauca.sgph.reservatemporal.application.input.GestionarReservaTemporalCUIntPort;
import co.edu.unicauca.sgph.seguridad.dto.JwtDto;
import co.edu.unicauca.sgph.seguridad.dto.LoginUsuario;
import co.edu.unicauca.sgph.seguridad.dto.TokenDto;
import co.edu.unicauca.sgph.seguridad.entity.UsuarioPrincipal;
import co.edu.unicauca.sgph.seguridad.jwt.JwtProvider;
import co.edu.unicauca.sgph.usuario.infrastructure.input.DTOResponse.UsuarioReservasDTO;
import co.edu.unicauca.sgph.usuario.infrastructure.output.persistence.entity.EstadoUsuarioEnum;

@RestController
@RequestMapping("/Autenticacion")
public class AuthController {

	private final String GOOGLE_TOKEN_INFO_URL = "https://oauth2.googleapis.com/tokeninfo?access_token=";
	
	@Autowired
	private AuthenticationManager authenticationManager;

	private RestTemplate restTemplate = new RestTemplate();

	@Autowired
	private JwtProvider jwtProvider;

	@Autowired
	private UserDetailsService userDetailsService;

	private ResponseEntity<String> consultarInfoTokenGoogle(String token) {
		String url = GOOGLE_TOKEN_INFO_URL + token;
		try {
			return restTemplate.exchange(url, HttpMethod.GET, null, String.class);
		} catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PostMapping("/loginGoogle")
	public ResponseEntity<TokenDto> loginGoogle(@RequestBody TokenDto tokenDto) throws IOException {

		ResponseEntity<String> response = this.consultarInfoTokenGoogle(tokenDto.getValue());
		// Se valida el token de google
		if (response.getStatusCode().is2xxSuccessful()) {
			JSONObject jsonObject = new JSONObject(response.getBody());
			String email = jsonObject.getString("email");
			try {
				UserDetails userDetails = userDetailsService.loadUserByUsername(email.split("@")[0]);				
				Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
						userDetails.getAuthorities());
				return this.validarAutenticarYGenerarJwt(authentication);
			} catch (Exception e) {
				return new ResponseEntity("Usuario invalido", HttpStatus.BAD_REQUEST);			
			}
		}else {
			return new ResponseEntity("Token invalido", HttpStatus.BAD_REQUEST);			
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PostMapping("/login")
	public ResponseEntity<JwtDto> login(@Valid @RequestBody LoginUsuario loginUsuario, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
	        return ResponseEntity.badRequest()
	                .body(new JwtDto(null, loginUsuario.getNombreUsuario(), "ERROR", null, null));
	    }

	    try {
	        UserDetails userDetails = userDetailsService.loadUserByUsername(loginUsuario.getNombreUsuario());
	        UsuarioPrincipal usuarioPrincipal = (UsuarioPrincipal) userDetails;

	        // Verificar si el usuario está activo antes de autenticar
	        if (!"ACTIVO".equalsIgnoreCase(usuarioPrincipal.getEstado().toString())) {
	            return ResponseEntity.ok(
	                new JwtDto(null, usuarioPrincipal.getUsername(), "INACTIVO", usuarioPrincipal.getAuthorities(), 
	                           usuarioPrincipal.getProgramas().stream()
	                                .map(obj -> obj.getIdPrograma())
	                                .collect(Collectors.toList()))
	            );
	        }

	        Authentication authentication = authenticationManager.authenticate(
	                new UsernamePasswordAuthenticationToken(loginUsuario.getNombreUsuario(), loginUsuario.getPassword()));

	        return this.validarAutenticarYGenerarJwt(authentication);

	    } catch (Exception e) {
	        return ResponseEntity.ok()
	                .body(new JwtDto(null, loginUsuario.getNombreUsuario(), "ERROR", null, null));
	    }
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private ResponseEntity validarAutenticarYGenerarJwt(Authentication authentication) {
		UsuarioPrincipal usuarioPrincipal = (UsuarioPrincipal) authentication.getPrincipal();
	    // Si pasa ambas validaciones, generar el token JWT
	    SecurityContextHolder.getContext().setAuthentication(authentication);
	    String jwt = jwtProvider.generateToken(authentication);
	    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
	    JwtDto jwtDTO = new JwtDto(jwt, userDetails.getUsername(), usuarioPrincipal.getEstado().toString(), userDetails.getAuthorities(),
	            ((UsuarioPrincipal) userDetails).getProgramas().stream()
	                .map(obj -> obj.getIdPrograma())
	                .collect(Collectors.toList()));

	    return new ResponseEntity(jwtDTO, HttpStatus.OK);
	}

	@GetMapping("/esEstudianteActivo")
	public Boolean esEstudianteActivo(String usuarioEst) {
		// URL del servicio externo
	    String url = "http://10.200.1.181:8081/api/v1/labor/dataTercero/" + usuarioEst;

	    UsuarioReservasDTO usuario;
	    try {
	        // Realizar la llamada al servicio externo
	        ResponseEntity<UsuarioReservasDTO> response = restTemplate.exchange(
	            url,
	            HttpMethod.GET,
	            null,
	            new ParameterizedTypeReference<UsuarioReservasDTO>() {}
	        );
	        usuario = response.getBody();
	    } catch (Exception e) {
	        throw new RuntimeException("Error al consumir el servicio externo: " + e.getMessage());
	    }
	    boolean esActivo = usuario.getPrograma().stream()
	            .anyMatch(programa -> "ACTIVO".equalsIgnoreCase(programa.getEstado()));
		return esActivo;
	}

	@GetMapping("/esAdministradorActivo")
	public Boolean esAdministradorActivo(String estado) {
		if(estado.equals("ACTIVO")) {
			return true;
		}
	    return false;
	}

}