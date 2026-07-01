import request from '@/utils/request'

const BASE = '/dept'

// Students
export const getStudents = () => request.get(`${BASE}/students`)
export const createStudent = (data) => request.post(`${BASE}/students`, data)
export const updateStudent = (id, data) => request.put(`${BASE}/students/${id}`, data)
export const deleteStudent = (id) => request.delete(`${BASE}/students/${id}`)

// Teachers
export const getTeachers = () => request.get(`${BASE}/teachers`)
export const createTeacher = (data) => request.post(`${BASE}/teachers`, data)
export const updateTeacher = (id, data) => request.put(`${BASE}/teachers/${id}`, data)
export const deleteTeacher = (id) => request.delete(`${BASE}/teachers/${id}`)

// Courses
export const getCourses = () => request.get(`${BASE}/courses`)
export const createCourse = (data) => request.post(`${BASE}/courses`, data)
export const updateCourse = (id, data) => request.put(`${BASE}/courses/${id}`, data)
export const deleteCourse = (id) => request.delete(`${BASE}/courses/${id}`)

// Enrollments
export const getEnrollments = () => request.get(`${BASE}/enrollments`)
export const createEnrollment = (data) => request.post(`${BASE}/enrollments`, data)
export const updateEnrollment = (id, data) => request.put(`${BASE}/enrollments/${id}`, data)
export const deleteEnrollment = (id) => request.delete(`${BASE}/enrollments/${id}`)

// Draw
export const executeDraw = (courseId) => request.post(`${BASE}/draw/${courseId}`)
export const getDrawResult = (courseId) => request.get(`${BASE}/draw/${courseId}/result`)

// Grades
export const submitGrade = (data) => request.post(`${BASE}/grades`, data)
export const getGrades = () => request.get(`${BASE}/grades`)
