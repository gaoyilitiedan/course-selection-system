<template>
  <div class="crud-page">
    <el-card>
      <template #header><span>我的选课</span></template>
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column label="课程名称" width="160"><template #default="{ row }">{{ row.course?.name }}</template></el-table-column>
        <el-table-column label="授课教师" width="100"><template #default="{ row }">{{ row.course?.teacher?.name }}</template></el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="STATUS_CONFIG[row.status]?.type">{{ STATUS_CONFIG[row.status]?.label }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="score" label="成绩" width="80">
          <template #default="{ row }">{{ row.score ?? '-' }}</template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { STATUS_CONFIG } from '@/utils/constants'
import * as studentApi from '@/api/student'

const tableData = ref([])
const loading = ref(false)

async function fetchData() {
  loading.value = true
  try { tableData.value = (await studentApi.getEnrollments()) || [] } finally { loading.value = false }
}

onMounted(fetchData)
</script>
