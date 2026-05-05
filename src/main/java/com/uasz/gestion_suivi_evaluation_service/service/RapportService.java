package com.uasz.gestion_suivi_evaluation_service.service;

import com.lowagie.text.Document;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
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

    private final SuiviEvaluationService suiviEvaluationService;

    public byte[] genererPdf(Long encadrementId, String token) {
        try {
            IndicateurProjetResponse indicateurs =
                    suiviEvaluationService.indicateurs(encadrementId, token);

            List<SuiviProjetResponse> suivis =
                    suiviEvaluationService.suivisParEncadrement(encadrementId);

            List<EvaluationProjetResponse> evaluations =
                    suiviEvaluationService.evaluationsParEncadrement(encadrementId);

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);

            document.open();

            com.lowagie.text.Font titleFont =
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);

            com.lowagie.text.Font sectionFont =
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13);

            com.lowagie.text.Font normalFont =
                    FontFactory.getFont(FontFactory.HELVETICA, 10);

            document.add(new Paragraph("Rapport de suivi et évaluation du projet", titleFont));
            document.add(new Paragraph("Université Assane Seck de Ziguinchor", normalFont));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Informations du projet", sectionFont));
            document.add(new Paragraph("Sujet : " + safe(indicateurs.getSujetTitre()), normalFont));
            document.add(new Paragraph("Étudiant : " + safe(indicateurs.getEtudiantNomComplet()), normalFont));
            document.add(new Paragraph("Encadreur : " + safe(indicateurs.getEnseignantNomComplet()), normalFont));
            document.add(new Paragraph("Statut : " + safe(indicateurs.getStatutProjet()), normalFont));
            document.add(new Paragraph("Avancement : " + indicateurs.getAvancementActuel() + "%", normalFont));
            document.add(new Paragraph("Niveau de risque : " + indicateurs.getNiveauRisque(), normalFont));
            document.add(new Paragraph("Nombre de livrables : " + indicateurs.getNombreLivrables(), normalFont));
            document.add(new Paragraph("Moyenne livrables : " + indicateurs.getMoyenneLivrables(), normalFont));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Derniers suivis", sectionFont));

            if (suivis.isEmpty()) {
                document.add(new Paragraph("Aucun suivi enregistré.", normalFont));
            } else {
                for (SuiviProjetResponse s : suivis) {
                    document.add(new Paragraph(
                            "- " + s.getDateSuivi()
                                    + " | Avancement : " + s.getAvancementPourcentage() + "%"
                                    + " | " + safe(s.getObservations()),
                            normalFont
                    ));
                }
            }

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Évaluations", sectionFont));

            if (evaluations.isEmpty()) {
                document.add(new Paragraph("Aucune évaluation enregistrée.", normalFont));
            } else {
                for (EvaluationProjetResponse e : evaluations) {
                    document.add(new Paragraph(
                            "- " + e.getDateEvaluation()
                                    + " | Note : " + e.getNoteGlobale() + "/20"
                                    + " | " + safe(e.getAppreciation()),
                            normalFont
                    ));
                }
            }

            document.close();

            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erreur génération PDF : " + e.getMessage());
        }
    }

    public byte[] genererExcel(Long encadrementId, String token) {
        try {
            IndicateurProjetResponse indicateurs =
                    suiviEvaluationService.indicateurs(encadrementId, token);

            List<SuiviProjetResponse> suivis =
                    suiviEvaluationService.suivisParEncadrement(encadrementId);

            List<EvaluationProjetResponse> evaluations =
                    suiviEvaluationService.evaluationsParEncadrement(encadrementId);

            Workbook workbook = new XSSFWorkbook();

            Sheet sheetInfo = workbook.createSheet("Indicateurs");

            Row header = sheetInfo.createRow(0);
            header.createCell(0).setCellValue("Champ");
            header.createCell(1).setCellValue("Valeur");

            String[][] data = {
                    {"Sujet", safe(indicateurs.getSujetTitre())},
                    {"Étudiant", safe(indicateurs.getEtudiantNomComplet())},
                    {"Encadreur", safe(indicateurs.getEnseignantNomComplet())},
                    {"Avancement", indicateurs.getAvancementActuel() + "%"},
                    {"Risque", String.valueOf(indicateurs.getNiveauRisque())},
                    {"Statut", safe(indicateurs.getStatutProjet())},
                    {"Nombre livrables", String.valueOf(indicateurs.getNombreLivrables())},
                    {"Moyenne livrables", String.valueOf(indicateurs.getMoyenneLivrables())}
            };

            for (int i = 0; i < data.length; i++) {
                Row row = sheetInfo.createRow(i + 1);
                row.createCell(0).setCellValue(data[i][0]);
                row.createCell(1).setCellValue(data[i][1]);
            }

            Sheet sheetSuivis = workbook.createSheet("Suivis");

            Row suiviHeader = sheetSuivis.createRow(0);
            suiviHeader.createCell(0).setCellValue("Date");
            suiviHeader.createCell(1).setCellValue("Avancement");
            suiviHeader.createCell(2).setCellValue("Qualité");
            suiviHeader.createCell(3).setCellValue("Délais");
            suiviHeader.createCell(4).setCellValue("Participation");
            suiviHeader.createCell(5).setCellValue("Risque");
            suiviHeader.createCell(6).setCellValue("Observations");

            int rowIndex = 1;

            for (SuiviProjetResponse s : suivis) {
                Row row = sheetSuivis.createRow(rowIndex++);
                row.createCell(0).setCellValue(String.valueOf(s.getDateSuivi()));
                row.createCell(1).setCellValue(valueOrZero(s.getAvancementPourcentage()));
                row.createCell(2).setCellValue(valueOrZero(s.getQualiteTravail()));
                row.createCell(3).setCellValue(valueOrZero(s.getRespectDelais()));
                row.createCell(4).setCellValue(valueOrZero(s.getParticipationEtudiant()));
                row.createCell(5).setCellValue(String.valueOf(s.getNiveauRisque()));
                row.createCell(6).setCellValue(safe(s.getObservations()));
            }

            Sheet sheetEval = workbook.createSheet("Evaluations");

            Row evalHeader = sheetEval.createRow(0);
            evalHeader.createCell(0).setCellValue("Date");
            evalHeader.createCell(1).setCellValue("Note");
            evalHeader.createCell(2).setCellValue("Appréciation");
            evalHeader.createCell(3).setCellValue("Points forts");
            evalHeader.createCell(4).setCellValue("Points à améliorer");

            rowIndex = 1;

            for (EvaluationProjetResponse e : evaluations) {
                Row row = sheetEval.createRow(rowIndex++);
                row.createCell(0).setCellValue(String.valueOf(e.getDateEvaluation()));
                row.createCell(1).setCellValue(valueOrZero(e.getNoteGlobale()));
                row.createCell(2).setCellValue(safe(e.getAppreciation()));
                row.createCell(3).setCellValue(safe(e.getPointsForts()));
                row.createCell(4).setCellValue(safe(e.getPointsAAmeliorer()));
            }

            autoSize(sheetInfo, 2);
            autoSize(sheetSuivis, 7);
            autoSize(sheetEval, 5);

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            workbook.write(out);
            workbook.close();

            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erreur génération Excel : " + e.getMessage());
        }
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private int valueOrZero(Integer value) {
        return value == null ? 0 : value;
    }

    private void autoSize(Sheet sheet, int numberOfColumns) {
        for (int i = 0; i < numberOfColumns; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}