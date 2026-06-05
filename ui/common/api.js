import { API_BASE_URL } from './config.js';
import { Auth } from './auth.js';

export const ApiClient = (() => {
  // 토큰 재발급 중인지 추적 (동시 요청 방지)
  let isTokenRefreshing = false;
  let refreshTokenPromise = null;

  const getHeaders = (customHeaders = {}) => {
    const headers = {
      'Content-Type': 'application/json',
      ...customHeaders
    };

    if (!headers['Authorization'] && Auth.getToken()) {
      headers['Authorization'] = `Bearer ${Auth.getToken()}`;
    }

    return headers;
  };

  // 토큰 재발급
  const refreshAccessToken = async () => {
    // 이미 재발급 중이면 진행 중인 promise 기다리기
    if (isTokenRefreshing) {
      return refreshTokenPromise;
    }

    isTokenRefreshing = true;
    refreshTokenPromise = (async () => {
      try {
        const refreshToken = Auth.getRefreshToken();
        if (!refreshToken) {
          return false;
        }

        // 리프레시 토큰으로 새 토큰 요청
        const response = await fetch(`${API_BASE_URL.AUTH}/auth/refresh`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${refreshToken}`
          }
        });

        const body = await response.json();

        if (response.ok && body.code === 'SUCCESS') {
          // 새 토큰 저장 (TokenInfo 구조)
          if (body.data.accessToken) {
            Auth.saveToken(body.data.accessToken);
          }
          if (body.data.refreshToken) {
            Auth.saveRefreshToken(body.data.refreshToken);
          }
          return true;
        }

        // 재발급 실패
        return false;
      } catch (error) {
        console.error('토큰 재발급 실패:', error);
        return false;
      } finally {
        isTokenRefreshing = false;
        refreshTokenPromise = null;
      }
    })();

    return refreshTokenPromise;
  };

  const logout = () => {
    Auth.clearToken();
    window.location.href = '/pages/auth/login.html';
  };

  // API 응답 처리
  const parseResponse = async (response) => {
    const body = await response.json();

    // HTTP 상태 코드 확인
    if (!response.ok) {
      // 401: 토큰 만료 또는 인증 실패
      if (response.status === 401) {
        const error = new Error(`HTTP ${response.status}: ${body.message || '요청 실패'}`);
        error.status = 401;
        error.shouldRefresh = true;
        throw error;
      }
      throw new Error(`HTTP ${response.status}: ${body.message || '요청 실패'}`);
    }

    // API 응답 코드 확인 (CommonResponse)
    if (body.code !== 'SUCCESS') {
      const error = new Error(body.message || '요청 실패');
      error.code = body.code;
      throw error;
    }

    return body.data;
  };

  // 재시도 로직을 포함한 HTTP 요청
  const fetchWithRetry = async (method, endpoint, body, customHeaders, retryCount = 0) => {
    try {
      const response = await fetch(endpoint, {
        method,
        headers: getHeaders(customHeaders),
        body: body ? JSON.stringify(body) : null
      });

      return await parseResponse(response);
    } catch (error) {
      // 401 에러이고 아직 재시도하지 않았으면
      if (error.shouldRefresh && retryCount === 0) {
        const refreshed = await refreshAccessToken();
        if (refreshed) {
          // 토큰 재발급 성공 → 원래 요청 재시도
          return fetchWithRetry(method, endpoint, body, customHeaders, 1);
        } else {
          // 토큰 재발급 실패 → 로그아웃
          logout();
          throw new Error('토큰이 만료되었습니다. 다시 로그인하세요.');
        }
      }
      throw error;
    }
  };

  return {
    /**
     * GET 요청 (토큰 재발급 자동 처리)
     * @param {string} endpoint - 엔드포인트 (예: /api/auth/login)
     * @param {object} customHeaders - 추가 헤더
     * @returns {Promise} API 응답 데이터
     */
    get: async (endpoint, customHeaders = {}) => {
      return fetchWithRetry('GET', endpoint, null, customHeaders);
    },

    /**
     * POST 요청 (토큰 재발급 자동 처리)
     * @param {string} endpoint - 엔드포인트
     * @param {object} body - 요청 본문
     * @param {object} customHeaders - 추가 헤더
     * @returns {Promise} API 응답 데이터
     */
    post: async (endpoint, body = null, customHeaders = {}) => {
      return fetchWithRetry('POST', endpoint, body, customHeaders);
    },

    /**
     * PUT 요청 (토큰 재발급 자동 처리)
     * @param {string} endpoint - 엔드포인트
     * @param {object} body - 요청 본문
     * @param {object} customHeaders - 추가 헤더
     * @returns {Promise} API 응답 데이터
     */
    put: async (endpoint, body = null, customHeaders = {}) => {
      return fetchWithRetry('PUT', endpoint, body, customHeaders);
    },

    /**
     * DELETE 요청 (토큰 재발급 자동 처리)
     * @param {string} endpoint - 엔드포인트
     * @param {object} customHeaders - 추가 헤더
     * @returns {Promise} API 응답 데이터
     */
    delete: async (endpoint, customHeaders = {}) => {
      return fetchWithRetry('DELETE', endpoint, null, customHeaders);
    }
  };
})();
