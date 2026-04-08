import api from './api'

// ── Auth ──────────────────────────────────────────────────────────────────────
export const authService = {
  register: (data)        => api.post('/auth/register', data),
  login:    (data)        => api.post('/auth/login', data),
  logout:   (refreshToken)=> api.post('/auth/logout', { refreshToken }),
  refresh:  (refreshToken)=> api.post('/auth/refresh', { refreshToken }),
}

// ── Products ──────────────────────────────────────────────────────────────────
export const productService = {
  getAll:      (params) => api.get('/products', { params }),
  getById:     (id)     => api.get(`/products/${id}`),
  search:      (params) => api.get('/products/search', { params }),
  getTopSelling: ()     => api.get('/products/top-selling'),
  create:      (data)   => api.post('/products', data),
  update:      (id, data)=> api.put(`/products/${id}`, data),
  delete:      (id)     => api.delete(`/products/${id}`),
}

// ── Categories ────────────────────────────────────────────────────────────────
export const categoryService = {
  getTree: () => api.get('/categories'),
  create:  (data)    => api.post('/categories', data),
  update:  (id, data)=> api.put(`/categories/${id}`, data),
  delete:  (id)      => api.delete(`/categories/${id}`),
}

// ── Cart ──────────────────────────────────────────────────────────────────────
export const cartService = {
  get:          ()          => api.get('/cart'),
  addItem:      (data)      => api.post('/cart/items', data),
  updateItem:   (itemId, data)=> api.put(`/cart/items/${itemId}`, data),
  removeItem:   (itemId)    => api.delete(`/cart/items/${itemId}`),
  applyCoupon:  (code)      => api.post('/cart/coupon', { code }),
  removeCoupon: ()          => api.delete('/cart/coupon'),
}

// ── Orders ────────────────────────────────────────────────────────────────────
export const orderService = {
  create:       (data)   => api.post('/orders', data),
  getById:      (id)     => api.get(`/orders/${id}`),
  getMyOrders:  (params) => api.get('/orders/my', { params }),
  getAll:       (params) => api.get('/orders', { params }),
  getSellerOrders:(params)=> api.get('/orders/seller', { params }),
  updateStatus: (id, data)=> api.put(`/orders/${id}/status`, data),
  cancel:       (id)     => api.put(`/orders/${id}/cancel`),
}

// ── Reviews ───────────────────────────────────────────────────────────────────
export const reviewService = {
  create:     (data)      => api.post('/reviews', data),
  getByProduct:(productId, params) => api.get(`/reviews/product/${productId}`, { params }),
  approve:    (id)        => api.put(`/reviews/${id}/approve`),
  getPending: (params)    => api.get('/reviews/pending', { params }),
}

// ── Coupons ───────────────────────────────────────────────────────────────────
export const couponService = {
  getAll:   ()         => api.get('/coupons'),
  create:   (data)     => api.post('/coupons', data),
  update:   (id, data) => api.put(`/coupons/${id}`, data),
  delete:   (id)       => api.delete(`/coupons/${id}`),
  validate: (code)     => api.get(`/coupons/validate/${code}`),
}

// ── Dashboard ─────────────────────────────────────────────────────────────────
export const dashboardService = {
  getAdmin:    () => api.get('/dashboard/admin'),
  getSeller:   () => api.get('/dashboard/seller'),
  getCustomer: () => api.get('/dashboard/customer'),
}

// ── Addresses ─────────────────────────────────────────────────────────────────
export const addressService = {
  getAll:  ()         => api.get('/addresses'),
  create:  (data)     => api.post('/addresses', data),
  delete:  (id)       => api.delete(`/addresses/${id}`),
}