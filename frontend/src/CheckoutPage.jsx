import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import useCartStore from './context/cartStore';
import { cartService } from './services/services';
import { Button, Input, Card } from './components/ui';
import toast from 'react-hot-toast';

export default function CheckoutPage() {
  const { cart, clearCart } = useCartStore();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [form, setForm] = useState({ adresse: '', ville: '', codePostal: '', telephone: '' });

  if (!cart || cart.items?.length === 0) {
    return (
      <div className="container" style={{ padding: '80px 0', textAlign: 'center' }}>
        <h2>Votre panier est vide</h2>
        <Button onClick={() => navigate('/catalogue')} style={{ marginTop: 20 }}>Retour au catalogue</Button>
      </div>
    );
  }

  const handlePayment = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      // Simulation de création de commande
      toast.success('Commande validée avec succès !');
      clearCart();
      navigate('/orders');
    } catch (err) {
      toast.error('Erreur lors de la validation');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container" style={{ padding: '40px 0' }}>
      <h1>Finaliser la commande</h1>
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 400px', gap: 40, marginTop: 32 }}>
        <form id="checkout-form" onSubmit={handlePayment} style={{ display: 'flex', flexDirection: 'column', gap: 20 }}>
          <Card style={{ padding: 24 }}>
            <h3 style={{ marginBottom: 20 }}>Informations de livraison</h3>
            <Input label="Adresse complète" value={form.adresse} onChange={e => setForm({...form, adresse: e.target.value})} required />
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 16, marginTop: 16 }}>
              <Input label="Ville" value={form.ville} onChange={e => setForm({...form, ville: e.target.value})} required />
              <Input label="Code Postal" value={form.codePostal} onChange={e => setForm({...form, codePostal: e.target.value})} required />
            </div>
            <div style={{ marginTop: 16 }}>
              <Input label="Téléphone" value={form.telephone} onChange={e => setForm({...form, telephone: e.target.value})} required />
            </div>
          </Card>

          <Card style={{ padding: 24 }}>
            <h3 style={{ marginBottom: 20 }}>Paiement</h3>
            <p style={{ fontSize: 14, color: 'var(--text2)' }}>Paiement sécurisé à la livraison.</p>
          </Card>
        </form>

        <aside>
          <Card style={{ padding: 24, background: 'var(--bg2)' }}>
            <h3 style={{ marginBottom: 20 }}>Récapitulatif</h3>
            {cart.items.map(item => (
              <div key={item.id} style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 12, fontSize: 14 }}>
                <span>{item.quantite}x {item.produitNom}</span>
                <span>{(item.prixUnitaire * item.quantite).toFixed(2)} TND</span>
              </div>
            ))}
            <hr style={{ margin: '16px 0', border: 'none', borderTop: '1px solid var(--border)' }} />
            <div style={{ display: 'flex', justifyContent: 'space-between', fontWeight: 700, fontSize: 18 }}>
              <span>Total</span>
              <span style={{ color: 'var(--accent)' }}>{cart.total.toFixed(2)} TND</span>
            </div>
            <Button form="checkout-form" type="submit" loading={loading} size="lg" style={{ width: '100%', marginTop: 24 }}>
              Confirmer la commande
            </Button>
          </Card>
        </aside>
      </div>
    </div>
  );
}