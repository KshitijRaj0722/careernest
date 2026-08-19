import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { jobsApi } from '@/api/jobsApi';
import { useAppSelector } from '@/app/hooks';
import type { Job } from '@/types';

export default function JobListPage() {
  const token = useAppSelector((s) => s.auth.token);

  const [jobs, setJobs] = useState<Job[]>([]);
  const [keyword, setKeyword] = useState('');
  const [location, setLocation] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const load = async () => {
    setLoading(true);
    setError(null);
    try {
      setJobs(await jobsApi.search({ keyword: keyword || undefined, location: location || undefined }));
    } catch (err) {
      setError((err as Error).message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (token) void load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [token]);

  if (!token) {
    return (
      <div className="mx-auto max-w-3xl px-4 py-16 text-center">
        <h1 className="text-2xl font-semibold text-slate-900">Find your next role</h1>
        <p className="mt-2 text-slate-600">Log in to browse and apply to open positions.</p>
        <div className="mt-6 flex justify-center gap-3">
          <Link to="/login" className="rounded-md border border-slate-300 px-4 py-2 text-slate-700">
            Log in
          </Link>
          <Link to="/register" className="rounded-md bg-indigo-600 px-4 py-2 text-white">
            Sign up
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="mx-auto max-w-3xl px-4 py-8">
      <h1 className="mb-6 text-2xl font-semibold text-slate-900">Open positions</h1>

      <form
        onSubmit={(e) => {
          e.preventDefault();
          void load();
        }}
        className="mb-6 flex flex-col gap-3 sm:flex-row"
      >
        <input
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
          placeholder="Title or keyword"
          aria-label="Search by keyword"
          className="flex-1 rounded-md border border-slate-300 px-3 py-2 focus:border-indigo-500 focus:outline-none"
        />
        <input
          value={location}
          onChange={(e) => setLocation(e.target.value)}
          placeholder="Location"
          aria-label="Filter by location"
          className="flex-1 rounded-md border border-slate-300 px-3 py-2 focus:border-indigo-500 focus:outline-none"
        />
        <button type="submit" className="rounded-md bg-indigo-600 px-4 py-2 text-white hover:bg-indigo-700">
          Search
        </button>
      </form>

      {error && <p className="rounded-md bg-red-50 px-3 py-2 text-sm text-red-700">{error}</p>}
      {loading && <p className="text-slate-500">Loading jobs…</p>}
      {!loading && !error && jobs.length === 0 && (
        <p className="text-slate-500">No jobs match your search yet.</p>
      )}

      <ul className="space-y-3">
        {jobs.map((job) => (
          <li key={job.id} className="rounded-lg border border-slate-200 bg-white p-4">
            <div className="flex items-start justify-between gap-4">
              <div>
                <Link to={`/jobs/${job.id}`} className="text-lg font-medium text-slate-900 hover:text-indigo-600">
                  {job.title}
                </Link>
                <p className="text-sm text-slate-500">{job.location}</p>
              </div>
              {job.salary != null && (
                <span className="whitespace-nowrap text-sm font-medium text-slate-700">
                  ₹{job.salary.toLocaleString('en-IN')}
                </span>
              )}
            </div>
            <p className="mt-2 line-clamp-2 text-sm text-slate-600">{job.description}</p>
            <p className="mt-2 text-xs text-slate-400">Apply by {job.deadline}</p>
          </li>
        ))}
      </ul>
    </div>
  );
}
