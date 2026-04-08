import { Link } from 'react-router-dom'

export function Footer() {
  return (
    <footer style={{ borderTop: '1px solid var(--border)', marginTop: 80, padding: '40px 0' }}>
      <div className="container" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: 16 }}>
        <span style={{ fontFamily: 'var(--font-head)', fontSize: 20, fontWeight: 800 }}>
          Shop<span style={{ color: 'var(--accent)' }}>Flow</span>
        </span>
        <div style={{ display: 'flex', gap: 24, fontSize: 13, color: 'var(--text3)' }}>
          <Link to="/catalogue">Catalogue</Link>
          <Link to="/login">Connexion</Link>
          <Link to="/register">Inscription</Link>
        </div>
        <span style={{ fontSize: 12, color: 'var(--text3)' }}>© 2025 ShopFlow. Tous droits réservés.</span>
      </div>
    </footer>
  )
}

export default function Layout({ children }) {
  return (
    <div style={{ minHeight: '100vh', display: 'flex', flexDirection: 'column' }}>
      <main style={{ flex: 1 }}>{children}</main>
      <Footer />
    </div>
  )
}