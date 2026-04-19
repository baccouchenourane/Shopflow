import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { MapPin, Plus, CreditCard, CheckCircle } from 'lucide-react';
import useCartStore from './context/cartStore';
import { orderService, addressService } from './services/services';
import { Button, Card, Spinner } from './components/ui';
import toast from 'react-hot-toast';

export default function CheckoutPage() {
  const { cart, clearCart } = useCartStore();
  const navigate = useNavigate();
  const [loading, setLoading]       = useState(false);
  const [addresses, setAddresses]   = useState([]);
  const [loadingAddr, setLoadingAddr] = useState(true);
  const [selectedAddr, setSelectedAddr] = useState(null);

  // ✅ CORRECTION : Le CheckoutPage original simulait la commande sans appeler l'API.
  //    Maintenant il charge les adresses réelles et crée la commande via orderService.
  useEffect(() => {
    addressService.getAll()
      .then(res => {
        setAddresses(res.data);
        const principal = res.data.find(a => a.principal) || res.data[0];
        if (principal) setSelectedAddr(principal.id);
      })
      .catch(() => toast.error('Impossible de charger les adresses'))
      .finally(() => setLoadingAddr(false));
  }, []);

  if (!cart || cart.items?.length === 0) {
    return (
      <div className="container" style={{ padding: '80px 0', textAlign: 'center' }}>
        <h2>Votre panier est vide</h2>
        <Button onClick={() => navigate('/catalogue')} style={{ marginTop: 20 }}>
          Retour au catalogue
        </Button>
      </div>
    );
  }

  const handleConfirm = async () => {
    if (!selectedAddr) { toast.error('Veuillez sélectionner une adresse de livraison'); return; }
    setLoading(true);
    try {
      const { data } = await orderService.create({ addressId: selectedAddr });
      clearCart();
      toast.success(`Commande ${data.numeroCommande} confirmée !`);
      navigate('/orders');
    } catch (err) {
      toast.error(err.response?.data?.message || 'Erreur lors de la validation');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container" style={{ padding: '40px 0 80px' }}>
      <h1 style={{ marginBottom: 8 }}>Finaliser la commande</h1>
      <p style={{ color: 'var(--text2)', marginBottom: 32 }}>
        Vérifiez vos informations avant de confirmer.
      </p>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 380px', gap: 32, alignItems: 'start' }}>
        {/* ─── Colonne gauche ─── */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: 24 }}>

          {/* Adresses */}
          <Card style={{ padding: 24 }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 10, marginBottom: 20 }}>
              <MapPin size={18} color="var(--accent)" />
              <h3 style={{ margin: 0 }}>Adresse de livraison</h3>
            </div>

            {loadingAddr ? <Spinner /> : addresses.length === 0 ? (
              <div style={{ textAlign: 'center', padding: '20px 0', color: 'var(--text2)' }}>
                <p>Vous n'avez pas encore d'adresse enregistrée.</p>
                <Button variant="secondary" size="sm" style={{ marginTop: 12 }}
                  onClick={() => navigate('/profil')}>
                  <Plus size={14} /> Ajouter une adresse
                </Button>
              </div>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
                {addresses.map(addr => (
                  <label key={addr.id} style={{
                    display: 'flex', alignItems: 'flex-start', gap: 14, padding: '14px 16px',
                    borderRadius: 'var(--radius)', border: `2px solid ${selectedAddr === addr.id ? 'var(--accent)' : 'var(--border)'}`,
                    cursor: 'pointer', transition: 'border-color 0.2s',
                    background: selectedAddr === addr.id ? 'rgba(232,197,71,0.06)' : 'var(--bg3)',
                  }}>
                    <input type="radio" name="address" value={addr.id} checked={selectedAddr === addr.id}
                      onChange={() => setSelectedAddr(addr.id)}
                      style={{ marginTop: 3, accentColor: 'var(--accent)' }} />
                    <div>
                      <p style={{ fontWeight: 600, marginBottom: 4, fontSize: 14 }}>
                        {addr.rue}
                        {addr.principal && (
                          <span className="badge badge-accent" style={{ marginLeft: 8, fontSize: 10 }}>Principale</span>
                        )}
                      </p>
                      <p style={{ color: 'var(--text2)', fontSize: 13 }}>
                        {addr.codePostal} {addr.ville}, {addr.pays}
                      </p>
                    </div>
                  </label>
                ))}
                <Button variant="ghost" size="sm" onClick={() => navigate('/profil')}
                  style={{ alignSelf: 'flex-start', color: 'var(--accent)' }}>
                  <Plus size={14} /> Ajouter une adresse
                </Button>
              </div>
            )}
          </Card>

          {/* Paiement simulé */}
          <Card style={{ padding: 24 }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 10, marginBottom: 16 }}>
              <CreditCard size={18} color="var(--accent)" />
              <h3 style={{ margin: 0 }}>Mode de paiement</h3>
            </div>
            <div style={{
              display: 'flex', alignItems: 'center', gap: 12, padding: '14px 16px',
              borderRadius: 'var(--radius)', border: '2px solid var(--accent)',
              background: 'rgba(232,197,71,0.06)',
            }}>
              <CheckCircle size={18} color="var(--accent)" />
              <div>
                <p style={{ fontWeight: 600, fontSize: 14 }}>Paiement à la livraison</p>
                <p style={{ color: 'var(--text2)', fontSize: 12, marginTop: 2 }}>
                  Règlement en espèces à la réception de votre colis.
                </p>
              </div>
            </div>
          </Card>
        </div>

        {/* ─── Récapitulatif ─── */}
        <aside>
          <Card style={{ padding: 24, position: 'sticky', top: 90 }}>
            <h3 style={{ marginBottom: 20 }}>Récapitulatif</h3>

            <div style={{ display: 'flex', flexDirection: 'column', gap: 10, marginBottom: 20 }}>
              {cart.items?.map(item => (
                <div key={item.id} style={{ display: 'flex', justifyContent: 'space-between', fontSize: 13, color: 'var(--text2)' }}>
                  <span style={{ flex: 1, paddingRight: 12 }}>
                    {item.quantite}× {item.productNom}
                    {item.variantInfo && <span style={{ color: 'var(--text3)' }}> ({item.variantInfo})</span>}
                  </span>
                  <span style={{ whiteSpace: 'nowrap' }}>
                    {Number(item.sousTotal).toFixed(2)} TND
                  </span>
                </div>
              ))}
            </div>

            <hr style={{ border: 'none', borderTop: '1px solid var(--border)', marginBottom: 16 }} />

            {[
              { label: 'Sous-total',       value: cart.sousTotal },
              { label: 'Frais de livraison', value: cart.fraisLivraison },
              ...(cart.remiseCoupon > 0 ? [{ label: `Coupon (${cart.couponCode})`, value: -cart.remiseCoupon }] : []),
            ].map(({ label, value }) => (
              <div key={label} style={{ display: 'flex', justifyContent: 'space-between', fontSize: 13, color: 'var(--text2)', marginBottom: 8 }}>
                <span>{label}</span>
                <span style={{ color: value < 0 ? 'var(--green)' : 'inherit' }}>
                  {value < 0 ? '-' : ''}{Math.abs(Number(value)).toFixed(2)} TND
                </span>
              </div>
            ))}

            <hr style={{ border: 'none', borderTop: '1px solid var(--border)', margin: '12px 0 16px' }} />

            <div style={{ display: 'flex', justifyContent: 'space-between', fontWeight: 700, fontSize: 18 }}>
              <span>Total TTC</span>
              <span style={{ color: 'var(--accent)' }}>{Number(cart.totalTTC).toFixed(2)} TND</span>
            </div>

            <Button
              onClick={handleConfirm}
              disabled={loading || !selectedAddr}
              size="lg"
              style={{ width: '100%', marginTop: 24 }}
            >
              {loading ? 'Validation…' : 'Confirmer la commande'}
            </Button>

            <p style={{ fontSize: 11, color: 'var(--text3)', textAlign: 'center', marginTop: 12 }}>
              En confirmant, vous acceptez nos conditions générales de vente.
            </p>
          </Card>
        </aside>
      </div>
    </div>
  );
}