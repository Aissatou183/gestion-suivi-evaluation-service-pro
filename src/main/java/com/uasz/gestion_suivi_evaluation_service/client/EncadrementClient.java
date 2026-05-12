package com.uasz.gestion_suivi_evaluation_service.client;

import com.uasz.gestion_suivi_evaluation_service.dto.EncadrementResponse;
import com.uasz.gestion_suivi_evaluation_service.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class EncadrementClient {

    private final RestTemplate restTemplate;

    @Value("${services.encadrement.url}")
    private String encadrementServiceUrl;

    public EncadrementResponse getEncadrement(Long id, String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            if (token != null && !token.isBlank()) {
                headers.setBearerAuth(token);
            }

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<EncadrementResponse> response =
                    restTemplate.exchange(
                            encadrementServiceUrl + "/encadrements/" + id,
                            HttpMethod.GET,
                            entity,
                            EncadrementResponse.class
                    );

            if (response.getBody() == null) {
                throw new BadRequestException("Encadrement introuvable : " + id);
            }

            return response.getBody();

        } catch (HttpClientErrorException.NotFound e) {
            throw new BadRequestException("Encadrement introuvable : " + id);
        } catch (HttpClientErrorException.Forbidden e) {
            throw new BadRequestException("Accès refusé au service des encadrements");
        } catch (HttpClientErrorException.Unauthorized e) {
            throw new BadRequestException("Token invalide ou expiré");
        } catch (Exception e) {
            throw new BadRequestException("Impossible de récupérer l'encadrement : " + id);
        }
    }
}