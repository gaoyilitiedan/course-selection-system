<template>
  <div class="crud-page">
    <el-card>
      <template #header><span>我的课程</span></template>
      <el-table :data="courseList" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="课程名称" width="160" />
        <el-table-column prop="description" label="课程介绍" min-width="200" show-overflow-tooltip />
        <el-table-column prop="capacity" label="容量" width="80" />
        <el-table-column label="中签人数" width="80">
          <template #default="{ row }">{{ selectedCounts[row.id] || 0 }}</template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="编辑课程" width="500px">
      <el-form ref="formRef" :model="form" label-width="80px">
        <el-form-item label="课程名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="课程介绍"><el-input v-model="form.description" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="容量"><el-input-number v-model="form.capacity" :min="1" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import * as teacherApi from '@/api/teacher'

const enrollments = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const submitting = ref(false)
const editId = ref(null)
const formRef = ref(null)
const form = reactive({ name: '', description: '', capacity: 30 })

// Derive unique courses from selected enrollments (backend returns nested Course in each enrollment)
const courseList = computed(() => {
  const map = new Map()
  for (const e of enrollments.value) {
    if (e.course && !map.has(e.course.id)) {
      map.set(e.course.id, { ...e.course })
    }
  }
  return Array.from(map.values())
})

const selectedCounts = computed(() => {
  const counts = {}
  for (const e of enrollments.value) {
    if (e.course) {
      counts[e.course.id] = (counts[e.course.id] || 0) + 1
    }
  }
  return counts
})

async function fetchData() {
  loading.value = true
  try {
    enrollments.value = (await teacherApi.getSelectedEnrollments()) || []
  } catch {
    enrollments.value = []
  } finally {
    loading.value = false
  }
}

function openEdit(row) {
  editId.value = row.id
  form.name = row.name
  form.description = row.description
  form.capacity = row.capacity
  dialogVisible.value = true
}

async function handleSubmit() {
  submitting.value = true
  try {
    await teacherApi.updateCourse(editId.value, { ...form })
    ElMessage.success('更新成功')
    dialogVisible.value = false
    fetchData()
  } finally {
    submitting.value = false
  }
}

onMounted(fetchData)
</script>
