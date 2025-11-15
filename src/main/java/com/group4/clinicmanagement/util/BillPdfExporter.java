package com.group4.clinicmanagement.util;

import com.group4.clinicmanagement.entity.Bill;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.format.DateTimeFormatter;

@Component
public class BillPdfExporter {

    public void export(Bill bill, OutputStream outputStream) throws Exception {

        Rectangle receiptSize = new Rectangle(320, 550);
        Document document = new Document(receiptSize, 20, 20, 20, 20);
        PdfWriter.getInstance(document, outputStream);
        document.open();

        // ================= LOAD FONT ĐÚNG CÁCH CHO OPENPDF =================
        Font normalFont = loadFont("fonts/Helvetica.ttf", 10, Font.NORMAL, Color.BLACK);
        Font boldFont = loadFont("fonts/Helvetica-Bold.ttf", 11, Font.BOLD, Color.BLACK);
        Font headerFont = loadFont("fonts/Helvetica-Bold.ttf", 12, Font.BOLD, new Color(0, 102, 204));
        Font titleFont = loadFont("fonts/Helvetica-Bold.ttf", 16, Font.BOLD, Color.BLACK);
        Font smallGrayFont = loadFont("fonts/Helvetica.ttf", 8, Font.NORMAL, Color.GRAY);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        // ================= HEADER =================
        Paragraph title = new Paragraph("HEATHHUB CLINIC", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        Paragraph subtitle = new Paragraph("Official Medical Bill", headerFont);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        document.add(subtitle);

        document.add(new Paragraph(" "));

        // ================= BILL INFO =================
        PdfPTable infoTable = new PdfPTable(1);
        infoTable.setWidthPercentage(100);
        infoTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

        infoTable.addCell(createCell("Bill ID: " + bill.getBillId(), normalFont));
        infoTable.addCell(createCell("Created at: " + dtf.format(bill.getCreatedAt()), normalFont));
        if (bill.getPaidAt() != null)
            infoTable.addCell(createCell("Paid at: " + dtf.format(bill.getPaidAt()), normalFont));
        infoTable.addCell(createCell("Status: " + bill.getStatus().name(), normalFont));

        document.add(infoTable);
        document.add(new Paragraph(" "));

        // ================= PATIENT =================
        Paragraph patientHeader = new Paragraph("Patient Information", headerFont);
        document.add(patientHeader);

        PdfPTable patientTable = new PdfPTable(1);
        patientTable.setWidthPercentage(100);
        patientTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

        patientTable.addCell(createCell("Name: " + bill.getPatient().getFullName(), normalFont));
        patientTable.addCell(createCell("Phone: " + bill.getPatient().getPhone(), normalFont));

        document.add(patientTable);
        document.add(new Paragraph(" "));

        // ================= APPOINTMENT / LAB =================
        if (bill.getAppointment() != null) {
            Paragraph appHeader = new Paragraph("Appointment Details", headerFont);
            document.add(appHeader);

            PdfPTable appTable = new PdfPTable(1);
            appTable.setWidthPercentage(100);
            appTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

            appTable.addCell(createCell("Doctor: " + bill.getAppointment().getDoctor().getStaff().getFullName(), normalFont));
            appTable.addCell(createCell("Date: " + bill.getAppointment().getAppointmentDate(), normalFont));
            appTable.addCell(createCell("Queue No: " + (bill.getAppointment().getQueueNumber() == null ? "-" : bill.getAppointment().getQueueNumber()), normalFont));
            appTable.addCell(createCell("Service: General Examination", normalFont));

            document.add(appTable);

        } else if (bill.getLabRequest() != null) {

            Paragraph labHeader = new Paragraph("Lab Test Details", headerFont);
            document.add(labHeader);

            PdfPTable labTable = new PdfPTable(1);
            labTable.setWidthPercentage(100);
            labTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

            labTable.addCell(createCell("Requested by: " + bill.getLabRequest().getDoctor().getStaff().getFullName(), normalFont));
            labTable.addCell(createCell("Test: " + bill.getLabRequest().getTest().getName(), normalFont));
            labTable.addCell(createCell("Description: " + bill.getLabRequest().getTest().getDescription(), normalFont));

            document.add(labTable);
        }

        document.add(new Paragraph(" "));

        // ================= TOTAL =================
        Paragraph totalTitle = new Paragraph("TOTAL AMOUNT", boldFont);
        totalTitle.setAlignment(Element.ALIGN_CENTER);
        document.add(totalTitle);

        Paragraph amountText = new Paragraph(String.format("%,.0f VND", bill.getAmount()), headerFont);
        amountText.setAlignment(Element.ALIGN_CENTER);
        document.add(amountText);

        document.add(new Paragraph(" "));

        // ================= FOOTER =================
        document.add(new Paragraph("Thank you for visiting HealHub Clinic.", smallGrayFont));
        document.add(new Paragraph("Please keep this receipt for your records.", smallGrayFont));

        document.close();
    }


    private Font loadFont(String classpathFont, float size, int style, Color color) throws Exception {
        ClassPathResource resource = new ClassPathResource(classpathFont);
        InputStream is = resource.getInputStream();
        byte[] fontBytes = is.readAllBytes();
        BaseFont bf = BaseFont.createFont(
                "LoadedFont.ttf",
                BaseFont.IDENTITY_H,
                BaseFont.EMBEDDED,
                false,
                fontBytes,
                null
        );

        Font font = new Font(bf, size, style);
        font.setColor(color);
        return font;
    }


    private PdfPCell createCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(2f);
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }
}
