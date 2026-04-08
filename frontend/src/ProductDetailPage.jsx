import { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import { ShoppingCart, ArrowLeft, Star } from 'lucide-react'
import { productService, reviewService } from './services/services'
import { cartService } from './services/services'
import useCartStore from './context/cartStore'
import useAuthStore from './context/authStore'
import { Button, Spinner, RatingStars, OrderStatusBadge, Card } from './components/ui'
import toast from 'react-hot-toast'

export default function ProductDetailPage() {
  const { id } = useParams()
  const [product, setProduct] = useState(null)
  const [reviews, setReviews]   = useState([])
  const [loading, setLoading]   = useState(true)
  const [selectedVariant, setSelectedVariant] = useState(null)
  const [qty, setQty]           = useState(1)
  const [adding, setAdding]     = useState(false)
  const [activeImg, setActiveImg] = useState(0)
  const { setCart } = useCartStore()
  const { isAuthenticated } = useAuthStore()

  useEffect(() => {
    Promise.all([
      productService.getById(id),
      reviewService.getByProduct(id, { size: 5 }),
    ]).then(([p, r]) => {
      setProduct(p.data)
      setReviews(r.data.content || [])
    }).finally(() => setLoading(false))
  }, [id])

  const addToCart = async () => {
    if (!isAuthenticated()) { toast.error('Connectez-vous pour ajouter au panier'); return }
    setAdding(true)
    try {
      const { data } = await cartService.addItem({
        productId: product.id,
        variantId: selectedVariant?.id || null,
        quantite: qty,
      })
      setCart(data)
      toast.success('Ajouté au panier !')
    } catch (err) {
      toast.error(err.response?.data?.message || 'Erreur')
    } finally { setAdding(false) }
  }

  if (loading) return <div style={{ paddingTop: 80 }}><Spinner /></div>
  if (!product) return <div className="container" style={{ paddingTop: 80 }}>Produit introuvable.</div>

  const price = product.enPromotion ? product.prixPromo : product.prix
  const variantPrice = selectedVariant ? Number(price) + Number(selectedVariant.prixDelta) : Number(price)

  // Grouper les variantes par attribut
  const variantsByAttr = product.variants?.reduce((acc, v) => {
    if (!acc[v.attribut]) acc[v.attribut] = []
    acc[v.attribut].push(v)
    return acc
  }, {}) || {}

  return (
    <div className="container" style={{ paddingTop: 32, paddingBottom: 80 }}>
      <Link to="/catalogue" style={{ display: 'inline-flex', alignItems: 'center', gap: 6, fontSize: 13, color: 'var(--text3)', marginBottom: 32 }}>
        <ArrowLeft size={14} /> Retour au catalogue
      </Link>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 48 }}>
        {/* Images */}
        <div>
          <div style={{ background: 'var(--bg2)', border: '1px solid var(--border)', borderRadius: 'var(--radius-lg)', overflow: 'hidden', aspectRatio: '1', marginBottom: 12 }}>
            {product.images?.[activeImg]
              ? <img src={product.images[activeImg]} alt={product.nom} style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
              : <div style={{ width: '100%', height: '100%', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 80 }}>📦</div>
            }
          </div>
          {product.images?.length > 1 && (
            <div style={{ display: 'flex', gap: 8 }}>
              {product.images.map((img, i) => (
                <button key={i} onClick={() => setActiveImg(i)} style={{
                  width: 64, height: 64, borderRadius: 8, overflow: 'hidden',
                  border: `2px solid ${i === activeImg ? 'var(--accent)' : 'var(--border)'}`,
                  cursor: 'pointer', background: 'var(--bg3)', padding: 0,
                }}>
                  <img src={img} alt="" style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
                </button>
              ))}
            </div>
          )}
        </div>

        {/* Info */}
        <div>
          <p style={{ fontSize: 12, color: 'var(--text3)', textTransform: 'uppercase', letterSpacing: '0.06em', marginBottom: 8 }}>
            {product.nomBoutique || 'ShopFlow'}
          </p>
          <h1 style={{ fontSize: 32, marginBottom: 16 }}>{product.nom}</h1>

          {product.noteMoyenne && (
            <div style={{ display: 'flex', alignItems: 'center', gap: 10, marginBottom: 16 }}>
              <RatingStars note={Math.round(product.noteMoyenne)} size={16} />
              <span style={{ fontSize: 14, color: 'var(--text2)' }}>{product.noteMoyenne.toFixed(1)} ({reviews.length} avis)</span>
            </div>
          )}

          <div style={{ marginBottom: 24 }}>
            <span style={{ fontSize: 36, fontWeight: 700, color: product.enPromotion ? 'var(--accent)' : 'var(--text)', fontFamily: 'var(--font-head)' }}>
              {variantPrice.toFixed(2)} TND
            </span>
            {product.enPromotion && (
              <span style={{ fontSize: 18, color: 'var(--text3)', textDecoration: 'line-through', marginLeft: 12 }}>
                {Number(product.prix).toFixed(2)} TND
              </span>
            )}
            {product.enPromotion && (
              <span className="badge badge-red" style={{ marginLeft: 12 }}>-{Math.round(product.pourcentageRemise)}%</span>
            )}
          </div>

          <p style={{ fontSize: 15, color: 'var(--text2)', lineHeight: 1.8, marginBottom: 28 }}>{product.description}</p>

          {/* Variantes */}
          {Object.entries(variantsByAttr).map(([attr, variants]) => (
            <div key={attr} style={{ marginBottom: 20 }}>
              <p style={{ fontSize: 13, fontWeight: 600, marginBottom: 10, color: 'var(--text2)' }}>{attr}</p>
              <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap' }}>
                {variants.map(v => (
                  <button key={v.id} onClick={() => setSelectedVariant(selectedVariant?.id === v.id ? null : v)}
                    style={{
                      padding: '8px 16px', borderRadius: 8, fontSize: 14, cursor: 'pointer',
                      border: `1.5px solid ${selectedVariant?.id === v.id ? 'var(--accent)' : 'var(--border)'}`,
                      background: selectedVariant?.id === v.id ? 'rgba(232,197,71,0.1)' : 'var(--bg3)',
                      color: selectedVariant?.id === v.id ? 'var(--accent)' : 'var(--text2)',
                      transition: 'var(--trans)',
                    }}>
                    {v.valeur}
                    {v.prixDelta > 0 && <span style={{ fontSize: 11, marginLeft: 4 }}>+{v.prixDelta} TND</span>}
                  </button>
                ))}
              </div>
            </div>
          ))}

          {/* Quantité + Ajouter */}
          <div style={{ display: 'flex', gap: 12, alignItems: 'center', marginBottom: 20 }}>
            <div style={{ display: 'flex', alignItems: 'center', border: '1px solid var(--border)', borderRadius: 'var(--radius)', overflow: 'hidden' }}>
              <button onClick={() => setQty(q => Math.max(1, q - 1))} style={{ padding: '10px 14px', background: 'var(--bg3)', color: 'var(--text)', fontSize: 18, border: 'none', cursor: 'pointer' }}>−</button>
              <span style={{ padding: '10px 16px', fontSize: 15, minWidth: 40, textAlign: 'center' }}>{qty}</span>
              <button onClick={() => setQty(q => Math.min(product.stock, q + 1))} style={{ padding: '10px 14px', background: 'var(--bg3)', color: 'var(--text)', fontSize: 18, border: 'none', cursor: 'pointer' }}>+</button>
            </div>
            <Button onClick={addToCart} loading={adding} disabled={product.stock === 0} size="lg" style={{ flex: 1 }}>
              <ShoppingCart size={18} />
              {product.stock === 0 ? 'Rupture de stock' : 'Ajouter au panier'}
            </Button>
          </div>

          <p style={{ fontSize: 13, color: product.stock > 5 ? 'var(--green)' : product.stock > 0 ? 'var(--accent)' : 'var(--red)' }}>
            {product.stock === 0 ? '✗ Rupture de stock' : product.stock <= 5 ? `⚠ Plus que ${product.stock} en stock` : `✓ En stock (${product.stock})`}
          </p>
        </div>
      </div>

      {/* Avis */}
      {reviews.length > 0 && (
        <section style={{ marginTop: 64 }}>
          <h2 style={{ fontSize: 24, marginBottom: 24 }}>Avis clients</h2>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
            {reviews.map(r => (
              <Card key={r.id} style={{ padding: 20 }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 10 }}>
                  <div>
                    <p style={{ fontWeight: 600, marginBottom: 4 }}>{r.clientNom}</p>
                    <RatingStars note={r.note} size={13} />
                  </div>
                  <span style={{ fontSize: 12, color: 'var(--text3)' }}>
                    {new Date(r.dateCreation).toLocaleDateString('fr-FR')}
                  </span>
                </div>
                {r.commentaire && <p style={{ fontSize: 14, color: 'var(--text2)', marginTop: 8 }}>{r.commentaire}</p>}
              </Card>
            ))}
          </div>
        </section>
      )}
    </div>
  )
}