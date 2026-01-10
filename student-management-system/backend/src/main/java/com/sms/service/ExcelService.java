package com.sms.service;

import com.sms.model.Student;
import com.sms.model.User;
import com.sms.repository.StudentRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelService {

    @Autowired
    private StudentRepository studentRepository;

    private static final String[] STUDENT_HEADERS = {
            "学号", "姓名", "班级", "性别", "手机", "邮箱"
    };

    /**
     * 导出学生数据到Excel
     */
    public byte[] exportStudents(List<Student> students) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("学生信息");

            // 创建标题行样式
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // 创建标题行
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < STUDENT_HEADERS.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(STUDENT_HEADERS[i]);
                cell.setCellStyle(headerStyle);
            }

            // 创建数据行
            int rowNum = 1;
            for (Student student : students) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(student.getStudentNumber() != null ? student.getStudentNumber() : "");
                row.createCell(1).setCellValue(student.getName() != null ? student.getName() : "");
                row.createCell(2).setCellValue(student.getClazz() != null ? student.getClazz().getClassName() : "");
                row.createCell(3).setCellValue(student.getGender() != null ? ("1".equals(student.getGender()) ? "男" : "女") : "");
                row.createCell(4).setCellValue(student.getPhone() != null ? student.getPhone() : "");
                row.createCell(5).setCellValue(student.getEmail() != null ? student.getEmail() : "");
            }

            // 自动调整列宽
            for (int i = 0; i < STUDENT_HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    /**
     * 从Excel导入学生数据
     */
    public List<Student> importStudents(MultipartFile file) throws IOException {
        List<Student> students = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            int rowCount = sheet.getPhysicalNumberOfRows();

            // 跳过标题行，从第二行开始
            for (int i = 1; i < rowCount; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Student student = new Student();
                student.setStudentNumber(getCellStringValue(row.getCell(0)));
                student.setName(getCellStringValue(row.getCell(1)));
                // 班级需要通过名称查找，这里暂时跳过
                String gender = getCellStringValue(row.getCell(3));
                if ("男".equals(gender)) {
                    student.setGender("1");
                } else if ("女".equals(gender)) {
                    student.setGender("2");
                }
                student.setPhone(getCellStringValue(row.getCell(4)));
                student.setEmail(getCellStringValue(row.getCell(5)));

                // 只有学号不为空才添加
                if (student.getStudentNumber() != null && !student.getStudentNumber().isEmpty()) {
                    students.add(student);
                }
            }
        }

        // 保存到数据库
        return studentRepository.saveAll(students);
    }

    /**
     * 生成导入模板
     */
    public byte[] generateTemplate() throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("学生信息模板");

            // 创建标题行样式
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // 创建标题行
            Row headerRow = sheet.createRow(0);
            String[] templateHeaders = {"学号", "姓名", "班级", "性别", "手机", "邮箱"};
            for (int i = 0; i < templateHeaders.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(templateHeaders[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 15 * 256);
            }

            // 添加示例数据行
            Row exampleRow = sheet.createRow(1);
            exampleRow.createCell(0).setCellValue("2024001");
            exampleRow.createCell(1).setCellValue("张三");
            exampleRow.createCell(2).setCellValue("计科2024-1班");
            exampleRow.createCell(3).setCellValue("男");
            exampleRow.createCell(4).setCellValue("13800138000");
            exampleRow.createCell(5).setCellValue("zhangsan@example.com");

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private String getCellStringValue(Cell cell) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toLocalDate().toString();
                }
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return null;
        }
    }
}
