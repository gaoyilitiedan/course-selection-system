import request from '@/utils/request'

const BASE = '/student'

// Enroll
export const enroll = (courseId) => request.post(`${BASE}/enroll`, { courseId })
export const getEnrollments = () => request.get(`${BASE}/enrollments`)

// Info
export const getInfo = () => request.get(`${BASE}/info`)

// Courses
export const getCourses = () => request.get(`${BASE}/courses`)

// Grades
export const getGrades = () => request.get(`${BASE}/grades`)

// Courseware
export const getCoursewares = (courseId) => request.get(`${BASE}/courseware/${courseId}`)

// Messages
export const sendMessage = (data) => request.post(`${BASE}/messages`, data)
export const getMessages = () => request.get(`${BASE}/messages`)

// Draw result
export const getDrawResult = () => request.get(`${BASE}/draw-result`)
