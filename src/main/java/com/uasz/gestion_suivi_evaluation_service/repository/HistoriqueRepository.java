package com.uasz.gestion_suivi_evaluation_service.repository;

import com.uasz.gestion_suivi_evaluation_service.entity.HistoriqueAction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoriqueRepository extends JpaRepository<HistoriqueAction, Long> {

    List<HistoriqueAction> findByEncadrementIdOrderByDateActionDesc(Long encadrementId);
}