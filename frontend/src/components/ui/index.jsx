import clsx from 'clsx'
import { Loader2 } from 'lucide-react'

/* ── Button ─────────────────────────────────────────────────────────────────── */
export function Button({ children, variant = 'primary', size = 'md', loading, className, ...props }) {
  const base = 'inline-flex items-center justify-center gap-2 font-medium transition-all rounded-lg disabled:opacity-50 disabled:cursor-not-allowed'
  const variants = {
    primary:  'bg-[--accent] text-[--bg] hover:bg-[--accent2] active:scale-95',
    secondary:'bg-[--bg3] text-[--text] border border-[--border] hover:border-[--border2] hover:bg-[--border] active:scale-95',
    ghost:    'text-[--text2] hover:text-[--text] hover:bg-[--bg3] active:scale-95',
    danger:   'bg-[--red] text-white hover:opacity-90 active:scale-95',
    outline:  'border border-[--accent] text-[--accent] hover:bg-[--accent] hover:text-[--bg] active:scale-95',
  }
  const sizes = {
    sm: 'px-3 py-1.5 text-sm',
    md: 'px-5 py-2.5 text-sm',
    lg: 'px-7 py-3.5 text-base',
  }
  return (
    <button className={clsx(base, variants[variant], sizes[size], className)} disabled={loading || props.disabled} {...props}>
      {loading && <Loader2 size={14} className="animate-spin" />}
      {children}
    </button>
  )
}

/* ── Input ──────────────────────────────────────────────────────────────────── */
export function Input({ label, error, className, ...props }) {
  return (
    <div className="flex flex-col gap-1.5">
      {label && <label style={{ fontSize: 13, color: 'var(--text2)', fontWeight: 500 }}>{label}</label>}
      <input
        style={{
          background: 'var(--bg3)',
          border: `1px solid ${error ? 'var(--red)' : 'var(--border)'}`,
          borderRadius: 'var(--radius)',
          color: 'var(--text)',
          padding: '10px 14px',
          fontSize: 14,
          outline: 'none',
          transition: 'border-color 0.2s',
          width: '100%',
        }}
        onFocus={e => e.target.style.borderColor = error ? 'var(--red)' : 'var(--accent)'}
        onBlur={e  => e.target.style.borderColor = error ? 'var(--red)' : 'var(--border)'}
        {...props}
      />
      {error && <span style={{ fontSize: 12, color: 'var(--red)' }}>{error}</span>}
    </div>
  )
}

/* ── Select ─────────────────────────────────────────────────────────────────── */
export function Select({ label, children, ...props }) {
  return (
    <div className="flex flex-col gap-1.5">
      {label && <label style={{ fontSize: 13, color: 'var(--text2)', fontWeight: 500 }}>{label}</label>}
      <select style={{
        background: 'var(--bg3)',
        border: '1px solid var(--border)',
        borderRadius: 'var(--radius)',
        color: 'var(--text)',
        padding: '10px 14px',
        fontSize: 14,
        outline: 'none',
        width: '100%',
        cursor: 'pointer',
      }} {...props}>
        {children}
      </select>
    </div>
  )
}

/* ── Spinner ────────────────────────────────────────────────────────────────── */
export function Spinner({ size = 24 }) {
  return (
    <div style={{ display: 'flex', justifyContent: 'center', padding: '40px 0' }}>
      <Loader2 size={size} style={{ animation: 'spin 0.8s linear infinite', color: 'var(--accent)' }} />
    </div>
  )
}

/* ── StatusBadge ────────────────────────────────────────────────────────────── */
const STATUS_MAP = {
  PENDING:    { label: 'En attente',   cls: 'badge-neutral' },
  PAID:       { label: 'Payé',         cls: 'badge-green'   },
  PROCESSING: { label: 'En cours',     cls: 'badge-accent'  },
  SHIPPED:    { label: 'Expédié',      cls: 'badge-accent'  },
  DELIVERED:  { label: 'Livré',        cls: 'badge-green'   },
  CANCELLED:  { label: 'Annulé',       cls: 'badge-red'     },
  REFUNDED:   { label: 'Remboursé',    cls: 'badge-red'     },
}

export function OrderStatusBadge({ status }) {
  const s = STATUS_MAP[status] || { label: status, cls: 'badge-neutral' }
  return <span className={`badge ${s.cls}`}>{s.label}</span>
}

/* ── RatingStars ────────────────────────────────────────────────────────────── */
export function RatingStars({ note, size = 14 }) {
  return (
    <div style={{ display: 'flex', gap: 2 }}>
      {[1,2,3,4,5].map(i => (
        <span key={i} style={{ fontSize: size, color: i <= note ? 'var(--accent)' : 'var(--border2)' }}>★</span>
      ))}
    </div>
  )
}

/* ── Card ───────────────────────────────────────────────────────────────────── */
export function Card({ children, className, style, ...props }) {
  return (
    <div style={{
      background: 'var(--bg2)',
      border: '1px solid var(--border)',
      borderRadius: 'var(--radius-lg)',
      ...style
    }} className={className} {...props}>
      {children}
    </div>
  )
}

/* ── EmptyState ─────────────────────────────────────────────────────────────── */
export function EmptyState({ icon, title, subtitle }) {
  return (
    <div style={{ textAlign: 'center', padding: '60px 0', color: 'var(--text3)' }}>
      <div style={{ fontSize: 48, marginBottom: 16 }}>{icon}</div>
      <div style={{ fontSize: 18, fontFamily: 'var(--font-head)', color: 'var(--text2)', marginBottom: 8 }}>{title}</div>
      {subtitle && <div style={{ fontSize: 14 }}>{subtitle}</div>}
    </div>
  )
}