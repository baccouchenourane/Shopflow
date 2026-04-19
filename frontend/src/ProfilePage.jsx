import { useState, useEffect } from 'react';
import { User, MapPin, Plus, Trash2, Star } from 'lucide-react';
import { addressService } from './services/services';
import useAuthStore from './context/authStore';
import { Button, Card, Input, Spinner, EmptyState } from './components/ui';
import toast from 'react-hot-toast';

// ✅ AJOUT : Page Profil manquante (adresses + infos compte)
export default function ProfilePage() {
  const { user } = useAuthStore();
  const [tab, setTab] = useState('profil');

  return (
    <div className="container" style={{ padding: '40px 0 80px' }}>
      <h1 style={{ marginBottom: 8 }}>Mon Espace</h1>
      <p style={{ color: 'var(--text2)', marginBottom: 32 }}>Gérez votre profil et vos adresses.</p>

      {/* Tabs */}
      <div style={{ display: 'flex', gap: 4, marginBottom: 32, borderBottom: '1px solid var(--border)', paddingBottom: 0 }}>
        {[
          { key: 'profil',   label: 'Profil',   icon: <User size={14} /> },
          { key: 'adresses', label: 'Adresses', icon: <MapPin size={14} /> },
        ].map(t => (
          <button key={t.key} onClick={() => setTab(t.key)} style={{
            display: 'flex', alignItems: 'center', gap: 6,
            padding: '10px 20px', fontSize: 14, fontWeight: 500,
            border: 'none', background: 'none', cursor: 'pointer',
            color: tab === t.key ? 'var(--accent)' : 'var(--text2)',
            borderBottom: `2px solid ${tab === t.key ? 'var(--accent)' : 'transparent'}`,
            marginBottom: -1, transition: 'color 0.2s, border-color 0.2s',
          }}>
            {t.icon} {t.label}
          </button>
        ))}
      </div>

      {tab === 'profil'   && <ProfilTab user={user} />}
      {tab === 'adresses' && <AdressesTab />}
    </div>
  );
}

/* ─── Onglet Profil ───────────────────────────────────────────────────────── */
function ProfilTab({ user }) {
  return (
    <Card style={{ padding: 32, maxWidth: 520 }}>
      <div style={{ display: 'flex', alignItems: 'center', gap: 20, marginBottom: 32 }}>
        <div style={{
          width: 72, height: 72, borderRadius: '50%',
          background: 'var(--accent)', color: 'var(--bg)',
          display: 'flex', alignItems: 'center', justifyContent: 'center',
          fontSize: 28, fontWeight: 700,
        }}>
          {user?.prenom?.[0]?.toUpperCase()}
        </div>
        <div>
          <h2 style={{ marginBottom: 4 }}>{user?.prenom} {user?.nom}</h2>
          <p style={{ color: 'var(--text2)', fontSize: 14 }}>{user?.email}</p>
          <span className={`badge ${user?.role === 'SELLER' ? 'badge-green' : 'badge-accent'}`} style={{ marginTop: 6 }}>
            {user?.role === 'ADMIN' ? 'Administrateur' : user?.role === 'SELLER' ? 'Vendeur' : 'Client'}
          </span>
        </div>
      </div>

      <div style={{ display: 'grid', gap: 16 }}>
        {[
          { label: 'Prénom', value: user?.prenom },
          { label: 'Nom',    value: user?.nom },
          { label: 'Email',  value: user?.email },
          { label: 'Rôle',   value: user?.role },
        ].map(({ label, value }) => (
          <div key={label}>
            <p style={{ fontSize: 12, color: 'var(--text3)', textTransform: 'uppercase', letterSpacing: '0.05em', marginBottom: 4 }}>
              {label}
            </p>
            <p style={{ fontSize: 15, fontWeight: 500 }}>{value}</p>
          </div>
        ))}
      </div>
    </Card>
  );
}

