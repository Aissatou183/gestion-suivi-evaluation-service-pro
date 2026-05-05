package com.uasz.gestion_suivi_evaluation_service.client;

import com.uasz.gestion_suivi_evaluation_service.dto.LivrableResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
@RequiredArgsConstructor
public class LivrableClient {

    private final RestTemplate restTemplate;

    @Value("${services.livrable.url}")
    private String livrableServiceUrl;

    public List<LivrableResponse> getLivrablesParEncadrement(Long encadrementId, String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<List<LivrableResponse>> response = restTemplate.exchange(
                    livrableServiceUrl + "/livrables/encadrement/" + encadrementId,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<LivrableResponse>>() {}
            );

            return response.getBody() == null ? List.of() : response.getBody();
        } catch (Exception e) {
            return List.of();
        }
    }
}
