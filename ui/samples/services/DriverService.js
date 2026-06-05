/**
 * 기사 서비스
 * 기사 프로필, 배차 승인/거절 등 기사 관련 API
 */

const DriverService = {
  /**
   * 기사 프로필 조회
   * @returns {Promise<DriverProfile>}
   */
  getProfile: async () => {
    const data = await ApiClient.get(`${API_BASE_URL.DRIVER}/drivers/me`);
    return new DriverProfile(data);
  },

  /**
   * 기사 프로필 등록
   * @param {string} name - 이름
   * @param {string} phone - 전화번호
   * @param {string} licenseNumber - 운전면허번호
   * @returns {Promise<DriverProfile>}
   */
  registerProfile: async (name, phone, licenseNumber) => {
    const request = new DriverProfileRequest(name, phone, licenseNumber);
    request.validate();
    const data = await ApiClient.post(`${API_BASE_URL.DRIVER}/drivers/me`, request);
    return new DriverProfile(data);
  },

  /**
   * 기사 프로필 수정
   * @param {string} name - 이름
   * @param {string} phone - 전화번호
   * @param {string} licenseNumber - 운전면허번호
   * @returns {Promise<DriverProfile>}
   */
  updateProfile: async (name, phone, licenseNumber) => {
    const request = new DriverProfileRequest(name, phone, licenseNumber);
    request.validate();
    const data = await ApiClient.put(`${API_BASE_URL.DRIVER}/drivers/me`, request);
    return new DriverProfile(data);
  },

  /**
   * 현재 위치 업데이트
   * @param {number} latitude - 위도
   * @param {number} longitude - 경도
   * @returns {Promise<DriverProfile>}
   */
  updateLocation: async (latitude, longitude) => {
    const request = new LocationRequest(latitude, longitude);
    request.validate();
    const data = await ApiClient.put(`${API_BASE_URL.DRIVER}/drivers/me/location`, request);
    return new DriverProfile(data);
  },

  /**
   * Pending 배차 목록 조회
   * @param {object} options - {page, size, sort, ...}
   * @returns {Promise<DriverDispatchList>}
   */
  getPendingDispatches: async (options = {}) => {
    const params = new URLSearchParams(options).toString();
    const data = await ApiClient.get(`${API_BASE_URL.DISPATCHER}/dispatches/drivers/pending${params ? '?' + params : ''}`);
    return new DriverDispatchList(data);
  },

  /**
   * 배차 승인
   * @param {number} dispatchId - 배차 ID
   * @returns {Promise<DriverDispatch>}
   */
  acceptDispatch: async (dispatchId) => {
    const data = await ApiClient.post(`${API_BASE_URL.DISPATCHER}/dispatches/drivers/${dispatchId}/accept`);
    return new DriverDispatch(data);
  },

  /**
   * 배차 거절
   * @param {number} dispatchId - 배차 ID
   * @param {string} [reason] - 거절 사유
   * @returns {Promise<DriverDispatch>}
   */
  rejectDispatch: async (dispatchId, reason = null) => {
    const request = new RejectDispatchRequest(dispatchId, reason);
    const data = await ApiClient.post(`${API_BASE_URL.DISPATCHER}/dispatches/drivers/${dispatchId}/reject`, request);
    return new DriverDispatch(data);
  },

  /**
   * 배차 상세 조회
   * @param {number} dispatchId - 배차 ID
   * @returns {Promise<DriverDispatch>}
   */
  getDispatch: async (dispatchId) => {
    const data = await ApiClient.get(`${API_BASE_URL.DISPATCHER}/dispatches/${dispatchId}`);
    return new DriverDispatch(data);
  },
};

// 사용 예:
// const profile = await DriverService.getProfile();
// await DriverService.updateLocation({latitude: 37.5, longitude: 126.9});
// const dispatches = await DriverService.getPendingDispatches({page: 0, size: 10});
// await DriverService.acceptDispatch(123);
