import axios from 'axios'

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 12000,
  headers: {
    'Content-Type': 'application/json'
  }
})

apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('coffeelab_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

apiClient.interceptors.response.use(
  (response) => {
    const payload = response.data
    if (payload && Object.prototype.hasOwnProperty.call(payload, 'code')) {
      if (payload.code !== 200) {
        return Promise.reject(new Error(payload.message || 'CoffeeLab API error'))
      }
      return payload.data
    }
    return payload
  },
  (error) => Promise.reject(error)
)

export default apiClient
