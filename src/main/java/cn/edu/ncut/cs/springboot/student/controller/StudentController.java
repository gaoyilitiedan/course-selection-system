package cn.edu.ncut.cs.springboot.student.controller;

import cn.edu.ncut.cs.springboot.student.entity.*;
import cn.edu.ncut.cs.springboot.student.exception.BusinessException;
import cn.edu.ncut.cs.springboot.student.service.StudentService;
import cn.edu.ncut.cs.springboot.student.util.TokenStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 学生控制器
 * 所有接口需要 ROLE_STUDENT 角色权限
 * API 前缀：/api/student/**
 */
@RestController
@RequestMapping("/api/student")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private TokenStore tokenStore;

    private User checkStudent(String authHeader) {
        User user = getUserFromToken(authHeader);
        if (user == null || !"ROLE_STUDENT".equals(user.getRole())) {
            throw new BusinessException(403, "无权限访问");
        }
        return user;
    }

    private User getUserFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        String token = authHeader.substring(7);
        return tokenStore.getUser(token);
    }

    /** 从 User 转换为 Student（User 是基类，Student 子类需要通过 Repository 查询） */
    private Student getStudent(User user) {
        // 直接从 TokenStore 中拿到的 User 是 Student 实例（JPA 代理对象）
        if (user instanceof Student) {
            return (Student) user;
        }
        throw new BusinessException("用户不是学生角色");
    }

    private Map<String, Object> success(Object data) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", data);
        return result;
    }

    // ========== 选课管理 ==========

    /** POST /api/student/enroll 选课（含先修检查） */
    @PostMapping("/enroll")
    public ResponseEntity<Map<String, Object>> enroll(
            @RequestHeader("Authorization") String auth,
            @RequestBody Map<String, Object> request) {
        User user = checkStudent(auth);
        Student student = getStudent(user);
        Long courseId = Long.valueOf(request.get("courseId").toString());
        return ResponseEntity.ok(success(studentService.enroll(student, courseId)));
    }

    /** GET /api/student/enrollments 查询已选课程 */
    @GetMapping("/enrollments")
    public ResponseEntity<Map<String, Object>> getEnrollments(
            @RequestHeader("Authorization") String auth) {
        User user = checkStudent(auth);
        return ResponseEntity.ok(success(studentService.getEnrollments(user.getId())));
    }

    // ========== 个人信息 ==========

    /** GET /api/student/info 查询个人信息 */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getInfo(
            @RequestHeader("Authorization") String auth) {
        User user = checkStudent(auth);
        return ResponseEntity.ok(success(studentService.getInfo(user.getId())));
    }

    // ========== 课程浏览 ==========

    /** GET /api/student/courses 浏览课程信息 */
    @GetMapping("/courses")
    public ResponseEntity<Map<String, Object>> getCourses(
            @RequestHeader("Authorization") String auth) {
        checkStudent(auth);
        return ResponseEntity.ok(success(studentService.getCourses()));
    }

    // ========== 成绩查询 ==========

    /** GET /api/student/grades 查询成绩 */
    @GetMapping("/grades")
    public ResponseEntity<Map<String, Object>> getGrades(
            @RequestHeader("Authorization") String auth) {
        User user = checkStudent(auth);
        return ResponseEntity.ok(success(studentService.getGrades(user.getId())));
    }

    // ========== 课件下载 ==========

    /** GET /api/student/courseware/{courseId} 获取课件列表 */
    @GetMapping("/courseware/{courseId}")
    public ResponseEntity<Map<String, Object>> getCoursewareList(
            @RequestHeader("Authorization") String auth,
            @PathVariable Long courseId) {
        checkStudent(auth);
        List<Courseware> coursewares = studentService.getCoursewaresByCourse(courseId);
        return ResponseEntity.ok(success(coursewares));
    }

    /** GET /api/student/courseware/{courseId}/download/{coursewareId} 下载指定课件 */
    @GetMapping("/courseware/{courseId}/download/{coursewareId}")
    public ResponseEntity<Resource> downloadCourseware(
            @RequestHeader("Authorization") String auth,
            @PathVariable Long courseId,
            @PathVariable Long coursewareId) {
        checkStudent(auth);
        Courseware courseware = studentService.getCourseware(coursewareId);
        File file = new File(courseware.getFilePath());
        if (!file.exists()) {
            throw new BusinessException("课件文件不存在");
        }
        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + courseware.getFileName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    // ========== 消息管理 ==========

    /** POST /api/student/messages 给教师发消息 */
    @PostMapping("/messages")
    public ResponseEntity<Map<String, Object>> sendMessage(
            @RequestHeader("Authorization") String auth,
            @RequestBody Map<String, Object> request) {
        User user = checkStudent(auth);
        Long courseId = Long.valueOf(request.get("courseId").toString());
        Long teacherId = Long.valueOf(request.get("teacherId").toString());
        String content = request.get("content").toString();
        return ResponseEntity.ok(success(studentService.sendMessage(courseId, teacherId, content, user.getId())));
    }

    /** GET /api/student/messages 查看/回复教师留言 */
    @GetMapping("/messages")
    public ResponseEntity<Map<String, Object>> getMessages(
            @RequestHeader("Authorization") String auth) {
        User user = checkStudent(auth);
        return ResponseEntity.ok(success(studentService.getMessages(user.getId())));
    }

    // ========== 抽签结果查询 ==========

    /** GET /api/student/draw-result 查询自己选课抽签结果 */
    @GetMapping("/draw-result")
    public ResponseEntity<Map<String, Object>> getDrawResult(
            @RequestHeader("Authorization") String auth) {
        User user = checkStudent(auth);
        return ResponseEntity.ok(success(studentService.getDrawResult(user.getId())));
    }
}