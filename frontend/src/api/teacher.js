import request from '@/utils/request'

const BASE = '/teacher'

// Courses
export const updateCourse = (id, data) => request.put(`${BASE}/courses/${id}`, data)

// Courseware
export const uploadCourseware = (courseId, file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request.post(`${BASE}/courses/${courseId}/courseware`, formData)
}
export const getCoursewares = (courseId) => request.get(`${BASE}/courses/${courseId}/courseware`)

// Prerequisite
export const setPrerequisite = (courseId, prerequisiteId) =>
  request.put(`${BASE}/courses/${courseId}/prerequisite`, { prerequisiteId })

// Selected students (enrollments)
export const getSelectedEnrollments = () => request.get(`${BASE}/enrollments/selected`)

// Grades
export const submitGrade = (data) => request.post(`${BASE}/grades`, data)
export const getStats = (courseId) => request.get(`${BASE}/stats/${courseId}`)

// Messages
export const sendMessage = (data) => request.post(`${BASE}/messages`, data)
export const getMessages = () => request.get(`${BASE}/messages`)
