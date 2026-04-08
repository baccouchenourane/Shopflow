import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import Layout from './components/layout/Layout';
import Navbar from './components/layout/Navbar';
import ProtectedRoute from './components/auth/ProtectedRoute';

// Pages
import HomePage from './HomePage';
import CataloguePage from './CataloguePage';
import ProductDetailPage from './ProductDetailPage';
import CartPage from './CartPage';
import OrdersPage from './OrdersPage';
import CheckoutPage from './CheckoutPage';
import SellerDashboard from './SellerDashboard';
import AdminDashboard from './AdminDashboard';
import { LoginPage, RegisterPage } from './services/AuthPages';

function App() {
  return (
    <Layout>
      <Navbar />
      <main style={{ minHeight: '80vh' }}>
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/catalogue" element={<CataloguePage />} />
          <Route path="/produit/:id" element={<ProductDetailPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/cart" element={<CartPage />} />

          {/* Routes Protégées */}
          <Route path="/checkout" element={<ProtectedRoute><CheckoutPage /></ProtectedRoute>} />
          <Route path="/orders" element={<ProtectedRoute><OrdersPage /></ProtectedRoute>} />
          
          <Route path="/seller" element={
            <ProtectedRoute roles={['SELLER']}><SellerDashboard /></ProtectedRoute>
          } />
          
          <Route path="/admin" element={
            <ProtectedRoute roles={['ADMIN']}><AdminDashboard /></ProtectedRoute>
          } />

          {/* Redirection 404 en dernier */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </main>
    </Layout>
  );
}

export default App;