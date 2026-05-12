package com.uasz.gestion_suivi_evaluation_service.service;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfWriter;
import com.uasz.gestion_suivi_evaluation_service.client.LivrableClient;
import com.uasz.gestion_suivi_evaluation_service.dto.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Row;
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
    private final HistoriqueService historique;

    public byte[] genererPdf(Long encadrementId, String token, Long userId, String nom, String role) {

        try {
            IndicateurResponse indicateurs = indicateurService.calculer(encadrementId, token);
            List<SuiviResponse> suivis = suiviService.parEncadrement(encadrementId);
            List<EvaluationResponse> evaluations = evaluationService.parEncadrement(encadrementId);
            List<HistoriqueResponse> historiques = historiqueService.parEncadrement(encadrementId);
            List<LivrableResponse> livrables = livrableClient.livrablesParEncadrement(encadrementId, token);

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
            document.add(new Paragraph("Nombre de livrables : " + indicateurs.getNombreLivrables(), normalFont));
            document.add(new Paragraph("Livrables validés/évalués : " + indicateurs.getLivrablesValides(), normalFont));
            document.add(new Paragraph("Livrables en retard : " + indicateurs.getLivrablesEnRetard(), normalFont));
            document.add(new Paragraph("Moyenne livrables : " + indicateurs.getMoyenneLivrables() + "/20", normalFont));
            document.add(new Paragraph("Niveau de risque : " + indicateurs.getNiveauRisque(), normalFont));
            document.add(new Paragraph("Statut projet : " + indicateurs.getStatutProjet(), normalFont));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("2. Livrables", sectionFont));
            for (LivrableResponse l : livrables) {
                document.add(new Paragraph(
                        "- " + l.getTypeLivrable()
                                + " | v" + l.getVersion()
                                + " | statut : " + l.getStatut()
                                + " | note : " + (l.getNote() == null ? "Non évalué" : l.getNote() + "/20"),
                        normalFont
                ));
            }
            document.add(new Paragraph(" "));

            document.add(new Paragraph("3. Suivis pédagogiques", sectionFont));
            for (SuiviResponse s : suivis) {
                document.add(new Paragraph(
                        "- " + s.getDateSuivi()
                                + " | Avancement : " + s.getAvancementPourcentage() + "%"
                                + " | Risque : " + s.getNiveauRisque(),
                        normalFont
                ));
                document.add(new Paragraph("  Observations : " + s.getObservations(), normalFont));
                document.add(new Paragraph("  Recommandations : " + safe(s.getRecommandations()), normalFont));
            }
            document.add(new Paragraph(" "));

            document.add(new Paragraph("4. Évaluations globales", sectionFont));
            for (EvaluationResponse e : evaluations) {
                document.add(new Paragraph(
                        "- Note globale : " + e.getNoteGlobale() + "/20 | Enseignant : " + e.getEnseignantNomComplet(),
                        normalFont
                ));
                document.add(new Paragraph("  Appréciation : " + safe(e.getAppreciation()), normalFont));
            }
            document.add(new Paragraph(" "));

            document.add(new Paragraph("5. Historique des actions", sectionFont));
            for (HistoriqueResponse h : historiques) {
                document.add(new Paragraph(
                        "- " + h.getDateAction()
                                + " | " + h.getAction()
                                + " | " + h.getActeurNomComplet()
                                + " | " + h.getDescription(),
                        normalFont
                ));
            }

            document.close();

            historique.ajouter(
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

    public byte[] genererExcel(Long encadrementId, String token, Long userId, String nom, String role) {

        try {
            IndicateurResponse indicateurs = indicateurService.calculer(encadrementId, token);
            List<SuiviResponse> suivis = suiviService.parEncadrement(encadrementId);
            List<EvaluationResponse> evaluations = evaluationService.parEncadrement(encadrementId);
            List<LivrableResponse> livrables = livrableClient.livrablesParEncadrement(encadrementId, token);

            Workbook workbook = new XSSFWorkbook();

            Sheet sheet = workbook.createSheet("Rapport Suivi");
            int rowIndex = 0;

            Row title = sheet.createRow(rowIndex++);
            title.createCell(0).setCellValue("Rapport de suivi et évaluation");

            rowIndex++;

            Row header = sheet.createRow(rowIndex++);
            header.createCell(0).setCellValue("Indicateur");
            header.createCell(1).setCellValue("Valeur");

            rowIndex = add(sheet, rowIndex, "Avancement", indicateurs.getAvancementActuel() + "%");
            rowIndex = add(sheet, rowIndex, "Nombre livrables", String.valueOf(indicateurs.getNombreLivrables()));
            rowIndex = add(sheet, rowIndex, "Livrables validés/évalués", String.valueOf(indicateurs.getLivrablesValides()));
            rowIndex = add(sheet, rowIndex, "Livrables en retard", String.valueOf(indicateurs.getLivrablesEnRetard()));
            rowIndex = add(sheet, rowIndex, "Moyenne livrables", indicateurs.getMoyenneLivrables() + "/20");
            rowIndex = add(sheet, rowIndex, "Risque", indicateurs.getNiveauRisque());
            rowIndex = add(sheet, rowIndex, "Statut", indicateurs.getStatutProjet());

            Sheet livSheet = workbook.createSheet("Livrables");
            Row livHeader = livSheet.createRow(0);
            livHeader.createCell(0).setCellValue("Type");
            livHeader.createCell(1).setCellValue("Version");
            livHeader.createCell(2).setCellValue("Statut");
            livHeader.createCell(3).setCellValue("Note");
            livHeader.createCell(4).setCellValue("Date dépôt");

            int i = 1;
            for (LivrableResponse l : livrables) {
                Row r = livSheet.createRow(i++);
                r.createCell(0).setCellValue(safe(l.getTypeLivrable()));
                r.createCell(1).setCellValue(l.getVersion() == null ? 0 : l.getVersion());
                r.createCell(2).setCellValue(safe(l.getStatut()));
                r.createCell(3).setCellValue(l.getNote() == null ? 0 : l.getNote());
                r.createCell(4).setCellValue(l.getDateDepot() == null ? "" : l.getDateDepot().toString());
            }

            Sheet suiviSheet = workbook.createSheet("Suivis");
            Row suiviHeader = suiviSheet.createRow(0);
            suiviHeader.createCell(0).setCellValue("Date");
            suiviHeader.createCell(1).setCellValue("Avancement");
            suiviHeader.createCell(2).setCellValue("Qualité");
            suiviHeader.createCell(3).setCellValue("Délais");
            suiviHeader.createCell(4).setCellValue("Participation");
            suiviHeader.createCell(5).setCellValue("Risque");
            suiviHeader.createCell(6).setCellValue("Observations");

            i = 1;
            for (SuiviResponse s : suivis) {
                Row r = suiviSheet.createRow(i++);
                r.createCell(0).setCellValue(s.getDateSuivi() == null ? "" : s.getDateSuivi().toString());
                r.createCell(1).setCellValue(s.getAvancementPourcentage());
                r.createCell(2).setCellValue(s.getQualiteTravail());
                r.createCell(3).setCellValue(s.getRespectDelais());
                r.createCell(4).setCellValue(s.getParticipationEtudiant());
                r.createCell(5).setCellValue(safe(s.getNiveauRisque()));
                r.createCell(6).setCellValue(safe(s.getObservations()));
            }

            Sheet evalSheet = workbook.createSheet("Evaluations");
            Row evalHeader = evalSheet.createRow(0);
            evalHeader.createCell(0).setCellValue("Date");
            evalHeader.createCell(1).setCellValue("Enseignant");
            evalHeader.createCell(2).setCellValue("Note globale");
            evalHeader.createCell(3).setCellValue("Appréciation");

            i = 1;
            for (EvaluationResponse e : evaluations) {
                Row r = evalSheet.createRow(i++);
                r.createCell(0).setCellValue(e.getDateEvaluation() == null ? "" : e.getDateEvaluation().toString());
                r.createCell(1).setCellValue(safe(e.getEnseignantNomComplet()));
                r.createCell(2).setCellValue(e.getNoteGlobale() == null ? 0 : e.getNoteGlobale());
                r.createCell(3).setCellValue(safe(e.getAppreciation()));
            }

            for (int c = 0; c < 6; c++) {
                sheet.autoSizeColumn(c);
                livSheet.autoSizeColumn(c);
                suiviSheet.autoSizeColumn(c);
                evalSheet.autoSizeColumn(c);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            workbook.close();

            historique.ajouter(
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

    private int add(Sheet sheet, int rowIndex, String label, String value) {
        Row row = sheet.createRow(rowIndex++);
        row.createCell(0).setCellValue(label);
        row.createCell(1).setCellValue(value);
        return rowIndex;
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}