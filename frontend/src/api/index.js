import apiClient from './client'

export const authApi = {
  register: (payload) => apiClient.post('/auth/register', payload),
  login: (payload) => apiClient.post('/auth/login', payload),
  getMe: () => apiClient.get('/users/me'),
  updateMe: (payload) => apiClient.put('/users/me', payload)
}

export const classicCoffeeApi = {
  list: () => apiClient.get('/classic-coffees'),
  detail: (id) => apiClient.get(`/classic-coffees/${id}`),
  recipeTemplate: (id) => apiClient.get(`/classic-coffees/${id}/recipe-template`)
}

export const recipeApi = {
  create: (payload) => apiClient.post('/recipes', payload),
  my: (params) => apiClient.get('/recipes/my', { params }),
  detail: (id) => apiClient.get(`/recipes/${id}`),
  update: (id, payload) => apiClient.put(`/recipes/${id}`, payload),
  remove: (id) => apiClient.delete(`/recipes/${id}`),
  copy: (id, payload) => apiClient.post(`/recipes/${id}/copy`, payload),
  publish: (id) => apiClient.post(`/recipes/${id}/publish`),
  unpublish: (id) => apiClient.post(`/recipes/${id}/unpublish`)
}

export const publicRecipeApi = {
  list: (params) => apiClient.get('/public-recipes', { params }),
  detail: (id) => apiClient.get(`/public-recipes/${id}`),
  rate: (id, payload) => apiClient.post(`/public-recipes/${id}/ratings`, payload),
  ratingSummary: (id) => apiClient.get(`/public-recipes/${id}/ratings/summary`),
  tryRecipe: (id) => apiClient.post(`/public-recipes/${id}/try`),
  favorite: (id) => apiClient.post(`/public-recipes/${id}/favorite`),
  unfavorite: (id) => apiClient.delete(`/public-recipes/${id}/favorite`),
  fork: (id, payload) => apiClient.post(`/public-recipes/${id}/fork`, payload)
}

export const rankingApi = {
  topRecipes: (params) => apiClient.get('/rankings/top-recipes', { params })
}
