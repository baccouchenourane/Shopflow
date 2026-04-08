import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { productService } from './services/services';
import ProductCard from './components/product/ProductCard';
import { Button, Spinner } from './components/ui';

export default function HomePage() {
  const [featuredProducts, setFeaturedProducts] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    productService.getAll({ size: 4, sort: 'dateCreation' })
      .then(res => setFeaturedProducts(res.data.content || []))
      .finally(() => setLoading(false));
  }, []);

  return (
    <div>
      {/* Hero Section */}
      <section style={{ 
        padding: '100px 0', 
        background: 'linear-gradient(rgba(0,0,0,0.5), rgba(0,0,0,0.5)), url("/hero-bg.jpg") center/cover',
        color: 'white',
        textAlign: 'center'
      }}>
        <div className="container">
          <h1 style={{ fontSize: '3.5rem', marginBottom: '1.5rem' }}>L'Artisanat de Luxe à portée de clic</h1>
          <p style={{ fontSize: '1.2rem', marginBottom: '2rem', maxWidth: '600px', marginInline: 'auto' }}>
            Découvrez nos collections exclusives de sacs faits main et accessoires uniques.
          </p>
          <Link to="/catalogue">
            <Button size="lg">Explorer le catalogue</Button>
          </Link>
        </div>
      </section>

      {/* Featured Products */}
      <section className="container" style={{ padding: '80px 0' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end', marginBottom: '40px' }}>
          <h2>Nouveautés</h2>
          <Link to="/catalogue" style={{ color: 'var(--accent)' }}>Voir tout →</Link>
        </div>

        {loading ? <Spinner /> : (
          <div className="grid-4" style={{ gridTemplateColumns: 'repeat(auto-fill, minmax(250px, 1fr))', gap: '24px' }}>
            {featuredProducts.map(p => <ProductCard key={p.id} product={p} />)}
          </div>
        )}
      </section>
    </div>
  );
}