package com.uasz.gestion_suivi_evaluation_service.service;

import com.uasz.gestion_suivi_evaluation_service.dto.HistoriqueResponse;
import com.uasz.gestion_suivi_evaluation_service.entity.HistoriqueAction;
import com.uasz.gestion_suivi_evaluation_service.repository.HistoriqueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoriqueService {

    private final HistoriqueRepository historiqueRepository;

    public void ajouter(
            Long encadrementId,
            Long acteurId,
            String acteurNomComplet,
            String acteurRole,
            String action,
            String titre,
            String description
    ) {
        HistoriqueAction historique = HistoriqueAction.builder()
                .encadrementId(encadrementId)
                .acteurId(acteurId)
                .acteurNomComplet(acteurNomComplet)
                .acteurRole(acteurRole)
                .action(action)
                .titre(titre)
                .description(description)
                .dateAction(LocalDateTime.now())
                .build();

        historiqueRepository.save(historique);
    }

    public List<HistoriqueResponse> parEncadrement(Long encadrementId) {
        return historiqueRepository.findByEncadrementIdOrderByDateActionDesc(encadrementId)
                .stream()
                .map(this::map)
                .toList();
    }

    private HistoriqueResponse map(HistoriqueAction h) {
        return HistoriqueResponse.builder()
                .id(h.getId())
                .encadrementId(h.getEncadrementId())
                .acteurId(h.getActeurId())
                .acteurNomComplet(h.getActeurNomComplet())
                .acteurRole(h.getActeurRole())
                .action(h.getAction())
                .titre(h.getTitre())
                .description(h.getDescription())
                .dateAction(h.getDateAction())
                .build();
    }
}