/**
 * 인증 관리
 * JWT 토큰의 저장, 조회, 검증을 담당합니다.
 *
 * localStorage 스키마:
 * - auth_token: JWT 토큰 (Access Token)
 * - auth_refresh_token: 리프레시 토큰 (토큰 재발급용)
 * - auth_role: 사용자 역할 (CUSTOMER, DRIVER)
 */

const Auth = (() => {
  const TOKEN_KEY = 'auth_token';
  const REFRESH_TOKEN_KEY = 'auth_refresh_token';
  const ROLE_KEY = 'auth_role';

  return {
    /**
     * 토큰 저장
     * @param {string} token - JWT 토큰
     */
    saveToken: (token) => {
      if (token) {
        localStorage.setItem(TOKEN_KEY, token);
      }
    },

    /**
     * 토큰 조회
     * @returns {string|null} JWT 토큰 또는 null
     */
    getToken: () => {
      return localStorage.getItem(TOKEN_KEY);
    },

    /**
     * 리프레시 토큰 저장
     * @param {string} refreshToken - 리프레시 토큰
     */
    saveRefreshToken: (refreshToken) => {
      if (refreshToken) {
        localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken);
      }
    },

    /**
     * 리프레시 토큰 조회
     * @returns {string|null} 리프레시 토큰 또는 null
     */
    getRefreshToken: () => {
      return localStorage.getItem(REFRESH_TOKEN_KEY);
    },

    /**
     * 토큰 삭제 (로그아웃)
     */
    clearToken: () => {
      localStorage.removeItem(TOKEN_KEY);
      localStorage.removeItem(REFRESH_TOKEN_KEY);
      localStorage.removeItem(ROLE_KEY);
    },

    /**
     * 인증 여부 확인
     * @returns {boolean} 토큰이 존재하면 true
     */
    isAuthenticated: () => {
      return !!Auth.getToken();
    },

    /**
     * 역할 저장
     * @param {string} role - 사용자 역할 (CUSTOMER, DRIVER)
     */
    saveRole: (role) => {
      if (role) {
        localStorage.setItem(ROLE_KEY, role);
      }
    },

    /**
     * 역할 조회
     * @returns {string|null} 사용자 역할 또는 null
     */
    getRole: () => {
      return localStorage.getItem(ROLE_KEY);
    },

    /**
     * 고객 여부 확인
     * @returns {boolean}
     */
    isCustomer: () => {
      return Auth.getRole() === 'CUSTOMER';
    },

    /**
     * 기사 여부 확인
     * @returns {boolean}
     */
    isDriver: () => {
      return Auth.getRole() === 'DRIVER';
    }
  };
})();

// 사용 예:
// Auth.saveToken(jwtToken);
// Auth.saveRole('CUSTOMER');
// if (Auth.isAuthenticated()) { /* 로그인됨 */ }
// if (Auth.isCustomer()) { /* 고객 */ }
// Auth.clearToken(); // 로그아웃
