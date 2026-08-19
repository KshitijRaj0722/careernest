import { createSlice } from '@reduxjs/toolkit';
import type { PayloadAction } from '@reduxjs/toolkit';
import type { AuthResponse, Role } from '@/types';

interface AuthState {
  token: string | null;
  email: string | null;
  role: Role | null;
}

/** Rehydrate from localStorage so a refresh doesn't log the user out. */
const initialState: AuthState = {
  token: localStorage.getItem('token'),
  email: localStorage.getItem('email'),
  role: (localStorage.getItem('role') as Role | null) ?? null,
};

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    setCredentials: (state, action: PayloadAction<AuthResponse>) => {
      const { token, email, role } = action.payload;
      state.token = token;
      state.email = email;
      state.role = role;
      localStorage.setItem('token', token);
      localStorage.setItem('email', email);
      localStorage.setItem('role', role);
    },
    logout: (state) => {
      state.token = null;
      state.email = null;
      state.role = null;
      localStorage.removeItem('token');
      localStorage.removeItem('email');
      localStorage.removeItem('role');
    },
  },
});

export const { setCredentials, logout } = authSlice.actions;
export default authSlice.reducer;
