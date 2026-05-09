<template>
  <main class="home-page">
    <section class="home-hero insta-hero">
      <div class="hero-copy editorial-copy">
        <div class="hero-kicker">
          <span>Specialty Coffee Laboratory</span>
          <i>2026 menu drop</i>
        </div>
        <h1 class="hero-title">
          <span>调制你的</span>
          <span class="script-line">专属咖啡</span>
        </h1>
        <p class="hero-lead">
          像整理一条咖啡灵感动态一样，选择基底、奶类、糖浆、冰块、奶泡与顶料。
          右侧杯子会实时生成分层、光影和风味标签，让每一次 DIY 都像在发布一杯原创作品。
        </p>
        <div class="hero-actions">
          <RouterLink class="btn primary" to="/diy">开始 DIY</RouterLink>
          <RouterLink class="btn ghost" to="/classic">浏览经典咖啡</RouterLink>
        </div>
        <div class="hero-social-proof">
          <span>4.9 community taste score</span>
          <span>Top20 inspiration wall</span>
          <span>DIY recipe archive</span>
        </div>
      </div>
      <div class="hero-visual-stack">
        <div class="floating-sticker sticker-one">oat milk mood</div>
        <div class="floating-sticker sticker-two">#coffeegram</div>
        <CoffeeCupPreview :recipe="previewRecipe" :summary="previewSummary" :pulse-key="1" />
        <article class="mini-post-card">
          <span></span>
          <strong>Today’s lab shot</strong>
          <p>焦糖榛果、燕麦奶、半糖，适合下午三点的松弛感。</p>
        </article>
      </div>
    </section>

    <section class="feature-grid editorial-grid">
      <article v-for="item in features" :key="item.title" class="glass-card feature-card">
        <span>{{ item.step }}</span>
        <h3>{{ item.title }}</h3>
        <p>{{ item.text }}</p>
      </article>
    </section>
  </main>
</template>

<script setup>
import CoffeeCupPreview from '../components/coffee/CoffeeCupPreview.vue'

const previewRecipe = {
  name: 'Signature Lab Cup',
  cupType: 'coldCup',
  coffeeBase: 'espresso',
  espressoShots: 2,
  milkType: 'oatMilk',
  sweetness: 'halfSugar',
  iceLevel: 'normalIce',
  syrups: ['caramel', 'hazelnut'],
  foam: 'lightFoam',
  toppings: ['cocoaPowder']
}
const previewSummary = { coffeeBase: '浓缩咖啡', milkType: '燕麦奶', sweetness: '半糖' }
const features = [
  { step: '01', title: '选择原料', text: '从精品咖啡实验台挑选基底、奶类、甜度与风味层。' },
  { step: '02', title: '实时预览', text: '杯体用 CSS/SVG 分层呈现液体、冰块、奶泡和顶料状态。' },
  { step: '03', title: '保存分享', text: '命名你的配方，保存到个人收藏或发布到分享广场。' }
]
</script>
