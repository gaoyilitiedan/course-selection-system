package cn.edu.ncut.cs.springboot.student.controller;

import cn.edu.ncut.cs.springboot.student.entity.*;
import cn.edu.ncut.cs.springboot.student.exception.BusinessException;
import cn.edu.ncut.cs.springboot.student.service.TeacherService;
import cn.edu.ncut.cs.springboot.student.util.TokenStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 教师控制器
 * 所有接口需要 ROLE_TEACHER 角色权限
 * API 前缀：/api/teacher/**
 */
@RestController
@RequestMapping("/api/teacher")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private TokenStore tokenStore;

    /** 上传文件存储目录 */
    private static final String UPLOAD_DIR = "src/main/resources/uploads/";

    private User checkTeacher(String authHeader) {
        User user = getUserFromToken(authHeader);
        if (user == null || !"ROLE_TEACHER".equals(user.getRole())) {
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

    private Map<String, Object> success(Object data) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", data);
        return result;
    }

    // ========== 课程管理 ==========

    /** PUT /api/teacher/courses/{id} 更新课程信息/介绍 */
    @PutMapping("/courses/{id}")
    public ResponseEntity<Map<String, Object>> updateCourse(
            @RequestHeader("Authorization") String auth,
            @PathVariable Long id,
            @RequestBody Course course) {
        User user = checkTeacher(auth);
        return ResponseEntity.ok(success(teacherService.updateCourse(id, course, user.getId())));
    }

    // ========== 课件管理 ==========

    /** POST /api/teacher/courses/{id}/courseware 上传课件 */
    @PostMapping("/courses/{id}/courseware")
    public ResponseEntity<Map<String, Object>> uploadCourseware(
            @RequestHeader("Authorization") String auth,
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        User user = checkTeacher(auth);

        // 确保上传目录存在
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 生成唯一文件名，防止冲突
        String originalFilename = file.getOriginalFilename();
        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String storedFileName = UUID.randomUUID().toString() + ext;

        try {
            Path filePath = Paths.get(UPLOAD_DIR, storedFileName);
            Files.write(filePath, file.getBytes());
            Courseware courseware = teacherService.uploadCourseware(id, originalFilename, filePath.toString(), user.getId());
            return ResponseEntity.ok(success(courseware));
        } catch (IOException e) {
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }
    }

    /** GET /api/teacher/courses/{id}/courseware 查看课件列表 */
    @GetMapping("/courses/{id}/courseware")
    public ResponseEntity<Map<String, Object>> getCoursewares(
            @RequestHeader("Authorization") String auth,
            @PathVariable Long id) {
        User user = checkTeacher(auth);
        return ResponseEntity.ok(success(teacherService.getCoursewares(id, user.getId())));
    }

    // ========== 先修课程设置 ==========

    /** PUT /api/teacher/courses/{id}/prerequisite 设置先修课程 */
    @PutMapping("/courses/{id}/prerequisite")
    public ResponseEntity<Map<String, Object>> setPrerequisite(
            @RequestHeader("Authorization") String auth,
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        User user = checkTeacher(auth);
        Long prerequisiteId = request.get("prerequisiteId") != null
                ? Long.valueOf(request.get("prerequisiteId").toString())
                : null;
        return ResponseEntity.ok(success(teacherService.setPrerequisite(id, prerequisiteId, user.getId())));
    }

    // ========== 中签学生查看 ==========

    /** GET /api/teacher/enrollments/selected 查看自己课程中签学生 */
    @GetMapping("/enrollments/selected")
    public ResponseEntity<Map<String, Object>> getSelectedEnrollments(
            @RequestHeader("Authorization") String auth) {
        User user = checkTeacher(auth);
        return ResponseEntity.ok(success(teacherService.getSelectedEnrollments(user.getId())));
    }

    // ========== 成绩录入 ==========

    /** POST /api/teacher/grades 录入学生成绩 */
    @PostMapping("/grades")
    public ResponseEntity<Map<String, Object>> setGrade(
            @RequestHeader("Authorization") String auth,
            @RequestBody Map<String, Object> request) {
        User user = checkTeacher(auth);
        Long enrollmentId = Long.valueOf(request.get("enrollmentId").toString());
        Double score = Double.valueOf(request.get("score").toString());
        return ResponseEntity.ok(success(teacherService.setGrade(enrollmentId, score, user.getId())));
    }

    // ========== 成绩统计 ==========

    /** GET /api/teacher/stats/{courseId} 成绩统计 */
    @GetMapping("/stats/{courseId}")
    public ResponseEntity<Map<String, Object>> getStats(
            @RequestHeader("Authorization") String auth,
            @PathVariable Long courseId) {
        User user = checkTeacher(auth);
        return ResponseEntity.ok(success(teacherService.getStats(courseId, user.getId())));
    }

    // ========== 消息管理 ==========

    /** POST /api/teacher/messages 给学生发消息 */
    @PostMapping("/messages")
    public ResponseEntity<Map<String, Object>> sendMessage(
            @RequestHeader("Authorization") String auth,
            @RequestBody Map<String, Object> request) {
        User user = checkTeacher(auth);
        Long courseId = Long.valueOf(request.get("courseId").toString());
        Long studentId = Long.valueOf(request.get("studentId").toString());
        String content = request.get("content").toString();
        return ResponseEntity.ok(success(teacherService.sendMessage(courseId, studentId, content, user.getId())));
    }

    /** GET /api/teacher/messages 查看自己的消息 */
    @GetMapping("/messages")
    public ResponseEntity<Map<String, Object>> getMessages(
            @RequestHeader("Authorization") String auth) {
        User user = checkTeacher(auth);
        return ResponseEntity.ok(success(teacherService.getMessages(user.getId())));
    }
}