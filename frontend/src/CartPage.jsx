import { Link, useNavigate } from 'react-router-dom'
import { Trash2, ShoppingBag, ArrowRight } from 'lucide-react'
import useCartStore from './context/cartStore'
import { Button, Card } from './components/ui'

export default function CartPage() {
  const { cart, setCart } = useCartStore()
  const navigate = useNavigate()

  if (!cart || cart.items?.length === 0) {
    return (
      <div className="container" style={{ padding: '80px 0', textAlign: 'center' }}>
        <div style={{ fontSize: 64, marginBottom: 20 }}>🛒</div>
        <h1>Votre panier est vide</h1>
        <p style={{ color: 'var(--text3)', marginBottom: 32 }}>Il semble que vous n'ayez pas encore ajouté d'articles.</p>
        <Link to="/catalogue"><Button>Découvrir nos produits</Button></Link>
      </div>
    )
  }

  return (
    <div className="container" style={{ padding: '40px 0' }}>
      <h1 style={{ marginBottom: 32 }}>Mon Panier</h1>
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 350px', gap: 32 }}>
        <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
          {cart.items.map(item => (
            <Card key={item.id} style={{ padding: 16, display: 'flex', gap: 16, alignItems: 'center' }}>
              <img src={item.produitImage} alt="" style={{ width: 80, height: 80, borderRadius: 8, objectFit: 'cover' }} />
              <div style={{ flex: 1 }}>
                <h4 style={{ marginBottom: 4 }}>{item.produitNom}</h4>
                <p style={{ fontSize: 13, color: 'var(--text3)' }}>{item.variantInfo || 'Standard'}</p>
                <p style={{ fontWeight: 600, marginTop: 4 }}>{item.prixUnitaire.toFixed(2)} TND</p>
              </div>
              <div style={{ textAlign: 'right' }}>
                <p style={{ fontSize: 14, marginBottom: 8 }}>Qté: {item.quantite}</p>
                <Button variant="ghost" size="sm" style={{ color: 'var(--red)' }}>
                  <Trash2 size={16} />
                </Button>
              </div>
            </Card>
          ))}
        </div>

        <aside>
          <Card style={{ padding: 24, position: 'sticky', top: 100 }}>
            <h3 style={{ marginBottom: 20 }}>Résumé</h3>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 12 }}>
              <span>Sous-total</span>
              <span>{cart.total.toFixed(2)} TND</span>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 24, fontWeight: 700, fontSize: 18 }}>
              <span>Total</span>
              <span style={{ color: 'var(--accent)' }}>{cart.total.toFixed(2)} TND</span>
            </div>
            <Button onClick={() => navigate('/checkout')} size="lg" style={{ width: '100%' }}>
              Passer la commande <ArrowRight size={18} />
            </Button>
          </Card>
        </aside>
      </div>
    </div>
  )
}