<template>
  <div class="crud-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>课件管理</span>
          <el-select v-model="courseId" placeholder="选择课程" @change="fetchCoursewares" class="course-select">
            <el-option v-for="c in courses" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </div>
      </template>

      <div v-if="courseId">
        <el-upload
          class="upload-section"
          :action="`/api/teacher/courses/${courseId}/courseware`"
          :headers="uploadHeaders"
          :on-success="onUploadSuccess"
          :on-error="onUploadError"
          :show-file-list="false"
        >
          <el-button type="primary">上传课件</el-button>
        </el-upload>

        <el-table :data="coursewares" v-loading="loading" border stripe class="mt-16">
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="fileName" label="文件名" min-width="200" />
        </el-table>
      </div>
      <el-empty v-else description="请先选择课程" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import * as teacherApi from '@/api/teacher'
import * as deptApi from '@/api/dept'

const route = useRoute()
const userStore = useUserStore()
const courses = ref([])
const courseId = ref(route.params.courseId ? Number(route.params.courseId) : null)
const coursewares = ref([])
const loading = ref(false)

const uploadHeaders = computed(() => ({
  Authorization: `Bearer ${userStore.token}`,
}))

async function loadCourses() {
  try { courses.value = (await deptApi.getCourses()) || [] } catch { courses.value = [] }
}

async function fetchCoursewares() {
  if (!courseId.value) return
  loading.value = true
  try { coursewares.value = (await teacherApi.getCoursewares(courseId.value)) || [] } finally { loading.value = false }
}

function onUploadSuccess() {
  ElMessage.success('上传成功')
  fetchCoursewares()
}

function onUploadError() {
  ElMessage.error('上传失败')
}

onMounted(() => { loadCourses(); if (courseId.value) fetchCoursewares() })
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.course-select { width: 300px; }
.upload-section { margin-bottom: 16px; }
.mt-16 { margin-top: 16px; }
</style>
