import { Users, Settings, BarChart3, ShieldAlert } from 'lucide-react'
import { Card } from './components/ui'

export default function AdminDashboard() {
  return (
    <div className="container" style={{ padding: '40px 0' }}>
      <h1 style={{ marginBottom: 32 }}>Administration Système</h1>
      
      <div style={{ display: 'grid', gridTemplateColumns: '250px 1fr', gap: 32 }}>
        <aside style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
          <Button variant="secondary" style={{ justifyContent: 'flex-start' }}><BarChart3 size={18} /> Vue d'ensemble</Button>
          <Button variant="ghost" style={{ justifyContent: 'flex-start' }}><Users size={18} /> Utilisateurs</Button>
          <Button variant="ghost" style={{ justifyContent: 'flex-start' }}><Settings size={18} /> Configuration</Button>
          <Button variant="ghost" style={{ justifyContent: 'flex-start', color: 'var(--red)' }}><ShieldAlert size={18} /> Sécurité</Button>
        </aside>

        <main>
          <Card style={{ padding: 32, textAlign: 'center' }}>
            <h2>Bienvenue dans l'interface Admin</h2>
            <p style={{ color: 'var(--text3)' }}>Sélectionnez une option dans le menu pour gérer la plateforme ShopFlow.</p>
          </Card>
        </main>
      </div>
    </div>
  )
}