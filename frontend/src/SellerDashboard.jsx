import { useState } from 'react'
import { Plus, Package, DollarSign, ShoppingCart } from 'lucide-react'
import { Card, Button } from './components/ui'

export default function SellerDashboard() {
  return (
    <div className="container" style={{ padding: '40px 0' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 32 }}>
        <h1>Tableau de bord Vendeur</h1>
        <Button><Plus size={18} /> Ajouter un produit</Button>
      </div>

      <div className="grid-3" style={{ marginBottom: 40 }}>
        <Card style={{ padding: 20 }}>
          <div style={{ display: 'flex', gap: 12 }}>
            <div style={{ padding: 10, background: 'rgba(232,197,71,0.1)', color: 'var(--accent)', borderRadius: 8 }}><DollarSign /></div>
            <div><p style={{ fontSize: 14, color: 'var(--text3)' }}>Chiffre d'affaires</p><h3>1,250 TND</h3></div>
          </div>
        </Card>
        <Card style={{ padding: 20 }}>
          <div style={{ display: 'flex', gap: 12 }}>
            <div style={{ padding: 10, background: 'rgba(74,222,128,0.1)', color: 'var(--green)', borderRadius: 8 }}><ShoppingCart /></div>
            <div><p style={{ fontSize: 14, color: 'var(--text3)' }}>Commandes</p><h3>24</h3></div>
          </div>
        </Card>
        <Card style={{ padding: 20 }}>
          <div style={{ display: 'flex', gap: 12 }}>
            <div style={{ padding: 10, background: 'rgba(96,165,250,0.1)', color: 'var(--blue)', borderRadius: 8 }}><Package /></div>
            <div><p style={{ fontSize: 14, color: 'var(--text3)' }}>Produits actifs</p><h3>12</h3></div>
          </div>
        </Card>
      </div>
      
      <h3>Mes produits récents</h3>
      {/* Liste des produits ici */}
    </div>
  )
}