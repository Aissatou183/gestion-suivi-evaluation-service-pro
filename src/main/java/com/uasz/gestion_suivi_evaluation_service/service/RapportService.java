package com.uasz.gestion_suivi_evaluation_service.service;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfWriter;
import com.uasz.gestion_suivi_evaluation_service.client.LivrableClient;
import com.uasz.gestion_suivi_evaluation_service.dto.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RapportService {

    private final IndicateurService indicateurService;
    private final SuiviService suiviService;
    private final EvaluationService evaluationService;
    private final HistoriqueService historiqueService;
    private final LivrableClient livrableClient;

    public byte[] genererPdf(
            Long encadrementId,
            String token,
            Long userId,
            String nom,
            String role
    ) {
        try {
            IndicateurResponse indicateurs =
                    indicateurService.calculer(encadrementId, userId, role, token);

            List<SuiviResponse> suivis =
                    suiviService.parEncadrement(encadrementId, userId, role, token);

            List<EvaluationResponse> evaluations =
                    evaluationService.parEncadrement(encadrementId, userId, role, token);

            List<HistoriqueResponse> historiques =
                    historiqueService.parEncadrement(encadrementId, userId, role, token);

            List<LivrableResponse> livrables =
                    livrableClient.livrablesParEncadrement(encadrementId, token);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

            document.add(new Paragraph("Rapport de suivi et évaluation", titleFont));
            document.add(new Paragraph("Encadrement ID : " + encadrementId, normalFont));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("1. Indicateurs d’avancement", sectionFont));
            document.add(new Paragraph("Avancement actuel : " + indicateurs.getAvancementActuel() + "%", normalFont));
            document.add(new Paragraph("Nombre de suivis : " + indicateurs.getNombreSuivis(), normalFont));
            document.add(new Paragraph("Nombre de livrables : " + indicateurs.getNombreLivrables(), normalFont));
            document.add(new Paragraph("Livrables validés : " + indicateurs.getLivrablesValides(), normalFont));
            document.add(new Paragraph("Livrables en retard : " + indicateurs.getLivrablesEnRetard(), normalFont));
            document.add(new Paragraph("Moyenne des livrables : " + indicateurs.getMoyenneLivrables() + "/20", normalFont));
            document.add(new Paragraph("Niveau de risque : " + indicateurs.getNiveauRisque(), normalFont));
            document.add(new Paragraph("Statut projet : " + indicateurs.getStatutProjet(), normalFont));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("2. Livrables", sectionFont));

            for (LivrableResponse l : livrables) {
                document.add(new Paragraph(
                        "- " + safe(l.getTypeLivrable())
                                + " | Version " + safeInt(l.getVersion())
                                + " | " + safe(l.getStatut())
                                + " | Note : " + (l.getNote() == null ? "Non évalué" : l.getNote() + "/20"),
                        normalFont
                ));
            }

            document.add(new Paragraph(" "));
            document.add(new Paragraph("3. Suivis pédagogiques", sectionFont));

            for (SuiviResponse s : suivis) {
                document.add(new Paragraph(
                        "- " + safeDate(s.getDateSuivi())
                                + " | Avancement : " + safeInt(s.getAvancementPourcentage()) + "%"
                                + " | Risque : " + safe(s.getNiveauRisque()),
                        normalFont
                ));

                document.add(new Paragraph("  Enseignant : " + safe(s.getEnseignantNomComplet()), normalFont));
                document.add(new Paragraph("  Observations : " + safe(s.getObservations()), normalFont));
                document.add(new Paragraph("  Recommandations : " + safe(s.getRecommandations()), normalFont));
            }

            document.add(new Paragraph(" "));
            document.add(new Paragraph("4. Évaluations", sectionFont));

            for (EvaluationResponse e : evaluations) {
                document.add(new Paragraph(
                        "- " + safe(e.getEnseignantNomComplet())
                                + " | Note : " + safeInt(e.getNoteGlobale()) + "/20",
                        normalFont
                ));

                document.add(new Paragraph("  Appréciation : " + safe(e.getAppreciation()), normalFont));
                document.add(new Paragraph("  Points forts : " + safe(e.getPointsForts()), normalFont));
                document.add(new Paragraph("  Points à améliorer : " + safe(e.getPointsAAmeliorer()), normalFont));
            }

            document.add(new Paragraph(" "));
            document.add(new Paragraph("5. Historique", sectionFont));

            for (HistoriqueResponse h : historiques) {
                document.add(new Paragraph(
                        "- " + safeDate(h.getDateAction())
                                + " | " + safe(h.getAction())
                                + " | " + safe(h.getActeurNomComplet())
                                + " | " + safe(h.getDescription()),
                        normalFont
                ));
            }

            document.close();

            historiqueService.ajouter(
                    encadrementId,
                    userId,
                    nom,
                    role,
                    "GENERATION_RAPPORT_PDF",
                    "Rapport PDF généré",
                    "Génération du rapport PDF du projet."
            );

            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erreur génération PDF : " + e.getMessage());
        }
    }

    public byte[] genererExcel(
            Long encadrementId,
            String token,
            Long userId,
            String nom,
            String role
    ) {
        try {
            IndicateurResponse indicateurs =
                    indicateurService.calculer(encadrementId, userId, role, token);

            List<SuiviResponse> suivis =
                    suiviService.parEncadrement(encadrementId, userId, role, token);

            List<EvaluationResponse> evaluations =
                    evaluationService.parEncadrement(encadrementId, userId, role, token);

            List<HistoriqueResponse> historiques =
                    historiqueService.parEncadrement(encadrementId, userId, role, token);

            List<LivrableResponse> livrables =
                    livrableClient.livrablesParEncadrement(encadrementId, token);

            Workbook workbook = new XSSFWorkbook();

            Sheet sheetIndicateurs = workbook.createSheet("Indicateurs");
            Row headerIndicateur = sheetIndicateurs.createRow(0);
            headerIndicateur.createCell(0).setCellValue("Indicateur");
            headerIndicateur.createCell(1).setCellValue("Valeur");

            Object[][] indicateurData = {
                    {"Avancement", indicateurs.getAvancementActuel() + "%"},
                    {"Nombre suivis", indicateurs.getNombreSuivis()},
                    {"Nombre livrables", indicateurs.getNombreLivrables()},
                    {"Livrables validés", indicateurs.getLivrablesValides()},
                    {"Livrables en retard", indicateurs.getLivrablesEnRetard()},
                    {"Moyenne", indicateurs.getMoyenneLivrables()},
                    {"Niveau risque", indicateurs.getNiveauRisque()},
                    {"Statut projet", indicateurs.getStatutProjet()}
            };

            int rowIdx = 1;
            for (Object[] row : indicateurData) {
                Row r = sheetIndicateurs.createRow(rowIdx++);
                r.createCell(0).setCellValue(String.valueOf(row[0]));
                r.createCell(1).setCellValue(String.valueOf(row[1]));
            }

            Sheet sheetLivrables = workbook.createSheet("Livrables");
            Row headerLivrable = sheetLivrables.createRow(0);
            headerLivrable.createCell(0).setCellValue("Type");
            headerLivrable.createCell(1).setCellValue("Version");
            headerLivrable.createCell(2).setCellValue("Statut");
            headerLivrable.createCell(3).setCellValue("Note");

            int livRow = 1;
            for (LivrableResponse l : livrables) {
                Row r = sheetLivrables.createRow(livRow++);
                r.createCell(0).setCellValue(safe(l.getTypeLivrable()));
                r.createCell(1).setCellValue(l.getVersion() == null ? 0 : l.getVersion());
                r.createCell(2).setCellValue(safe(l.getStatut()));
                r.createCell(3).setCellValue(l.getNote() == null ? 0 : l.getNote());
            }

            Sheet sheetSuivis = workbook.createSheet("Suivis");
            Row headerSuivi = sheetSuivis.createRow(0);
            headerSuivi.createCell(0).setCellValue("Date");
            headerSuivi.createCell(1).setCellValue("Enseignant");
            headerSuivi.createCell(2).setCellValue("Avancement");
            headerSuivi.createCell(3).setCellValue("Qualité");
            headerSuivi.createCell(4).setCellValue("Délais");
            headerSuivi.createCell(5).setCellValue("Participation");
            headerSuivi.createCell(6).setCellValue("Risque");

            int suiviRow = 1;
            for (SuiviResponse s : suivis) {
                Row r = sheetSuivis.createRow(suiviRow++);
                r.createCell(0).setCellValue(safeDate(s.getDateSuivi()));
                r.createCell(1).setCellValue(safe(s.getEnseignantNomComplet()));
                r.createCell(2).setCellValue(safeInt(s.getAvancementPourcentage()));
                r.createCell(3).setCellValue(safeInt(s.getQualiteTravail()));
                r.createCell(4).setCellValue(safeInt(s.getRespectDelais()));
                r.createCell(5).setCellValue(safeInt(s.getParticipationEtudiant()));
                r.createCell(6).setCellValue(safe(s.getNiveauRisque()));
            }

            Sheet sheetEvaluations = workbook.createSheet("Evaluations");
            Row headerEvaluation = sheetEvaluations.createRow(0);
            headerEvaluation.createCell(0).setCellValue("Enseignant");
            headerEvaluation.createCell(1).setCellValue("Note");
            headerEvaluation.createCell(2).setCellValue("Appréciation");

            int evalRow = 1;
            for (EvaluationResponse e : evaluations) {
                Row r = sheetEvaluations.createRow(evalRow++);
                r.createCell(0).setCellValue(safe(e.getEnseignantNomComplet()));
                r.createCell(1).setCellValue(safeInt(e.getNoteGlobale()));
                r.createCell(2).setCellValue(safe(e.getAppreciation()));
            }

            Sheet sheetHistorique = workbook.createSheet("Historique");
            Row headerHistorique = sheetHistorique.createRow(0);
            headerHistorique.createCell(0).setCellValue("Date");
            headerHistorique.createCell(1).setCellValue("Action");
            headerHistorique.createCell(2).setCellValue("Acteur");
            headerHistorique.createCell(3).setCellValue("Description");

            int histRow = 1;
            for (HistoriqueResponse h : historiques) {
                Row r = sheetHistorique.createRow(histRow++);
                r.createCell(0).setCellValue(safeDate(h.getDateAction()));
                r.createCell(1).setCellValue(safe(h.getAction()));
                r.createCell(2).setCellValue(safe(h.getActeurNomComplet()));
                r.createCell(3).setCellValue(safe(h.getDescription()));
            }

            for (int i = 0; i < 2; i++) sheetIndicateurs.autoSizeColumn(i);
            for (int i = 0; i < 4; i++) sheetLivrables.autoSizeColumn(i);
            for (int i = 0; i < 7; i++) sheetSuivis.autoSizeColumn(i);
            for (int i = 0; i < 3; i++) sheetEvaluations.autoSizeColumn(i);
            for (int i = 0; i < 4; i++) sheetHistorique.autoSizeColumn(i);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            workbook.close();

            historiqueService.ajouter(
                    encadrementId,
                    userId,
                    nom,
                    role,
                    "GENERATION_RAPPORT_EXCEL",
                    "Rapport Excel généré",
                    "Génération du rapport Excel du projet."
            );

            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erreur génération Excel : " + e.getMessage());
        }
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String safeDate(Object value) {
        return value == null ? "" : value.toString();
    }

    private int safeInt(Integer value) {
        return value == null ? 0 : value;
    }
}