package org.com.imaapi.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FotoService {
    String salvarFoto(String tipoUsuario, Integer usuarioId, MultipartFile file) throws IOException;
}
