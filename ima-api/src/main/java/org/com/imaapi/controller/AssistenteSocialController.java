package org.com.imaapi.controller;

import lombok.RequiredArgsConstructor;
import org.com.imaapi.model.usuario.input.AssistenteSocialInput;
import org.com.imaapi.model.usuario.output.AssistenteSocialOutput;
import org.com.imaapi.service.AssistenteSocialService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/assistentes-sociais")
@RequiredArgsConstructor
public class AssistenteSocialController {

    private final AssistenteSocialService assistenteSocialService;

    @PostMapping
    public ResponseEntity<AssistenteSocialOutput> cadastrar(@RequestBody AssistenteSocialInput input) {
        return ResponseEntity.ok(assistenteSocialService.cadastrarAssistenteSocial(input));
    }

    @GetMapping("/perfil")
    public ResponseEntity<AssistenteSocialOutput> getPerfil(@AuthenticationPrincipal UserDetails userDetails) {
        // Aqui precisamos implementar a lógica para pegar o ID do usuário logado
        Integer userId = Integer.parseInt(userDetails.getUsername());
        return ResponseEntity.ok(assistenteSocialService.buscarAssistenteSocial(userId));
    }

    @PutMapping("/perfil")
    public ResponseEntity<AssistenteSocialOutput> atualizarPerfil(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody AssistenteSocialInput input) {
        Integer userId = Integer.parseInt(userDetails.getUsername());
        return ResponseEntity.ok(assistenteSocialService.atualizarAssistenteSocial(userId, input));
    }

    @PatchMapping("/perfil/completo")
    public ResponseEntity<AssistenteSocialOutput> atualizarPerfilCompleto(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody AssistenteSocialInput input) {
        try {
            Integer userId = Integer.parseInt(userDetails.getUsername());
            AssistenteSocialOutput output = assistenteSocialService.atualizarAssistenteSocial(userId, input);
            return ResponseEntity.ok(output);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
