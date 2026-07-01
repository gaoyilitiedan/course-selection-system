<template>
  <div class="dashboard">
    <h2>欢迎使用学生选课系统</h2>
    <p class="welcome-text">
      当前用户：<strong>{{ userStore.userInfo.username }}</strong>
      &nbsp;&nbsp;角色：<el-tag>{{ roleLabel }}</el-tag>
    </p>
    <el-divider />
    <el-descriptions :column="2" border>
      <el-descriptions-item label="用户名">{{ userStore.userInfo.username }}</el-descriptions-item>
      <el-descriptions-item label="角色">{{ roleLabel }}</el-descriptions-item>
      <el-descriptions-item v-if="userStore.userInfo.departmentName" label="所属学院">
        {{ userStore.userInfo.departmentName }}
      </el-descriptions-item>
    </el-descriptions>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useUserStore } from '@/stores/user'
import { ROLE_LABELS } from '@/utils/constants'

const userStore = useUserStore()
const roleLabel = computed(() => ROLE_LABELS[userStore.userInfo.role] || '')
</script>

<style scoped>
.dashboard {
  padding: 10px;
}
.dashboard h2 {
  margin-bottom: 10px;
}
.welcome-text {
  font-size: 15px;
  color: #606266;
}
</style>
