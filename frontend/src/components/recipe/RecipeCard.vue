<template>
  <article class="recipe-card recipe-postcard" :class="{ 'is-interactive': interactive }">
    <div class="film-perf" aria-hidden="true">
      <span v-for="dot in 8" :key="dot"></span>
    </div>

    <div class="recipe-photo">
      <span v-if="rankLabel" class="rank-sticker">{{ rankLabel }}</span>
      <div class="mini-cup" :class="item.cupType || item.temperatureType">
        <span></span>
      </div>
      <i aria-hidden="true"></i>
    </div>

    <div class="recipe-copy">
      <div class="recipe-heading">
        <p class="eyebrow">{{ authorLabel }}</p>
        <span class="postage-stamp">CL</span>
      </div>

      <h3>{{ title }}</h3>

      <div v-if="tags.length" class="tag-row">
        <span v-for="tag in tags" :key="tag">{{ tag }}</span>
      </div>

      <div class="metric-strip" aria-label="配方互动数据">
        <span>
          <b>{{ ratingLabel }}</b>
          评分
        </span>
        <span>
          <b>{{ formatCount(item.triedCount) }}</b>
          尝试
        </span>
        <span>
          <b>{{ formatCount(item.favoriteCount) }}</b>
          收藏
        </span>
        <span>
          <b>{{ formatCount(item.forkCount) }}</b>
          复刻
        </span>
      </div>

      <div v-if="interactive" class="recipe-actions" aria-label="配方操作">
        <div class="rating-dots" role="group" aria-label="给配方评分">
          <button
            v-for="score in 5"
            :key="score"
            type="button"
            class="rating-dot"
            :class="{ active: selectedRating >= score, 'is-busy': busyAction === 'rate' }"
            :disabled="busyAction === 'rate'"
            :aria-label="`评分 ${score} 分`"
            :aria-pressed="selectedRating === score"
            @click="emit('rate', score)"
          >
            <span></span>
          </button>
        </div>

        <div class="button-row">
          <button
            type="button"
            class="action-chip"
            :class="{ 'is-busy': busyAction === 'try' }"
            :disabled="busyAction === 'try'"
            @click="emit('try')"
          >
            已尝试
          </button>
          <button
            type="button"
            class="action-chip favorite-chip"
            :class="{ active: favorited, 'is-busy': busyAction === 'favorite' }"
            :disabled="busyAction === 'favorite'"
            @click="emit('favorite')"
          >
            {{ favorited ? '已收藏' : '收藏' }}
          </button>
          <button
            type="button"
            class="action-chip fork-chip"
            :class="{ 'is-busy': busyAction === 'fork' }"
            :disabled="busyAction === 'fork'"
            @click="emit('fork')"
          >
            复刻
          </button>
        </div>
      </div>
    </div>
  </article>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  item: { type: Object, required: true },
  interactive: { type: Boolean, default: false },
  busyAction: { type: String, default: '' },
  favorited: { type: Boolean, default: false },
  selectedRating: { type: Number, default: 0 }
})

const emit = defineEmits(['rate', 'try', 'favorite', 'fork'])

const title = computed(() => props.item.recipeName || props.item.name || '未命名配方')
const tags = computed(() => Array.isArray(props.item.flavorTags) ? props.item.flavorTags : [])
const authorLabel = computed(() => {
  if (props.item.authorName) return `BY ${props.item.authorName}`
  return props.item.isPublic ? 'PUBLIC RECIPE' : 'PRIVATE RECIPE'
})
const rankLabel = computed(() => {
  const rank = props.item.rank || props.item.hotRank
  return rank ? `#${rank}` : ''
})
const ratingLabel = computed(() => {
  const rating = Number(props.item.averageRating)
  return Number.isFinite(rating) && rating > 0 ? rating.toFixed(1) : 'NEW'
})

function formatCount(value) {
  const count = Number(value)
  if (!Number.isFinite(count) || count <= 0) return '0'
  if (count >= 10000) return `${(count / 10000).toFixed(1)}w`
  if (count >= 1000) return `${(count / 1000).toFixed(1)}k`
  return String(count)
}
</script>

<style scoped>
.recipe-postcard {
  grid-template-columns: 104px minmax(0, 1fr);
  gap: 20px;
  padding: 18px 18px 18px 24px;
  border-color: rgba(255, 255, 255, 0.78);
  background:
    radial-gradient(circle at 92% 12%, rgba(238, 201, 189, 0.56), transparent 8rem),
    radial-gradient(circle at 7% 88%, rgba(189, 211, 209, 0.42), transparent 7rem),
    linear-gradient(145deg, rgba(255, 250, 241, 0.82), rgba(255, 247, 234, 0.5));
}

.recipe-postcard::before {
  position: absolute;
  inset: 10px;
  pointer-events: none;
  border: 1px dashed rgba(58, 33, 23, 0.12);
  border-radius: 24px;
  content: "";
}

.recipe-postcard::after {
  opacity: 0.72;
  filter: blur(1px);
}

.film-perf {
  position: absolute;
  top: 14px;
  bottom: 14px;
  left: 7px;
  display: grid;
  align-content: space-between;
  pointer-events: none;
}

.film-perf span {
  width: 8px;
  height: 14px;
  border-radius: 999px;
  background: rgba(93, 55, 33, 0.14);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.5);
}

.recipe-photo,
.recipe-copy {
  position: relative;
  z-index: 1;
}

