<template>
  <div class="crud-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>成绩统计</span>
          <el-select v-model="courseId" placeholder="选择课程" @change="fetchStats">
            <el-option v-for="c in courses" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </div>
      </template>

      <div v-if="stats" v-loading="loading">
        <el-row :gutter="20" class="stat-cards">
          <el-col :span="8"><el-statistic title="总人数" :value="stats.total" /></el-col>
          <el-col :span="8"><el-statistic title="及格率" :value="stats.passRate" /></el-col>
        </el-row>
        <div ref="chartRef" class="chart-box"></div>
      </div>
      <el-empty v-else description="请选择课程查看统计" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick, watch } from 'vue'
import { useRoute } from 'vue-router'
import * as echarts from 'echarts'
import * as teacherApi from '@/api/teacher'
import * as deptApi from '@/api/dept'
import { SCORE_RANGES } from '@/utils/constants'

const route = useRoute()
const courses = ref([])
const courseId = ref(route.params.courseId ? Number(route.params.courseId) : null)
const stats = ref(null)
const loading = ref(false)
const chartRef = ref(null)
let chart = null

async function loadCourses() {
  try { courses.value = (await deptApi.getCourses()) || [] } catch { courses.value = [] }
}

async function fetchStats() {
  if (!courseId.value) return
  loading.value = true
  try {
    stats.value = await teacherApi.getStats(courseId.value)
    await nextTick()
    renderChart()
  } finally { loading.value = false }
}

function renderChart() {
  if (!chartRef.value || !stats.value) return
  if (chart) chart.dispose()
  chart = echarts.init(chartRef.value)
  const data = SCORE_RANGES.map(k => stats.value[k] || 0)
  chart.setOption({
    title: { text: '成绩分布' },
    tooltip: {},
    xAxis: { data: SCORE_RANGES },
    yAxis: { name: '人数' },
    series: [{ type: 'bar', data, itemStyle: { color: '#409eff' } }],
  })
}

onMounted(() => { loadCourses().then(() => { if (courseId.value) fetchStats() }) })
onUnmounted(() => { if (chart) chart.dispose() })
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.stat-cards { margin-bottom: 24px; }
.chart-box { width: 100%; height: 400px; }
</style>
