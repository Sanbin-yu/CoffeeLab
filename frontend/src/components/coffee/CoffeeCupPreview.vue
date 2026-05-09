<template>
  <section class="cup-stage instagram-stage" :class="[recipe.cupType, { pulse: pulseKey }]">
    <div class="grain-layer"></div>
    <div class="ambient-orb orb-one"></div>
    <div class="ambient-orb orb-two"></div>
    <div class="ambient-orb orb-three"></div>
    <div class="story-strip">
      <span>01</span>
      <span>brew</span>
      <span>mix</span>
      <span>share</span>
    </div>
    <div v-if="recipe.cupType === 'hotCup'" class="steam">
      <span></span>
      <span></span>
      <span></span>
    </div>
    <div class="cup-polaroid">
      <div class="polaroid-pin"></div>
      <div class="cup-wrap">
      <div class="cup" :class="recipe.cupType">
        <div class="cup-shine"></div>
        <div class="pour-stream"></div>
        <div class="liquid coffee" :style="coffeeStyle"></div>
        <div v-if="recipe.milkType !== 'none'" class="liquid milk" :style="milkStyle"></div>
        <div v-for="syrup in recipe.syrups" :key="syrup" class="syrup-ribbon" :class="syrup"></div>
        <div v-if="showIce" class="ice-layer">
          <span v-for="cube in iceCubes" :key="cube" :style="{ '--i': cube }"></span>
        </div>
        <div v-if="recipe.foam !== 'none'" class="foam" :class="recipe.foam"></div>
        <div v-if="recipe.toppings.length" class="toppings">
          <span v-for="top in recipe.toppings" :key="top" :class="top"></span>
        </div>
      </div>
      <div v-if="recipe.cupType === 'hotCup'" class="mug-handle"></div>
      <div class="saucer"></div>
      </div>
      <div class="cup-shadow-note">
        <span>fresh layer</span>
        <span>{{ recipe.cupType === 'coldCup' ? 'iced gloss' : 'warm steam' }}</span>
      </div>
    </div>
    <div class="preview-caption">
      <div>
        <small>current recipe</small>
        <p>{{ recipe.name || '未命名配方' }}</p>
      </div>
      <span>{{ caption }}</span>
    </div>
  </section>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  recipe: { type: Object, required: true },
  summary: { type: Object, required: true },
  pulseKey: { type: Number, required: true }
})

const coffeeStyle = computed(() => ({
  height: `${28 + props.recipe.espressoShots * 8}%`,
  opacity: props.recipe.coffeeBase === 'americano' ? 0.7 : 0.92
}))

const milkStyle = computed(() => ({
  height: props.recipe.milkType === 'thickMilk' ? '48%' : '42%',
  '--milk-color': props.recipe.milkType === 'oatMilk' ? '#ead8b8' : props.recipe.milkType === 'coconutMilk' ? '#fff8eb' : '#f4dfc1'
}))

const iceCubes = computed(() => ({ noIce: 0, lessIce: 3, normalIce: 5, extraIce: 8 }[props.recipe.iceLevel] || 0))
const showIce = computed(() => props.recipe.cupType === 'coldCup' && iceCubes.value > 0)
const caption = computed(() => `${props.summary.coffeeBase} / ${props.summary.milkType} / ${props.summary.sweetness}`)
</script>
