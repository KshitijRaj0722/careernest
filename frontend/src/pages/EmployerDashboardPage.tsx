import { useEffect, useState } from 'react';
import { applicationsApi, jobsApi } from '@/api/jobsApi';
import type { ApplicationStatus, Job, JobApplication, JobPayload } from '@/types';

const STATUSES: ApplicationStatus[] = ['APPLIED', 'REVIEWED', 'SHORTLISTED', 'REJECTED', 'HIRED'];

const EMPTY_FORM: JobPayload = {
  title: '',
  description: '',
  location: '',
  salary: 0,
  deadline: '',
};

export default function EmployerDashboardPage() {
  const [jobs, setJobs] = useState<Job[]>([]);
  const [form, setForm] = useState<JobPayload>(EMPTY_FORM);
  const [applications, setApplications] = useState<Record<string, JobApplication[]>>({});
  const [expanded, setExpanded] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);
  /** null = the form creates a new posting; an id = it edits that posting. */
  const [editingId, setEditingId] = useState<string | null>(null);

  const loadJobs = () =>
    jobsApi
      .myJobs()
      .then(setJobs)
      .catch((err) => setError((err as Error).message));

  useEffect(() => {
    void loadJobs();
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    setError(null);
    try {
      const payload = { ...form, salary: Number(form.salary) };
      if (editingId) {
        await jobsApi.update(editingId, payload);
      } else {
        await jobsApi.create(payload);
      }
      cancelEdit();
      await loadJobs();
    } catch (err) {
      setError((err as Error).message);
    } finally {
      setSaving(false);
    }
  };

  const startEdit = (job: Job) => {
    setEditingId(job.id);
    setForm({
      title: job.title,
      description: job.description,
      location: job.location,
      salary: job.salary,
      deadline: job.deadline,
    });
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const cancelEdit = () => {
    setEditingId(null);
    setForm(EMPTY_FORM);
  };

  const handleDelete = async (id: string) => {
    setError(null);
    try {
      await jobsApi.remove(id);
      await loadJobs();
    } catch (err) {
      setError((err as Error).message);
    }
  };

  const toggleApplicants = async (jobId: string) => {
    if (expanded === jobId) {
      setExpanded(null);
      return;
    }
    setExpanded(jobId);
    if (!applications[jobId]) {
      try {
        const list = await applicationsApi.forJob(jobId);
        setApplications((prev) => ({ ...prev, [jobId]: list }));
      } catch (err) {
        setError((err as Error).message);
      }
    }
  };

  const changeStatus = async (jobId: string, applicationId: string, status: ApplicationStatus) => {
    setError(null);
    try {
      const updated = await applicationsApi.updateStatus(applicationId, status);
      setApplications((prev) => ({
        ...prev,
        [jobId]: prev[jobId].map((a) => (a.id === applicationId ? updated : a)),
      }));
    } catch (err) {
      setError((err as Error).message);
    }
  };

  const update = (field: keyof JobPayload, value: string) =>
    setForm((prev) => ({ ...prev, [field]: value }));

  return (
    <div className="mx-auto max-w-3xl px-4 py-8">
      <h1 className="mb-6 text-2xl font-semibold text-slate-900">Employer dashboard</h1>

      {error && <p className="mb-4 rounded-md bg-red-50 px-3 py-2 text-sm text-red-700">{error}</p>}

      <section className="mb-10 rounded-lg border border-slate-200 bg-white p-4">
        <h2 className="mb-3 font-medium text-slate-900">
          {editingId ? 'Edit posting' : 'Post a new job'}
        </h2>
        <form onSubmit={handleSubmit} className="space-y-3">
          <input
            required
            placeholder="Job title"
            aria-label="Job title"
            value={form.title}
            onChange={(e) => update('title', e.target.value)}
            className="w-full rounded-md border border-slate-300 px-3 py-2 focus:border-indigo-500 focus:outline-none"
          />
          <textarea
            required
            placeholder="Description"
            aria-label="Description"
            rows={3}
            value={form.description}
            onChange={(e) => update('description', e.target.value)}
            className="w-full rounded-md border border-slate-300 px-3 py-2 focus:border-indigo-500 focus:outline-none"
          />
          <div className="flex flex-col gap-3 sm:flex-row">
            <input
              required
              placeholder="Location"
              aria-label="Location"
              value={form.location}
              onChange={(e) => update('location', e.target.value)}
              className="flex-1 rounded-md border border-slate-300 px-3 py-2 focus:border-indigo-500 focus:outline-none"
            />
            <input
              type="number"
              min={0}
              placeholder="Salary"
              aria-label="Salary"
              value={form.salary || ''}
              onChange={(e) => update('salary', e.target.value)}
              className="flex-1 rounded-md border border-slate-300 px-3 py-2 focus:border-indigo-500 focus:outline-none"
            />
            <input
              required
              type="date"
              aria-label="Application deadline"
              value={form.deadline}
              onChange={(e) => update('deadline', e.target.value)}
              className="flex-1 rounded-md border border-slate-300 px-3 py-2 focus:border-indigo-500 focus:outline-none"
            />
          </div>
          <div className="flex gap-2">
            <button
              type="submit"
              disabled={saving}
              className="rounded-md bg-indigo-600 px-4 py-2 font-medium text-white hover:bg-indigo-700 disabled:opacity-60"
            >
              {saving ? 'Saving…' : editingId ? 'Save changes' : 'Post job'}
            </button>
            {editingId && (
              <button
                type="button"
                onClick={cancelEdit}
                className="rounded-md border border-slate-300 px-4 py-2 font-medium text-slate-700 hover:bg-slate-50"
              >
                Cancel
              </button>
            )}
          </div>
        </form>
      </section>

      <h2 className="mb-3 font-medium text-slate-900">Your postings</h2>
      {jobs.length === 0 && <p className="text-slate-500">You have not posted any jobs yet.</p>}

      <ul className="space-y-3">
        {jobs.map((job) => (
          <li
            key={job.id}
            className={`rounded-lg border bg-white p-4 ${
              editingId === job.id ? 'border-indigo-400 ring-1 ring-indigo-200' : 'border-slate-200'
            }`}
          >
            <div className="flex items-start justify-between gap-4">
              <div>
                <p className="font-medium text-slate-900">{job.title}</p>
                <p className="text-sm text-slate-500">
                  {job.location} · apply by {job.deadline}
                </p>
              </div>
              <div className="flex gap-2">
                <button
                  onClick={() => void toggleApplicants(job.id)}
                  className="rounded-md border border-slate-300 px-3 py-1.5 text-sm text-slate-700 hover:bg-slate-50"
                >
                  {expanded === job.id ? 'Hide' : 'Applicants'}
                </button>
                <button
                  onClick={() => startEdit(job)}
                  className="rounded-md border border-slate-300 px-3 py-1.5 text-sm text-slate-700 hover:bg-slate-50"
                >
                  Edit
                </button>
                <button
                  onClick={() => void handleDelete(job.id)}
                  className="rounded-md border border-red-200 px-3 py-1.5 text-sm text-red-600 hover:bg-red-50"
                >
                  Delete
                </button>
              </div>
            </div>

            {expanded === job.id && (
              <div className="mt-4 border-t border-slate-100 pt-3">
                {(applications[job.id] ?? []).length === 0 ? (
                  <p className="text-sm text-slate-500">No applications yet.</p>
                ) : (
                  <ul className="space-y-2">
                    {applications[job.id].map((application) => (
                      <li key={application.id} className="flex items-start justify-between gap-3">
                        <div className="min-w-0">
                          <p className="truncate text-sm font-medium text-slate-800">
                            {application.jobSeekerName}
                          </p>
                          {application.jobSeekerEmail && (
                            <a
                              href={`mailto:${application.jobSeekerEmail}`}
                              className="block truncate text-xs text-indigo-600 hover:underline"
                            >
                              {application.jobSeekerEmail}
                            </a>
                          )}
                          {application.jobSeekerPhone ? (
                            <a
                              href={`tel:${application.jobSeekerPhone}`}
                              className="block text-xs text-slate-500 hover:underline"
                            >
                              {application.jobSeekerPhone}
                            </a>
                          ) : (
                            <p className="text-xs text-slate-400">No phone on file</p>
                          )}
                        </div>
                        <select
                          value={application.status}
                          aria-label={`Status for ${application.jobSeekerName}`}
                          onChange={(e) =>
                            void changeStatus(job.id, application.id, e.target.value as ApplicationStatus)
                          }
                          className="shrink-0 rounded-md border border-slate-300 px-2 py-1 text-sm"
                        >
                          {STATUSES.map((status) => (
                            <option key={status} value={status}>
                              {status}
                            </option>
                          ))}
                        </select>
                      </li>
                    ))}
                  </ul>
                )}
              </div>
            )}
          </li>
        ))}
      </ul>
    </div>
  );
}
