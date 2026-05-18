package com.uasz.gestion_suivi_evaluation_service.controller;

import com.uasz.gestion_suivi_evaluation_service.security.JwtService;
import com.uasz.gestion_suivi_evaluation_service.service.RapportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/rapports")
@RequiredArgsConstructor
public class RapportController {

    private final RapportService rapportService;
    private final JwtService jwtService;

    private String token(String authorizationHeader) {
        if (authorizationHeader == null
                || !authorizationHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token JWT manquant ou invalide.");
        }

        return authorizationHeader.substring(7);
    }

    private Long userId(String jwt) {
        return jwtService.extractUserId(jwt);
    }

    private String nomComplet(String jwt) {
        return jwtService.extractNomComplet(jwt);
    }

    private String role(String jwt) {
        return jwtService.extractRole(jwt);
    }

    @GetMapping("/{encadrementId}/pdf")
    public ResponseEntity<byte[]> pdf(
            @PathVariable Long encadrementId,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        String jwt = token(authorizationHeader);

        byte[] data = rapportService.genererPdf(
                encadrementId,
                jwt,
                userId(jwt),
                nomComplet(jwt),
                role(jwt)
        );

        ContentDisposition disposition = ContentDisposition
                .attachment()
                .filename(
                        "rapport-suivi-encadrement-" + encadrementId + ".pdf",
                        StandardCharsets.UTF_8
                )
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        disposition.toString()
                )
                .body(data);
    }

    @GetMapping("/{encadrementId}/excel")
    public ResponseEntity<byte[]> excel(
            @PathVariable Long encadrementId,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        String jwt = token(authorizationHeader);

        byte[] data = rapportService.genererExcel(
                encadrementId,
                jwt,
                userId(jwt),
                nomComplet(jwt),
                role(jwt)
        );

        ContentDisposition disposition = ContentDisposition
                .attachment()
                .filename(
                        "rapport-suivi-encadrement-" + encadrementId + ".xlsx",
                        StandardCharsets.UTF_8
                )
                .build();

        return ResponseEntity.ok()
                .contentType(
                        MediaType.parseMediaType(
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                        )
                )
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        disposition.toString()
                )
                .body(data);
    }
}