package org.com.imaapi.service;

        import org.com.imaapi.model.Usuario.Voluntario;
        import org.com.imaapi.model.Usuario.input.VoluntarioInput;
        import org.com.imaapi.repository.UsuarioRepository;
        import org.com.imaapi.repository.VoluntarioRepository;
        import org.slf4j.Logger;
        import org.slf4j.LoggerFactory;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.http.HttpStatus;
        import org.springframework.http.ResponseEntity;
        import org.springframework.stereotype.Service;
        import org.springframework.web.bind.annotation.PostMapping;
        import org.springframework.web.bind.annotation.RequestBody;

        import java.time.LocalDateTime;

@Service
        public class VoluntarioService {

            private static final Logger logger = LoggerFactory.getLogger(VoluntarioService.class);

            @Autowired
            private VoluntarioRepository voluntarioRepository;

            @Autowired
            private UsuarioRepository usuarioRepository;

            @PostMapping
            public ResponseEntity<Voluntario> cadastrarVoluntario(@RequestBody VoluntarioInput voluntarioInput) {
                try {
                    Voluntario voluntario = gerarObjetoVoluntario(voluntarioInput);
                    Voluntario salvarVoluntario = voluntarioRepository.save(voluntario);
                    return new ResponseEntity<>(salvarVoluntario, HttpStatus.CREATED);
                } catch (Exception erro) {
                    logger.error("Erro ao cadastrar voluntário: {}", erro.getMessage());
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }

            private Voluntario gerarObjetoVoluntario(VoluntarioInput voluntarioInput) {
                Voluntario voluntario = new Voluntario();
                voluntario.setFuncao(voluntarioInput.getFuncao());
                voluntario.setDataCadastro(LocalDateTime.now());
                voluntario.setUsuario(usuarioRepository.findById(voluntarioInput.getFkUsuario()).orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado")));
                return voluntario;
            }
        }