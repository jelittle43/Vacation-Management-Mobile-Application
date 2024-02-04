package com.example.capstone.entities;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class PdfGenerator {

    public static void generateVacationReport(Vacation vacation, List<Excursion> excursions, String filePath) {
        if (vacation == null || excursions == null || filePath == null) {
            Log.e("PdfGenerator", "Invalid parameters provided for PDF generation");
            return;
        }

        new PdfGenerationTask().execute(vacation, excursions, filePath);
    }

    private static class PdfGenerationTask extends AsyncTask<Object, Void, String> {

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
