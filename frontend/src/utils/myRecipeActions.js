export const getMyRecipeId = (item) => item?.recipeId ?? item?.id

export const recipeTitle = (item) => item?.recipeName || item?.name || 'CoffeeLab 配方'

const localId = () => `local-${Date.now()}-${Math.random().toString(16).slice(2, 8)}`

export const applyPublishState = (items, recipeId, isPublic) =>
  items.map((item) => (getMyRecipeId(item) === recipeId ? { ...item, isPublic } : item))

export const removeRecipeById = (items, recipeId) =>
  items.filter((item) => getMyRecipeId(item) !== recipeId)

export const applyCopyRecipe = (items, recipe, created = {}) => {
  const copied = {
    ...recipe,
    ...created,
    id: created.id ?? localId(),
    recipeId: created.recipeId,
    name: created.name || `${recipeTitle(recipe)} Copy`,
    recipeName: created.recipeName || created.name || `${recipeTitle(recipe)} Copy`,
    isPublic: false,
    createdAt: created.createdAt || new Date().toISOString()
  }

  return [copied, ...items]
}

export const toDiyTemplate = (recipe) => ({
  ...recipe,
  name: recipeTitle(recipe),
  isPublic: false
})
