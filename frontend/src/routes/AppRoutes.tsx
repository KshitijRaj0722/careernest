import { Routes, Route } from 'react-router-dom';
import LoginPage from '@/pages/LoginPage';
import RegisterPage from '@/pages/RegisterPage';
import JobListPage from '@/pages/JobListPage';
import JobDetailsPage from '@/pages/JobDetailsPage';
import EmployerDashboardPage from '@/pages/EmployerDashboardPage';
import MyApplicationsPage from '@/pages/MyApplicationsPage';
import RequireAuth from '@/routes/RequireAuth';

export default function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<JobListPage />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />

      <Route element={<RequireAuth />}>
        <Route path="/jobs/:id" element={<JobDetailsPage />} />
      </Route>

      <Route element={<RequireAuth role="JOB_SEEKER" />}>
        <Route path="/my-applications" element={<MyApplicationsPage />} />
      </Route>

      <Route element={<RequireAuth role="EMPLOYER" />}>
        <Route path="/employer/dashboard" element={<EmployerDashboardPage />} />
      </Route>

      <Route path="*" element={<div className="mx-auto max-w-3xl px-4 py-16 text-slate-500">Page not found.</div>} />
    </Routes>
  );
}
