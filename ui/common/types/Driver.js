export class DriverProfile {
  /**
   * @param {Object} data - API 응답 데이터
   * @param {number} data.id - 기사 ID
   * @param {string} data.email - 이메일
   * @param {string} [data.name] - 이름
   * @param {string} [data.phone] - 전화번호
   * @param {string} [data.licenseNumber] - 운전면허번호
   * @param {Object} [data.location] - 현재 위치
   */
  constructor(data = {}) {
    this.id = data.id || null;
    this.email = data.email || '';
    this.name = data.name || '';
    this.phone = data.phone || '';
    this.licenseNumber = data.licenseNumber || '';
    this.location = data.location || { latitude: null, longitude: null };
    this.createdAt = data.createdAt || null;
    this.updatedAt = data.updatedAt || null;
  }

  /**
   * 프로필이 완성되었는지 확인 (필수 정보)
   * @returns {boolean}
   */
  isComplete() {
    return !!this.name && !!this.phone && !!this.licenseNumber;
  }

  /**
   * 위치가 등록되었는지 확인
   * @returns {boolean}
   */
  hasLocation() {
    return !!this.location && !!this.location.latitude && !!this.location.longitude;
  }
}

export class DriverProfileRequest {
  /**
   * @param {string} name - 이름
   * @param {string} phone - 전화번호
   * @param {string} licenseNumber - 운전면허번호
   */
  constructor(name, phone, licenseNumber) {
    this.name = name;
    this.phone = phone;
    this.licenseNumber = licenseNumber;
  }

  /**
   * 요청 검증
   * @throws {Error}
   */
  validate() {
    if (!this.name || typeof this.name !== 'string') {
      throw new Error('이름은 필수입니다');
    }
    if (!this.phone || typeof this.phone !== 'string') {
      throw new Error('전화번호는 필수입니다');
    }
    if (!this.licenseNumber || typeof this.licenseNumber !== 'string') {
      throw new Error('운전면허번호는 필수입니다');
    }
  }
}

export class LocationRequest {
  /**
   * @param {number} latitude - 위도
   * @param {number} longitude - 경도
   */
  constructor(latitude, longitude) {
    this.latitude = latitude;
    this.longitude = longitude;
  }

  /**
   * 요청 검증
   * @throws {Error}
   */
  validate() {
    if (typeof this.latitude !== 'number' || this.latitude < -90 || this.latitude > 90) {
      throw new Error('유효한 위도를 입력하세요 (-90 ~ 90)');
    }
    if (typeof this.longitude !== 'number' || this.longitude < -180 || this.longitude > 180) {
      throw new Error('유효한 경도를 입력하세요 (-180 ~ 180)');
    }
  }
}

export class DriverDispatch {
  /**
   * @param {Object} data - API 응답 데이터
   * @param {number} data.id - 배차 ID
   * @param {string} data.status - 상태
   * @param {string} data.startLocation - 출발지
   * @param {string} data.endLocation - 도착지
   * @param {Object} [data.customer] - 고객 정보
   */
  constructor(data = {}) {
    this.id = data.id || null;
    this.status = data.status || 'REQUESTED';
    this.startLocation = data.startLocation || '';
    this.endLocation = data.endLocation || '';
    this.customer = data.customer || null;
    this.requestedAt = data.requestedAt || null;
    this.assignedAt = data.assignedAt || null;
  }

  /**
   * 상태 문자열 한글로 변환
   * @returns {string}
   */
  getStatusLabel() {
    const labels = {
      'REQUESTED': '요청됨',
      'ASSIGNED': '배정됨',
      'IN_PROGRESS': '진행중',
      'COMPLETED': '완료',
      'CANCELLED': '취소됨'
    };
    return labels[this.status] || this.status;
  }

  /**
   * 승인 가능한지 확인
   * @returns {boolean}
   */
  canAccept() {
    return this.status === 'REQUESTED';
  }

  /**
   * 거절 가능한지 확인
   * @returns {boolean}
   */
  canReject() {
    return this.status === 'REQUESTED';
  }
}

export class DriverDispatchList {
  /**
   * @param {Object} data - API 응답 데이터
   * @param {Array} data.content - 배차 목록
   * @param {number} data.totalElements - 전체 개수
   * @param {number} data.totalPages - 전체 페이지
   * @param {number} data.currentPage - 현재 페이지
   */
  constructor(data = {}) {
    this.dispatches = (data.content || []).map(item => new DriverDispatch(item));
    this.totalElements = data.totalElements || 0;
    this.totalPages = data.totalPages || 0;
    this.currentPage = data.currentPage || 0;
  }

  /**
   * 수락 가능한 배차 개수
   * @returns {number}
   */
  getAcceptableCount() {
    return this.dispatches.filter(d => d.canAccept()).length;
  }

  /**
   * 다음 페이지가 있는지 확인
   * @returns {boolean}
   */
  hasNextPage() {
    return this.currentPage < this.totalPages - 1;
  }
}

export class RejectDispatchRequest {
  /**
   * @param {number} dispatchId - 배차 ID
   * @param {string} [reason] - 거절 사유
   */
  constructor(dispatchId, reason = null) {
    this.dispatchId = dispatchId;
    this.reason = reason || '';
  }
}