import { useState, useEffect, useCallback } from 'react'
import { useSearchParams } from 'react-router-dom'
import { SlidersHorizontal, X } from 'lucide-react'
import { productService, categoryService } from './services/services'
import ProductCard from './components/product/ProductCard'
import { Spinner, EmptyState, Button } from './components/ui'

export default function CataloguePage() {
  const [searchParams, setSearchParams] = useSearchParams()
  const [products, setProducts] = useState([])
  const [categories, setCategories] = useState([])
  const [totalPages, setTotalPages] = useState(0)
  const [loading, setLoading] = useState(true)
  const [showFilters, setShowFilters] = useState(false)

  const page       = Number(searchParams.get('page') || 0)
  const q          = searchParams.get('q') || ''
  const categoryId = searchParams.get('categoryId') || ''
  const minPrix    = searchParams.get('minPrix') || ''
  const maxPrix    = searchParams.get('maxPrix') || ''
  const promo      = searchParams.get('promo') || ''
  const sort       = searchParams.get('sort') || 'dateCreation'

  const fetchProducts = useCallback(async () => {
    setLoading(true)
    try {
      const params = { page, size: 12, sort }
      if (q)          params.q = q
      if (categoryId) params.categoryId = categoryId
      if (minPrix)    params.minPrix = minPrix
      if (maxPrix)    params.maxPrix = maxPrix
      if (promo)      params.promo = true

      const { data } = q
        ? await productService.search({ q, page, size: 12 })
        : await productService.getAll(params)

      setProducts(data.content || data)
      setTotalPages(data.totalPages || 1)
    } finally { setLoading(false) }
  }, [page, q, categoryId, minPrix, maxPrix, promo, sort])

  useEffect(() => { fetchProducts() }, [fetchProducts])
  useEffect(() => { categoryService.getTree().then(r => setCategories(r.data)) }, [])

  const setParam = (key, val) => {
    const next = new URLSearchParams(searchParams)
    if (val) next.set(key, val); else next.delete(key)
    next.delete('page')
    setSearchParams(next)
  }

  const clearFilters = () => setSearchParams({})

  const hasFilters = q || categoryId || minPrix || maxPrix || promo

  return (
    <div className="container" style={{ paddingTop: 40, paddingBottom: 80 }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 32, flexWrap: 'wrap', gap: 12 }}>
        <div>
          <h1 style={{ fontSize: 32, marginBottom: 4 }}>{q ? `"${q}"` : 'Catalogue'}</h1>
          {hasFilters && <p style={{ fontSize: 13, color: 'var(--text3)' }}>Filtres actifs</p>}
        </div>
        <div style={{ display: 'flex', gap: 10, alignItems: 'center' }}>
          {hasFilters && (
            <Button variant="ghost" size="sm" onClick={clearFilters}>
              <X size={14} /> Effacer les filtres
            </Button>
          )}
          <select
            value={sort}
            onChange={e => setParam('sort', e.target.value)}
            style={{ background: 'var(--bg3)', border: '1px solid var(--border)', borderRadius: 'var(--radius)', color: 'var(--text)', padding: '8px 12px', fontSize: 14, cursor: 'pointer' }}
          >
            <option value="dateCreation">Plus récents</option>
            <option value="prix_asc">Prix croissant</option>
            <option value="prix_desc">Prix décroissant</option>
            <option value="popularite">Popularité</option>
          </select>
          <Button variant="secondary" size="sm" onClick={() => setShowFilters(!showFilters)}>
            <SlidersHorizontal size={14} /> Filtres
          </Button>
        </div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: showFilters ? '240px 1fr' : '1fr', gap: 32, alignItems: 'start' }}>
        {/* Sidebar filtres */}
        {showFilters && (
          <aside style={{ background: 'var(--bg2)', border: '1px solid var(--border)', borderRadius: 'var(--radius-lg)', padding: 24, position: 'sticky', top: 80 }}>
            <h3 style={{ fontSize: 16, marginBottom: 20 }}>Filtres</h3>

            {/* Catégories */}
            <div style={{ marginBottom: 24 }}>
              <p style={{ fontSize: 12, color: 'var(--text3)', textTransform: 'uppercase', letterSpacing: '0.05em', marginBottom: 10 }}>Catégorie</p>
              <div style={{ display: 'flex', flexDirection: 'column', gap: 6 }}>
                <button onClick={() => setParam('categoryId', '')} style={{ textAlign: 'left', fontSize: 14, color: !categoryId ? 'var(--accent)' : 'var(--text2)', padding: '4px 0', background: 'none', border: 'none', cursor: 'pointer' }}>
                  Toutes
                </button>
                {categories.map(c => (
                  <button key={c.id} onClick={() => setParam('categoryId', c.id)} style={{ textAlign: 'left', fontSize: 14, color: categoryId == c.id ? 'var(--accent)' : 'var(--text2)', padding: '4px 0', background: 'none', border: 'none', cursor: 'pointer' }}>
                    {c.nom}
                  </button>
                ))}
              </div>
            </div>

            {/* Prix */}
            <div style={{ marginBottom: 24 }}>
              <p style={{ fontSize: 12, color: 'var(--text3)', textTransform: 'uppercase', letterSpacing: '0.05em', marginBottom: 10 }}>Prix (TND)</p>
              <div style={{ display: 'flex', gap: 8 }}>
                <input placeholder="Min" type="number" value={minPrix} onChange={e => setParam('minPrix', e.target.value)}
                  style={{ width: '50%', background: 'var(--bg3)', border: '1px solid var(--border)', borderRadius: 'var(--radius)', color: 'var(--text)', padding: '8px 10px', fontSize: 13 }} />
                <input placeholder="Max" type="number" value={maxPrix} onChange={e => setParam('maxPrix', e.target.value)}
                  style={{ width: '50%', background: 'var(--bg3)', border: '1px solid var(--border)', borderRadius: 'var(--radius)', color: 'var(--text)', padding: '8px 10px', fontSize: 13 }} />
              </div>
            </div>

            {/* Promo */}
            <label style={{ display: 'flex', alignItems: 'center', gap: 10, cursor: 'pointer', fontSize: 14, color: 'var(--text2)' }}>
              <input type="checkbox" checked={!!promo} onChange={e => setParam('promo', e.target.checked ? 'true' : '')} />
              Promotions uniquement
            </label>
          </aside>
        )}

        {/* Grid */}
        <div>
          {loading ? <Spinner /> : products.length === 0 ? (
            <EmptyState icon="🔍" title="Aucun produit trouvé" subtitle="Essayez d'autres filtres ou termes de recherche" />
          ) : (
            <>
              <div className="grid-4" style={{ gridTemplateColumns: 'repeat(auto-fill, minmax(220px, 1fr))' }}>
                {products.map(p => <ProductCard key={p.id} product={p} />)}
              </div>

              {/* Pagination */}
              {totalPages > 1 && (
                <div style={{ display: 'flex', justifyContent: 'center', gap: 8, marginTop: 40 }}>
                  {Array.from({ length: totalPages }, (_, i) => (
                    <button key={i} onClick={() => setParam('page', i)}
                      style={{
                        padding: '8px 14px', borderRadius: 'var(--radius)', fontSize: 14,
                        background: i === page ? 'var(--accent)' : 'var(--bg3)',
                        color: i === page ? 'var(--bg)' : 'var(--text2)',
                        border: '1px solid var(--border)', cursor: 'pointer',
                      }}>{i + 1}</button>
                  ))}
                </div>
              )}
            </>
          )}
        </div>
      </div>
    </div>
  )
}