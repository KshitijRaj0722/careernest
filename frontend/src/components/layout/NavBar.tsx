import { Link, useNavigate } from 'react-router-dom';
import { useAppDispatch, useAppSelector } from '@/app/hooks';
import { logout } from '@/features/auth/authSlice';

export default function NavBar() {
  const { token, email, role } = useAppSelector((s) => s.auth);
  const dispatch = useAppDispatch();
  const navigate = useNavigate();

  const handleLogout = () => {
    dispatch(logout());
    navigate('/login');
  };

  return (
    <nav className="bg-white border-b border-slate-200">
      <div className="mx-auto flex max-w-5xl items-center justify-between px-4 py-3">
        <Link to="/" className="text-lg font-semibold text-slate-900">
          Career<span className="text-indigo-600">Nest</span>
        </Link>

        <div className="flex items-center gap-4 text-sm">
          <Link to="/" className="text-slate-600 hover:text-slate-900">
            Jobs
          </Link>

          {token && role === 'JOB_SEEKER' && (
            <Link to="/my-applications" className="text-slate-600 hover:text-slate-900">
              My Applications
            </Link>
          )}

          {token && role === 'EMPLOYER' && (
            <Link to="/employer/dashboard" className="text-slate-600 hover:text-slate-900">
              Dashboard
            </Link>
          )}

          {token ? (
            <>
              <span className="hidden text-slate-500 sm:inline">{email}</span>
              <button
                onClick={handleLogout}
                className="rounded-md border border-slate-300 px-3 py-1.5 text-slate-700 hover:bg-slate-50"
              >
                Log out
              </button>
            </>
          ) : (
            <>
              <Link to="/login" className="text-slate-600 hover:text-slate-900">
                Log in
              </Link>
              <Link
                to="/register"
                className="rounded-md bg-indigo-600 px-3 py-1.5 text-white hover:bg-indigo-700"
              >
                Sign up
              </Link>
            </>
          )}
        </div>
      </div>
    </nav>
  );
}
