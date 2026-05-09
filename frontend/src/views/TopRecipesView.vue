<template>
  <main class="page-wrap">
    <PageHero title="Top20 灵感榜" description="根据评分、尝试、收藏、复刻等综合热度生成的精品咖啡灵感排行。" />
    <section class="podium">
      <article v-for="(recipe, index) in topRecipes.slice(0, 3)" :key="recipe.publicRecipeId || recipe.id" class="rank-card glass-card" :class="`rank-${index + 1}`">
        <strong>#{{ index + 1 }}</strong>
        <h3>{{ recipe.recipeName || recipe.name }}</h3>
        <p>{{ recipe.authorName }} · Hot {{ recipe.hotScore }}</p>
        <div class="tag-row">
          <span v-for="tag in recipe.flavorTags" :key="tag">{{ tag }}</span>
        </div>
      </article>
    </section>
    <section class="rank-list">
      <RecipeCard v-for="recipe in topRecipes" :key="recipe.publicRecipeId || recipe.id" :item="recipe" />
    </section>
  </main>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import PageHero from '../components/common/PageHero.vue'
import RecipeCard from '../components/recipe/RecipeCard.vue'
import { rankingApi } from '../api'
import { publicRecipes as mockPublicRecipes } from '../data/coffeeLabMock'

const topRecipes = ref(mockPublicRecipes)

const loadTopRecipes = async () => {
  try {
    topRecipes.value = await rankingApi.topRecipes({ range: 'all' })
  } catch (error) {
    topRecipes.value = mockPublicRecipes
  }
}

onMounted(loadTopRecipes)
</script>
