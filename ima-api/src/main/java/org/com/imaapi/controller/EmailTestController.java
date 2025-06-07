package org.com.imaapi.controller;

import org.com.imaapi.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/email-test")
public class EmailTestController {

    @Autowired
    private EmailService emailService;

    @GetMapping("/test-connection")
    public ResponseEntity<String> testEmailConnection(@RequestParam String email) {
        try {
            String resultado = emailService.enviarEmail(email, "Teste", "bem vindo");
            return ResponseEntity.ok("Teste de email: " + resultado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro no teste: " + e.getMessage());
        }
    }
}
