<template>
  <main class="page-wrap my-recipes-page">
    <PageHero
      title="我的配方"
      description="你的私人咖啡胶片库。支持再次使用、编辑、公开分享、复制和轻量删除确认；接口不可用时也能保留本地演示体验。"
    />
    <p v-if="message" class="form-message my-recipe-message">{{ message }}</p>
    <section class="card-grid my-recipe-grid">
      <article
        v-for="recipe in recipes"
        :key="getMyRecipeId(recipe)"
        class="my-recipe-frame"
        :class="{ 'is-busy': Boolean(busyMap[getMyRecipeId(recipe)]) }"
      >
        <RecipeCard :item="recipe" />
        <div class="my-recipe-actions" :aria-label="`${recipeTitle(recipe)} 操作`">
          <span class="share-badge" :class="{ active: recipe.isPublic }">
            {{ recipe.isPublic ? '已公开' : '私人' }}
          </span>
          <button type="button" @click="useRecipe(recipe)">再次使用</button>
          <button type="button" @click="useRecipe(recipe)">编辑</button>
          <button type="button" :disabled="isBusy(recipe)" @click="togglePublish(recipe)">
            {{ recipe.isPublic ? '取消公开' : '公开分享' }}
          </button>
          <button type="button" :disabled="isBusy(recipe)" @click="copyRecipe(recipe)">
            复制配方
          </button>
          <button
            v-if="confirmDeleteId !== getMyRecipeId(recipe)"
            type="button"
            class="danger"
            :disabled="isBusy(recipe)"
            @click="askDelete(recipe)"
          >
            删除
          </button>
          <span v-else class="delete-confirm">
            <button type="button" class="danger solid" :disabled="isBusy(recipe)" @click="deleteRecipe(recipe)">
              确认删除
            </button>
            <button type="button" @click="cancelDelete">取消</button>
          </span>
        </div>
      </article>
    </section>
    <p v-if="!recipes.length" class="empty-note">
      还没有保存的配方。去 DIY 实验台调一杯属于你的咖啡吧。
    </p>
  </main>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import PageHero from '../components/common/PageHero.vue'
import RecipeCard from '../components/recipe/RecipeCard.vue'
import { recipeApi } from '../api'
import { myRecipes as mockMyRecipes } from '../data/coffeeLabMock'
import { useDiyRecipeStore } from '../stores/diyRecipe'
import {
  applyCopyRecipe,
  applyPublishState,
  getMyRecipeId,
  recipeTitle,
  removeRecipeById,
  toDiyTemplate
} from '../utils/myRecipeActions'

const recipes = ref(mockMyRecipes)
const message = ref('')
const busyMap = ref({})
const confirmDeleteId = ref(null)
const router = useRouter()
const diyStore = useDiyRecipeStore()

const setMessage = (nextMessage) => {
  message.value = nextMessage
}

const hasAuthToken = () => Boolean(localStorage.getItem('coffeelab_token'))

const setBusy = (id, action) => {
  busyMap.value = { ...busyMap.value, [id]: action }
}

const clearBusy = (id) => {
  const nextBusy = { ...busyMap.value }
  delete nextBusy[id]
  busyMap.value = nextBusy
}

const isBusy = (recipe) => Boolean(busyMap.value[getMyRecipeId(recipe)])

const applyLocalDemoMessage = (action) => {
  if (hasAuthToken()) {
    setMessage(`后端暂不可用，已为你本地演示${action}。`)
  } else {
    setMessage(`未登录，已为你本地演示${action}；登录后可同步到账号。`)
  }
}

const loadMyRecipes = async () => {
  try {
    const page = await recipeApi.my({ page: 1, pageSize: 12 })
    recipes.value = page.records || []
    setMessage('')
  } catch (error) {
    recipes.value = mockMyRecipes
    setMessage('未连接到后端或尚未登录，已切换为本地演示配方。')
  }
}

const useRecipe = (recipe) => {
  diyStore.loadRecipeTemplate(toDiyTemplate(recipe))
  setMessage('已把这张配方装入 DIY 实验台，可以继续微调。')
  router.push('/diy')
}

const togglePublish = async (recipe) => {
  const id = getMyRecipeId(recipe)
  if (!id || isBusy(recipe)) return

  const shouldPublish = !recipe.isPublic
  setBusy(id, 'publish')
  try {
    if (!hasAuthToken()) {
      applyLocalDemoMessage(shouldPublish ? '公开分享' : '取消公开')
    } else {
      if (shouldPublish) {
        await recipeApi.publish(id)
      } else {
        await recipeApi.unpublish(id)
      }
      setMessage(shouldPublish ? '已公开分享到灵感广场。' : '已取消公开，回到私人配方。')
    }
  } catch (error) {
    applyLocalDemoMessage(shouldPublish ? '公开分享' : '取消公开')
  } finally {
    recipes.value = applyPublishState(recipes.value, id, shouldPublish)
    clearBusy(id)
  }
}

