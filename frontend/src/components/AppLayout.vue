<template>
  <el-container class="app-layout">
    <el-aside :width="sidebarCollapsed ? '64px' : '220px'" class="app-aside">
      <div class="logo">
        <span v-if="!sidebarCollapsed">学生选课系统</span>
        <span v-else>选课</span>
      </div>
      <SidebarMenu />
    </el-aside>
    <el-container>
      <el-header class="app-header">
        <Topbar @toggle-sidebar="toggleSidebar" />
      </el-header>
      <el-main class="app-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useAppStore } from '@/stores/app'
import SidebarMenu from './SidebarMenu.vue'
import Topbar from './Topbar.vue'

const appStore = useAppStore()
const sidebarCollapsed = computed(() => appStore.sidebarCollapsed)

function toggleSidebar() {
  appStore.toggleSidebar()
}
</script>

<style scoped>
.app-layout {
  height: 100vh;
}
.app-aside {
  background-color: #304156;
  transition: width 0.3s;
  overflow: hidden;
}
.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  white-space: nowrap;
}
.app-header {
  background: #fff;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  padding: 0 20px;
}
.app-main {
  background: #f0f2f5;
  min-height: calc(100vh - 60px);
}
</style>
