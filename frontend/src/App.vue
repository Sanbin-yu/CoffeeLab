<template>
  <div class="app-shell">
    <header class="site-header">
      <RouterLink class="brand" to="/">
        <span class="brand-mark">CL</span>
        <span>CoffeeLab</span>
      </RouterLink>
      <nav class="site-nav">
        <RouterLink to="/">首页</RouterLink>
        <RouterLink to="/diy">DIY 实验台</RouterLink>
        <RouterLink to="/classic">经典咖啡</RouterLink>
        <RouterLink to="/my-recipes">我的配方</RouterLink>
        <RouterLink to="/share">分享广场</RouterLink>
        <RouterLink to="/top">Top20</RouterLink>
      </nav>
      <RouterLink class="login-link" to="/login">登录</RouterLink>
    </header>
    <RouterView />
  </div>
</template>
