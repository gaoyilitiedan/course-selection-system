package cn.edu.ncut.cs.springboot.student.service;

import cn.edu.ncut.cs.springboot.student.entity.Course;
import cn.edu.ncut.cs.springboot.student.entity.Enrollment;
import cn.edu.ncut.cs.springboot.student.repository.CourseRepository;
import cn.edu.ncut.cs.springboot.student.repository.EnrollmentRepository;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * 统计服务
 * 提供选课人数柱状图生成等功能
 */
@Service
public class StatisticsService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    /**
     * 横轴：课程名称，纵轴：选课人数（所有 status 均计入）
     */
    public byte[] generateChartBytes() {
        List<Course> courses = courseRepository.findAll();
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Course course : courses) {
            List<Enrollment> enrollments = enrollmentRepository.findByCourseId(course.getId());
            dataset.addValue(enrollments.size(), "选课人数", course.getName());
        }

        JFreeChart barChart = ChartFactory.createBarChart(
                "选课人数统计",           // 图表标题
                "课程名称",               // 横轴标签
                "选课人数",               // 纵轴标签
                dataset,                  // 数据集
                PlotOrientation.VERTICAL,
                false,                    // 不显示图例
                true,                     // 显示工具提示
                false                     // 不显示 URL
        );

        // 输出为 PNG 字节流
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ChartUtils.writeChartAsPNG(baos, barChart, 800, 600);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("生成图表失败", e);
        }
    }
}