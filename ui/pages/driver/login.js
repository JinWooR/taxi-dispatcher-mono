import { Auth } from '../../common/auth.js';
import { AuthService } from '../../common/services/AuthService.js';

function initLoginPage() {
  attachEventListeners();
  redirectIfAlreadyLoggedIn();
}

/**
 * 이미 로그인된 경우 대시보드로 리다이렉트
 */
function redirectIfAlreadyLoggedIn() {
  if (Auth.isAuthenticated()) {
    const role = Auth.getRole();
    if (role === 'DRIVER') {
      window.location.href = '/pages/driver/index.html';
    } else if (role === 'USER') {
      window.location.href = '/pages/customer/index.html';
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
 */
async function handleLoginSubmit(event) {
  event.preventDefault();

  const emailInput = document.getElementById('email');
  const passwordInput = document.getElementById('password');
  const btnLogin = document.getElementById('btn-login');
  const errorMessage = document.getElementById('error-message');
  const loading = document.getElementById('loading');

  const loginId = emailInput.value.trim();
  const password = passwordInput.value;

  if (!loginId || !password) {
    showError('이메일과 비밀번호를 입력하세요.');
    return;
  }

  btnLogin.disabled = true;
  loading.style.display = 'block';
  errorMessage.style.display = 'none';

  try {
    // 1. 기사 권한 로그인 시도
    let response;
    try {
      response = await AuthService.loginDriver(loginId, password);
    } catch (error) {
      // 2. 실패 시 기본 로그인 시도
      response = await AuthService.login(loginId, password);
    }

    if (!response.isValid()) {
      showError('로그인 응답이 유효하지 않습니다.');
      btnLogin.disabled = false;
      loading.style.display = 'none';
      return;
    }

    const accessToken = response.getAccessToken();
    const refreshToken = response.getRefreshToken();
    const role = response.getRole();

    if (!accessToken || !refreshToken || !role) {
      showError('로그인 응답이 유효하지 않습니다.');
      btnLogin.disabled = false;
      loading.style.display = 'none';
      return;
    }

    // 토큰 저장
    Auth.saveToken(accessToken);
    Auth.saveRefreshToken(refreshToken);
    Auth.saveRole(role);

    // 3. 역할에 따라 분기
    if (role === 'DRIVER') {
      window.location.href = '/pages/driver/index.html';
    } else if (role === 'USER') {
      showError('고객 권한으로 로그인되었습니다. 고객 페이지로 이동합니다.');
      setTimeout(() => {
        window.location.href = '/pages/customer/index.html';
      }, 2000);
    } else if (role === 'BASIC') {
      window.location.href = '/pages/driver/profile.html';
    } else {
      window.location.href = '/index.html';
    }
  } catch (error) {
    showError(error.message || '로그인에 실패했습니다. 계정 정보를 확인하세요.');
    btnLogin.disabled = false;
    loading.style.display = 'none';
  }
}

/**
 * 에러 메시지 표시
 */
function showError(message) {
  const errorMessage = document.getElementById('error-message');
  errorMessage.textContent = message;
  errorMessage.style.display = 'block';
}

window.addEventListener('load', initLoginPage);
