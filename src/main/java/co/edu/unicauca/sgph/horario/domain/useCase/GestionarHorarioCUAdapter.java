package co.edu.unicauca.sgph.horario.domain.useCase;

import java.util.List;

import co.edu.unicauca.sgph.curso.domain.model.Curso;
import co.edu.unicauca.sgph.horario.aplication.input.GestionarHorarioCUIntPort;
import co.edu.unicauca.sgph.horario.aplication.output.GestionarHorarioGatewayIntPort;
import co.edu.unicauca.sgph.horario.aplication.output.HorarioFormatterResultsIntPort;
import co.edu.unicauca.sgph.horario.domain.model.Horario;

public class GestionarHorarioCUAdapter implements GestionarHorarioCUIntPort {
	
	private final GestionarHorarioGatewayIntPort gestionarHorarioGatewayIntPort;
	private final HorarioFormatterResultsIntPort horarioFormatterResultsIntPort;
			
	public GestionarHorarioCUAdapter(GestionarHorarioGatewayIntPort gestionarHorarioGatewayIntPort,
			HorarioFormatterResultsIntPort horarioFormatterResultsIntPort) {
		this.gestionarHorarioGatewayIntPort = gestionarHorarioGatewayIntPort;
		this.horarioFormatterResultsIntPort = horarioFormatterResultsIntPort;
	}
	
	/** 
	 * @see co.edu.unicauca.sgph.horario.aplication.input.GestionarHorarioCUIntPort#consultarHorarioPorCurso(co.edu.unicauca.sgph.curso.domain.model.Curso)
	 */
	@Override
	public List<Horario> consultarHorarioPorCurso(Curso curso) {
		return this.gestionarHorarioGatewayIntPort.consultarHorarioPorCurso(curso);
	}

}