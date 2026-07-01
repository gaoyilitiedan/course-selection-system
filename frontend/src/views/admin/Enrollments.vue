<template>
  <div class="crud-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>选课管理</span>
          <el-button type="primary" @click="openDialog()">新增选课</el-button>
        </div>
      </template>
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column label="学生" width="100">
          <template #default="{ row }">{{ row.student?.name }}</template>
        </el-table-column>
        <el-table-column label="课程" width="160">
          <template #default="{ row }">{{ row.course?.name }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusTag(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="score" label="成绩" width="80" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openDialog(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑选课' : '新增选课'" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="学生ID" prop="studentId">
          <el-input-number v-model="form.studentId" :min="1" />
        </el-form-item>
        <el-form-item label="课程ID" prop="courseId">
          <el-input-number v-model="form.courseId" :min="1" />
        </el-form-item>
        <el-form-item v-if="isEdit" label="状态" prop="status">
          <el-select v-model="form.status">
            <el-option label="待抽签" value="PENDING" />
            <el-option label="已中签" value="SELECTED" />
            <el-option label="未中签" value="REJECTED" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="isEdit" label="成绩">
          <el-input-number v-model="form.score" :min="0" :max="100" :precision="1" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessageBox } from 'element-plus'
import { STATUS_CONFIG } from '@/utils/constants'
import * as adminApi from '@/api/admin'

const tableData = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const editId = ref(null)
const formRef = ref(null)

const form = reactive({
  studentId: null,
  courseId: null,
  status: 'PENDING',
  score: null,
})

const rules = {
  studentId: [{ required: true, message: '请输入学生ID' }],
  courseId: [{ required: true, message: '请输入课程ID' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }],
}

function statusTag(status) {
  return STATUS_CONFIG[status]?.type || 'info'
}

function statusLabel(status) {
  return STATUS_CONFIG[status]?.label || status
}

async function fetchData() {
  loading.value = true
  try {
    tableData.value = (await adminApi.getEnrollments()) || []
  } finally {
    loading.value = false
  }
}

function resetForm() {
  form.studentId = null
  form.courseId = null
  form.status = 'PENDING'
  form.score = null
  editId.value = null
  formRef.value?.resetFields()
}

function openDialog(row) {
  resetForm()
  if (row) {
    isEdit.value = true
    editId.value = row.id
    form.studentId = row.student?.id
    form.courseId = row.course?.id
    form.status = row.status
    form.score = row.score
  } else {
    isEdit.value = false
  }
  dialogVisible.value = true
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  const payload = {
    student: { id: form.studentId },
    course: { id: form.courseId },
    status: form.status,
    ...(form.score != null ? { score: form.score } : {}),
  }

  submitting.value = true
  try {
    if (isEdit.value) {
      await adminApi.updateEnrollment(editId.value, { status: form.status, score: form.score })
    } else {
      await adminApi.createEnrollment(payload)
    }
    dialogVisible.value = false
    fetchData()
  } finally {
    submitting.value = false
  }
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除该选课记录吗？`, '确认删除', { type: 'warning' })
  await adminApi.deleteEnrollment(row.id)
  fetchData()
}

onMounted(fetchData)
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
