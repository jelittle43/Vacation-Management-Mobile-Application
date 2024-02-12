package com.example.capstone.util;



import com.example.capstone.entities.Excursion;
import com.example.capstone.entities.Vacation;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PdfGenerator {

    private static PdfFont font;

    static {
        try {
            font = PdfFontFactory.createFont(FontConstants.HELVETICA);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void generateVacationsReport(List<Vacation> vacations, List<Excursion> excursions, String filePath) {
        try {

            File file = new File(filePath);
            File parentDir = file.getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }


            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = "VacationsReport_" + timeStamp + ".pdf";
            String fullFilePath = parentDir.getAbsolutePath() + File.separator + fileName;

            PdfWriter writer = new PdfWriter(fullFilePath);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);


            document.add(new Paragraph("Vacations Report").setFont(font).setFontSize(18).setBold());

            for (Vacation vacation : vacations) {
                document.add(new Paragraph("\n"));
                document.add(new Paragraph("Vacation ID: " + vacation.getVacationID()).setFont(font).setBold());


                Table vacationTable = new Table(new float[]{2, 2, 2, 2}); // Set equal column width


                vacationTable.setWidth(UnitValue.createPercentValue(100));

                vacationTable.addCell(createHeaderCell("Vacation Name"));
                vacationTable.addCell(createHeaderCell("Hotel"));
                vacationTable.addCell(createHeaderCell("Start Date"));
                vacationTable.addCell(createHeaderCell("End Date"));

                vacationTable.addCell(createValueCell(vacation.getVacationName()));
                vacationTable.addCell(createValueCell(vacation.getHotel()));
                vacationTable.addCell(createValueCell(vacation.getStartDate()));
                vacationTable.addCell(createValueCell(vacation.getEndDate()));

                document.add(vacationTable);


                document.add(new Paragraph().setMarginBottom(10));


                List<Excursion> vacationExcursions = filterExcursionsByVacation(excursions, vacation.getVacationID());


                if (!vacationExcursions.isEmpty()) {

                    Table excursionTable = new Table(new float[]{2, 2}); // Set equal column width


                    excursionTable.setWidth(UnitValue.createPercentValue(100));

                    excursionTable.addCell(createHeaderCell("Excursion Name"));
                    excursionTable.addCell(createHeaderCell("Excursion Date"));


                    for (Excursion excursion : vacationExcursions) {
                        excursionTable.addCell(createValueCell(excursion.getExcursionName()));
                        excursionTable.addCell(createValueCell(excursion.getExcursionDate()));
                    }

                    document.add(excursionTable);

                }
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String formattedDate = sdf.format(new Date());
            document.add(new Paragraph("Report generated on: " + formattedDate).setFont(font));

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Cell createHeaderCell(String content) {
        return new Cell().add(new Paragraph(content).setFont(font).setBold().setTextAlignment(TextAlignment.CENTER));
    }

    private static Cell createValueCell(String content) {
        return new Cell().add(new Paragraph(content).setFont(font).setTextAlignment(TextAlignment.CENTER));
    }


    private static List<Excursion> filterExcursionsByVacation(List<Excursion> excursions, int vacationID) {

        List<Excursion> filteredExcursions = new ArrayList<>();
        for (Excursion excursion : excursions) {
            if (excursion.getVacationID() == vacationID) {
                filteredExcursions.add(excursion);
            }
        }
        return filteredExcursions;
    }
}
