package com.uasz.gestion_suivi_evaluation_service.repository;

import com.uasz.gestion_suivi_evaluation_service.entity.EvaluationProjet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvaluationProjetRepository extends JpaRepository<EvaluationProjet, Long> {

    List<EvaluationProjet> findByEncadrementIdOrderByDateEvaluationDesc(Long encadrementId);
}