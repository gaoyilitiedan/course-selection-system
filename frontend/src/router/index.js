import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  // Public routes
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { public: true },
  },
  {
    path: '/403',
    name: 'Forbidden',
    component: () => import('@/views/Forbidden.vue'),
    meta: { public: true },
  },
  {
    path: '/404',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue'),
    meta: { public: true },
  },

  // Dashboard (role-redirect)
  {
    path: '/',
    redirect: '/dashboard',
  },
  {
    path: '/dashboard',
    component: () => import('@/components/AppLayout.vue'),
    children: [
      {
        path: '',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
      },
    ],
  },

  // Admin routes (ROLE_ADMIN)
  {
    path: '/admin',
    component: () => import('@/components/AppLayout.vue'),
    meta: { role: 'ROLE_ADMIN' },
    children: [
      { path: 'chart', name: 'AdminChart', component: () => import('@/views/admin/Chart.vue') },
      { path: 'students', name: 'AdminStudents', component: () => import('@/views/admin/Students.vue') },
      { path: 'teachers', name: 'AdminTeachers', component: () => import('@/views/admin/Teachers.vue') },
      { path: 'courses', name: 'AdminCourses', component: () => import('@/views/admin/Courses.vue') },
      { path: 'enrollments', name: 'AdminEnrollments', component: () => import('@/views/admin/Enrollments.vue') },
      { path: 'grades', name: 'AdminGrades', component: () => import('@/views/admin/Grades.vue') },
    ],
  },

  // Dept Admin routes (ROLE_DEPT_ADMIN)
  {
    path: '/dept',
    component: () => import('@/components/AppLayout.vue'),
    meta: { role: 'ROLE_DEPT_ADMIN' },
    children: [
      { path: 'students', name: 'DeptStudents', component: () => import('@/views/dept/Students.vue') },
      { path: 'teachers', name: 'DeptTeachers', component: () => import('@/views/dept/Teachers.vue') },
      { path: 'courses', name: 'DeptCourses', component: () => import('@/views/dept/Courses.vue') },
      { path: 'enrollments', name: 'DeptEnrollments', component: () => import('@/views/dept/Enrollments.vue') },
      { path: 'draw', name: 'DeptDraw', component: () => import('@/views/dept/Draw.vue') },
      { path: 'grades', name: 'DeptGrades', component: () => import('@/views/dept/Grades.vue') },
    ],
  },

  // Teacher routes (ROLE_TEACHER)
  {
    path: '/teacher',
    component: () => import('@/components/AppLayout.vue'),
    meta: { role: 'ROLE_TEACHER' },
    children: [
      { path: 'courses', name: 'TeacherCourses', component: () => import('@/views/teacher/Courses.vue') },
      { path: 'courseware/:courseId?', name: 'TeacherCourseware', component: () => import('@/views/teacher/Courseware.vue') },
      { path: 'prerequisite/:courseId?', name: 'TeacherPrerequisite', component: () => import('@/views/teacher/Prerequisite.vue') },
      { path: 'selected', name: 'TeacherSelected', component: () => import('@/views/teacher/SelectedStudents.vue') },
      { path: 'grades', name: 'TeacherGrades', component: () => import('@/views/teacher/Grades.vue') },
      { path: 'stats/:courseId?', name: 'TeacherStats', component: () => import('@/views/teacher/Stats.vue') },
      { path: 'messages', name: 'TeacherMessages', component: () => import('@/views/teacher/Messages.vue') },
    ],
  },

  // Student routes (ROLE_STUDENT)
  {
    path: '/student',
    component: () => import('@/components/AppLayout.vue'),
    meta: { role: 'ROLE_STUDENT' },
    children: [
      { path: 'courses', name: 'StudentCourses', component: () => import('@/views/student/Courses.vue') },
      { path: 'my-courses', name: 'StudentMyCourses', component: () => import('@/views/student/MyCourses.vue') },
      { path: 'info', name: 'StudentInfo', component: () => import('@/views/student/Info.vue') },
      { path: 'grades', name: 'StudentGrades', component: () => import('@/views/student/Grades.vue') },
      { path: 'courseware/:courseId?', name: 'StudentCourseware', component: () => import('@/views/student/Courseware.vue') },
      { path: 'messages', name: 'StudentMessages', component: () => import('@/views/student/Messages.vue') },
      { path: 'draw-result', name: 'StudentDrawResult', component: () => import('@/views/student/DrawResult.vue') },
    ],
  },

  // Catch-all
  {
    path: '/:pathMatch(.*)*',
    redirect: '/404',
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// Navigation guard
router.beforeEach((to, from, next) => {
  // Public routes always allowed
  if (to.meta.public) {
    return next()
  }

  const userStore = useUserStore()

  // Not logged in → redirect to login
  if (!userStore.isLoggedIn) {
    return next({ path: '/login', query: { redirect: to.fullPath } })
  }

  // Role check
  if (to.meta.role && userStore.userInfo.role !== to.meta.role) {
    return next('/403')
  }

  next()
})

export default router
