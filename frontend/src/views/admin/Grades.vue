<template>
  <div class="crud-page">
    <el-card>
      <template #header><span>成绩管理</span></template>
      <el-form :model="form" label-width="120px" class="grade-form">
        <el-form-item label="选课记录ID">
          <el-input-number v-model="form.enrollmentId" :min="1" />
        </el-form-item>
        <el-form-item label="成绩">
          <el-input-number v-model="form.score" :min="0" :max="100" :precision="1" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">录入/修改成绩</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="mt-16">
      <template #header><span>选课记录</span></template>
      <el-table :data="enrollments" v-loading="loading" border stripe>
        <el-table-column prop="id" label="选课ID" width="80" />
        <el-table-column label="学生" width="100">
          <template #default="{ row }">{{ row.student?.name }}</template>
        </el-table-column>
        <el-table-column label="课程" min-width="160">
          <template #default="{ row }">{{ row.course?.name }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="STATUS_CONFIG[row.status]?.type">{{ STATUS_CONFIG[row.status]?.label }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="score" label="成绩" width="80" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { STATUS_CONFIG } from '@/utils/constants'
import * as adminApi from '@/api/admin'

const enrollments = ref([])
const loading = ref(false)
const submitting = ref(false)

const form = reactive({
  enrollmentId: null,
  score: null,
})

async function fetchEnrollments() {
  loading.value = true
  try {
    enrollments.value = (await adminApi.getEnrollments()) || []
  } finally {
    loading.value = false
  }
}

async function handleSubmit() {
  if (!form.enrollmentId || form.score == null) {
    ElMessage.warning('请填写选课记录ID和成绩')
    return
  }
  submitting.value = true
  try {
    await adminApi.submitGrade({ enrollmentId: form.enrollmentId, score: form.score })
    ElMessage.success('成绩录入成功')
    form.enrollmentId = null
    form.score = null
    fetchEnrollments()
  } finally {
    submitting.value = false
  }
}

onMounted(fetchEnrollments)
</script>

<style scoped>
.grade-form {
  max-width: 500px;
}
.mt-16 {
  margin-top: 16px;
}
</style>
