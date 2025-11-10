package com.group4.clinicmanagement.util;

import com.group4.clinicmanagement.entity.Bill;
import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.time.format.DateTimeFormatter;

@Component
public class BillPdfExporter {
    public void export(Bill bill, OutputStream outputStream) throws Exception {
        // Custom receipt size
        Rectangle receiptSize = new Rectangle(300, 600);
        Document document = new Document(receiptSize, 20, 20, 20, 20);
        PdfWriter.getInstance(document, outputStream);
        document.open();

        // Load Unicode fonts (from resources/fonts)
        String regularFontPath = new ClassPathResource("fonts/Helvetica.ttf").getFile().getPath();
        String boldFontPath = new ClassPathResource("fonts/Helvetica-Bold.ttf").getFile().getPath();

        Font titleFont = loadUnicodeFont(boldFontPath, 14f, Font.BOLD);
        Font normalFont = loadUnicodeFont(regularFontPath, 10f, Font.NORMAL);
        Font boldFont = loadUnicodeFont(boldFontPath, 12f, Font.BOLD);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        // Title
        Paragraph title = new Paragraph("MEDICAL BILL", titleFont);
        title.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph(" "));

        // Bill Info
        document.add(new Paragraph("Created at: " + dtf.format(bill.getCreatedAt()), normalFont));
        if (bill.getPaidAt() != null) {
            document.add(new Paragraph("Paid at: " + dtf.format(bill.getPaidAt()), normalFont));
        }
        document.add(new Paragraph("Status: " + bill.getStatus(), normalFont));
        document.add(new Paragraph(" "));

        // Patient Info
        document.add(new Paragraph("Patient: " + bill.getPatient().getUser().getFullName(), normalFont));
        document.add(new Paragraph("Phone: " + bill.getPatient().getUser().getPhone(), normalFont));

        // Appointment or Lab Test Info
        if (bill.getAppointment() != null) {
            document.add(new Paragraph("Appointment Date: " + bill.getAppointment().getAppointmentDate(), normalFont));
            document.add(new Paragraph("Doctor: " + bill.getAppointment().getDoctor().getUser().getFullName(), normalFont));
            document.add(new Paragraph("Queue Number: " + bill.getAppointment().getQueueNumber(), normalFont));
        } else if (bill.getLabRequest() != null) {
            document.add(new Paragraph("Lab Test: " + bill.getLabRequest().getTest().getName(), normalFont));
        }

        document.add(new Paragraph(" "));

        // Total
        document.add(new Paragraph("Total Amount: $" + String.format("%.2f", bill.getAmount()), boldFont));

        document.close();
    }

    private Font loadUnicodeFont(String fontPath, float size, int style) throws Exception {
        BaseFont baseFont = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        return new Font(baseFont, size, style);
    }
}