.recipe-photo {
  display: grid;
  min-height: 128px;
  place-items: center;
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.82);
  border-radius: 26px;
  background:
    radial-gradient(circle at 35% 24%, rgba(255, 255, 255, 0.86), transparent 2.2rem),
    linear-gradient(145deg, rgba(255, 255, 255, 0.46), rgba(231, 183, 98, 0.28));
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.82), 0 18px 34px rgba(84, 45, 24, 0.12);
  transform: rotate(-1.5deg);
}

.recipe-photo .mini-cup {
  width: 74px;
  height: 98px;
  transform: rotate(3deg);
  transition: transform 0.26s ease;
}

.recipe-postcard:hover .recipe-photo .mini-cup {
  transform: translateY(-5px) rotate(-2deg);
}

.recipe-photo i {
  position: absolute;
  right: 12px;
  bottom: 12px;
  width: 38px;
  height: 38px;
  border: 1px solid rgba(58, 33, 23, 0.12);
  border-radius: 50%;
  background:
    radial-gradient(circle, rgba(58, 33, 23, 0.72) 13%, transparent 15%),
    rgba(255, 255, 255, 0.44);
}

.rank-sticker,
.postage-stamp {
  display: inline-grid;
  place-items: center;
  font-weight: 900;
}

.rank-sticker {
  position: absolute;
  top: 10px;
  left: 10px;
  z-index: 2;
  min-width: 38px;
  height: 30px;
  padding: 0 9px;
  border-radius: 999px;
  color: var(--cream);
  font-size: 13px;
  background: var(--espresso);
  box-shadow: 0 10px 22px rgba(58, 33, 23, 0.18);
  transform: rotate(-7deg);
}

.recipe-heading {
  display: flex;
  align-items: start;
  justify-content: space-between;
  gap: 12px;
}

.postage-stamp {
  width: 34px;
  height: 40px;
  flex: 0 0 auto;
  border: 1px solid rgba(58, 33, 23, 0.16);
  border-radius: 10px;
  color: rgba(58, 33, 23, 0.54);
  font-size: 11px;
  background:
    linear-gradient(135deg, rgba(255, 255, 255, 0.58), rgba(231, 183, 98, 0.26));
  transform: rotate(4deg);
}

.recipe-copy h3 {
  margin-bottom: 6px;
  color: var(--espresso);
  font-size: clamp(22px, 2.8vw, 31px);
  line-height: 0.96;
}

.metric-strip {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 8px;
  margin-top: 14px;
}

.metric-strip span {
  display: grid;
  gap: 2px;
  padding: 9px 8px;
  border: 1px solid rgba(58, 33, 23, 0.09);
  border-radius: 16px;
  color: rgba(38, 24, 18, 0.56);
  font-size: 11px;
  font-weight: 900;
  text-align: center;
  background: rgba(255, 255, 255, 0.38);
}

.metric-strip b {
  color: var(--espresso);
  font-size: 14px;
}

.recipe-actions {
  display: grid;
  gap: 12px;
  margin-top: 16px;
  padding-top: 14px;
  border-top: 1px dashed rgba(58, 33, 23, 0.16);
}

.rating-dots,
.button-row {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
}

.rating-dot,
.action-chip {
  position: relative;
  overflow: hidden;
  border: 1px solid rgba(58, 33, 23, 0.13);
  color: var(--ink);
  cursor: pointer;
  background: rgba(255, 255, 255, 0.52);
  box-shadow: 0 8px 18px rgba(85, 46, 25, 0.08);
  transition: transform 0.2s ease, border-color 0.2s ease, background 0.2s ease, box-shadow 0.2s ease;
}

.rating-dot {
  display: grid;
  width: 34px;
  height: 34px;
  place-items: center;
  border-radius: 50%;
}

.rating-dot span {
  width: 11px;
  height: 11px;
  border-radius: 50%;
  background: rgba(58, 33, 23, 0.18);
  transition: transform 0.2s ease, background 0.2s ease;
}

.rating-dot.active {
  border-color: rgba(199, 128, 61, 0.5);
  background: rgba(231, 183, 98, 0.28);
}

.rating-dot.active span {
  background: linear-gradient(135deg, var(--honey), var(--caramel));
  transform: scale(1.22);
}

.action-chip {
  min-height: 38px;
  padding: 9px 13px;
  border-radius: 999px;
  font-size: 13px;
  font-weight: 900;
}

.favorite-chip.active {
  border-color: rgba(199, 128, 61, 0.58);
  background: linear-gradient(135deg, rgba(238, 201, 189, 0.72), rgba(231, 183, 98, 0.38));
}

.fork-chip {
  background: rgba(189, 211, 209, 0.42);
}

.rating-dot:hover:not(:disabled),
.action-chip:hover:not(:disabled) {
  transform: translateY(-3px) rotate(-1deg);
  border-color: rgba(199, 128, 61, 0.56);
  box-shadow: 0 14px 30px rgba(85, 46, 25, 0.14);
}

.rating-dot:active:not(:disabled),
.action-chip:active:not(:disabled) {
  transform: translateY(0) scale(0.97);
}

.rating-dot:disabled,
.action-chip:disabled {
  cursor: wait;
  opacity: 0.68;
}

.rating-dot.is-busy,
.action-chip.is-busy {
  background-image: linear-gradient(110deg, transparent 0 35%, rgba(255, 255, 255, 0.64) 45%, transparent 55% 100%);
  background-size: 220% 100%;
  animation: actionWait 1.05s ease-in-out infinite;
}

@keyframes actionWait {
  to {
    background-position: -180% 0;
  }
}

@media (max-width: 680px) {
  .recipe-postcard {
    grid-template-columns: 1fr;
    padding-left: 22px;
  }

  .recipe-photo {
    min-height: 160px;
  }

  .metric-strip {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
</style>
