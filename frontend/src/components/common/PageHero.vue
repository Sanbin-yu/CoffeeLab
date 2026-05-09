<template>
  <section class="page-hero">
    <p class="eyebrow">{{ eyebrow }}</p>
    <h1>{{ title }}</h1>
    <p>{{ description }}</p>
  </section>
</template>

<script setup>
defineProps({
  eyebrow: { type: String, default: 'CoffeeLab' },
  title: { type: String, required: true },
  description: { type: String, required: true }
})
</script>
