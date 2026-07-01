<template>
  <el-menu
    :default-active="activePath"
    background-color="#304156"
    text-color="#bfcbd9"
    active-text-color="#409eff"
    :collapse="appStore.sidebarCollapsed"
    router
    class="sidebar-menu"
  >
    <el-menu-item
      v-for="item in menuItems"
      :key="item.path"
      :index="item.path"
    >
      <el-icon><component :is="item.icon" /></el-icon>
      <template #title>{{ item.title }}</template>
    </el-menu-item>
  </el-menu>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useAppStore } from '@/stores/app'
import { MENU_CONFIG } from '@/utils/constants'

const route = useRoute()
const userStore = useUserStore()
const appStore = useAppStore()

const activePath = computed(() => route.path)

const menuItems = computed(() => {
  const role = userStore.userInfo.role
  return MENU_CONFIG[role] || []
})
</script>

<style scoped>
.sidebar-menu {
  border-right: none;
}
</style>
