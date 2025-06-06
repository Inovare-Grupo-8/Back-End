package org.com.imaapi.model.consulta.dto;

import lombok.Data;
import org.com.imaapi.model.enums.ModalidadeConsulta;
import org.com.imaapi.model.enums.StatusConsulta;
import org.com.imaapi.model.especialidade.Especialidade;
import org.com.imaapi.model.usuario.Usuario;

import java.time.LocalDateTime;

@Data
public class ConsultaDetailDTO {
    private Integer id;
    private LocalDateTime horario;
    private StatusConsulta status;
    private ModalidadeConsulta modalidade;
    private String local;
    private String observacoes;
    private Integer especialidadeId;
    private String especialidadeNome;
    private Integer assistidoId;
    private String assistidoNome;
    private String assistidoEmail;
    private Integer voluntarioId;
    private String voluntarioNome;
    private String voluntarioEmail;

    public static ConsultaDetailDTO fromEntities(
            LocalDateTime horario, 
            StatusConsulta status, 
            ModalidadeConsulta modalidade, 
            String local,
            String observacoes,
            Especialidade especialidade,
            Usuario assistido,
            Usuario voluntario) {
        
        ConsultaDetailDTO dto = new ConsultaDetailDTO();
        dto.setHorario(horario);
        dto.setStatus(status);
        dto.setModalidade(modalidade);
        dto.setLocal(local);
        dto.setObservacoes(observacoes);
        
        if (especialidade != null) {
            dto.setEspecialidadeId(especialidade.getId());
            dto.setEspecialidadeNome(especialidade.getNome());
        }
        
        if (assistido != null) {
            dto.setAssistidoId(assistido.getIdUsuario());
            dto.setAssistidoEmail(assistido.getEmail());
            if (assistido.getFicha() != null) {
                dto.setAssistidoNome(assistido.getFicha().getNome());
            }
        }
        
        if (voluntario != null) {
            dto.setVoluntarioId(voluntario.getIdUsuario());
            dto.setVoluntarioEmail(voluntario.getEmail());
            if (voluntario.getFicha() != null) {
                dto.setVoluntarioNome(voluntario.getFicha().getNome());
            }
        }
        
        return dto;
    }
}
