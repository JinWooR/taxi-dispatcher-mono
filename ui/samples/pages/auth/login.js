/**
 * 로그인 페이지 로직
 *
 * 흐름:
 * 1. 폼 제출 이벤트 감지
 * 2. 입력값 검증
 * 3. API 호출 (POST /api/auth/login)
 * 4. 토큰 및 역할 저장
 * 5. 역할에 따라 대시보드로 이동
 */

function initLoginPage() {
  attachEventListeners();
  redirectIfAlreadyLoggedIn();
}

/**
 * 이미 로그인된 경우 대시보드로 리다이렉트
 */
function redirectIfAlreadyLoggedIn() {
  if (Auth.isAuthenticated()) {
    if (Auth.isCustomer()) {
      window.location.href = '/samples/pages/customer/index.html';
    } else if (Auth.isDriver()) {
      window.location.href = '/samples/pages/driver/index.html';
    }
  }
}

/**
 * 이벤트 리스너 부착
 */
function attachEventListeners() {
  const form = document.getElementById('login-form');
  if (form) {
    form.addEventListener('submit', handleLoginSubmit);
  }
}

/**
 * 로그인 폼 제출 처리
 * @param {Event} event
 */
async function handleLoginSubmit(event) {
  event.preventDefault();

  const emailInput = document.getElementById('email');
  const passwordInput = document.getElementById('password');
  const btnLogin = document.getElementById('btn-login');
  const errorMessage = document.getElementById('error-message');
  const loading = document.getElementById('loading');

  const email = emailInput.value.trim();
  const password = passwordInput.value;

  // 입력값 검증
  if (!email || !password) {
    showError('이메일과 비밀번호를 입력하세요.');
    return;
  }

  // 로딩 상태
  btnLogin.disabled = true;
  loading.style.display = 'block';
  errorMessage.style.display = 'none';

  try {
    // 로그인 API 호출
    const response = await AuthService.login(email, password);

    // 토큰 및 역할 저장
    if (response.token) {
      Auth.saveToken(response.token);
    }
    if (response.refreshToken) {
      Auth.saveRefreshToken(response.refreshToken);
    }
    if (response.role) {
      Auth.saveRole(response.role);
    }

    // 역할에 따라 이동
    if (response.role === 'CUSTOMER') {
      window.location.href = '/samples/pages/customer/index.html';
    } else if (response.role === 'DRIVER') {
      window.location.href = '/samples/pages/driver/index.html';
    } else {
      // 역할 불명
      window.location.href = '/samples/index.html';
    }
  } catch (error) {
    showError(error.message || '로그인에 실패했습니다.');
    btnLogin.disabled = false;
    loading.style.display = 'none';
  }
}

/**
 * 에러 메시지 표시
 * @param {string} message
 */
function showError(message) {
  const errorMessage = document.getElementById('error-message');
  errorMessage.textContent = message;
  errorMessage.style.display = 'block';
}

// 페이지 로드 시 초기화
window.addEventListener('load', initLoginPage);
