package com.sms.config;

import com.sms.service.DictService;
import com.sms.service.DepartmentService;
import com.sms.model.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private DictService dictService;

    @Autowired
    private DepartmentService departmentService;

    @Override
    public void run(String... args) {
        try {
            dictService.initDefaultDicts();
            System.out.println("✅ 字典数据初始化完成");
        } catch (Exception e) {
            System.err.println("字典初始化失败: " + e.getMessage());
        }

        try {
            initDefaultDepartments();
            System.out.println("✅ 院系数据初始化完成");
        } catch (Exception e) {
            System.err.println("院系初始化失败: " + e.getMessage());
        }
    }

    private void initDefaultDepartments() {
        if (departmentService.getAllDepartments().isEmpty()) {
            createDepartment("计算机科学与技术学院", "计算机相关专业");
            createDepartment("软件学院", "软件工程相关专业");
            createDepartment("电子信息工程学院", "电子信息相关专业");
            createDepartment("数学与统计学院", "数学统计相关专业");
            createDepartment("外国语学院", "外语相关专业");
            createDepartment("经济管理学院", "经济管理相关专业");
        }
    }

    private void createDepartment(String name, String description) {
        Department dept = new Department();
        dept.setName(name);
        dept.setDescription(description);
        departmentService.createDepartment(dept);
    }
}
