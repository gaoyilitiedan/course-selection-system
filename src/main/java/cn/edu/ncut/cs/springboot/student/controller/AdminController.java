package cn.edu.ncut.cs.springboot.student.controller;

import cn.edu.ncut.cs.springboot.student.entity.*;
import cn.edu.ncut.cs.springboot.student.exception.BusinessException;
import cn.edu.ncut.cs.springboot.student.service.AdminService;
import cn.edu.ncut.cs.springboot.student.service.StatisticsService;
import cn.edu.ncut.cs.springboot.student.util.TokenStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统管理员控制器
 * 所有接口需要 ROLE_ADMIN 角色权限
 * API 前缀：/api/admin/**
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    private TokenStore tokenStore;

    /** 从请求头中提取用户并校验角色 */
    private User checkAdmin(String authHeader) {
        User user = getUserFromToken(authHeader);
        if (user == null || !"ROLE_ADMIN".equals(user.getRole())) {
            throw new BusinessException(403, "无权限访问");
        }
        return user;
    }

    /** 从 Authorization 请求头提取 Token 并获取用户 */
    private User getUserFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        String token = authHeader.substring(7);
        return tokenStore.getUser(token);
    }

    // ========== 统一响应工具 ==========

    private Map<String, Object> success(Object data) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", data);
        return result;
    }

    // ========== 学生管理 ==========

    @PostMapping("/students")
    public ResponseEntity<Map<String, Object>> createStudent(
            @RequestHeader("Authorization") String auth,
            @RequestBody Student student) {
        checkAdmin(auth);
        return ResponseEntity.ok(success(adminService.createStudent(student)));
    }

    @PutMapping("/students/{id}")
    public ResponseEntity<Map<String, Object>> updateStudent(
            @RequestHeader("Authorization") String auth,
            @PathVariable Long id,
            @RequestBody Student student) {
        checkAdmin(auth);
        return ResponseEntity.ok(success(adminService.updateStudent(id, student)));
    }

    @DeleteMapping("/students/{id}")
    public ResponseEntity<Map<String, Object>> deleteStudent(
            @RequestHeader("Authorization") String auth,
            @PathVariable Long id) {
        checkAdmin(auth);
        adminService.deleteStudent(id);
        return ResponseEntity.ok(success(null));
    }

    @GetMapping("/students")
    public ResponseEntity<Map<String, Object>> getStudents(
            @RequestHeader("Authorization") String auth) {
        checkAdmin(auth);
        return ResponseEntity.ok(success(adminService.getAllStudents()));
    }

    // ========== 教师管理 ==========

    @PostMapping("/teachers")
    public ResponseEntity<Map<String, Object>> createTeacher(
            @RequestHeader("Authorization") String auth,
            @RequestBody Teacher teacher) {
        checkAdmin(auth);
        return ResponseEntity.ok(success(adminService.createTeacher(teacher)));
    }

    @PutMapping("/teachers/{id}")
    public ResponseEntity<Map<String, Object>> updateTeacher(
            @RequestHeader("Authorization") String auth,
            @PathVariable Long id,
            @RequestBody Teacher teacher) {
        checkAdmin(auth);
        return ResponseEntity.ok(success(adminService.updateTeacher(id, teacher)));
    }

    @DeleteMapping("/teachers/{id}")
    public ResponseEntity<Map<String, Object>> deleteTeacher(
            @RequestHeader("Authorization") String auth,
            @PathVariable Long id) {
        checkAdmin(auth);
        adminService.deleteTeacher(id);
        return ResponseEntity.ok(success(null));
    }

    @GetMapping("/teachers")
    public ResponseEntity<Map<String, Object>> getTeachers(
            @RequestHeader("Authorization") String auth) {
        checkAdmin(auth);
        return ResponseEntity.ok(success(adminService.getAllTeachers()));
    }

    // ========== 课程管理 ==========

    @PostMapping("/courses")
    public ResponseEntity<Map<String, Object>> createCourse(
            @RequestHeader("Authorization") String auth,
            @RequestBody Course course) {
        checkAdmin(auth);
        return ResponseEntity.ok(success(adminService.createCourse(course)));
    }

    @PutMapping("/courses/{id}")
    public ResponseEntity<Map<String, Object>> updateCourse(
            @RequestHeader("Authorization") String auth,
            @PathVariable Long id,
            @RequestBody Course course) {
        checkAdmin(auth);
        return ResponseEntity.ok(success(adminService.updateCourse(id, course)));
    }

    @DeleteMapping("/courses/{id}")
    public ResponseEntity<Map<String, Object>> deleteCourse(
            @RequestHeader("Authorization") String auth,
            @PathVariable Long id) {
        checkAdmin(auth);
        adminService.deleteCourse(id);
        return ResponseEntity.ok(success(null));
    }

    @GetMapping("/courses")
    public ResponseEntity<Map<String, Object>> getCourses(
            @RequestHeader("Authorization") String auth) {
        checkAdmin(auth);
        return ResponseEntity.ok(success(adminService.getAllCourses()));
    }

    // ========== 选课管理 ==========

    @PostMapping("/enrollments")
    public ResponseEntity<Map<String, Object>> createEnrollment(
            @RequestHeader("Authorization") String auth,
            @RequestBody Enrollment enrollment) {
        checkAdmin(auth);
        return ResponseEntity.ok(success(adminService.createEnrollment(enrollment)));
    }

    @PutMapping("/enrollments/{id}")
    public ResponseEntity<Map<String, Object>> updateEnrollment(
            @RequestHeader("Authorization") String auth,
            @PathVariable Long id,
            @RequestBody Enrollment enrollment) {
        checkAdmin(auth);
        return ResponseEntity.ok(success(adminService.updateEnrollment(id, enrollment)));
    }

    @DeleteMapping("/enrollments/{id}")
    public ResponseEntity<Map<String, Object>> deleteEnrollment(
            @RequestHeader("Authorization") String auth,
            @PathVariable Long id) {
        checkAdmin(auth);
        adminService.deleteEnrollment(id);
        return ResponseEntity.ok(success(null));
    }

    @GetMapping("/enrollments")
    public ResponseEntity<Map<String, Object>> getEnrollments(
            @RequestHeader("Authorization") String auth) {
        checkAdmin(auth);
        return ResponseEntity.ok(success(adminService.getAllEnrollments()));
    }

    // ========== 成绩管理 ==========

    @PostMapping("/grades")
    public ResponseEntity<Map<String, Object>> setGrade(
            @RequestHeader("Authorization") String auth,
            @RequestBody Map<String, Object> request) {
        checkAdmin(auth);
        Long enrollmentId = Long.valueOf(request.get("enrollmentId").toString());
        Double score = Double.valueOf(request.get("score").toString());
        return ResponseEntity.ok(success(adminService.setGrade(enrollmentId, score)));
    }

    // ========== 学院列表 ==========

    @GetMapping("/departments")
    public ResponseEntity<Map<String, Object>> getDepartments(
            @RequestHeader("Authorization") String auth) {
        checkAdmin(auth);
        return ResponseEntity.ok(success(adminService.getAllDepartments()));
    }

    // ========== 统计图表 ==========

    @GetMapping("/stats/chart")
    public ResponseEntity<byte[]> getChart(
            @RequestHeader("Authorization") String auth) {
        checkAdmin(auth);
        byte[] chartBytes = statisticsService.generateChartBytes();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=chart.png")
                .contentType(MediaType.IMAGE_PNG)
                .body(chartBytes);
    }
}