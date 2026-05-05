package com.uasz.gestion_suivi_evaluation_service.repository;

import com.uasz.gestion_suivi_evaluation_service.entity.HistoriqueAction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface HistoriqueActionRepository extends JpaRepository<HistoriqueAction, Long> {
    List<HistoriqueAction> findByEncadrementIdOrderByDateActionDesc(Long encadrementId);
    List<HistoriqueAction> findBySujetIdOrderByDateActionDesc(Long sujetId);
    List<HistoriqueAction> findByActeurIdOrderByDateActionDesc(Long acteurId);
    List<HistoriqueAction> findAllByOrderByDateActionDesc();
}
