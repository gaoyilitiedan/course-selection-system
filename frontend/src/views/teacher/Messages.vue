<template>
  <div class="messages-page">
    <el-card>
      <template #header><span>学生留言</span></template>

      <div class="message-form mb-16">
        <el-form :model="form" inline>
          <el-form-item label="课程"><el-input-number v-model="form.courseId" :min="1" /></el-form-item>
          <el-form-item label="学生ID"><el-input-number v-model="form.studentId" :min="1" /></el-form-item>
          <el-form-item label="内容"><el-input v-model="form.content" placeholder="输入消息内容" style="width: 300px" /></el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="sending" @click="sendMsg">发送</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table :data="messages" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column label="课程" width="120"><template #default="{ row }">{{ row.course?.name }}</template></el-table-column>
        <el-table-column label="消息内容" min-width="300"><template #default="{ row }">{{ row.content }}</template></el-table-column>
        <el-table-column label="发送方" width="100">
          <template #default="{ row }">{{ row.fromUserType === 'TEACHER' ? '我' : '学生' }}</template>
        </el-table-column>
        <el-table-column label="时间" width="180">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import * as teacherApi from '@/api/teacher'

const messages = ref([])
const loading = ref(false)
const sending = ref(false)
const form = reactive({ courseId: null, studentId: null, content: '' })

function formatTime(t) {
  if (!t) return ''
  if (Array.isArray(t)) return new Date(t[0], t[1] - 1, t[2], t[3] || 0, t[4] || 0).toLocaleString()
  return new Date(t).toLocaleString()
}

async function fetchMessages() {
  loading.value = true
  try { messages.value = (await teacherApi.getMessages()) || [] } finally { loading.value = false }
}

async function sendMsg() {
  if (!form.courseId || !form.studentId || !form.content) { ElMessage.warning('请填写完整信息'); return }
  sending.value = true
  try {
    await teacherApi.sendMessage({ courseId: form.courseId, studentId: form.studentId, content: form.content })
    ElMessage.success('发送成功')
    form.content = ''
    fetchMessages()
  } finally { sending.value = false }
}

onMounted(fetchMessages)
</script>

<style scoped>
.mb-16 { margin-bottom: 16px; }
</style>
