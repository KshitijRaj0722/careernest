import axiosClient from '@/api/axiosClient';
import type { Job, JobPayload, JobApplication, ApplicationStatus } from '@/types';

export const jobsApi = {
  search: (params: { location?: string; keyword?: string }) =>
    axiosClient.get<Job[]>('/jobs', { params }).then((r) => r.data),

  getById: (id: string) => axiosClient.get<Job>(`/jobs/${id}`).then((r) => r.data),

  myJobs: () => axiosClient.get<Job[]>('/jobs/mine').then((r) => r.data),

  create: (payload: JobPayload) => axiosClient.post<Job>('/jobs', payload).then((r) => r.data),

  update: (id: string, payload: JobPayload) =>
    axiosClient.put<Job>(`/jobs/${id}`, payload).then((r) => r.data),

  remove: (id: string) => axiosClient.delete<void>(`/jobs/${id}`).then((r) => r.data),
};

export const applicationsApi = {
  apply: (jobId: string) =>
    axiosClient.post<JobApplication>(`/applications/${jobId}`).then((r) => r.data),

  mine: () => axiosClient.get<JobApplication[]>('/applications/my').then((r) => r.data),

  forJob: (jobId: string) =>
    axiosClient.get<JobApplication[]>(`/applications/job/${jobId}`).then((r) => r.data),

  updateStatus: (id: string, status: ApplicationStatus) =>
    axiosClient.patch<JobApplication>(`/applications/${id}/status`, { status }).then((r) => r.data),
};
