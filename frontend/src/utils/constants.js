// Role constants
export const ROLES = {
  ADMIN: 'ROLE_ADMIN',
  DEPT_ADMIN: 'ROLE_DEPT_ADMIN',
  TEACHER: 'ROLE_TEACHER',
  STUDENT: 'ROLE_STUDENT',
}

// Enrollment status display config
export const STATUS_CONFIG = {
  PENDING: { label: '待抽签', type: 'warning' },
  SELECTED: { label: '已中签', type: 'success' },
  REJECTED: { label: '未中签', type: 'danger' },
}

// Score range labels
export const SCORE_RANGES = ['0-59', '60-69', '70-79', '80-89', '90-100']

// Sidebar menu configuration per role
export const MENU_CONFIG = {
  ROLE_ADMIN: [
    { path: '/admin/chart', icon: 'DataAnalysis', title: '统计图表' },
    { path: '/admin/students', icon: 'User', title: '学生管理' },
    { path: '/admin/teachers', icon: 'UserFilled', title: '教师管理' },
    { path: '/admin/courses', icon: 'Reading', title: '课程管理' },
    { path: '/admin/enrollments', icon: 'Notebook', title: '选课管理' },
    { path: '/admin/grades', icon: 'TrendCharts', title: '成绩管理' },
  ],
  ROLE_DEPT_ADMIN: [
    { path: '/dept/students', icon: 'User', title: '学生管理' },
    { path: '/dept/teachers', icon: 'UserFilled', title: '教师管理' },
    { path: '/dept/courses', icon: 'Reading', title: '课程管理' },
    { path: '/dept/enrollments', icon: 'Notebook', title: '选课管理' },
    { path: '/dept/draw', icon: 'MagicStick', title: '抽签管理' },
    { path: '/dept/grades', icon: 'TrendCharts', title: '成绩管理' },
  ],
  ROLE_TEACHER: [
    { path: '/teacher/courses', icon: 'Reading', title: '我的课程' },
    { path: '/teacher/courseware', icon: 'FolderOpened', title: '课件管理' },
    { path: '/teacher/prerequisite', icon: 'Link', title: '先修课程' },
    { path: '/teacher/selected', icon: 'Checked', title: '中签学生' },
    { path: '/teacher/grades', icon: 'EditPen', title: '成绩录入' },
    { path: '/teacher/stats', icon: 'DataLine', title: '成绩统计' },
    { path: '/teacher/messages', icon: 'ChatDotRound', title: '学生留言' },
  ],
  ROLE_STUDENT: [
    { path: '/student/courses', icon: 'Collection', title: '浏览课程' },
    { path: '/student/my-courses', icon: 'Notebook', title: '我的选课' },
    { path: '/student/info', icon: 'User', title: '个人信息' },
    { path: '/student/grades', icon: 'TrendCharts', title: '成绩查询' },
    { path: '/student/courseware', icon: 'FolderOpened', title: '课件下载' },
    { path: '/student/messages', icon: 'ChatDotRound', title: '师生留言' },
    { path: '/student/draw-result', icon: 'Tickets', title: '抽签结果' },
  ],
}

// Role display labels
export const ROLE_LABELS = {
  ROLE_ADMIN: '系统管理员',
  ROLE_DEPT_ADMIN: '学院管理员',
  ROLE_TEACHER: '教师',
  ROLE_STUDENT: '学生',
}
