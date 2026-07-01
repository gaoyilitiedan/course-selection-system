<template>
  <div class="crud-page">
    <el-card>
      <template #header><span>个人信息</span></template>
      <el-descriptions :column="2" border v-loading="loading">
        <el-descriptions-item label="用户名">{{ info.username }}</el-descriptions-item>
        <el-descriptions-item label="角色">学生</el-descriptions-item>
        <el-descriptions-item label="学号">{{ info.studentNo }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ info.name }}</el-descriptions-item>
        <el-descriptions-item label="所属学院">{{ info.department?.name }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as studentApi from '@/api/student'

const info = ref({})
const loading = ref(false)

async function fetchData() {
  loading.value = true
  try { info.value = (await studentApi.getInfo()) || {} } finally { loading.value = false }
}

onMounted(fetchData)
</script>
