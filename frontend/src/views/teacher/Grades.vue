<template>
  <div class="crud-page">
    <el-card>
      <template #header><span>成绩录入</span></template>
      <el-form :model="form" inline>
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
      <template #header><span>中签学生列表</span></template>
      <el-table :data="students" v-loading="loading" border stripe>
        <el-table-column prop="id" label="选课ID" width="80" />
        <el-table-column label="学生" width="100"><template #default="{ row }">{{ row.student?.name }}</template></el-table-column>
        <el-table-column label="课程" min-width="160"><template #default="{ row }">{{ row.course?.name }}</template></el-table-column>
        <el-table-column prop="score" label="成绩" width="80" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import * as teacherApi from '@/api/teacher'

const students = ref([])
const loading = ref(false)
const submitting = ref(false)
const form = reactive({ enrollmentId: null, score: null })

async function fetchStudents() {
  loading.value = true
  try { students.value = (await teacherApi.getSelectedEnrollments()) || [] } finally { loading.value = false }
}

async function handleSubmit() {
  if (!form.enrollmentId || form.score == null) { ElMessage.warning('请填写选课记录ID和成绩'); return }
  submitting.value = true
  try {
    await teacherApi.submitGrade({ enrollmentId: form.enrollmentId, score: form.score })
    ElMessage.success('成绩录入成功')
    form.enrollmentId = null; form.score = null
    fetchStudents()
  } finally { submitting.value = false }
}

onMounted(fetchStudents)
</script>

<style scoped>
.mt-16 { margin-top: 16px; }
</style>
