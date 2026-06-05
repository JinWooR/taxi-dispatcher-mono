export class CustomerProfile {
  /**
   * @param {Object} data - API 응답 데이터
   * @param {string} data.customerId - 고객 ID (UUID)
   * @param {string} data.accountId - 계정 ID (UUID)
   * @param {string} data.name - 이름
   * @param {string} data.phone - 연락처
   * @param {string} data.status - 상태 (ACTIVE 등)
   * @param {string} data.createdAt - 생성 시각 (ISO 8601)
   * @param {string} data.updatedAt - 수정 시각 (ISO 8601)
   */
  constructor(data = {}) {
    this.customerId = data.customerId || '';
    this.accountId = data.accountId || '';
    this.name = data.name || '';
    this.phone = data.phone || '';
    this.status = data.status || '';
    this.createdAt = data.createdAt || null;
    this.updatedAt = data.updatedAt || null;
  }

  /**
   * 프로필이 완성되었는지 확인 (필수 정보)
   * @returns {boolean}
   */
  isComplete() {
    return !!this.name && !!this.phone;
  }
}

export class CustomerProfileRequest {
  /**
   * @param {string} name - 이름
   * @param {string} phone - 연락처
   */
  constructor(name, phone) {
    this.name = name;
    this.phone = phone;
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
      throw new Error('연락처는 필수입니다');
    }
  }
}

export class DispatchRequest {
  /**
   * @param {string} startLocation - 출발지
   * @param {string} endLocation - 도착지
   * @param {Object} [options] - 추가 옵션
   * @param {string} [options.memo] - 메모
   */
  constructor(startLocation, endLocation, options = {}) {
    this.startLocation = startLocation;
    this.endLocation = endLocation;
    this.memo = options.memo || '';
  }

  /**
   * 요청 검증
   * @throws {Error}
   */
  validate() {
    if (!this.startLocation || typeof this.startLocation !== 'string') {
      throw new Error('출발지는 필수입니다');
    }
    if (!this.endLocation || typeof this.endLocation !== 'string') {
      throw new Error('도착지는 필수입니다');
    }
  }
}

export class Dispatch {
  /**
   * @param {Object} data - API 응답 데이터
   * @param {number} data.id - 배차 ID
   * @param {string} data.status - 상태 (REQUESTED, ASSIGNED, IN_PROGRESS, COMPLETED, CANCELLED)
   * @param {string} data.startLocation - 출발지
   * @param {string} data.endLocation - 도착지
   * @param {Object} [data.customer] - 고객 정보
   * @param {Object} [data.driver] - 기사 정보
   */
  constructor(data = {}) {
    this.id = data.id || null;
    this.status = data.status || 'REQUESTED';
    this.startLocation = data.startLocation || '';
    this.endLocation = data.endLocation || '';
    this.customer = data.customer || null;
    this.driver = data.driver || null;
    this.requestedAt = data.requestedAt || null;
    this.assignedAt = data.assignedAt || null;
    this.completedAt = data.completedAt || null;
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
}

export class DispatchList {
  /**
   * @param {Object} data - API 응답 데이터
   * @param {Array} data.content - 배차 목록
   * @param {number} data.totalElements - 전체 개수
   * @param {number} data.totalPages - 전체 페이지
   * @param {number} data.currentPage - 현재 페이지
   */
  constructor(data = {}) {
    this.dispatches = (data.content || []).map(item => new Dispatch(item));
    this.totalElements = data.totalElements || 0;
    this.totalPages = data.totalPages || 0;
    this.currentPage = data.currentPage || 0;
  }

  /**
   * 다음 페이지가 있는지 확인
   * @returns {boolean}
   */
  hasNextPage() {
    return this.currentPage < this.totalPages - 1;
  }

  /**
   * 이전 페이지가 있는지 확인
   * @returns {boolean}
   */
  hasPreviousPage() {
    return this.currentPage > 0;
  }
}
