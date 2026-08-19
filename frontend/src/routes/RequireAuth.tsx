import { Navigate, Outlet } from 'react-router-dom';
import { useAppSelector } from '@/app/hooks';
import type { Role } from '@/types';

interface Props {
  /** When set, the logged-in user must hold this role. */
  role?: Role;
}

export default function RequireAuth({ role }: Props) {
  const auth = useAppSelector((s) => s.auth);

  if (!auth.token) return <Navigate to="/login" replace />;
  if (role && auth.role !== role) return <Navigate to="/" replace />;

  return <Outlet />;
}
