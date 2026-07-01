package cn.edu.ncut.cs.springboot.student.controller;

import cn.edu.ncut.cs.springboot.student.entity.*;
import cn.edu.ncut.cs.springboot.student.exception.BusinessException;
import cn.edu.ncut.cs.springboot.student.service.DeptAdminService;
import cn.edu.ncut.cs.springboot.student.util.TokenStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 学院管理员控制器
 * 所有接口需要 ROLE_DEPT_ADMIN 角色权限
 * API 前缀：/api/dept/**
 */
@RestController
@RequestMapping("/api/dept")
public class DeptAdminController {

    @Autowired
    private DeptAdminService deptAdminService;

    @Autowired
    private TokenStore tokenStore;

    /** 从请求头中提取用户并校验角色，返回用户以便获取 departmentId */
    private User checkDeptAdmin(String authHeader) {
        User user = getUserFromToken(authHeader);
        if (user == null || !"ROLE_DEPT_ADMIN".equals(user.getRole())) {
            throw new BusinessException(403, "无权限访问");
        }
        if (user.getDepartment() == null) {
            throw new BusinessException(400, "学院管理员必须属于某个学院");
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

    private Map<String, Object> success(Object data) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", data);
        return result;
    }

    // ========== 本学院学生管理 ==========

    @PostMapping("/students")
    public ResponseEntity<Map<String, Object>> createStudent(
            @RequestHeader("Authorization") String auth,
            @RequestBody Student student) {
        User user = checkDeptAdmin(auth);
        return ResponseEntity.ok(success(deptAdminService.createStudent(student, user.getDepartment().getId())));
    }

    @PutMapping("/students/{id}")
    public ResponseEntity<Map<String, Object>> updateStudent(
            @RequestHeader("Authorization") String auth,
            @PathVariable Long id,
            @RequestBody Student student) {
        User user = checkDeptAdmin(auth);
        return ResponseEntity.ok(success(deptAdminService.updateStudent(id, student, user.getDepartment().getId())));
    }

    @DeleteMapping("/students/{id}")
    public ResponseEntity<Map<String, Object>> deleteStudent(
            @RequestHeader("Authorization") String auth,
            @PathVariable Long id) {
        User user = checkDeptAdmin(auth);
        deptAdminService.deleteStudent(id, user.getDepartment().getId());
        return ResponseEntity.ok(success(null));
    }

    @GetMapping("/students")
    public ResponseEntity<Map<String, Object>> getStudents(
            @RequestHeader("Authorization") String auth) {
        User user = checkDeptAdmin(auth);
        return ResponseEntity.ok(success(deptAdminService.getStudentsByDept(user.getDepartment().getId())));
    }

    // ========== 本学院教师管理 ==========

    @PostMapping("/teachers")
    public ResponseEntity<Map<String, Object>> createTeacher(
            @RequestHeader("Authorization") String auth,
            @RequestBody Teacher teacher) {
        User user = checkDeptAdmin(auth);
        return ResponseEntity.ok(success(deptAdminService.createTeacher(teacher, user.getDepartment().getId())));
    }

    @PutMapping("/teachers/{id}")
    public ResponseEntity<Map<String, Object>> updateTeacher(
            @RequestHeader("Authorization") String auth,
            @PathVariable Long id,
            @RequestBody Teacher teacher) {
        User user = checkDeptAdmin(auth);
        return ResponseEntity.ok(success(deptAdminService.updateTeacher(id, teacher, user.getDepartment().getId())));
    }

    @DeleteMapping("/teachers/{id}")
    public ResponseEntity<Map<String, Object>> deleteTeacher(
            @RequestHeader("Authorization") String auth,
            @PathVariable Long id) {
        User user = checkDeptAdmin(auth);
        deptAdminService.deleteTeacher(id, user.getDepartment().getId());
        return ResponseEntity.ok(success(null));
    }

    @GetMapping("/teachers")
    public ResponseEntity<Map<String, Object>> getTeachers(
            @RequestHeader("Authorization") String auth) {
        User user = checkDeptAdmin(auth);
        return ResponseEntity.ok(success(deptAdminService.getTeachersByDept(user.getDepartment().getId())));
    }

    // ========== 本学院课程管理 ==========

    @PostMapping("/courses")
    public ResponseEntity<Map<String, Object>> createCourse(
            @RequestHeader("Authorization") String auth,
            @RequestBody Course course) {
        User user = checkDeptAdmin(auth);
        return ResponseEntity.ok(success(deptAdminService.createCourse(course, user.getDepartment().getId())));
    }

    @PutMapping("/courses/{id}")
    public ResponseEntity<Map<String, Object>> updateCourse(
            @RequestHeader("Authorization") String auth,
            @PathVariable Long id,
            @RequestBody Course course) {
        User user = checkDeptAdmin(auth);
        return ResponseEntity.ok(success(deptAdminService.updateCourse(id, course, user.getDepartment().getId())));
    }

    @DeleteMapping("/courses/{id}")
    public ResponseEntity<Map<String, Object>> deleteCourse(
            @RequestHeader("Authorization") String auth,
            @PathVariable Long id) {
        User user = checkDeptAdmin(auth);
        deptAdminService.deleteCourse(id, user.getDepartment().getId());
        return ResponseEntity.ok(success(null));
    }

    @GetMapping("/courses")
    public ResponseEntity<Map<String, Object>> getCourses(
            @RequestHeader("Authorization") String auth) {
        User user = checkDeptAdmin(auth);
        return ResponseEntity.ok(success(deptAdminService.getCoursesByDept(user.getDepartment().getId())));
    }

    // ========== 本学院选课管理 ==========

    @PostMapping("/enrollments")
    public ResponseEntity<Map<String, Object>> createEnrollment(
            @RequestHeader("Authorization") String auth,
            @RequestBody Enrollment enrollment) {
        User user = checkDeptAdmin(auth);
        return ResponseEntity.ok(success(deptAdminService.createEnrollment(enrollment, user.getDepartment().getId())));
    }

    @PutMapping("/enrollments/{id}")
    public ResponseEntity<Map<String, Object>> updateEnrollment(
            @RequestHeader("Authorization") String auth,
            @PathVariable Long id,
            @RequestBody Enrollment enrollment) {
        User user = checkDeptAdmin(auth);
        return ResponseEntity.ok(success(deptAdminService.updateEnrollment(id, enrollment, user.getDepartment().getId())));
    }

    @DeleteMapping("/enrollments/{id}")
    public ResponseEntity<Map<String, Object>> deleteEnrollment(
            @RequestHeader("Authorization") String auth,
            @PathVariable Long id) {
        User user = checkDeptAdmin(auth);
        deptAdminService.deleteEnrollment(id, user.getDepartment().getId());
        return ResponseEntity.ok(success(null));
    }

    @GetMapping("/enrollments")
    public ResponseEntity<Map<String, Object>> getEnrollments(
            @RequestHeader("Authorization") String auth) {
        User user = checkDeptAdmin(auth);
        return ResponseEntity.ok(success(deptAdminService.getEnrollmentsByDept(user.getDepartment().getId())));
    }

    // ========== 抽签管理 ==========

    @PostMapping("/draw/{courseId}")
    public ResponseEntity<Map<String, Object>> draw(
            @RequestHeader("Authorization") String auth,
            @PathVariable Long courseId) {
        User user = checkDeptAdmin(auth);
        deptAdminService.draw(courseId, user.getDepartment().getId());
        return ResponseEntity.ok(success(null));
    }

    @GetMapping("/draw/{courseId}/result")
    public ResponseEntity<Map<String, Object>> getDrawResult(
            @RequestHeader("Authorization") String auth,
            @PathVariable Long courseId) {
        User user = checkDeptAdmin(auth);
        return ResponseEntity.ok(success(deptAdminService.getDrawResult(courseId, user.getDepartment().getId())));
    }

    // ========== 成绩管理 ==========

    @PostMapping("/grades")
    public ResponseEntity<Map<String, Object>> setGrade(
            @RequestHeader("Authorization") String auth,
            @RequestBody Map<String, Object> request) {
        User user = checkDeptAdmin(auth);
        Long enrollmentId = Long.valueOf(request.get("enrollmentId").toString());
        Double score = Double.valueOf(request.get("score").toString());
        return ResponseEntity.ok(success(deptAdminService.setGrade(enrollmentId, score, user.getDepartment().getId())));
    }

    @GetMapping("/grades")
    public ResponseEntity<Map<String, Object>> getGrades(
            @RequestHeader("Authorization") String auth) {
        User user = checkDeptAdmin(auth);
        return ResponseEntity.ok(success(deptAdminService.getGradesByDept(user.getDepartment().getId())));
    }
}