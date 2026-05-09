import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import { defaultRecipe, ingredientGroups } from '../data/coffeeLabMock'

const labelOf = (key, value) => {
  const group = ingredientGroups.find((item) => item.key === key)
  const option = group?.options.find((item) => item.value === value)
  return option?.label || value
}

export const useDiyRecipeStore = defineStore('diyRecipe', () => {
  const recipe = ref(structuredClone(defaultRecipe))
  const pulseKey = ref(0)

  const selectOption = (group, value) => {
    if (group.mode === 'multi') {
      const current = new Set(recipe.value[group.key])
      if (current.has(value)) current.delete(value)
      else current.add(value)
      recipe.value[group.key] = [...current]
    } else {
      recipe.value[group.key] = value
      if (group.key === 'cupType') {
        recipe.value.temperatureType = value === 'hotCup' ? 'hot' : 'cold'
        if (value === 'hotCup') recipe.value.iceLevel = 'noIce'
      }
    }
    pulseKey.value += 1
  }

  const updateField = (key, value) => {
    recipe.value[key] = value
  }

  const resetRecipe = () => {
    recipe.value = structuredClone(defaultRecipe)
    pulseKey.value += 1
  }

  const loadRecipeTemplate = (template) => {
    recipe.value = {
      ...structuredClone(defaultRecipe),
      ...template,
      name: template.name?.replace(/^Classic\s+/i, '') || ''
    }
    pulseKey.value += 1
  }

  const selectedSummary = computed(() => ({
    cupType: labelOf('cupType', recipe.value.cupType),
    coffeeBase: labelOf('coffeeBase', recipe.value.coffeeBase),
    espressoShots: labelOf('espressoShots', recipe.value.espressoShots),
    milkType: labelOf('milkType', recipe.value.milkType),
    sweetness: labelOf('sweetness', recipe.value.sweetness),
    iceLevel: labelOf('iceLevel', recipe.value.iceLevel),
    foam: labelOf('foam', recipe.value.foam),
    syrups: recipe.value.syrups.map((item) => labelOf('syrups', item)),
    toppings: recipe.value.toppings.map((item) => labelOf('toppings', item))
  }))

  const flavorRadar = computed(() => {
    const sweetnessScore = { noSugar: 0, lowSugar: 1, halfSugar: 2, lessSugar: 3, fullSugar: 5 }[recipe.value.sweetness]
    const milkiness = recipe.value.milkType === 'none' ? 0 : recipe.value.milkType === 'thickMilk' ? 5 : 4
    const freshness = recipe.value.cupType === 'coldCup' ? ({ noIce: 1, lessIce: 2, normalIce: 4, extraIce: 5 }[recipe.value.iceLevel] || 3) : 1
    return {
      bitterness: Math.min(5, recipe.value.espressoShots + (recipe.value.coffeeBase === 'espresso' ? 1 : 0)),
      sweetness: Math.min(5, sweetnessScore + Math.min(2, recipe.value.syrups.length)),
      acidity: recipe.value.coffeeBase === 'americano' ? 3 : 1,
      milkiness,
      richness: Math.min(5, recipe.value.espressoShots + recipe.value.syrups.length + (recipe.value.foam === 'none' ? 0 : 1)),
      freshness
    }
  })

  const flavorTags = computed(() => {
    const tags = []
    if (recipe.value.cupType === 'coldCup') tags.push('冰爽')
    if (recipe.value.cupType === 'hotCup') tags.push('温热')
    if (recipe.value.milkType !== 'none') tags.push('奶香浓郁')
    if (recipe.value.sweetness === 'noSugar' || recipe.value.sweetness === 'lowSugar') tags.push('低糖')
    if (recipe.value.espressoShots >= 3) tags.push('高咖啡因')
    if (recipe.value.syrups.includes('caramel') || recipe.value.syrups.includes('seaSaltCaramel')) tags.push('焦糖风味')
    if (recipe.value.syrups.includes('hazelnut')) tags.push('榛果风味')
    return tags.slice(0, 6)
  })

  const apiPayload = computed(() => ({
    ...recipe.value,
    flavorTags: flavorTags.value,
    flavorRadar: flavorRadar.value
  }))

  return {
    recipe,
    pulseKey,
    selectedSummary,
    flavorRadar,
    flavorTags,
    apiPayload,
    selectOption,
    updateField,
    resetRecipe,
    loadRecipeTemplate
  }
})
