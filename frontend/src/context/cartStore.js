import { create } from 'zustand'
import { cartService } from '../services/services'

const useCartStore = create((set, get) => ({
  cart: null,
  loading: false,

  fetchCart: async () => {
    set({ loading: true })
    try {
      const { data } = await cartService.get()
      set({ cart: data })
    } catch (e) {
      // non connecté : pas de panier
    } finally {
      set({ loading: false })
    }
  },

  itemCount: () => {
    const cart = get().cart
    if (!cart?.items) return 0
    return cart.items.reduce((sum, i) => sum + i.quantite, 0)
  },

  setCart: (cart) => set({ cart }),
}))

export default useCartStore