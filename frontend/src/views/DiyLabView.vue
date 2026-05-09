<template>
  <main class="diy-page">
    <section class="diy-intro">
      <p class="eyebrow">DIY Coffee Studio</p>
      <h1>把一杯咖啡调成你的风格动态</h1>
      <p>左侧选择原料，右侧杯子实时生成分层、冰块、奶泡和顶料。每一次点击都会让画面重新呼吸一下。</p>
    </section>
    <section class="diy-layout">
      <IngredientWorkbench
        :groups="ingredientGroups"
        :recipe="store.recipe"
        :summary="store.selectedSummary"
        @select="store.selectOption"
      />

      <aside class="preview-panel">
        <CoffeeCupPreview :recipe="store.recipe" :summary="store.selectedSummary" :pulse-key="store.pulseKey" />
        <section class="recipe-console glass-card publish-card">
          <div class="publish-heading">
            <div>
              <p class="eyebrow">Post your cup</p>
              <h2>给这杯作品命名</h2>
            </div>
            <span>draft</span>
          </div>
          <div class="field-row">
            <label>
              配方名称
              <input :value="store.recipe.name" placeholder="例如：午夜榛果拿铁" @input="store.updateField('name', $event.target.value)" />
            </label>
          </div>
          <label>
            备注
            <textarea :value="store.recipe.note" placeholder="记录少奶、多咖啡或口感偏好" @input="store.updateField('note', $event.target.value)"></textarea>
          </label>
          <div class="tag-row">
            <span v-for="tag in store.flavorTags" :key="tag">{{ tag }}</span>
          </div>
          <p class="radar-title">Flavor mood</p>
          <div class="radar-bars">
            <div v-for="(value, key) in store.flavorRadar" :key="key">
              <span>{{ radarLabel[key] }}</span>
              <i><b :style="{ width: `${value * 20}%` }"></b></i>
            </div>
          </div>
          <div class="console-actions">
            <button class="btn primary" type="button" @click="saveRecipe(false)">保存到我的配方</button>
            <button class="btn ghost" type="button" @click="saveRecipe(true)">保存并分享</button>
            <button class="btn text" type="button" @click="store.resetRecipe">重置配方</button>
          </div>
          <p v-if="message" class="form-message">{{ message }}</p>
        </section>
      </aside>
    </section>
  </main>
</template>

<script setup>
import { ref } from 'vue'
import CoffeeCupPreview from '../components/coffee/CoffeeCupPreview.vue'
import IngredientWorkbench from '../components/coffee/IngredientWorkbench.vue'
import { recipeApi } from '../api'
import { ingredientGroups } from '../data/coffeeLabMock'
import { useDiyRecipeStore } from '../stores/diyRecipe'

const store = useDiyRecipeStore()
const message = ref('')
const radarLabel = {
  bitterness: '苦味',
  sweetness: '甜度',
  acidity: '酸度',
  milkiness: '奶香',
  richness: '浓郁',
  freshness: '清爽'
}

const saveRecipe = async (shareAfterSave) => {
  message.value = ''
  if (!store.apiPayload.name?.trim()) {
    message.value = '先给这杯作品取一个名字吧。'
    return
  }
  try {
    const created = await recipeApi.create(store.apiPayload)
    if (shareAfterSave) {
      await recipeApi.publish(created.id)
      message.value = '已保存并分享到灵感广场。'
    } else {
      message.value = '已保存到我的配方。'
    }
  } catch (error) {
    message.value = '暂时没有连接到后端，请登录并确认服务已启动。'
  }
}
</script>
