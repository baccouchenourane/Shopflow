import { create } from 'zustand'

const useAuthStore = create((set, get) => ({
  user: JSON.parse(localStorage.getItem('user') || 'null'),
  accessToken: localStorage.getItem('accessToken') || null,

  setAuth: (data) => {
    const user = {
      email: data.email,
      role:  data.role,
      prenom: data.prenom,
      nom:   data.nom,
    }
    localStorage.setItem('accessToken',  data.accessToken)
    localStorage.setItem('refreshToken', data.refreshToken)
    localStorage.setItem('user', JSON.stringify(user))
    set({ user, accessToken: data.accessToken })
  },

  logout: () => {
    localStorage.clear()
    set({ user: null, accessToken: null })
  },

  isAuthenticated: () => !!get().accessToken,
  isAdmin:    () => get().user?.role === 'ADMIN',
  isSeller:   () => get().user?.role === 'SELLER',
  isCustomer: () => get().user?.role === 'CUSTOMER',
}))

export default useAuthStore