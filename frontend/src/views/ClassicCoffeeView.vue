<template>
  <main class="page-wrap classic-page">
    <PageHero
      eyebrow="Coffee moodboard"
      title="经典咖啡"
      description="不想从零开始时，先从经典风味里找灵感。每张卡片都像一条咖啡灵感动态，点击后可以进入 DIY 实验台继续改造。"
    />
    <section class="classic-showcase">
      <article
        v-for="(coffee, index) in coffees"
        :key="coffee.id"
        class="classic-card insta-classic-card"
        :style="{ '--delay': `${index * 80}ms` }"
      >
        <div class="classic-photo">
          <span class="photo-index">0{{ index + 1 }}</span>
          <div class="photo-glow"></div>
          <div class="mini-cup hotCup"><span></span></div>
          <i></i>
        </div>
        <div class="classic-content">
          <p class="eyebrow">CoffeeLab preset</p>
          <h3>{{ coffee.name }}</h3>
          <p>{{ coffee.description }}</p>
          <div class="classic-meta">
            <span>咖啡因 {{ coffee.caffeineLevel }}/5</span>
            <span>{{ coffee.suitableCrowd }}</span>
          </div>
          <div class="tag-row">
            <span v-for="tag in coffee.tags" :key="tag">{{ tag }}</span>
          </div>
          <RouterLink class="btn ghost" to="/diy" @click="useClassic(coffee)">一键使用并微调</RouterLink>
        </div>
      </article>
    </section>
  </main>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import PageHero from '../components/common/PageHero.vue'
import { classicCoffeeApi } from '../api'
import { classicCoffees as mockClassicCoffees } from '../data/coffeeLabMock'
import { useDiyRecipeStore } from '../stores/diyRecipe'

const coffees = ref(mockClassicCoffees)
const diyStore = useDiyRecipeStore()

const loadClassicCoffees = async () => {
  try {
    coffees.value = await classicCoffeeApi.list()
  } catch (error) {
    coffees.value = mockClassicCoffees
  }
}

const useClassic = (coffee) => {
  if (coffee.defaultRecipe) {
    diyStore.loadRecipeTemplate(coffee.defaultRecipe)
  }
}

onMounted(loadClassicCoffees)
</script>
