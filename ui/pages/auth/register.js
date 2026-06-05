import { Auth } from '../../common/auth.js';
import { AuthService } from '../../common/services/AuthService.js';

let selectedRole = null;
let redirectTo = null;

function initRegisterPage() {
  // URL query param에서 role과 redirectTo 가져오기
  const params = new URLSearchParams(window.location.search);
  selectedRole = params.get('role'); // CUSTOMER or DRIVER
  redirectTo = params.get('redirectTo'); // 원래 페이지 URL

  // role이 없으면 메인 페이지로 리다이렉트
  if (!selectedRole) {
    window.location.href = '/index.html';
    return;
  }

  attachEventListeners();
  redirectIfAlreadyLoggedIn();
}

/**
 * 이미 로그인된 경우 대시보드로 리다이렉트
 */
function redirectIfAlreadyLoggedIn() {
  if (Auth.isAuthenticated()) {
    const role = Auth.getRole();
    if (role === 'USER') {
      window.location.href = '/pages/customer/index.html';
    } else if (role === 'DRIVER') {
      window.location.href = '/pages/driver/index.html';
    }
  }
}

/**
 * 이벤트 리스너 부착
 */
function attachEventListeners() {
  const form = document.getElementById('register-form');
  if (form) {
    form.addEventListener('submit', handleRegisterSubmit);
  }
}

/**
 * 회원가입 폼 제출 처리
 */
async function handleRegisterSubmit(event) {
  event.preventDefault();

  const emailInput = document.getElementById('email');
  const passwordInput = document.getElementById('password');
  const btnRegister = document.getElementById('btn-register');
  const errorMessage = document.getElementById('error-message');
  const loading = document.getElementById('loading');

  const loginId = emailInput.value.trim();
  const password = passwordInput.value;

  if (!loginId || !password) {
    showError('모든 필드를 입력하세요.');
    return;
  }

  btnRegister.disabled = true;
  loading.style.display = 'block';
  errorMessage.style.display = 'none';

  try {
    // 1. 기본 회원가입 (역할 없이)
    const registerResponse = await AuthService.register(loginId, password);

    if (!registerResponse.isValid()) {
      showError('회원가입 응답이 유효하지 않습니다.');
      btnRegister.disabled = false;
      loading.style.display = 'none';
      return;
    }

    // 2. 자동 로그인
    const loginResponse = await AuthService.login(loginId, password);

    if (!loginResponse.isValid()) {
      showError('로그인 응답이 유효하지 않습니다.');
      btnRegister.disabled = false;
      loading.style.display = 'none';
      return;
    }

    // 3. 토큰 저장
    Auth.saveToken(loginResponse.getAccessToken());
    Auth.saveRefreshToken(loginResponse.getRefreshToken());
    Auth.saveRole(loginResponse.getRole());

    // 4. 권한에 따라 프로필 등록 페이지로 이동
    if (selectedRole === 'CUSTOMER') {
      window.location.href = '/pages/customer/profile.html';
    } else if (selectedRole === 'DRIVER') {
      window.location.href = '/pages/driver/profile.html';
    } else {
      showError('올바르지 않은 역할입니다.');
      btnRegister.disabled = false;
      loading.style.display = 'none';
    }
  } catch (error) {
    showError(error.message || '회원가입에 실패했습니다.');
    btnRegister.disabled = false;
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

window.addEventListener('load', initRegisterPage);
