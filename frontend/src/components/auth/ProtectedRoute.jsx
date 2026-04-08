import { Navigate } from 'react-router-dom'
import useAuthStore from '../../context/authStore'

export default function ProtectedRoute({ children, roles }) {
  const { user, isAuthenticated } = useAuthStore()

  if (!isAuthenticated()) return <Navigate to="/login" replace />
  if (roles && !roles.includes(user?.role)) return <Navigate to="/" replace />

  return children
}