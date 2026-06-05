/**
 * 서비스 설정
 * 각 마이크로서비스의 baseURL을 정의합니다.
 */

const API_BASE_URL = {
  AUTH: 'http://localhost:8001',          // Account Service
  CUSTOMER: 'http://localhost:8002',      // Customer Service
  DRIVER: 'http://localhost:8003',        // Driver Service
  DISPATCHER: 'http://localhost:8004'     // Dispatcher Service
};

// 모듈 내보내기 (ES6 모듈 미사용, 전역 객체로 노출)
// 사용 예: API_BASE_URL.AUTH
