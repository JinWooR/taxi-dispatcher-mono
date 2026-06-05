import { API_BASE_URL } from '../config.js';
import { ApiClient } from '../api.js';
import { Auth } from '../auth.js';
import { LoginRequest, LoginResponse, RegisterRequest, RegisterResponse, RefreshResponse, UpgradeResponse } from '../types/Auth.js';

export const AuthService = {
  /**
   * 기본 로그인
   * @param {string} loginId - 로그인 ID (이메일)
   * @param {string} password - 비밀번호
   * @returns {Promise<LoginResponse>}
   */
  login: async (loginId, password) => {
    const request = new LoginRequest(loginId, password);
    request.validate();
    const data = await ApiClient.post(`${API_BASE_URL.AUTH}/auth/login`, request);
    return new LoginResponse(data);
  },

  /**
   * 고객 권한 로그인
   * @param {string} loginId - 로그인 ID (이메일)
   * @param {string} password - 비밀번호
   * @returns {Promise<LoginResponse>}
   */
  loginCustomer: async (loginId, password) => {
    const request = new LoginRequest(loginId, password);
    request.validate();
    const data = await ApiClient.post(`${API_BASE_URL.AUTH}/auth/login/customer`, request);
    return new LoginResponse(data);
  },

  /**
   * 기사 권한 로그인
   * @param {string} loginId - 로그인 ID (이메일)
   * @param {string} password - 비밀번호
   * @returns {Promise<LoginResponse>}
   */
  loginDriver: async (loginId, password) => {
    const request = new LoginRequest(loginId, password);
    request.validate();
    const data = await ApiClient.post(`${API_BASE_URL.AUTH}/auth/login/driver`, request);
    return new LoginResponse(data);
  },

  /**
   * 회원가입 (역할 선택 후, 프로필 등록 시 권한 승격 필요)
   * @param {string} loginId - 로그인 ID (이메일)
   * @param {string} password - 비밀번호
   * @returns {Promise<RegisterResponse>}
   */
  register: async (loginId, password) => {
    const request = new RegisterRequest(loginId, password);
    request.validate();
    const data = await ApiClient.post(`${API_BASE_URL.AUTH}/auth/register`, request);
    return new RegisterResponse(data);
  },

  /**
   * 고객 권한 승격 (고객 프로필 등록 완료 후)
   * @returns {Promise<LoginResponse>}
   */
  upgradeCustomer: async () => {
    const refreshToken = Auth.getRefreshToken();
    const data = await ApiClient.post(
      `${API_BASE_URL.AUTH}/auth/upgrade/customer`,
      null,
      { 'Authorization': `Bearer ${refreshToken}` }
    );
    return new LoginResponse(data);
  },

  /**
   * 기사 권한 승격 (기사 프로필 등록 완료 후)
   * @returns {Promise<LoginResponse>}
   */
  upgradeDriver: async () => {
    const refreshToken = Auth.getRefreshToken();
    const data = await ApiClient.post(
      `${API_BASE_URL.AUTH}/auth/upgrade/driver`,
      null,
      { 'Authorization': `Bearer ${refreshToken}` }
    );
    return new LoginResponse(data);
  },

  /**
   * 토큰 재발급
   * @returns {Promise<RefreshResponse>}
   */
  refresh: async () => {
    const data = await ApiClient.post(`${API_BASE_URL.AUTH}/auth/refresh`);
    return new RefreshResponse(data);
  },
};
