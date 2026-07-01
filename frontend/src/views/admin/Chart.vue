<template>
  <div class="chart-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>选课人数统计图表</span>
          <el-button type="primary" @click="downloadChart">下载 PNG</el-button>
        </div>
      </template>
      <div v-loading="loading" class="chart-container">
        <img v-if="chartUrl" :src="chartUrl" alt="选课人数统计图表" class="chart-img" />
        <el-empty v-else description="暂无图表数据" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { fetchBlobUrl } from '@/utils/download'
import { downloadBlob } from '@/utils/download'

const userStore = useUserStore()
const chartUrl = ref(null)
const loading = ref(false)

async function loadChart() {
  loading.value = true
  try {
    const url = '/api/admin/stats/chart'
    chartUrl.value = await fetchBlobUrl(url, userStore.token)
  } catch {
    chartUrl.value = null
  } finally {
    loading.value = false
  }
}

function downloadChart() {
  downloadBlob('/api/admin/stats/chart', '选课人数统计.png', userStore.token)
}

onMounted(loadChart)

onUnmounted(() => {
  if (chartUrl.value) {
    URL.revokeObjectURL(chartUrl.value)
  }
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.chart-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
}
.chart-img {
  max-width: 100%;
  height: auto;
}
</style>
