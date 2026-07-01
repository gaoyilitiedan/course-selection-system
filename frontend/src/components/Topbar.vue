<template>
  <div class="topbar">
    <div class="topbar-left">
      <el-icon class="collapse-btn" @click="$emit('toggleSidebar')">
        <Fold v-if="!appStore.sidebarCollapsed" />
        <Expand v-else />
      </el-icon>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item v-if="currentPageTitle">{{ currentPageTitle }}</el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <div class="topbar-right">
      <el-dropdown trigger="click">
        <span class="user-info">
          <el-icon><UserFilled /></el-icon>
          <span class="username">{{ userStore.userInfo.username }}</span>
          <el-tag size="small" type="info">{{ roleLabel }}</el-tag>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="handleLogout">
              <el-icon><SwitchButton /></el-icon>
              退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useAppStore } from '@/stores/app'
import { MENU_CONFIG, ROLE_LABELS } from '@/utils/constants'

defineEmits(['toggleSidebar'])

const route = useRoute()
const userStore = useUserStore()
const appStore = useAppStore()

const roleLabel = computed(() => ROLE_LABELS[userStore.userInfo.role] || '')

const currentPageTitle = computed(() => {
  const menuItems = MENU_CONFIG[userStore.userInfo.role] || []
  const item = menuItems.find((m) => m.path === route.path)
  return item ? item.title : ''
})

function handleLogout() {
  userStore.logout()
}
</script>

<style scoped>
.topbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}
.topbar-left {
  display: flex;
  align-items: center;
  gap: 16px;
}
.collapse-btn {
  font-size: 20px;
  cursor: pointer;
}
.topbar-right {
  display: flex;
  align-items: center;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}
.username {
  font-weight: 500;
}
</style>
