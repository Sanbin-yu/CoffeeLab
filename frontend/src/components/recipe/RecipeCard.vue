<template>
  <article class="recipe-card">
    <div class="mini-cup" :class="item.cupType">
      <span></span>
    </div>
    <div>
      <p class="eyebrow">{{ item.authorName || (item.isPublic ? '已公开' : '私人配方') }}</p>
      <h3>{{ item.recipeName || item.name }}</h3>
      <div class="tag-row">
        <span v-for="tag in item.flavorTags" :key="tag">{{ tag }}</span>
      </div>
      <p v-if="item.averageRating" class="metric-line">
        {{ item.averageRating }} 分 · {{ item.triedCount }} 次尝试 · {{ item.favoriteCount }} 收藏
      </p>
    </div>
  </article>
</template>

<script setup>
defineProps({
  item: { type: Object, required: true }
})
</script>
