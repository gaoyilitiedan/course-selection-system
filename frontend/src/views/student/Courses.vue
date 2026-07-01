<template>
  <div class="crud-page">
    <el-card>
      <template #header><span>浏览课程</span></template>
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="课程名称" width="160" />
        <el-table-column prop="description" label="课程介绍" min-width="200" show-overflow-tooltip />
        <el-table-column prop="capacity" label="容量" width="80" />
        <el-table-column label="授课教师" width="100"><template #default="{ row }">{{ row.teacher?.name }}</template></el-table-column>
        <el-table-column label="开课学院" width="180"><template #default="{ row }">{{ row.department?.name }}</template></el-table-column>
        <el-table-column label="先修课程" width="140"><template #default="{ row }">{{ row.prerequisite?.name || '无' }}</template></el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" :disabled="enrolledIds.has(row.id)" :loading="enrollingId === row.id" @click="handleEnroll(row)">
              {{ enrolledIds.has(row.id) ? '已选' : '选课' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import * as studentApi from '@/api/student'

const tableData = ref([])
const loading = ref(false)
const enrolledIds = ref(new Set())
const enrollingId = ref(null)

async function fetchData() {
  loading.value = true
  try {
    const [courses, enrollments] = await Promise.all([
      studentApi.getCourses(),
      studentApi.getEnrollments(),
    ])
    tableData.value = courses || []
    enrolledIds.value = new Set((enrollments || []).map(e => e.course?.id).filter(Boolean))
  } finally {
    loading.value = false
  }
}

async function handleEnroll(row) {
  await ElMessageBox.confirm(`确定选课 "${row.name}" 吗？`, '确认选课', { type: 'info' })
  enrollingId.value = row.id
  try {
    await studentApi.enroll(row.id)
    ElMessage.success('选课成功，等待抽签')
    enrolledIds.value.add(row.id)
  } finally {
    enrollingId.value = null
  }
}

onMounted(fetchData)
</script>
