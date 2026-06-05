/**
 * API 클라이언트
 * 모든 HTTP 요청을 통합하여 관리합니다.
 *
 * 기능:
 * - fetch 래핑
 * - CommonResponse 파싱
 * - 에러 처리
 * - 토큰 자동 첨부
 * - 401 시 토큰 재발급 자동 시도
 * - 재발급 실패 시 자동 로그아웃 및 로그인 페이지 이동
 */

const ApiClient = (() => {
  // 토큰 재발급 중인지 추적 (동시 요청 방지)
  let isTokenRefreshing = false;
  let refreshTokenPromise = null;

  // 헤더에 토큰 자동 포함
  const getHeaders = (customHeaders = {}) => {
    const headers = {
      'Content-Type': 'application/json',
      ...customHeaders
    };

    // localStorage에서 토큰 조회
    if (typeof Auth !== 'undefined' && Auth.getToken()) {
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
        if (typeof Auth === 'undefined') {
          return false;
        }

        const refreshToken = Auth.getRefreshToken();
        if (!refreshToken) {
          return false;
        }

        // 리프레시 토큰으로 새 토큰 요청
        const response = await fetch(`${API_BASE_URL.AUTH}/api/auth/refresh`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${refreshToken}`
          }
        });

        const body = await response.json();

        if (response.ok && body.code === 'SUCCESS') {
          // 새 토큰 저장
          if (body.data.token) {
            Auth.saveToken(body.data.token);
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

  // 로그아웃
  const logout = () => {
    if (typeof Auth !== 'undefined') {
      Auth.clearToken();
    }
    window.location.href = '/samples/pages/auth/login.html';
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

// 사용 예:
// const user = await ApiClient.get(`${API_BASE_URL.CUSTOMER}/api/customers/me`);
// const result = await ApiClient.post(`${API_BASE_URL.AUTH}/api/auth/login`, { email, password });
