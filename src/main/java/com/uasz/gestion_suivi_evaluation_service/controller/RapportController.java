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

    private String token(String auth) {
        return auth == null ? "" : auth.replace("Bearer ", "");
    }

    @GetMapping("/{encadrementId}/pdf")
    public ResponseEntity<byte[]> pdf(
            @PathVariable Long encadrementId,
            @RequestHeader("Authorization") String authorization
    ) {
        String token = token(authorization);

        byte[] data = rapportService.genererPdf(
                encadrementId,
                token,
                jwtService.extractUserId(token),
                jwtService.extractNomComplet(token),
                jwtService.extractRole(token)
        );

        ContentDisposition disposition = ContentDisposition.attachment()
                .filename("rapport-suivi-" + encadrementId + ".pdf", StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .body(data);
    }

    @GetMapping("/{encadrementId}/excel")
    public ResponseEntity<byte[]> excel(
            @PathVariable Long encadrementId,
            @RequestHeader("Authorization") String authorization
    ) {
        String token = token(authorization);

        byte[] data = rapportService.genererExcel(
                encadrementId,
                token,
                jwtService.extractUserId(token),
                jwtService.extractNomComplet(token),
                jwtService.extractRole(token)
        );

        ContentDisposition disposition = ContentDisposition.attachment()
                .filename("rapport-suivi-" + encadrementId + ".xlsx", StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                ))
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .body(data);
    }
}