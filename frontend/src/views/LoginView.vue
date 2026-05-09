<template>
  <main class="login-page">
    <section class="login-art">
      <p class="eyebrow">Member lab</p>
      <h1>登录后保存你的每一次灵感萃取</h1>
      <p>第一版支持手机号或邮箱登录。当前页面提供 mock 登录状态，便于前端联调。</p>
    </section>
    <form class="login-form glass-card" @submit.prevent="submit">
      <h2>登录 CoffeeLab</h2>
      <label>
        手机号或邮箱
        <input v-model="form.account" placeholder="latte@example.com" />
      </label>
      <label>
        密码
        <input v-model="form.password" type="password" placeholder="请输入密码" />
      </label>
      <button class="btn primary" type="submit">{{ loading ? '正在进入...' : '进入实验室' }}</button>
      <p v-if="message">{{ message }}</p>
      <p v-if="userStore.user">已登录：{{ userStore.user.nickname }}</p>
    </form>
  </main>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useUserStore } from '../stores/user'

const userStore = useUserStore()
const form = reactive({ account: 'latte@example.com', password: '123456' })
const loading = ref(false)
const message = ref('')

const submit = async () => {
  loading.value = true
  message.value = ''
  try {
    await userStore.login(form)
    message.value = '已连接后端，登录成功。'
  } catch (error) {
    userStore.loginMock(form)
    message.value = '后端暂不可用，已使用本地体验登录。'
  } finally {
    loading.value = false
  }
}
</script>
