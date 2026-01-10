package com.sms.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@Service
public class FileContentExtractor {

    /**
     * 根据文件类型提取文本内容
     */
    public String extractContent(byte[] fileData, String fileName) {
        try {
            String extension = getFileExtension(fileName).toLowerCase();

            switch (extension) {
                case "pdf":
                    return extractPdfContent(fileData);
                case "docx":
                case "doc":
                    return extractWordContent(fileData);
                case "pptx":
                case "ppt":
                    return extractPptContent(fileData);
                case "txt":
                    return new String(fileData, "UTF-8");
                default:
                    return "";
            }
        } catch (Exception e) {
            System.err.println("文件内容提取失败: " + e.getMessage());
            return "";
        }
    }

    private String extractPdfContent(byte[] fileData) throws IOException {
        try (PDDocument document = PDDocument.load(fileData)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    private String extractWordContent(byte[] fileData) throws IOException {
        try (XWPFDocument document = new XWPFDocument(new ByteArrayInputStream(fileData))) {
            StringBuilder content = new StringBuilder();
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            for (XWPFParagraph paragraph : paragraphs) {
                content.append(paragraph.getText()).append("\n");
            }
            return content.toString();
        }
    }

    private String extractPptContent(byte[] fileData) throws IOException {
        try (XMLSlideShow ppt = new XMLSlideShow(new ByteArrayInputStream(fileData))) {
            StringBuilder content = new StringBuilder();
            for (XSLFSlide slide : ppt.getSlides()) {
                for (XSLFTextShape shape : slide.getPlaceholders()) {
                    content.append(shape.getText()).append("\n");
                }
            }
            return content.toString();
        }
    }

    private String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        return lastDot > 0 ? fileName.substring(lastDot + 1) : "";
    }
}
