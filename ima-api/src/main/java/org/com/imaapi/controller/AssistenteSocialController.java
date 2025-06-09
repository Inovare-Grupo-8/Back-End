package org.com.imaapi.controller;

import org.com.imaapi.model.usuario.input.AssistenteSocialInput;
import org.com.imaapi.model.usuario.output.AssistenteSocialOutput;
import org.com.imaapi.service.AssistenteSocialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/assistentes-sociais")
public class AssistenteSocialController {

    @Autowired
    private AssistenteSocialService assistenteSocialService;

    @PostMapping
    public ResponseEntity<AssistenteSocialOutput> cadastrar(@RequestBody AssistenteSocialInput input) {
        return ResponseEntity.ok(assistenteSocialService.cadastrarAssistenteSocial(input));
    }

    @GetMapping("/perfil")
    public ResponseEntity<AssistenteSocialOutput> getPerfil(@AuthenticationPrincipal UserDetails userDetails) {
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
}