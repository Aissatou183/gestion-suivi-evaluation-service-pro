package com.uasz.gestion_suivi_evaluation_service.client;

import com.uasz.gestion_suivi_evaluation_service.dto.LivrableResponse;
import com.uasz.gestion_suivi_evaluation_service.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LivrableClient {

    private final RestTemplate restTemplate;

    @Value("${services.livrable.url}")
    private String livrableUrl;

    public List<LivrableResponse> livrablesParEncadrement(Long encadrementId, String token) {
        try {
            HttpHeaders headers = new HttpHeaders();

            if (token != null && !token.isBlank()) {
                headers.setBearerAuth(token);
            }

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<List<LivrableResponse>> response =
                    restTemplate.exchange(
                            livrableUrl + "/livrables/encadrement/" + encadrementId,
                            HttpMethod.GET,
                            entity,
                            new ParameterizedTypeReference<>() {}
                    );

            return response.getBody() == null ? List.of() : response.getBody();

        } catch (Exception e) {
            throw new BadRequestException("Impossible de récupérer les livrables de l'encadrement : " + encadrementId);
        }
    }
}