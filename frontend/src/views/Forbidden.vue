<template>
  <div class="error-page">
    <h1>403</h1>
    <p>您没有权限访问此页面</p>
    <el-button type="primary" @click="$router.push('/dashboard')">返回首页</el-button>
  </div>
</template>

<style scoped>
.error-page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}
.error-page h1 {
  font-size: 72px;
  color: #e6a23c;
  margin: 0;
}
.error-page p {
  font-size: 18px;
  color: #909399;
  margin: 20px 0;
}
</style>
