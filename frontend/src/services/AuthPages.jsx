import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { authService } from '../services/services'
import useAuthStore from '../context/authStore.js'
import useCartStore from '../context/cartStore.js'
import { Button, Input } from '../components/ui'
import toast from 'react-hot-toast'

export function LoginPage() {
  const [form, setForm] = useState({ email: '', motDePasse: '' })
  const [loading, setLoading] = useState(false)
  const { setAuth } = useAuthStore()
  const { fetchCart } = useCartStore()
  const navigate = useNavigate()

  const handleSubmit = async e => {
    e.preventDefault()
    setLoading(true)
    try {
      const { data } = await authService.login(form)
      setAuth(data)
      await fetchCart()
      toast.success(`Bienvenue ${data.prenom} !`)
      const role = data.role
      navigate(role === 'ADMIN' ? '/admin' : role === 'SELLER' ? '/seller' : '/')
    } catch (err) {
      toast.error(err.response?.data?.message || 'Email ou mot de passe incorrect')
    } finally { setLoading(false) }
  }

  return <AuthLayout title="Connexion" subtitle="Heureux de vous revoir">
    <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
      <Input label="Email" type="email" value={form.email} onChange={e => setForm({...form, email: e.target.value})} required placeholder="vous@exemple.com" />
      <Input label="Mot de passe" type="password" value={form.motDePasse} onChange={e => setForm({...form, motDePasse: e.target.value})} required placeholder="••••••••" />
      <Button type="submit" loading={loading} size="lg" style={{ marginTop: 8 }}>Se connecter</Button>
      <p style={{ textAlign: 'center', fontSize: 14, color: 'var(--text3)' }}>
        Pas encore de compte ? <Link to="/register" style={{ color: 'var(--accent)' }}>S'inscrire</Link>
      </p>
    </form>
  </AuthLayout>
}

export function RegisterPage() {
  const [form, setForm] = useState({ email: '', motDePasse: '', prenom: '', nom: '', role: 'CUSTOMER', nomBoutique: '' })
  const [loading, setLoading] = useState(false)
  const { setAuth } = useAuthStore()
  const { fetchCart } = useCartStore()
  const navigate = useNavigate()

  const handleSubmit = async e => {
    e.preventDefault()
    setLoading(true)
    try {
      const payload = { ...form }
      if (form.role !== 'SELLER') delete payload.nomBoutique
      const { data } = await authService.register(payload)
      setAuth(data)
      await fetchCart()
      toast.success('Compte créé avec succès !')
      navigate('/')
    } catch (err) {
      toast.error(err.response?.data?.message || 'Erreur lors de l\'inscription')
    } finally { setLoading(false) }
  }

  const f = (k, v) => setForm({ ...form, [k]: v })

  return <AuthLayout title="Inscription" subtitle="Créez votre compte ShopFlow">
    <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: 14 }}>
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 14 }}>
        <Input label="Prénom" value={form.prenom} onChange={e => f('prenom', e.target.value)} required placeholder="Prénom" />
        <Input label="Nom" value={form.nom} onChange={e => f('nom', e.target.value)} required placeholder="Nom" />
      </div>
      <Input label="Email" type="email" value={form.email} onChange={e => f('email', e.target.value)} required placeholder="vous@exemple.com" />
      <Input label="Mot de passe" type="password" value={form.motDePasse} onChange={e => f('motDePasse', e.target.value)} required placeholder="Min. 8 caractères, 1 majuscule, 1 chiffre" />

      <div>
        <label style={{ fontSize: 13, color: 'var(--text2)', fontWeight: 500, display: 'block', marginBottom: 8 }}>Type de compte</label>
        <div style={{ display: 'flex', gap: 10 }}>
          {['CUSTOMER', 'SELLER'].map(r => (
            <button key={r} type="button" onClick={() => f('role', r)}
              style={{
                flex: 1, padding: '10px', borderRadius: 'var(--radius)', fontSize: 14, cursor: 'pointer',
                border: `1.5px solid ${form.role === r ? 'var(--accent)' : 'var(--border)'}`,
                background: form.role === r ? 'rgba(232,197,71,0.1)' : 'var(--bg3)',
                color: form.role === r ? 'var(--accent)' : 'var(--text2)',
              }}>
              {r === 'CUSTOMER' ? '🛍 Client' : '🏪 Vendeur'}
            </button>
          ))}
        </div>
      </div>

      {form.role === 'SELLER' && (
        <Input label="Nom de la boutique" value={form.nomBoutique} onChange={e => f('nomBoutique', e.target.value)} required placeholder="Ma super boutique" />
      )}

      <Button type="submit" loading={loading} size="lg" style={{ marginTop: 8 }}>Créer mon compte</Button>
      <p style={{ textAlign: 'center', fontSize: 14, color: 'var(--text3)' }}>
        Déjà un compte ? <Link to="/login" style={{ color: 'var(--accent)' }}>Se connecter</Link>
      </p>
    </form>
  </AuthLayout>
}

function AuthLayout({ title, subtitle, children }) {
  return (
    <div style={{ minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center', padding: 24, background: 'var(--bg)' }}>
      <div style={{ width: '100%', maxWidth: 440 }}>
        <Link to="/" style={{ display: 'block', textAlign: 'center', fontFamily: 'var(--font-head)', fontSize: 24, fontWeight: 800, marginBottom: 32 }}>
          Shop<span style={{ color: 'var(--accent)' }}>Flow</span>
        </Link>
        <div style={{ background: 'var(--bg2)', border: '1px solid var(--border)', borderRadius: 'var(--radius-lg)', padding: '40px 36px' }}>
          <h1 style={{ fontSize: 26, marginBottom: 6 }}>{title}</h1>
          <p style={{ fontSize: 14, color: 'var(--text3)', marginBottom: 32 }}>{subtitle}</p>
          {children}
        </div>
      </div>
    </div>
  )
}