<template>
  <div class="crud-page">
    <el-card>
      <template #header><span>成绩查询</span></template>
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column label="课程名称" width="160"><template #default="{ row }">{{ row.course?.name }}</template></el-table-column>
        <el-table-column label="授课教师" width="100"><template #default="{ row }">{{ row.course?.teacher?.name }}</template></el-table-column>
        <el-table-column prop="score" label="成绩" width="80">
          <template #default="{ row }">
            <span :class="scoreClass(row.score)">{{ row.score ?? '-' }}</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as studentApi from '@/api/student'

const tableData = ref([])
const loading = ref(false)

function scoreClass(score) {
  if (score == null) return ''
  if (score >= 90) return 'score-excellent'
  if (score >= 60) return 'score-pass'
  return 'score-fail'
}

async function fetchData() {
  loading.value = true
  try { tableData.value = (await studentApi.getGrades()) || [] } finally { loading.value = false }
}

onMounted(fetchData)
</script>

<style scoped>
.score-excellent { color: #67c23a; font-weight: bold; }
.score-pass { color: #409eff; }
.score-fail { color: #f56c6c; font-weight: bold; }
</style>
