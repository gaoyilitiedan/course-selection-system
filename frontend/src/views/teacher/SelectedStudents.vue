<template>
  <div class="crud-page">
    <el-card>
      <template #header><span>中签学生名单</span></template>
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="选课ID" width="80" />
        <el-table-column label="学生姓名" width="100"><template #default="{ row }">{{ row.student?.name }}</template></el-table-column>
        <el-table-column label="学号" width="120"><template #default="{ row }">{{ row.student?.studentNo }}</template></el-table-column>
        <el-table-column label="课程" min-width="160"><template #default="{ row }">{{ row.course?.name }}</template></el-table-column>
        <el-table-column prop="score" label="成绩" width="80" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as teacherApi from '@/api/teacher'

const tableData = ref([])
const loading = ref(false)

async function fetchData() {
  loading.value = true
  try { tableData.value = (await teacherApi.getSelectedEnrollments()) || [] } finally { loading.value = false }
}

onMounted(fetchData)
</script>
