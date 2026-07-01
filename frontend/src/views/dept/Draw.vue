<template>
  <div class="crud-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>抽签管理</span>
        </div>
      </template>
      <el-form :inline="true">
        <el-form-item label="选择课程">
          <el-select v-model="selectedCourseId" placeholder="请选择课程" @change="onCourseChange">
            <el-option v-for="c in courses" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="danger" :disabled="!selectedCourseId" :loading="drawing" @click="executeDraw">开始抽签</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card v-if="drawResult" class="mt-16">
      <template #header><span>抽签结果</span></template>
      <el-alert type="success" :closable="false" show-icon class="mb-16">
        <template #title>
          课程容量: {{ courseInfo?.capacity }}，选课人数: {{ totalCount }}，中签: {{ selectedCount }}人，未中签: {{ rejectedCount }}人
        </template>
      </el-alert>
      <el-table :data="drawResult" border stripe>
        <el-table-column label="学生" width="100"><template #default="{ row }">{{ row.student?.name }}</template></el-table-column>
        <el-table-column label="学号" width="120"><template #default="{ row }">{{ row.student?.studentNo }}</template></el-table-column>
        <el-table-column prop="status" label="结果" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'SELECTED' ? 'success' : 'danger'">{{ row.status === 'SELECTED' ? '中签' : '未中签' }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import * as deptApi from '@/api/dept'

const courses = ref([])
const selectedCourseId = ref(null)
const courseInfo = ref(null)
const drawResult = ref(null)
const drawing = ref(false)

const totalCount = computed(() => drawResult.value?.length || 0)
const selectedCount = computed(() => drawResult.value?.filter(r => r.status === 'SELECTED').length || 0)
const rejectedCount = computed(() => drawResult.value?.filter(r => r.status === 'REJECTED').length || 0)

async function loadCourses() {
  courses.value = (await deptApi.getCourses()) || []
}

function onCourseChange(id) {
  courseInfo.value = courses.value.find(c => c.id === id)
  drawResult.value = null
}

async function executeDraw() {
  await ElMessageBox.confirm('确认对该课程进行抽签？此操作不可撤销！', '确认抽签', { type: 'warning', confirmButtonText: '确认抽签' })
  drawing.value = true
  try {
    await deptApi.executeDraw(selectedCourseId.value)
    ElMessage.success('抽签完成')
    drawResult.value = (await deptApi.getDrawResult(selectedCourseId.value)) || []
  } finally {
    drawing.value = false
  }
}

onMounted(loadCourses)
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.mt-16 { margin-top: 16px; }
.mb-16 { margin-bottom: 16px; }
</style>
