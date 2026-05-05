package com.uasz.gestion_suivi_evaluation_service.controller;

import com.uasz.gestion_suivi_evaluation_service.service.RapportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rapports")
@RequiredArgsConstructor
public class RapportController {

    private final RapportService rapportService;

    private String token(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return "";
        }
        return authorizationHeader.substring(7);
    }

    @GetMapping("/{encadrementId}/pdf")
    public ResponseEntity<byte[]> rapportPdf(
            @PathVariable Long encadrementId,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        byte[] data = rapportService.genererPdf(encadrementId, token(authorizationHeader));

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"rapport-suivi-" + encadrementId + ".pdf\"")
                .body(data);
    }

    @GetMapping("/{encadrementId}/excel")
    public ResponseEntity<byte[]> rapportExcel(
            @PathVariable Long encadrementId,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        byte[] data = rapportService.genererExcel(encadrementId, token(authorizationHeader));

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"rapport-suivi-" + encadrementId + ".xlsx\"")
                .body(data);
    }
}
