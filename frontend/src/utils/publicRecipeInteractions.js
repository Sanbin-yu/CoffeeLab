export const getPublicRecipeId = (item) => item?.publicRecipeId ?? item?.id

const toNumber = (value, fallback = 0) => {
  const number = Number(value)
  return Number.isFinite(number) ? number : fallback
}

const clampScore = (score) => Math.min(5, Math.max(1, toNumber(score, 5)))

export const calculateHotScore = (item) =>
  Number((
    toNumber(item.averageRating) * 40 +
    toNumber(item.ratingCount) * 2 +
    toNumber(item.triedCount) * 1.5 +
    toNumber(item.favoriteCount) * 2 +
    toNumber(item.forkCount) * 2
  ).toFixed(1))

const withHotScore = (item) => ({
  ...item,
  hotScore: calculateHotScore(item)
})

export const updateRecipeById = (items, publicRecipeId, updater) =>
  items.map((item) => {
    if (getPublicRecipeId(item) !== publicRecipeId) return item
    return withHotScore(updater({ ...item }))
  })

export const applyRating = (item, score, previousScore = 0) => {
  const nextScore = clampScore(score)
  const oldScore = toNumber(previousScore)
  const currentAverage = toNumber(item.averageRating)
  const currentCount = toNumber(item.ratingCount)
  const nextCount = oldScore > 0 ? Math.max(currentCount, 1) : currentCount + 1
  const previousTotal = currentAverage * currentCount - (oldScore > 0 ? oldScore : 0)
  const nextAverage = (previousTotal + nextScore) / Math.max(nextCount, 1)

  return {
    ...item,
    averageRating: Number(nextAverage.toFixed(1)),
    ratingCount: nextCount
  }
}

export const applyTried = (item) => ({
  ...item,
  triedCount: toNumber(item.triedCount) + 1
})

export const applyFavorite = (item, shouldFavorite) => ({
  ...item,
  favoriteCount: Math.max(0, toNumber(item.favoriteCount) + (shouldFavorite ? 1 : -1))
})

export const applyFork = (item) => ({
  ...item,
  forkCount: toNumber(item.forkCount) + 1
})

export const sortPublicRecipes = (items, sort) => {
  const sorted = [...items]
  const score = (item) => toNumber(item.hotScore)

  if (sort === 'rating') {
    return sorted.sort((a, b) => toNumber(b.averageRating) - toNumber(a.averageRating))
  }

  if (sort === 'tried') {
    return sorted.sort((a, b) => toNumber(b.triedCount) - toNumber(a.triedCount))
  }

  if (sort === 'favorite') {
    return sorted.sort((a, b) => toNumber(b.favoriteCount) - toNumber(a.favoriteCount))
  }

  if (sort === 'hot') {
    return sorted.sort((a, b) => score(b) - score(a))
  }

  return sorted
}
