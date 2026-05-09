import { describe, expect, it } from 'vitest'
import {
  applyFavorite,
  applyFork,
  applyRating,
  applyTried,
  sortPublicRecipes,
  updateRecipeById
} from '../publicRecipeInteractions'

describe('publicRecipeInteractions', () => {
  it('updates rating totals when a user rates for the first time', () => {
    const recipe = { averageRating: 4.5, ratingCount: 2 }

    expect(applyRating(recipe, 5)).toMatchObject({
      averageRating: 4.7,
      ratingCount: 3
    })
  })

  it('replaces the previous user score without increasing rating count', () => {
    const recipe = { averageRating: 4.5, ratingCount: 2 }

    expect(applyRating(recipe, 3, 5)).toMatchObject({
      averageRating: 3.5,
      ratingCount: 2
    })
  })

  it('keeps counters non-negative while applying public recipe actions', () => {
    expect(applyTried({ triedCount: 1 })).toMatchObject({ triedCount: 2 })
    expect(applyFavorite({ favoriteCount: 0 }, false)).toMatchObject({ favoriteCount: 0 })
    expect(applyFavorite({ favoriteCount: 0 }, true)).toMatchObject({ favoriteCount: 1 })
    expect(applyFork({ forkCount: 4 })).toMatchObject({ forkCount: 5 })
  })

  it('recalculates hotScore with the same formula used by the backend ranking', () => {
    const [updated] = updateRecipeById(
      [
        {
          id: 9,
          averageRating: 4.8,
          ratingCount: 24,
          triedCount: 118,
          favoriteCount: 36,
          forkCount: 14
        }
      ],
      9,
      (recipe) => recipe
    )

    expect(updated.hotScore).toBe(517)
  })

  it('sorts public recipes by recalculated hot score', () => {
    const items = [
      { id: 1, hotScore: 10 },
      { id: 2, hotScore: 30 },
      { id: 3, hotScore: 20 }
    ]

    expect(sortPublicRecipes(items, 'hot').map((item) => item.id)).toEqual([2, 3, 1])
  })
})
