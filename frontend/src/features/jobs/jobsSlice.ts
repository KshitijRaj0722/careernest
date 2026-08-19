import { createSlice } from '@reduxjs/toolkit';
import type { PayloadAction } from '@reduxjs/toolkit';
import type { Job } from '@/types';

interface JobsState {
  items: Job[];
  selectedJob: Job | null;
}

const initialState: JobsState = {
  items: [],
  selectedJob: null,
};

const jobsSlice = createSlice({
  name: 'jobs',
  initialState,
  reducers: {
    setJobs: (state, action: PayloadAction<Job[]>) => {
      state.items = action.payload;
    },
    setSelectedJob: (state, action: PayloadAction<Job | null>) => {
      state.selectedJob = action.payload;
    },
  },
});

export const { setJobs, setSelectedJob } = jobsSlice.actions;
export default jobsSlice.reducer;