const copyRecipe = async (recipe) => {
  const id = getMyRecipeId(recipe)
  if (!id || isBusy(recipe)) return

  setBusy(id, 'copy')
  try {
    let created = {}
    const copyName = `${recipeTitle(recipe)} Copy`
    if (hasAuthToken()) {
      created = await recipeApi.copy(id, { name: copyName })
      setMessage('已复制到我的配方，可继续编辑。')
    } else {
      applyLocalDemoMessage('复制配方')
    }
    recipes.value = applyCopyRecipe(recipes.value, recipe, { ...created, name: copyName })
  } catch (error) {
    applyLocalDemoMessage('复制配方')
    recipes.value = applyCopyRecipe(recipes.value, recipe)
  } finally {
    clearBusy(id)
  }
}

const askDelete = (recipe) => {
  confirmDeleteId.value = getMyRecipeId(recipe)
  setMessage(`再点“确认删除”才会移除「${recipeTitle(recipe)}」。`)
}

const cancelDelete = () => {
  confirmDeleteId.value = null
  setMessage('已取消删除。')
}

const deleteRecipe = async (recipe) => {
  const id = getMyRecipeId(recipe)
  if (!id || isBusy(recipe)) return

  setBusy(id, 'delete')
  try {
    if (hasAuthToken()) {
      await recipeApi.remove(id)
      setMessage('已删除这张配方。')
    } else {
      applyLocalDemoMessage('删除配方')
    }
  } catch (error) {
    applyLocalDemoMessage('删除配方')
  } finally {
    recipes.value = removeRecipeById(recipes.value, id)
    confirmDeleteId.value = null
    clearBusy(id)
  }
}

onMounted(loadMyRecipes)
</script>

<style scoped>
.my-recipes-page {
  position: relative;
}

.my-recipe-message,
.empty-note {
  margin: -14px 0 24px;
  padding: 13px 18px;
  border: 1px dashed rgba(58, 33, 23, 0.18);
  border-radius: 22px;
  background: rgba(255, 250, 241, 0.58);
  box-shadow: 0 14px 38px rgba(84, 45, 24, 0.08);
}

.my-recipe-grid {
  align-items: start;
}

.my-recipe-frame {
  position: relative;
  display: grid;
  gap: 12px;
}

.my-recipe-frame.is-busy {
  cursor: wait;
}

.my-recipe-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
  padding: 10px;
  border: 1px solid rgba(255, 255, 255, 0.68);
  border-radius: 24px;
  background:
    radial-gradient(circle at 8% 12%, rgba(238, 201, 189, 0.54), transparent 5rem),
    rgba(255, 250, 241, 0.62);
  box-shadow: 0 16px 38px rgba(84, 45, 24, 0.1);
  backdrop-filter: blur(18px) saturate(1.15);
}

.my-recipe-actions button,
.share-badge {
  min-height: 34px;
  padding: 8px 11px;
  border: 1px solid rgba(58, 33, 23, 0.14);
  border-radius: 999px;
  color: rgba(38, 24, 18, 0.72);
  font-size: 12px;
  font-weight: 900;
  background: rgba(255, 255, 255, 0.48);
}

.my-recipe-actions button {
  cursor: pointer;
  transition: transform 0.2s ease, border-color 0.2s ease, background 0.2s ease, box-shadow 0.2s ease;
}

.my-recipe-actions button:hover:not(:disabled) {
  transform: translateY(-2px) rotate(-1deg);
  border-color: rgba(199, 128, 61, 0.52);
  background: rgba(231, 183, 98, 0.26);
  box-shadow: 0 12px 24px rgba(84, 45, 24, 0.12);
}

.my-recipe-actions button:disabled {
  cursor: wait;
  opacity: 0.58;
}

.share-badge {
  color: var(--cream);
  background: var(--espresso);
  transform: rotate(-2deg);
}

.share-badge.active {
  color: #2a160d;
  background: linear-gradient(135deg, var(--honey), var(--caramel));
}

.my-recipe-actions .danger {
  color: #8a2d1b;
  background: rgba(238, 201, 189, 0.42);
}

.my-recipe-actions .danger.solid {
  color: var(--cream);
  background: #8a2d1b;
}

.delete-confirm {
  display: inline-flex;
  flex-wrap: wrap;
  gap: 8px;
}

.empty-note {
  margin-top: 22px;
  text-align: center;
}

@media (max-width: 680px) {
  .my-recipe-actions {
    border-radius: 22px;
  }

  .my-recipe-actions button,
  .share-badge,
  .delete-confirm {
    flex: 1 1 auto;
    justify-content: center;
    text-align: center;
  }
}
</style>
