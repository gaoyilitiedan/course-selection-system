<template>
  <div class="crud-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>本学院课程管理</span>
          <el-button type="primary" @click="openDialog()">新增课程</el-button>
        </div>
      </template>
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="课程名称" width="160" />
        <el-table-column prop="description" label="课程介绍" min-width="200" show-overflow-tooltip />
        <el-table-column prop="capacity" label="容量" width="80" />
        <el-table-column label="授课教师" width="100">
          <template #default="{ row }">{{ row.teacher?.name }}</template>
        </el-table-column>
        <el-table-column label="先修课程" width="140">
          <template #default="{ row }">{{ row.prerequisite?.name || '无' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openDialog(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑课程' : '新增课程'" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="课程名称" prop="name"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="课程介绍" prop="description"><el-input v-model="form.description" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="容量" prop="capacity"><el-input-number v-model="form.capacity" :min="1" :max="9999" /></el-form-item>
        <el-form-item label="授课教师"><el-input v-model="teacherName" placeholder="请输入教师姓名" /></el-form-item>
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
import * as deptApi from '@/api/dept'

const tableData = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const editId = ref(null)
const formRef = ref(null)
const teacherName = ref('')

const form = reactive({ name: '', description: '', capacity: 30, teacher: null })
const rules = {
  name: [{ required: true, message: '请输入课程名称', trigger: 'blur' }],
  description: [{ required: true, message: '请输入课程介绍', trigger: 'blur' }],
  capacity: [{ required: true, message: '请输入课程容量' }],
}

async function fetchData() { loading.value = true; try { tableData.value = (await deptApi.getCourses()) || [] } finally { loading.value = false } }
function resetForm() { form.name = ''; form.description = ''; form.capacity = 30; form.teacher = null; teacherName.value = ''; editId.value = null; formRef.value?.resetFields() }
function openDialog(row) {
  resetForm()
  if (row) { isEdit.value = true; editId.value = row.id; form.name = row.name; form.description = row.description; form.capacity = row.capacity; teacherName.value = row.teacher?.name || '' }
  else { isEdit.value = false }
  dialogVisible.value = true
}
async function handleSubmit() {
  if (!(await formRef.value.validate().catch(() => false))) return
  const payload = { ...form }
  if (teacherName.value) payload.teacher = { name: teacherName.value }
  submitting.value = true
  try { if (isEdit.value) await deptApi.updateCourse(editId.value, payload); else await deptApi.createCourse(payload); dialogVisible.value = false; fetchData() } finally { submitting.value = false }
}
async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除课程 "${row.name}" 吗？`, '确认删除', { type: 'warning' })
  await deptApi.deleteCourse(row.id); fetchData()
}
onMounted(fetchData)
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
