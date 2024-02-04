package com.example.capstone.entities;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;

public class PdfGenerator {

    private static PdfFont font;

    static {
        try {
            font = PdfFontFactory.createFont(FontConstants.HELVETICA);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void generateVacationReport(Vacation vacation, List<Excursion> excursions, String filePath) {
        try {
            PdfWriter writer = new PdfWriter(filePath);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Add Vacation information
            document.add(new Paragraph("Vacation Report").setFont(font).setFontSize(18).setBold());

            // Create a table for Vacation information
            Table vacationTable = new Table(3); // 3 columns
            vacationTable.addCell(createHeaderCell("Vacation ID"));
            vacationTable.addCell(createHeaderCell("Name"));
            vacationTable.addCell(createHeaderCell("Date"));

            vacationTable.addCell(createValueCell(String.valueOf(vacation.getVacationID())));
            vacationTable.addCell(createValueCell(vacation.getVacationName()));
            vacationTable.addCell(createValueCell(vacation.getStartDate() + " - " + vacation.getEndDate()));

            document.add(vacationTable);

            // Add a newline for better separation
            document.add(new Paragraph("\n"));

            // Add Excursions information
            document.add(new Paragraph("Excursions").setFont(font).setFontSize(16).setBold());

            // Create a table for Excursions information
            Table excursionTable = new Table(3); // Adjust the number of columns based on your Excursion class properties
            excursionTable.addCell(createHeaderCell("Excursion ID"));
            excursionTable.addCell(createHeaderCell("Excursion Name"));
            excursionTable.addCell(createHeaderCell("Excursion Date"));

            // Loop through excursions and add them to the table
            for (Excursion excursion : excursions) {
                excursionTable.addCell(createValueCell(String.valueOf(excursion.getExcursionID())));
                excursionTable.addCell(createValueCell(excursion.getExcursionName()));
                excursionTable.addCell(createValueCell(excursion.getExcursionDate()));
            }

            document.add(excursionTable);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Helper method to create header cell
    private static Cell createHeaderCell(String content) {
        return new Cell().add(new Paragraph(content).setFont(font).setBold().setTextAlignment(TextAlignment.CENTER));
    }

    // Helper method to create value cell
    private static Cell createValueCell(String content) {
        return new Cell().add(new Paragraph(content).setFont(font).setTextAlignment(TextAlignment.CENTER));
    }
}

    class PdfGenerationTask extends AsyncTask<Object, Void, String> {

        @Override
        protected String doInBackground(Object... params) {
            Vacation vacation = (Vacation) params[0];
            List<Excursion> excursions = (List<Excursion>) params[1];
            String filePath = (String) params[2];

            // PDF generation logic here
            generatePdf(vacation, excursions, filePath);

            return "PDF generated successfully";
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i("PdfGenerator", result);
        }



    private static void generatePdf(Vacation vacation, List<Excursion> excursions, String filePath) {
        try {
            // Initialize PDF document
            PdfWriter writer = new PdfWriter(new FileOutputStream(filePath));
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Add content to the PDF
            document.add(new Paragraph("Vacation Details:"));
            document.add(new Paragraph(vacationToString(vacation) + "\n\n"));

            document.add(new Paragraph("Excursions:"));
            for (Excursion excursion : excursions) {
                document.add(new Paragraph(excursionToString(excursion) + "\n"));
            }

            // Close the document
            document.close();

            Log.i("PdfGenerator", "PDF generated successfully and saved to: " + filePath);
        } catch (IOException e) {
            Log.e("PdfGenerator", "Error generating PDF: " + e.getMessage());
        }
    }

    private static String vacationToString(Vacation vacation) {
        if (vacation == null) {
            return "Vacation: null";
        }

        // Customize this based on the actual structure of your Vacation class
        return "Vacation ID: " + vacation.getVacationID() + ", Name: " + vacation.getVacationName() + ", Date: " + vacation.getStartDate();
    }

    private static String excursionToString(Excursion excursion) {
        if (excursion == null) {
            return "Excursion: null";
        }

        // Customize this based on the actual structure of your Excursion class
        return "Excursion ID: " + excursion.getExcursionID() + ", Name: " + excursion.getExcursionName() + ", Date: " + excursion.getExcursionDate();
    }
}
