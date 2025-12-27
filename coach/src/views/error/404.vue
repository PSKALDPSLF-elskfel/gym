<template>
  <div style="text-align: center; padding: 50px">
    <a-result
      status="404"
      title="404"
      sub-title="页面未找到"
    >
      <template #extra>
        <a-button type="primary" @click="handleGoHome">
          返回首页
        </a-button>
      </template>
    </a-result>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const handleGoHome = () => {
  router.push('/')
}
</script>

<style scoped>
</style>
