import { ref } from 'vue'
import { defineStore } from 'pinia'
import { authApi } from '../api'

export const useUserStore = defineStore('user', () => {
  const user = ref(null)
  const token = ref(localStorage.getItem('coffeelab_token') || '')

  const setSession = (session) => {
    token.value = session.token
    user.value = session.user
    localStorage.setItem('coffeelab_token', session.token)
  }

  const loginMock = (payload) => {
    setSession({
      token: 'mock-token-value',
      user: {
        id: 12,
        nickname: payload.account?.split('@')[0] || 'Coffee Maker',
        email: payload.account || 'latte@example.com',
        avatarUrl: null
      }
    })
  }

  const login = async (payload) => {
    const session = await authApi.login(payload)
    setSession(session)
  }

  const register = async (payload) => {
    user.value = await authApi.register(payload)
  }

  const loadMe = async () => {
    if (!token.value) return null
    user.value = await authApi.getMe()
    return user.value
  }

  const logout = () => {
    token.value = ''
    user.value = null
    localStorage.removeItem('coffeelab_token')
  }

  return { user, token, login, loginMock, register, loadMe, logout }
})
