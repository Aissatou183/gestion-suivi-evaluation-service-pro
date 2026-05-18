package com.uasz.gestion_suivi_evaluation_service.client;

import com.uasz.gestion_suivi_evaluation_service.dto.LivrableResponse;
import com.uasz.gestion_suivi_evaluation_service.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.*;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LivrableClient {

    private final RestTemplate restTemplate;

    @Value("${services.livrable.url}")
    private String livrableUrl;

    public List<LivrableResponse> livrablesParEncadrement(
            Long encadrementId,
            String token
    ) {
        if (encadrementId == null) {
            throw new BadRequestException(
                    "L'identifiant de l'encadrement est obligatoire."
            );
        }

        try {
            String url = nettoyerUrl(livrableUrl)
                    + "/livrables/encadrement/"
                    + encadrementId;

            ResponseEntity<List<LivrableResponse>> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.GET,
                            entity(token),
                            new ParameterizedTypeReference<>() {}
                    );

            return response.getBody() == null
                    ? List.of()
                    : response.getBody();

        } catch (HttpClientErrorException.NotFound e) {
            return List.of();

        } catch (HttpClientErrorException.Forbidden e) {
            throw new BadRequestException(
                    "Accès refusé au service livrable."
            );

        } catch (HttpClientErrorException.Unauthorized e) {
            throw new BadRequestException(
                    "Token invalide ou expiré."
            );

        } catch (ResourceAccessException e) {
            throw new BadRequestException(
                    "Service livrable indisponible."
            );

        } catch (RestClientException e) {
            throw new BadRequestException(
                    "Erreur lors de l'appel au service livrable : "
                            + e.getMessage()
            );

        } catch (Exception e) {
            throw new BadRequestException(
                    "Impossible de récupérer les livrables de l'encadrement "
                            + encadrementId
                            + " : "
                            + e.getMessage()
            );
        }
    }

    private HttpEntity<Void> entity(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        if (token != null && !token.isBlank()) {
            headers.setBearerAuth(token);
        }

        return new HttpEntity<>(headers);
    }

    private String nettoyerUrl(String url) {
        if (url == null || url.isBlank()) {
            throw new BadRequestException(
                    "URL du service livrable non configurée."
            );
        }

        return url.endsWith("/")
                ? url.substring(0, url.length() - 1)
                : url;
    }
}