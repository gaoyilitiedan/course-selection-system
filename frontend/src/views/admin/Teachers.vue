<template>
  <div class="crud-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>教师管理</span>
          <el-button type="primary" @click="openDialog()">新增教师</el-button>
        </div>
      </template>
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="teacherNo" label="工号" width="120" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column label="所属学院" width="180">
          <template #default="{ row }">{{ row.department?.name }}</template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openDialog(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑教师' : '新增教师'" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" show-password />
        </el-form-item>
        <el-form-item label="工号" prop="teacherNo">
          <el-input v-model="form.teacherNo" />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="学院">
          <el-select v-model="form.departmentId" placeholder="请选择学院" style="width: 100%">
            <el-option v-for="d in departments" :key="d.id" :label="d.name" :value="d.id" />
          </el-select>
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
import * as adminApi from '@/api/admin'

const tableData = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const editId = ref(null)
const formRef = ref(null)
const departments = ref([])

const form = reactive({
  username: '',
  password: '',
  teacherNo: '',
  name: '',
  departmentId: null,
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  teacherNo: [{ required: true, message: '请输入工号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
}

async function fetchData() {
  loading.value = true
  try {
    tableData.value = (await adminApi.getTeachers()) || []
  } finally {
    loading.value = false
  }
}

function resetForm() {
  form.username = ''
  form.password = ''
  form.teacherNo = ''
  form.name = ''
  form.departmentId = null
  editId.value = null
  formRef.value?.resetFields()
}

function openDialog(row) {
  resetForm()
  if (row) {
    isEdit.value = true
    editId.value = row.id
    form.username = row.username
    form.password = ''
    form.teacherNo = row.teacherNo
    form.name = row.name
    form.departmentId = row.department?.id || null
  } else {
    isEdit.value = false
  }
  dialogVisible.value = true
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  const payload = {
    username: form.username,
    password: form.password,
    teacherNo: form.teacherNo,
    name: form.name,
    department: form.departmentId ? { id: form.departmentId } : null,
  }

  submitting.value = true
  try {
    if (isEdit.value) {
      await adminApi.updateTeacher(editId.value, payload)
    } else {
      await adminApi.createTeacher(payload)
    }
    dialogVisible.value = false
    fetchData()
  } finally {
    submitting.value = false
  }
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除教师 "${row.name}" 吗？`, '确认删除', { type: 'warning' })
  await adminApi.deleteTeacher(row.id)
  fetchData()
}

onMounted(async () => {
  departments.value = (await adminApi.getDepartments()) || []
  fetchData()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
