<template>
  <main class="page-wrap">
    <PageHero title="我的配方" description="你的私人咖啡收藏馆。后续接入后端后会通过 GET /api/recipes/my 拉取。" />
    <section class="card-grid">
      <RecipeCard v-for="recipe in recipes" :key="recipe.id" :item="recipe" />
    </section>
  </main>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import PageHero from '../components/common/PageHero.vue'
import RecipeCard from '../components/recipe/RecipeCard.vue'
import { recipeApi } from '../api'
import { myRecipes as mockMyRecipes } from '../data/coffeeLabMock'

const recipes = ref(mockMyRecipes)

const loadMyRecipes = async () => {
  try {
    const page = await recipeApi.my({ page: 1, pageSize: 12 })
    recipes.value = page.records
  } catch (error) {
    recipes.value = mockMyRecipes
  }
}

onMounted(loadMyRecipes)
</script>
