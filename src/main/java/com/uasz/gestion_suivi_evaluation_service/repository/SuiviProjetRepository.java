package com.uasz.gestion_suivi_evaluation_service.repository;

import com.uasz.gestion_suivi_evaluation_service.entity.SuiviProjet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface SuiviProjetRepository extends JpaRepository<SuiviProjet, Long> {
    List<SuiviProjet> findByEncadrementIdOrderByDateSuiviDesc(Long encadrementId);
    List<SuiviProjet> findByEnseignantIdOrderByDateSuiviDesc(Long enseignantId);
    List<SuiviProjet> findByEtudiantIdOrderByDateSuiviDesc(Long etudiantId);
    Optional<SuiviProjet> findTopByEncadrementIdOrderByDateSuiviDesc(Long encadrementId);
    long countByEncadrementId(Long encadrementId);
}
