import { Auth } from '../../common/auth.js';
import { AuthService } from '../../common/services/AuthService.js';
import { DriverService } from '../../common/services/DriverService.js';

let pageMode = null;
let currentProfile = null;

function initProfilePage() {
  loadProfile();
  attachEventListeners();
}

/**
 * 프로필 정보 로드
 */
async function loadProfile() {
  try {
    const profile = await DriverService.getProfile();
    currentProfile = profile;
    setMode('update', profile);
  } catch (error) {
    if (error.message && error.message.includes('404')) {
      // 프로필이 없음 - 등록 모드
      setMode('register');
    } else {
      showMessage(error.message || '프로필 조회에 실패했습니다.', 'error');
      setTimeout(() => {
        window.location.href = '/pages/auth/login.html';
      }, 2000);
    }
  }
}

/**
 * 페이지 모드 설정
 * @param {string} mode - 'register' 또는 'update'
 * @param {DriverProfile} [profile] - 프로필 정보 (update 모드일 때만)
 */
function setMode(mode, profile = null) {
  pageMode = mode;

  const subtitle = document.getElementById('subtitle');
  const form = document.getElementById('profile-form');
  const nameInput = document.getElementById('name');
  const phoneInput = document.getElementById('phone');
  const licenseNumberInput = document.getElementById('licenseNumber');
  const loading = document.getElementById('loading');
  const formSection = document.getElementById('form-section');

  loading.style.display = 'none';
  formSection.style.display = 'block';

  if (mode === 'register') {
    subtitle.textContent = '기사 프로필을 등록하세요';
    nameInput.value = '';
    phoneInput.value = '';
    licenseNumberInput.value = '';
  } else if (mode === 'update' && profile) {
    subtitle.textContent = '프로필 정보를 수정하세요';
    nameInput.value = profile.name || '';
    phoneInput.value = profile.phone || '';
    licenseNumberInput.value = profile.licenseNumber || '';
  }
}

/**
 * 이벤트 리스너 부착
 */
function attachEventListeners() {
  const form = document.getElementById('profile-form');
  const btnCancel = document.getElementById('btn-cancel');

  if (form) {
    form.addEventListener('submit', handleFormSubmit);
  }

  if (btnCancel) {
    btnCancel.addEventListener('click', handleCancel);
  }
}

/**
 * 폼 제출 처리
 */
async function handleFormSubmit(event) {
  event.preventDefault();

  const nameInput = document.getElementById('name');
  const phoneInput = document.getElementById('phone');
  const licenseNumberInput = document.getElementById('licenseNumber');
  const btnSave = document.getElementById('btn-save');

  const name = nameInput.value.trim();
  const phone = phoneInput.value.trim();
  const licenseNumber = licenseNumberInput.value.trim();

  if (!name || !phone || !licenseNumber) {
    showMessage('모든 필드를 입력하세요.', 'error');
    return;
  }

  btnSave.disabled = true;

  try {
    // 1. 프로필 저장
    if (pageMode === 'register') {
      await DriverService.registerProfile(name, phone, licenseNumber);
    } else if (pageMode === 'update') {
      await DriverService.updateProfile(name, phone, licenseNumber);
    }

    showMessage('프로필이 저장되었습니다. 권한을 승격 중입니다...', 'success');

    // 2. 권한 승격
    const upgradeResponse = await AuthService.upgradeDriver();

    if (!upgradeResponse.isValid()) {
      showMessage('권한 승격 응답이 유효하지 않습니다.', 'error');
      btnSave.disabled = false;
      return;
    }

    // 3. 새 토큰 저장
    const accessToken = upgradeResponse.getAccessToken();
    const refreshToken = upgradeResponse.getRefreshToken();
    const role = upgradeResponse.getRole();

    if (accessToken) {
      Auth.saveToken(accessToken);
    }
    if (refreshToken) {
      Auth.saveRefreshToken(refreshToken);
    }
    if (role) {
      Auth.saveRole(role);
    }

    // 4. 대시보드로 이동
    showMessage('권한이 승격되었습니다. 대시보드로 이동합니다...', 'success');
    setTimeout(() => {
      window.location.href = '/pages/driver/index.html';
    }, 1000);
  } catch (error) {
    showMessage(error.message || '프로필 저장에 실패했습니다.', 'error');
    btnSave.disabled = false;
  }
}

/**
 * 취소 버튼 처리
 */
function handleCancel() {
  window.location.href = '/index.html';
}

/**
 * 메시지 표시
 * @param {string} message - 메시지
 * @param {string} type - 'success' 또는 'error'
 */
function showMessage(message, type) {
  const messageEl = document.getElementById('message');
  messageEl.textContent = message;
  messageEl.className = `message ${type}`;
  messageEl.style.display = 'block';
}

window.addEventListener('load', initProfilePage);
