import { Link } from 'react-router-dom'
import { ShoppingCart } from 'lucide-react'
import { RatingStars } from '../ui'
import { cartService } from '../../services/services'
import useCartStore from '../../context/cartStore'
import useAuthStore from '../../context/authStore'
import toast from 'react-hot-toast'
import { useState } from 'react'

export default function ProductCard({ product }) {
  const { setCart } = useCartStore()
  const { isAuthenticated } = useAuthStore()
  const [adding, setAdding] = useState(false)

  const price     = product.enPromotion ? product.prixPromo : product.prix
  const imageUrl  = product.images?.[0] || null

  const addToCart = async e => {
    e.preventDefault()
    if (!isAuthenticated()) { toast.error('Connectez-vous pour ajouter au panier'); return }
    setAdding(true)
    try {
      const { data } = await cartService.addItem({ productId: product.id, quantite: 1 })
      setCart(data)
      toast.success('Ajouté au panier')
    } catch (err) {
      toast.error(err.response?.data?.message || 'Erreur')
    } finally { setAdding(false) }
  }

  return (
    <Link to={`/produit/${product.id}`} style={{ display: 'block', textDecoration: 'none' }}>
      <div style={{
        background: 'var(--bg2)',
        border: '1px solid var(--border)',
        borderRadius: 'var(--radius-lg)',
        overflow: 'hidden',
        transition: 'border-color 0.2s, transform 0.2s',
        cursor: 'pointer',
      }}
        onMouseEnter={e => { e.currentTarget.style.borderColor = 'var(--border2)'; e.currentTarget.style.transform = 'translateY(-2px)' }}
        onMouseLeave={e => { e.currentTarget.style.borderColor = 'var(--border)'; e.currentTarget.style.transform = 'translateY(0)' }}
      >
        {/* Image */}
        <div style={{ height: 200, background: 'var(--bg3)', position: 'relative', overflow: 'hidden' }}>
          {imageUrl
            ? <img src={imageUrl} alt={product.nom} style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
            : <div style={{ width: '100%', height: '100%', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 40 }}>📦</div>
          }
          {product.enPromotion && (
            <span className="badge badge-red" style={{ position: 'absolute', top: 10, left: 10 }}>
              -{Math.round(product.pourcentageRemise)}%
            </span>
          )}
        </div>

        {/* Info */}
        <div style={{ padding: '14px 16px 16px' }}>
          <p style={{ fontSize: 13, color: 'var(--text3)', marginBottom: 4, textTransform: 'uppercase', letterSpacing: '0.05em' }}>
            {product.nomBoutique || 'ShopFlow'}
          </p>
          <h3 style={{ fontSize: 15, fontFamily: 'var(--font-head)', fontWeight: 600, marginBottom: 8, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
            {product.nom}
          </h3>

          {product.noteMoyenne && (
            <div style={{ marginBottom: 10, display: 'flex', alignItems: 'center', gap: 6 }}>
              <RatingStars note={Math.round(product.noteMoyenne)} size={12} />
              <span style={{ fontSize: 12, color: 'var(--text3)' }}>{product.noteMoyenne.toFixed(1)}</span>
            </div>
          )}

          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
            <div>
              <span style={{ fontSize: 18, fontWeight: 700, color: product.enPromotion ? 'var(--accent)' : 'var(--text)' }}>
                {Number(price).toFixed(2)} TND
              </span>
              {product.enPromotion && (
                <span style={{ fontSize: 13, color: 'var(--text3)', textDecoration: 'line-through', marginLeft: 6 }}>
                  {Number(product.prix).toFixed(2)}
                </span>
              )}
            </div>

            <button
              onClick={addToCart}
              disabled={adding || product.stock === 0}
              style={{
                background: 'var(--accent)',
                color: 'var(--bg)',
                border: 'none',
                borderRadius: 8,
                padding: '8px 10px',
                cursor: product.stock === 0 ? 'not-allowed' : 'pointer',
                opacity: product.stock === 0 ? 0.4 : 1,
                transition: 'opacity 0.2s, transform 0.1s',
                display: 'flex', alignItems: 'center',
              }}
              onMouseEnter={e => { if (product.stock > 0) e.currentTarget.style.transform = 'scale(1.1)' }}
              onMouseLeave={e => e.currentTarget.style.transform = 'scale(1)'}
            >
              <ShoppingCart size={16} />
            </button>
          </div>

          {product.stock === 0 && <p style={{ fontSize: 11, color: 'var(--red)', marginTop: 6 }}>Rupture de stock</p>}
          {product.stock > 0 && product.stock <= 5 && <p style={{ fontSize: 11, color: 'var(--accent)', marginTop: 6 }}>Plus que {product.stock} en stock</p>}
        </div>
      </div>
    </Link>
  )
}