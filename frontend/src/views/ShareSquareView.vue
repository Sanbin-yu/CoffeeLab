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
    <p v-if="message" class="form-message">{{ message }}</p>
    <section class="card-grid">
      <RecipeCard
        v-for="recipe in recipes"
        :key="getPublicRecipeId(recipe)"
        :item="recipe"
        interactive
        :favorited="favoritedIds.has(getPublicRecipeId(recipe))"
        :selected-rating="ratedScores[getPublicRecipeId(recipe)] || 0"
        :busy-action="busyMap[getPublicRecipeId(recipe)] || ''"
        @rate="(score) => handleRate(recipe, score)"
        @try="() => handleTry(recipe)"
        @favorite="() => handleFavorite(recipe)"
        @fork="() => handleFork(recipe)"
      />
    </section>
  </main>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import PageHero from '../components/common/PageHero.vue'
import RecipeCard from '../components/recipe/RecipeCard.vue'
import { publicRecipeApi } from '../api'
import { publicRecipes as mockPublicRecipes } from '../data/coffeeLabMock'
import {
  applyFavorite,
  applyFork,
  applyRating,
  applyTried,
  getPublicRecipeId,
  sortPublicRecipes,
  updateRecipeById
} from '../utils/publicRecipeInteractions'

const sorts = ['latest', 'rating', 'tried', 'favorite', 'hot']
const sort = ref('latest')
const recipes = ref(mockPublicRecipes)
const favoritedIds = ref(new Set())
const ratedScores = ref({})
const busyMap = ref({})
const message = ref('')

const setMessage = (nextMessage) => {
  message.value = nextMessage
}

const setBusy = (id, action) => {
  busyMap.value = { ...busyMap.value, [id]: action }
}

const clearBusy = (id) => {
  const nextBusy = { ...busyMap.value }
  delete nextBusy[id]
  busyMap.value = nextBusy
}

const setRatedScore = (id, score) => {
  ratedScores.value = { ...ratedScores.value, [id]: score }
}

const hasAuthToken = () => Boolean(localStorage.getItem('coffeelab_token'))

const replaceRecipe = (id, updater) => {
  recipes.value = sortPublicRecipes(updateRecipeById(recipes.value, id, updater), sort.value)
}

const loadRecipes = async () => {
  try {
    const page = await publicRecipeApi.list({ sort: sort.value, page: 1, pageSize: 12 })
    recipes.value = page.records || []
  } catch (error) {
    recipes.value = sortPublicRecipes(mockPublicRecipes, sort.value)
    setMessage('后端暂不可用，已切换到本地演示数据。')
  }
}

const changeSort = (nextSort) => {
  sort.value = nextSort
  loadRecipes()
}

const handleRate = async (recipe, score) => {
  const id = getPublicRecipeId(recipe)
  if (!id || busyMap.value[id]) return

  const previousScore = ratedScores.value[id] || 0
  setBusy(id, 'rate')
  try {
    if (hasAuthToken()) {
      await publicRecipeApi.rate(id, { score, comment: '' })
      setMessage('评分已记录。')
    } else {
      setMessage('已本地记录评分，登录后可同步到账号。')
    }
  } catch (error) {
    setMessage('后端暂不可用，已用本地演示记录评分。')
  } finally {
    replaceRecipe(id, (item) => applyRating(item, score, previousScore))
    setRatedScore(id, score)
    clearBusy(id)
  }
}

const handleTry = async (recipe) => {
  const id = getPublicRecipeId(recipe)
  if (!id || busyMap.value[id]) return

  setBusy(id, 'try')
  try {
    if (hasAuthToken()) {
      await publicRecipeApi.tryRecipe(id)
      setMessage('已标记尝试。')
    } else {
      setMessage('已本地标记尝试，登录后可同步到账号。')
    }
  } catch (error) {
    setMessage('后端暂不可用，已用本地演示记录尝试。')
  } finally {
    replaceRecipe(id, applyTried)
    clearBusy(id)
  }
}

const handleFavorite = async (recipe) => {
  const id = getPublicRecipeId(recipe)
  if (!id || busyMap.value[id]) return

  const shouldFavorite = !favoritedIds.value.has(id)
  setBusy(id, 'favorite')
  try {
    if (hasAuthToken()) {
      if (shouldFavorite) {
        await publicRecipeApi.favorite(id)
      } else {
        await publicRecipeApi.unfavorite(id)
      }
      setMessage(shouldFavorite ? '已收藏配方。' : '已取消收藏。')
    } else {
      setMessage(shouldFavorite ? '已本地收藏，登录后可同步到账号。' : '已取消本地收藏。')
    }
  } catch (error) {
    setMessage(shouldFavorite ? '后端暂不可用，已用本地演示收藏。' : '后端暂不可用，已用本地演示取消收藏。')
  } finally {
    const nextFavoritedIds = new Set(favoritedIds.value)
    if (shouldFavorite) {
      nextFavoritedIds.add(id)
    } else {
      nextFavoritedIds.delete(id)
    }
    favoritedIds.value = nextFavoritedIds
    replaceRecipe(id, (item) => applyFavorite(item, shouldFavorite))
    clearBusy(id)
  }
}

const handleFork = async (recipe) => {
  const id = getPublicRecipeId(recipe)
  if (!id || busyMap.value[id]) return

  setBusy(id, 'fork')
  try {
    if (hasAuthToken()) {
      await publicRecipeApi.fork(id, { name: `${recipe.recipeName || recipe.name || 'CoffeeLab 配方'} 复刻` })
      setMessage('已复刻到我的配方')
    } else {
      setMessage('已本地复刻灵感，登录后可保存到我的配方。')
    }
  } catch (error) {
    // Keep the public demo loop usable even when the API is offline.
    setMessage('后端暂不可用，已用本地演示复刻。')
  } finally {
    replaceRecipe(id, applyFork)
    clearBusy(id)
  }
}

onMounted(loadRecipes)
</script>
