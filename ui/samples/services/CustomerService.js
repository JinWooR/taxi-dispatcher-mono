/**
 * 고객 서비스
 * 고객 프로필, 배차 요청/관리 등 고객 관련 API
 */

const CustomerService = {
  /**
   * 고객 프로필 조회
   * @returns {Promise<CustomerProfile>}
   */
  getProfile: async () => {
    const data = await ApiClient.get(`${API_BASE_URL.CUSTOMER}/customers/me`);
    return new CustomerProfile(data);
  },

  /**
   * 고객 프로필 등록
   * @param {string} name - 이름
   * @param {string} phone - 전화번호
   * @param {string} address - 주소
   * @returns {Promise<CustomerProfile>}
   */
  registerProfile: async (name, phone) => {
    const request = new CustomerProfileRequest(name, phone);
    request.validate();
    const data = await ApiClient.post(`${API_BASE_URL.CUSTOMER}/customers`, request);
    return new CustomerProfile(data);
  },

  /**
   * 고객 프로필 수정
   * @param {string} name - 이름
   * @param {string} phone - 전화번호
   * @param {string} address - 주소
   * @returns {Promise<CustomerProfile>}
   */
  updateProfile: async (name, phone) => {
    const request = new CustomerProfileRequest(name, phone);
    request.validate();
    const data = await ApiClient.put(`${API_BASE_URL.CUSTOMER}/customers/me`, request);
    return new CustomerProfile(data);
  },

  /**
   * 배차 요청 생성
   * @param {string} startLocation - 출발지
   * @param {string} endLocation - 도착지
   * @param {object} [options] - 추가 옵션
   * @returns {Promise<Dispatch>}
   */
  createDispatch: async (startLocation, endLocation, options = {}) => {
    const request = new DispatchRequest(startLocation, endLocation, options);
    request.validate();
    const data = await ApiClient.post(`${API_BASE_URL.DISPATCHER}/dispatches/customers`, request);
    return new Dispatch(data);
  },

  /**
   * 내 배차 목록 조회
   * @param {object} options - {page, size, sort, ...}
   * @returns {Promise<DispatchList>}
   */
  getDispatches: async (options = {}) => {
    const params = new URLSearchParams(options).toString();
    const data = await ApiClient.get(`${API_BASE_URL.DISPATCHER}/dispatches/customers${params ? '?' + params : ''}`);
    return new DispatchList(data);
  },

  /**
   * 배차 상세 조회
   * @param {number} dispatchId - 배차 ID
   * @returns {Promise<Dispatch>}
   */
  getDispatch: async (dispatchId) => {
    const data = await ApiClient.get(`${API_BASE_URL.DISPATCHER}/dispatches/${dispatchId}`);
    return new Dispatch(data);
  },
};

// 사용 예:
// const profile = await CustomerService.getProfile();
// await CustomerService.registerProfile({name: '김철수', phone: '010-1234-5678'});
// const dispatches = await CustomerService.getDispatches({page: 0, size: 10});