/* ─── Onglet Adresses ─────────────────────────────────────────────────────── */
function AdressesTab() {
  const [addresses, setAddresses] = useState([]);
  const [loading, setLoading]     = useState(true);
  const [showForm, setShowForm]   = useState(false);
  const [saving, setSaving]       = useState(false);
  const [form, setForm]           = useState({ rue: '', ville: '', codePostal: '', pays: 'Tunisie', principal: false });

  const load = () => {
    setLoading(true);
    addressService.getAll()
      .then(r => setAddresses(r.data))
      .catch(() => toast.error('Erreur chargement adresses'))
      .finally(() => setLoading(false));
  };

  useEffect(load, []);

  const handleSave = async () => {
    if (!form.rue || !form.ville || !form.codePostal || !form.pays) {
      toast.error('Tous les champs sont obligatoires'); return;
    }
    setSaving(true);
    try {
      await addressService.create(form);
      toast.success('Adresse ajoutée !');
      setShowForm(false);
      setForm({ rue: '', ville: '', codePostal: '', pays: 'Tunisie', principal: false });
      load();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Erreur');
    } finally { setSaving(false); }
  };

  const handleSetPrincipal = async (id) => {
    try {
      await addressService.setPrincipal(id);
      load();
      toast.success('Adresse principale mise à jour');
    } catch { toast.error('Erreur'); }
  };

  const handleDelete = async (id) => {
    if (!confirm('Supprimer cette adresse ?')) return;
    try {
      await addressService.delete(id);
      setAddresses(prev => prev.filter(a => a.id !== id));
      toast.success('Adresse supprimée');
    } catch { toast.error('Erreur'); }
  };

  return (
    <div style={{ maxWidth: 620 }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
        <h3 style={{ margin: 0 }}>Mes adresses de livraison</h3>
        {!showForm && (
          <Button size="sm" onClick={() => setShowForm(true)}>
            <Plus size={14} /> Ajouter
          </Button>
        )}
      </div>

      {/* Formulaire ajout */}
      {showForm && (
        <Card style={{ padding: 24, marginBottom: 24, border: '1px solid var(--accent)' }}>
          <h4 style={{ marginBottom: 20 }}>Nouvelle adresse</h4>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 14 }}>
            <Input label="Rue / N°" value={form.rue} onChange={e => setForm({ ...form, rue: e.target.value })} placeholder="12 Rue de la République" />
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 14 }}>
              <Input label="Ville" value={form.ville} onChange={e => setForm({ ...form, ville: e.target.value })} placeholder="Tunis" />
              <Input label="Code postal" value={form.codePostal} onChange={e => setForm({ ...form, codePostal: e.target.value })} placeholder="1000" />
            </div>
            <Input label="Pays" value={form.pays} onChange={e => setForm({ ...form, pays: e.target.value })} placeholder="Tunisie" />
            <label style={{ display: 'flex', alignItems: 'center', gap: 10, fontSize: 14, cursor: 'pointer', color: 'var(--text2)' }}>
              <input type="checkbox" checked={form.principal} onChange={e => setForm({ ...form, principal: e.target.checked })}
                style={{ accentColor: 'var(--accent)' }} />
              Définir comme adresse principale
            </label>
            <div style={{ display: 'flex', gap: 12, marginTop: 4 }}>
              <Button onClick={handleSave} disabled={saving}>{saving ? 'Enregistrement…' : 'Enregistrer'}</Button>
              <Button variant="ghost" onClick={() => setShowForm(false)}>Annuler</Button>
            </div>
          </div>
        </Card>
      )}

      {/* Liste */}
      {loading ? <Spinner /> : addresses.length === 0 ? (
        <EmptyState icon="📍" title="Aucune adresse enregistrée"
          subtitle="Ajoutez une adresse pour faciliter vos commandes." />
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
          {addresses.map(addr => (
            <Card key={addr.id} style={{
              padding: '16px 20px',
              border: `1px solid ${addr.principal ? 'var(--accent)' : 'var(--border)'}`,
              background: addr.principal ? 'rgba(232,197,71,0.04)' : 'var(--bg2)',
            }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                <div>
                  <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 4 }}>
                    <p style={{ fontWeight: 600, fontSize: 15 }}>{addr.rue}</p>
                    {addr.principal && (
                      <span className="badge badge-accent" style={{ fontSize: 10 }}>Principale</span>
                    )}
                  </div>
                  <p style={{ color: 'var(--text2)', fontSize: 13 }}>
                    {addr.codePostal} {addr.ville}, {addr.pays}
                  </p>
                </div>
                <div style={{ display: 'flex', gap: 8 }}>
                  {!addr.principal && (
                    <button onClick={() => handleSetPrincipal(addr.id)} title="Définir comme principale"
                      style={{ background: 'none', border: '1px solid var(--border)', borderRadius: 6, padding: '6px 10px', cursor: 'pointer', color: 'var(--text2)', fontSize: 12, display: 'flex', alignItems: 'center', gap: 4 }}>
                      <Star size={12} /> Principale
                    </button>
                  )}
                  <button onClick={() => handleDelete(addr.id)} title="Supprimer"
                    style={{ background: 'none', border: '1px solid var(--border)', borderRadius: 6, padding: '6px 8px', cursor: 'pointer', color: 'var(--red)' }}>
                    <Trash2 size={14} />
                  </button>
                </div>
              </div>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
}