package org.com.imaapi.service.impl;

import org.com.imaapi.service.FotoService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FotoServiceImpl implements FotoService {

    @Override
    public String salvarFoto(String tipoUsuario, Integer usuarioId, MultipartFile file) throws IOException {
        Path uploadPath = Paths.get("uploads/");
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Usar extensão do arquivo original para manter o tipo
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // Criar nome único para evitar conflitos entre tipos de usuário
        String fileName = String.format("%s_user_%d%s", tipoUsuario, usuarioId, fileExtension);
        Path filePath = uploadPath.resolve(fileName);

        // Remover arquivo anterior se existir (substituição)
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }

        // Salvar o novo arquivo
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return "/uploads/" + fileName;
    }
}
