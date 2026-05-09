import { describe, expect, it } from 'vitest'
import {
  applyCopyRecipe,
  applyPublishState,
  getMyRecipeId,
  removeRecipeById,
  toDiyTemplate
} from '../myRecipeActions'

describe('myRecipeActions', () => {
  it('toggles a private recipe into public state without mutating the original list', () => {
    const recipes = [{ id: 1001, name: 'Caramel oat', isPublic: false }]

    const updated = applyPublishState(recipes, 1001, true)

    expect(updated).toEqual([{ id: 1001, name: 'Caramel oat', isPublic: true }])
    expect(recipes[0].isPublic).toBe(false)
  })

  it('adds a copied recipe at the front with a distinct id and private status', () => {
    const recipes = [{ id: 1001, name: 'Caramel oat', isPublic: true }]

    const updated = applyCopyRecipe(recipes, recipes[0], { id: 1002 })

    expect(updated[0]).toMatchObject({
      id: 1002,
      name: 'Caramel oat Copy',
      isPublic: false
    })
    expect(updated).toHaveLength(2)
  })

  it('removes only the selected recipe', () => {
    const recipes = [{ id: 1001 }, { id: 1002 }]

    expect(removeRecipeById(recipes, 1001)).toEqual([{ id: 1002 }])
  })

  it('normalizes alternate id fields and builds a DIY template from a recipe card', () => {
    const recipe = {
      recipeId: 9,
      recipeName: 'Cold brew card',
      isPublic: true,
      cupType: 'coldCup'
    }

    expect(getMyRecipeId(recipe)).toBe(9)
    expect(toDiyTemplate(recipe)).toMatchObject({
      name: 'Cold brew card',
      isPublic: false,
      cupType: 'coldCup'
    })
  })
})
