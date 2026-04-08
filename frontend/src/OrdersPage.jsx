import { useEffect, useState } from 'react'
import { orderService } from './services/services'
import { Card, OrderStatusBadge, Spinner } from './components/ui'

export default function OrdersPage() {
  const [orders, setOrders] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    orderService.getMyOrders().then(res => setOrders(res.data)).finally(() => setLoading(false))
  }, [])

  if (loading) return <Spinner />

  return (
    <div className="container" style={{ padding: '40px 0' }}>
      <h1 style={{ marginBottom: 32 }}>Mes Commandes</h1>
      {orders.length === 0 ? (
        <p>Aucune commande effectuée pour le moment.</p>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: 20 }}>
          {orders.map(order => (
            <Card key={order.id} style={{ padding: 20 }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 16 }}>
                <div>
                  <span style={{ fontSize: 13, color: 'var(--text3)' }}>Commande #{order.id}</span>
                  <p style={{ fontWeight: 600 }}>{new Date(order.dateCreation).toLocaleDateString()}</p>
                </div>
                <OrderStatusBadge status={order.statut} />
              </div>
              <div style={{ borderTop: '1px solid var(--border)', paddingTop: 16 }}>
                <p><strong>Total:</strong> {order.total.toFixed(2)} TND</p>
              </div>
            </Card>
          ))}
        </div>
      )}
    </div>
  )
}