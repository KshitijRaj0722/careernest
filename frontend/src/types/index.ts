export type Role = 'JOB_SEEKER' | 'EMPLOYER';

export interface AuthResponse {
  token: string;
  email: string;
  role: Role;
}

export interface LoginPayload {
  email: string;
  password: string;
}

export interface RegisterPayload {
  fullName: string;
  email: string;
  password: string;
  phoneNumber?: string;
  role: Role;
}

export interface Job {
  id: string;
  title: string;
  description: string;
  location: string;
  salary: number;
  deadline: string;
  employerId: string;
}

export type JobPayload = Omit<Job, 'id' | 'employerId'>;

export type ApplicationStatus = 'APPLIED' | 'REVIEWED' | 'SHORTLISTED' | 'REJECTED' | 'HIRED';

export interface JobApplication {
  id: string;
  jobId: string;
  jobTitle: string;
  jobSeekerId: string;
  jobSeekerName: string;
  jobSeekerEmail: string | null;
  jobSeekerPhone: string | null;
  status: ApplicationStatus;
  appliedAt: string;
}
