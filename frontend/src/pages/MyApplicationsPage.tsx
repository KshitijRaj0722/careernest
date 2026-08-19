import { useEffect, useState } from 'react';
import { applicationsApi } from '@/api/jobsApi';
import type { ApplicationStatus, JobApplication } from '@/types';

const STATUS_STYLES: Record<ApplicationStatus, string> = {
  APPLIED: 'bg-slate-100 text-slate-700',
  REVIEWED: 'bg-blue-100 text-blue-700',
  SHORTLISTED: 'bg-amber-100 text-amber-800',
  REJECTED: 'bg-red-100 text-red-700',
  HIRED: 'bg-green-100 text-green-700',
};

export default function MyApplicationsPage() {
  const [applications, setApplications] = useState<JobApplication[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    applicationsApi
      .mine()
      .then(setApplications)
      .catch((err) => setError((err as Error).message))
      .finally(() => setLoading(false));
  }, []);

  return (
    <div className="mx-auto max-w-3xl px-4 py-8">
      <h1 className="mb-6 text-2xl font-semibold text-slate-900">My applications</h1>

      {error && <p className="rounded-md bg-red-50 px-3 py-2 text-sm text-red-700">{error}</p>}
      {loading && <p className="text-slate-500">Loading…</p>}
      {!loading && !error && applications.length === 0 && (
        <p className="text-slate-500">You have not applied to any jobs yet.</p>
      )}

      <ul className="space-y-3">
        {applications.map((application) => (
          <li
            key={application.id}
            className="flex items-center justify-between rounded-lg border border-slate-200 bg-white p-4"
          >
            <div>
              <p className="font-medium text-slate-900">{application.jobTitle}</p>
              <p className="text-xs text-slate-400">
                Applied {new Date(application.appliedAt).toLocaleDateString()}
              </p>
            </div>
            <span className={`rounded-full px-3 py-1 text-xs font-medium ${STATUS_STYLES[application.status]}`}>
              {application.status}
            </span>
          </li>
        ))}
      </ul>
    </div>
  );
}
