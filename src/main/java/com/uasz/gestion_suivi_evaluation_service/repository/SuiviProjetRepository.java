package com.uasz.gestion_suivi_evaluation_service.repository;

import com.uasz.gestion_suivi_evaluation_service.entity.SuiviProjet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SuiviProjetRepository extends JpaRepository<SuiviProjet, Long> {

    List<SuiviProjet> findByEncadrementIdOrderByDateSuiviDesc(Long encadrementId);

    Optional<SuiviProjet> findFirstByEncadrementIdOrderByDateSuiviDesc(Long encadrementId);

    long countByEncadrementId(Long encadrementId);
}