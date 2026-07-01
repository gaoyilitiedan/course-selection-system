import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi } from '@/api/auth'
import router from '@/router'

export const useUserStore = defineStore('user', () => {
  // State
  const token = ref(null)
  const userInfo = ref({
    userId: null,
    username: '',
    role: '',
    departmentId: null,
    departmentName: '',
  })

  // Getters
  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => userInfo.value.role === 'ROLE_ADMIN')
  const isDeptAdmin = computed(() => userInfo.value.role === 'ROLE_DEPT_ADMIN')
  const isTeacher = computed(() => userInfo.value.role === 'ROLE_TEACHER')
  const isStudent = computed(() => userInfo.value.role === 'ROLE_STUDENT')
  const currentRole = computed(() => userInfo.value.role)

  // Actions
  async function login(username, password) {
    const data = await loginApi(username, password)
    token.value = data.token
    userInfo.value = {
      userId: data.userId,
      username: data.username,
      role: data.role,
      departmentId: data.departmentId || null,
      departmentName: data.departmentName || '',
    }
    // Persist to localStorage
    localStorage.setItem('token', data.token)
    localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
  }

  function logout() {
    token.value = null
    userInfo.value = {
      userId: null,
      username: '',
      role: '',
      departmentId: null,
      departmentName: '',
    }
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    router.push('/login')
  }

  function restoreSession() {
    const savedToken = localStorage.getItem('token')
    const savedUserInfo = localStorage.getItem('userInfo')
    if (savedToken && savedUserInfo) {
      token.value = savedToken
      try {
        userInfo.value = JSON.parse(savedUserInfo)
      } catch {
        // Corrupted data, clear
        logout()
      }
    }
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    isAdmin,
    isDeptAdmin,
    isTeacher,
    isStudent,
    currentRole,
    login,
    logout,
    restoreSession,
  }
})
