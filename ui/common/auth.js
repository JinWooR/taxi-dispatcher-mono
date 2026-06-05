const TOKEN_KEY = 'auth_token';
const REFRESH_TOKEN_KEY = 'auth_refresh_token';
const ROLE_KEY = 'auth_role';

export const Auth = {
  saveToken: (token) => {
    if (token) {
      localStorage.setItem(TOKEN_KEY, token);
    }
  },

  getToken: () => {
    return localStorage.getItem(TOKEN_KEY);
  },

  saveRefreshToken: (refreshToken) => {
    if (refreshToken) {
      localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken);
    }
  },

  getRefreshToken: () => {
    return localStorage.getItem(REFRESH_TOKEN_KEY);
  },

  clearToken: () => {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(REFRESH_TOKEN_KEY);
    localStorage.removeItem(ROLE_KEY);
  },

  isAuthenticated: () => {
    return !!Auth.getToken();
  },

  saveRole: (role) => {
    if (role) {
      localStorage.setItem(ROLE_KEY, role);
    }
  },

  getRole: () => {
    return localStorage.getItem(ROLE_KEY);
  },

  isCustomer: () => {
    return Auth.getRole() === 'USER';
  },

  isDriver: () => {
    return Auth.getRole() === 'DRIVER';
  }
};
