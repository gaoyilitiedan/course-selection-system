import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const request = axios.create({
  baseURL: '/api',
  timeout: 15000,
})

// Request interceptor: attach Bearer token
request.interceptors.request.use(
  (config) => {
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers.Authorization = `Bearer ${userStore.token}`
    }
    // Let the browser set Content-Type for FormData (multipart with boundary)
    if (!(config.data instanceof FormData)) {
      config.headers['Content-Type'] = 'application/json'
    }
    return config
  },
  (error) => Promise.reject(error)
)

// Response interceptor: unwrap {code, message, data}
request.interceptors.response.use(
  (response) => {
    const res = response.data
    // Handle blob responses (e.g., file downloads) — pass through
    if (response.config.responseType === 'blob') {
      return response
    }
    if (res.code === 200) {
      return res.data
    }
    // Business errors
    if (res.code === 401) {
      ElMessage.error('登录已过期，请重新登录')
      const userStore = useUserStore()
      userStore.logout()
    } else if (res.code === 403) {
      ElMessage.error(res.message || '无权限访问')
    } else {
      ElMessage.error(res.message || '请求失败')
    }
    return Promise.reject(res)
  },
  (error) => {
    if (error.response) {
      const { status, data } = error.response
      if (status === 401) {
        ElMessage.error('登录已过期，请重新登录')
        const userStore = useUserStore()
        userStore.logout()
      } else if (status === 403) {
        ElMessage.error('无权限访问')
      } else if (status === 500) {
        ElMessage.error(data?.message || '服务器内部错误')
      } else {
        ElMessage.error(data?.message || '请求失败')
      }
    } else {
      ElMessage.error('网络错误，请稍后重试')
    }
    return Promise.reject(error)
  }
)

export default request
