<template>
  <main class="page-wrap">
    <PageHero title="Top20 灵感榜" description="根据评分、尝试、收藏、复刻等综合热度生成的精品咖啡灵感排行。" />
    <p v-if="message" class="form-message">{{ message }}</p>
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
      <RecipeCard
        v-for="recipe in topRecipes"
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
import { publicRecipeApi, rankingApi } from '../api'
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

const topRecipes = ref(mockPublicRecipes)
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

const rerankRecipes = (items) =>
  sortPublicRecipes(items, 'hot').map((item, index) => ({
    ...item,
    rank: index + 1
  }))

const replaceRecipe = (id, updater) => {
  topRecipes.value = rerankRecipes(updateRecipeById(topRecipes.value, id, updater))
}

const loadTopRecipes = async () => {
  try {
    topRecipes.value = rerankRecipes(await rankingApi.topRecipes({ range: 'all' }))
  } catch (error) {
    topRecipes.value = rerankRecipes(mockPublicRecipes)
    setMessage('后端暂不可用，已切换到本地 Top20 演示数据。')
  }
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
    // Local mock update keeps Top20 interactive when the API is unavailable.
    setMessage('后端暂不可用，已用本地演示复刻。')
  } finally {
    replaceRecipe(id, applyFork)
    clearBusy(id)
  }
}

onMounted(loadTopRecipes)
</script>
