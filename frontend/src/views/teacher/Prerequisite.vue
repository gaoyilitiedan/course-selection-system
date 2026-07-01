<template>
  <div class="crud-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>设置先修课程</span>
        </div>
      </template>
      <el-form inline>
        <el-form-item label="选择课程">
          <el-select v-model="courseId" placeholder="请选择课程" @change="onCourseChange">
            <el-option v-for="c in courses" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
      </el-form>

      <div v-if="courseId" class="mt-16">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="课程">{{ selectedCourse?.name }}</el-descriptions-item>
          <el-descriptions-item label="当前先修课程">{{ selectedCourse?.prerequisite?.name || '无' }}</el-descriptions-item>
        </el-descriptions>
        <el-form inline class="mt-16">
          <el-form-item label="设置先修课程">
            <el-select v-model="prerequisiteId" placeholder="选择先修课程" clearable>
              <el-option v-for="c in availablePrerequisites" :key="c.id" :label="c.name" :value="c.id" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="submitting" @click="handleSubmit">保存</el-button>
            <el-button @click="clearPrerequisite" :loading="submitting">清除先修课程</el-button>
          </el-form-item>
        </el-form>
      </div>
      <el-empty v-else description="请先选择课程" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import * as teacherApi from '@/api/teacher'
import * as deptApi from '@/api/dept'

const route = useRoute()
const courses = ref([])
const courseId = ref(route.params.courseId ? Number(route.params.courseId) : null)
const prerequisiteId = ref(null)
const submitting = ref(false)

const selectedCourse = computed(() => courses.value.find(c => c.id === courseId.value))
const availablePrerequisites = computed(() => courses.value.filter(c => c.id !== courseId.value))

async function loadCourses() {
  try { courses.value = (await deptApi.getCourses()) || [] } catch { courses.value = [] }
}

function onCourseChange(id) {
  const c = courses.value.find(co => co.id === id)
  prerequisiteId.value = c?.prerequisite?.id || null
}

async function handleSubmit() {
  submitting.value = true
  try {
    await teacherApi.setPrerequisite(courseId.value, prerequisiteId.value)
    ElMessage.success('设置成功')
    loadCourses()
  } finally {
    submitting.value = false
  }
}

async function clearPrerequisite() {
  submitting.value = true
  try {
    await teacherApi.setPrerequisite(courseId.value, null)
    prerequisiteId.value = null
    ElMessage.success('已清除先修课程')
    loadCourses()
  } finally {
    submitting.value = false
  }
}

onMounted(() => { loadCourses().then(() => { if (courseId.value) onCourseChange(courseId.value) }) })
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.mt-16 { margin-top: 16px; }
</style>
