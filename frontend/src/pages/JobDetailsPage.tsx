import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { applicationsApi, jobsApi } from '@/api/jobsApi';
import { useAppSelector } from '@/app/hooks';
import type { Job } from '@/types';

export default function JobDetailsPage() {
  const { id } = useParams<{ id: string }>();
  const role = useAppSelector((s) => s.auth.role);

  const [job, setJob] = useState<Job | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [notice, setNotice] = useState<string | null>(null);
  const [applying, setApplying] = useState(false);

  useEffect(() => {
    if (!id) return;
    jobsApi
      .getById(id)
      .then(setJob)
      .catch((err) => setError((err as Error).message));
  }, [id]);

  const handleApply = async () => {
    if (!id) return;
    setApplying(true);
    setError(null);
    setNotice(null);
    try {
      await applicationsApi.apply(id);
      setNotice('Application submitted. You will get an SMS as its status changes.');
    } catch (err) {
      setError((err as Error).message);
    } finally {
      setApplying(false);
    }
  };

  if (error && !job) return <p className="mx-auto max-w-3xl px-4 py-8 text-red-700">{error}</p>;
  if (!job) return <p className="mx-auto max-w-3xl px-4 py-8 text-slate-500">Loading…</p>;

  return (
    <div className="mx-auto max-w-3xl px-4 py-8">
      <h1 className="text-2xl font-semibold text-slate-900">{job.title}</h1>
      <p className="mt-1 text-slate-500">{job.location}</p>

      {job.salary != null && (
        <p className="mt-2 font-medium text-slate-700">₹{job.salary.toLocaleString('en-IN')}</p>
      )}

      <p className="mt-4 whitespace-pre-line text-slate-700">{job.description}</p>
      <p className="mt-4 text-sm text-slate-400">Apply by {job.deadline}</p>

      {notice && <p className="mt-6 rounded-md bg-green-50 px-3 py-2 text-sm text-green-700">{notice}</p>}
      {error && <p className="mt-6 rounded-md bg-red-50 px-3 py-2 text-sm text-red-700">{error}</p>}

      {role === 'JOB_SEEKER' && (
        <button
          onClick={handleApply}
          disabled={applying || notice !== null}
          className="mt-6 rounded-md bg-indigo-600 px-5 py-2 font-medium text-white hover:bg-indigo-700 disabled:opacity-60"
        >
          {applying ? 'Submitting…' : 'Apply for this job'}
        </button>
      )}
    </div>
  );
}
