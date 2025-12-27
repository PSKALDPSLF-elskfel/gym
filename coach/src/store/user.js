import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  const user = ref(JSON.parse(localStorage.getItem('coach_user')) || null)
  const token = ref(localStorage.getItem('coach_token'))

  const isAuthenticated = computed(() => !!token.value)

  const setUser = (userData) => {
    user.value = userData
    localStorage.setItem('coach_user', JSON.stringify(userData))
  }

  const setToken = (newToken) => {
    token.value = newToken
    localStorage.setItem('coach_token', newToken)
  }

  const logout = () => {
    user.value = null
    token.value = null
    localStorage.removeItem('coach_token')
    localStorage.removeItem('coach_user')
  }

  const initFromStorage = () => {
    const savedToken = localStorage.getItem('coach_token')
    const savedUser = localStorage.getItem('coach_user')
    if (savedToken) {
      token.value = savedToken
    }
    if (savedUser) {
      user.value = JSON.parse(savedUser)
    }
  }

  return {
    user,
    token,
    isAuthenticated,
    setUser,
    setToken,
    logout,
    initFromStorage
  }
})
