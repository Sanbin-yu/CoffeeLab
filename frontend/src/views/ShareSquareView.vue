<template>
  <main class="page-wrap">
    <PageHero title="分享广场" description="公开配方的灵感墙，支持按最新、评分、尝试、收藏和热度排序。" />
    <div class="filter-pills">
      <button
        v-for="item in sorts"
        :key="item"
        type="button"
        :class="{ active: sort === item }"
        @click="changeSort(item)"
      >
        {{ item }}
      </button>
    </div>
    <section class="card-grid">
      <RecipeCard v-for="recipe in recipes" :key="recipe.id" :item="recipe" />
    </section>
  </main>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import PageHero from '../components/common/PageHero.vue'
import RecipeCard from '../components/recipe/RecipeCard.vue'
import { publicRecipeApi } from '../api'
import { publicRecipes as mockPublicRecipes } from '../data/coffeeLabMock'

const sorts = ['latest', 'rating', 'tried', 'favorite', 'hot']
const sort = ref('latest')
const recipes = ref(mockPublicRecipes)

const loadRecipes = async () => {
  try {
    const page = await publicRecipeApi.list({ sort: sort.value, page: 1, pageSize: 12 })
    recipes.value = page.records
  } catch (error) {
    recipes.value = mockPublicRecipes
  }
}

const changeSort = (nextSort) => {
  sort.value = nextSort
  loadRecipes()
}

onMounted(loadRecipes)
</script>
