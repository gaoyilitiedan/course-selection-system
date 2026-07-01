<template>
  <div class="crud-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>课件下载</span>
          <el-select v-model="courseId" placeholder="选择课程" @change="fetchCoursewares">
            <el-option v-for="c in myCourses" :key="c.course?.id" :label="c.course?.name" :value="c.course?.id" />
          </el-select>
        </div>
      </template>

      <div v-if="courseId">
        <el-table :data="coursewares" v-loading="loading" border stripe>
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="fileName" label="文件名" min-width="200" />
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button size="small" type="primary" @click="handleDownload(row)">下载</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <el-empty v-else description="请选择已中签的课程" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { downloadBlob } from '@/utils/download'
import * as studentApi from '@/api/student'

const route = useRoute()
const userStore = useUserStore()
const myCourses = ref([])
const courseId = ref(route.params.courseId ? Number(route.params.courseId) : null)
const coursewares = ref([])
const loading = ref(false)

async function loadMyCourses() {
  myCourses.value = (await studentApi.getEnrollments()) || []
  myCourses.value = myCourses.value.filter(e => e.status === 'SELECTED')
}

async function fetchCoursewares() {
  if (!courseId.value) return
  loading.value = true
  try { coursewares.value = (await studentApi.getCoursewares(courseId.value)) || [] } finally { loading.value = false }
}

function handleDownload(row) {
  downloadBlob(
    `/api/student/courseware/${courseId.value}/download/${row.id}`,
    row.fileName,
    userStore.token
  )
}

onMounted(() => { loadMyCourses().then(() => { if (courseId.value) fetchCoursewares() }) })
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
