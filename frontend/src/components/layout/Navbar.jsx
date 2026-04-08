import { Link, useNavigate, useLocation } from 'react-router-dom'
import { ShoppingCart, User, Search, LogOut, LayoutDashboard, Package } from 'lucide-react'
import { useState } from 'react'
import useAuthStore from '../../context/authStore'
import useCartStore from '../../context/cartStore'
import toast from 'react-hot-toast'
import { authService } from '../../services/services'

export default function Navbar() {
  const { user, logout, isAuthenticated } = useAuthStore()
  const itemCount = useCartStore(s => s.itemCount())
  const navigate = useNavigate()
  const location = useLocation()
  const [search, setSearch] = useState('')
  const [menuOpen, setMenuOpen] = useState(false)

  const handleLogout = async () => {
    const rt = localStorage.getItem('refreshToken')
    try { if (rt) await authService.logout(rt) } catch {}
    logout()
    toast.success('Déconnecté')
    navigate('/')
  }

  const handleSearch = e => {
    e.preventDefault()
    if (search.trim()) {
      navigate(`/catalogue?q=${encodeURIComponent(search.trim())}`)
    }
  }

  const dashLink = user?.role === 'ADMIN' ? '/admin' : user?.role === 'SELLER' ? '/seller' : '/account'

  return (
    <nav style={{
      position: 'sticky', top: 0, zIndex: 100,
      background: 'rgba(10,10,10,0.92)',
      backdropFilter: 'blur(12px)',
      borderBottom: '1px solid var(--border)',
    }}>
      <div className="container" style={{ display: 'flex', alignItems: 'center', gap: 24, height: 64 }}>

        {/* Logo */}
        <Link to="/" style={{ fontFamily: 'var(--font-head)', fontSize: 22, fontWeight: 800, letterSpacing: '-0.03em', flexShrink: 0 }}>
          Shop<span style={{ color: 'var(--accent)' }}>Flow</span>
        </Link>

        {/* Search */}
        <form onSubmit={handleSearch} style={{ flex: 1, maxWidth: 420, position: 'relative' }}>
          <Search size={15} style={{ position: 'absolute', left: 12, top: '50%', transform: 'translateY(-50%)', color: 'var(--text3)' }} />
          <input
            value={search}
            onChange={e => setSearch(e.target.value)}
            placeholder="Rechercher un produit..."
            style={{
              width: '100%',
              background: 'var(--bg3)',
              border: '1px solid var(--border)',
              borderRadius: 'var(--radius)',
              color: 'var(--text)',
              padding: '8px 14px 8px 36px',
              fontSize: 14,
              outline: 'none',
              transition: 'border-color 0.2s',
            }}
            onFocus={e => e.target.style.borderColor = 'var(--accent)'}
            onBlur={e  => e.target.style.borderColor = 'var(--border)'}
          />
        </form>

        {/* Nav links */}
        <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginLeft: 'auto' }}>
          <Link to="/catalogue" style={{
            padding: '6px 14px', borderRadius: 'var(--radius)', fontSize: 14,
            color: location.pathname.startsWith('/catalogue') ? 'var(--accent)' : 'var(--text2)',
            transition: 'color 0.2s',
          }}>Catalogue</Link>

          {/* Cart */}
          <Link to="/panier" style={{ position: 'relative', padding: '8px', display: 'flex', alignItems: 'center', color: 'var(--text2)' }}>
            <ShoppingCart size={20} />
            {itemCount > 0 && (
              <span style={{
                position: 'absolute', top: 2, right: 2,
                background: 'var(--accent)', color: 'var(--bg)',
                borderRadius: '100px', fontSize: 10, fontWeight: 700,
                padding: '1px 5px', lineHeight: 1.4,
              }}>{itemCount}</span>
            )}
          </Link>

          {/* Auth */}
          {isAuthenticated() ? (
            <div style={{ position: 'relative' }}>
              <button
                onClick={() => setMenuOpen(!menuOpen)}
                style={{
                  display: 'flex', alignItems: 'center', gap: 8,
                  background: 'var(--bg3)', border: '1px solid var(--border)',
                  borderRadius: 'var(--radius)', padding: '6px 12px',
                  color: 'var(--text)', fontSize: 14, cursor: 'pointer',
                }}
              >
                <User size={15} />
                <span style={{ maxWidth: 100, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                  {user?.prenom}
                </span>
              </button>

              {menuOpen && (
                <div style={{
                  position: 'absolute', right: 0, top: 'calc(100% + 8px)',
                  background: 'var(--bg2)', border: '1px solid var(--border)',
                  borderRadius: 'var(--radius-lg)', minWidth: 180,
                  boxShadow: 'var(--shadow)', zIndex: 200, overflow: 'hidden',
                }} onClick={() => setMenuOpen(false)}>
                  <Link to={dashLink} style={{ display: 'flex', alignItems: 'center', gap: 10, padding: '12px 16px', fontSize: 14, color: 'var(--text2)', borderBottom: '1px solid var(--border)' }}>
                    <LayoutDashboard size={15} /> Dashboard
                  </Link>
                  <Link to="/account/orders" style={{ display: 'flex', alignItems: 'center', gap: 10, padding: '12px 16px', fontSize: 14, color: 'var(--text2)', borderBottom: '1px solid var(--border)' }}>
                    <Package size={15} /> Mes commandes
                  </Link>
                  <button onClick={handleLogout} style={{ display: 'flex', alignItems: 'center', gap: 10, padding: '12px 16px', fontSize: 14, color: 'var(--red)', width: '100%', cursor: 'pointer' }}>
                    <LogOut size={15} /> Déconnexion
                  </button>
                </div>
              )}
            </div>
          ) : (
            <Link to="/login" style={{
              background: 'var(--accent)', color: 'var(--bg)',
              padding: '8px 18px', borderRadius: 'var(--radius)',
              fontSize: 14, fontWeight: 500,
            }}>Connexion</Link>
          )}
        </div>
      </div>
    </nav>
  )
}