package com.uasz.gestion_suivi_evaluation_service.client;

import com.uasz.gestion_suivi_evaluation_service.dto.EncadrementResponse;
import com.uasz.gestion_suivi_evaluation_service.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.*;

@Component
@RequiredArgsConstructor
public class EncadrementClient {

    private final RestTemplate restTemplate;

    @Value("${services.encadrement.url}")
    private String encadrementServiceUrl;

    public EncadrementResponse trouverParId(Long id, String token) {
        if (id == null) {
            throw new BadRequestException("L'identifiant de l'encadrement est obligatoire.");
        }

        try {
            String url = nettoyerUrl(encadrementServiceUrl)
                    + "/encadrements/"
                    + id;

            ResponseEntity<EncadrementResponse> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.GET,
                            entity(token),
                            EncadrementResponse.class
                    );

            if (response.getBody() == null) {
                throw new BadRequestException("Encadrement introuvable : " + id);
            }

            return response.getBody();

        } catch (BadRequestException e) {
            throw e;

        } catch (HttpClientErrorException.NotFound e) {
            throw new BadRequestException("Encadrement introuvable : " + id);

        } catch (HttpClientErrorException.Forbidden e) {
            throw new BadRequestException("Accès refusé au service des encadrements.");

        } catch (HttpClientErrorException.Unauthorized e) {
            throw new BadRequestException("Token invalide ou expiré.");

        } catch (ResourceAccessException e) {
            throw new BadRequestException("Service encadrement indisponible.");

        } catch (RestClientException e) {
            throw new BadRequestException(
                    "Erreur lors de l'appel au service encadrement : " + e.getMessage()
            );

        } catch (Exception e) {
            throw new BadRequestException(
                    "Impossible de récupérer l'encadrement "
                            + id
                            + " : "
                            + e.getMessage()
            );
        }
    }

    public EncadrementResponse getEncadrement(Long id, String token) {
        return trouverParId(id, token);
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
            throw new BadRequestException("URL du service encadrement non configurée.");
        }

        return url.endsWith("/")
                ? url.substring(0, url.length() - 1)
                : url;
    }
}