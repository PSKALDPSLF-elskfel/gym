<template>
  <div class="auth-form">
    <!-- 表单标题 -->
    <div class="form-header">
      <h2>{{ title }}</h2>
    </div>

    <!-- 表单内容 -->
    <a-form 
      :model="formData" 
      :rules="rules" 
      ref="formRef"
      @submit.prevent="handleSubmit"
    >
      <!-- 动态渲染表单项 -->
      <a-form-item 
        v-for="field in fields" 
        :key="field.prop"
        :name="field.prop"
      >
        <a-input 
          v-if="field.type !== 'password'"
          v-model:value="formData[field.prop]"
          :placeholder="field.placeholder"
          allow-clear
          @pressEnter="handleSubmit"
        >
          <template #prefix v-if="field.icon">
            <component :is="field.icon" />
          </template>
        </a-input>
        <a-input-password 
          v-else
          v-model:value="formData[field.prop]"
          :placeholder="field.placeholder"
          allow-clear
          @pressEnter="handleSubmit"
        >
          <template #prefix v-if="field.icon">
            <component :is="field.icon" />
          </template>
        </a-input-password>
      </a-form-item>
      
      <!-- 提交按钮 -->
      <a-form-item>
        <a-button 
          type="primary" 
          :loading="loading" 
          @click="handleSubmit"
          block
        >
          {{ submitText }}
        </a-button>
      </a-form-item>
    </a-form>

    <!-- 底部链接 -->
    <div class="form-links" v-if="links && links.length">
      <div v-for="link in links" :key="link.text" class="link-item">
        <router-link :to="link.to">{{ link.text }}</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({
  title: {
    type: String,
    required: true
  },
  fields: {
    type: Array,
    required: true
  },
  formData: {
    type: Object,
    required: true
  },
  rules: {
    type: Object,
    required: true
  },
  submitText: {
    type: String,
    default: '提交'
  },
  loading: {
    type: Boolean,
    default: false
  },
  links: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['submit'])

const formRef = ref(null)

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    emit('submit')
  } catch (error) {
    // 验证失败，不做任何操作
  }
}

defineExpose({
  formRef
})
</script>

<style scoped>
.auth-form {
  width: 100%;
}

.form-header {
  text-align: center;
  margin-bottom: 2rem;
}

.form-header h2 {
  margin: 0 0 0.5rem 0;
  color: #333;
}

.form-header p {
  margin: 0;
  color: #666;
  font-size: 0.9rem;
}

.form-links {
  text-align: center;
  margin-top: 1rem;
}

.link-item {
  margin: 0.5rem 0;
}

.link-item a {
  color: #409eff;
  text-decoration: none;
  font-size: 0.9rem;
}

.link-item a:hover {
  text-decoration: underline;
}
</style>
