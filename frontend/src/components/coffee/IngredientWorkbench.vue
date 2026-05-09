<template>
  <section class="workbench">
    <div class="bench-heading">
      <p class="eyebrow">Ingredient bench</p>
      <h2>分类原料实验台</h2>
      <span>点击任一原料，右侧杯子会实时更新。</span>
    </div>

    <article v-for="group in groups" :key="group.key" class="ingredient-group">
      <div class="group-title">
        <h3>{{ group.title }}</h3>
        <span>{{ selectedText(group) }}</span>
      </div>
      <div class="ingredient-grid">
        <button
          v-for="option in group.options"
          :key="option.value"
          class="ingredient-card"
          :class="{ active: isActive(group, option.value) }"
          type="button"
          @click="$emit('select', group, option.value)"
        >
          <strong>{{ option.label }}</strong>
          <small>{{ option.description }}</small>
          <em>{{ option.impact }}</em>
        </button>
      </div>
    </article>
  </section>
</template>

<script setup>
const props = defineProps({
  groups: { type: Array, required: true },
  recipe: { type: Object, required: true },
  summary: { type: Object, required: true }
})

defineEmits(['select'])

const isActive = (group, value) => {
  const current = props.recipe[group.key]
  return Array.isArray(current) ? current.includes(value) : current === value
}

const selectedText = (group) => {
  const value = props.summary[group.key]
  return Array.isArray(value) ? value.join(' / ') || '未选择' : value || '未选择'
}
</script>
