import { createSlice } from '@reduxjs/toolkit';
import type { PayloadAction } from '@reduxjs/toolkit';
import type { JobApplication } from '@/types';

interface ApplicationsState {
  items: JobApplication[];
}

const initialState: ApplicationsState = {
  items: [],
};

const applicationsSlice = createSlice({
  name: 'applications',
  initialState,
  reducers: {
    setApplications: (state, action: PayloadAction<JobApplication[]>) => {
      state.items = action.payload;
    },
  },
});

export const { setApplications } = applicationsSlice.actions;
export default applicationsSlice.reducer;
