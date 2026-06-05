export class LoginRequest {
  /**
   * @param {string} loginId - 이메일
   * @param {string} password - 비밀번호
   */
  constructor(loginId, password) {
    this.loginId = loginId;
    this.password = password;
  }

  /**
   * 요청 검증
   * @throws {Error}
   */
  validate() {
    if (!this.loginId || typeof this.loginId !== 'string') {
      throw new Error('이메일은 필수입니다');
    }
    if (!this.password || typeof this.password !== 'string') {
      throw new Error('비밀번호는 필수입니다');
    }
  }
}

export class LoginResponse {
  /**
   * @param {Object} data - API 응답 데이터
   * @param {string} data.accountId - 계정 ID (UUID)
   * @param {string} data.role - 권한 (BASIC, USER, DRIVER 등)
   * @param {string} data.actor - 주체 종류 (ACCOUNT, CUSTOMER, DRIVER)
   * @param {string} data.credentialId - 인증 수단 ID (UUID)
   * @param {Object} data.token - 토큰 정보
   * @param {string} data.token.accessToken - Access Token (JWT)
   * @param {string} data.token.refreshToken - Refresh Token (JWT)
   * @param {string} data.token.accessExpiresAt - Access Token 만료 시각 (ISO 8601)
   * @param {string} data.token.refreshExpiresAt - Refresh Token 만료 시각 (ISO 8601)
   * @param {string} data.token.role - 토큰 권한
   * @param {string} data.token.actor - 주체 종류
   */
  constructor(data = {}) {
    this.accountId = data.accountId || '';
    this.role = data.role || '';
    this.actor = data.actor || '';
    this.credentialId = data.credentialId || '';
    this.token = data.token || {};
  }

  /**
   * Access Token 반환
   * @returns {string}
   */
  getAccessToken() {
    return this.token?.accessToken || '';
  }

  /**
   * Refresh Token 반환
   * @returns {string}
   */
  getRefreshToken() {
    return this.token?.refreshToken || '';
  }

  /**
   * 권한 반환
   * @returns {string}
   */
  getRole() {
    return this.token?.role || this.role || '';
  }

  /**
   * 유효한 응답인지 확인 (accessToken과 role은 필수, refreshToken은 선택)
   * @returns {boolean}
   */
  isValid() {
    return !!this.getAccessToken() && !!this.getRole();
  }
}

export class RegisterRequest {
  /**
   * @param {string} loginId - 로그인 ID (이메일)
   * @param {string} password - 비밀번호 (6~30자)
   */
  constructor(loginId, password) {
    this.loginId = loginId;
    this.password = password;
  }

  /**
   * 요청 검증
   * @throws {Error}
   */
  validate() {
    if (!this.loginId || typeof this.loginId !== 'string') {
      throw new Error('이메일은 필수입니다');
    }
    if (!this.password || this.password.length < 6 || this.password.length > 30) {
      throw new Error('비밀번호는 6~30자여야 합니다');
    }
  }
}

export class RegisterResponse {
  /**
   * @param {Object} data - API 응답 데이터
   * @param {string} data.accountId - 계정 ID (UUID)
   * @param {string} data.loginId - 로그인 ID (이메일)
   * @param {string} data.status - 계정 상태 (ACTIVE)
   * @param {string} data.createdAt - 생성 시각 (ISO 8601)
   */
  constructor(data = {}) {
    this.accountId = data.accountId || '';
    this.loginId = data.loginId || '';
    this.status = data.status || '';
    this.createdAt = data.createdAt || '';
  }

  /**
   * 유효한 응답인지 확인
   * @returns {boolean}
   */
  isValid() {
    return !!this.accountId && !!this.loginId && !!this.status;
  }
}

export class RefreshResponse {
  /**
   * @param {Object} data - API 응답 데이터
   * @param {string} data.accessToken - Access Token (JWT)
   * @param {string} data.refreshToken - Refresh Token (JWT)
   * @param {string} data.accessExpiresAt - Access Token 만료 시각 (ISO 8601)
   * @param {string} data.refreshExpiresAt - Refresh Token 만료 시각 (ISO 8601)
   * @param {string} data.role - 토큰 권한
   * @param {string} data.actor - 주체 종류
   */
  constructor(data = {}) {
    this.accessToken = data.accessToken || '';
    this.refreshToken = data.refreshToken || '';
    this.accessExpiresAt = data.accessExpiresAt || '';
    this.refreshExpiresAt = data.refreshExpiresAt || '';
    this.role = data.role || '';
    this.actor = data.actor || '';
  }

  /**
   * Access Token 반환
   * @returns {string}
   */
  getAccessToken() {
    return this.accessToken;
  }

  /**
   * Refresh Token 반환
   * @returns {string}
   */
  getRefreshToken() {
    return this.refreshToken;
  }

  /**
   * 유효한 응답인지 확인
   * @returns {boolean}
   */
  isValid() {
    return !!this.accessToken;
  }
}

export class UpgradeResponse extends LoginResponse {
  constructor(data = {}) {
    super(data);
  }
}
