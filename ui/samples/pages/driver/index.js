/**
 * 기사 대시보드 로직
 *
 * 흐름:
 * 1. 페이지 로드 시 인증 확인
 * 2. 사용자 정보 조회 및 표시
 * 3. 메뉴 선택에 따라 콘텐츠 영역 업데이트
 */

function initDriverDashboard() {
  checkAuthentication();
  loadUserProfile();
}

/**
 * 인증 확인 (로그인 필수, 기사 권한 필수)
 */
function checkAuthentication() {
  if (!Auth.isAuthenticated()) {
    window.location.href = '/samples/pages/auth/login.html';
    return;
  }

  if (!Auth.isDriver()) {
    // 기사가 아닌 경우 고객 대시보드로 이동
    window.location.href = '/samples/pages/customer/index.html';
    return;
  }
}

/**
 * 사용자 프로필 조회 및 표시
 */
async function loadUserProfile() {
  try {
    const userEmail = document.getElementById('user-email');
    userEmail.textContent = 'loading...';

    // API 호출 (실제로는 서비스 조회, 여기서는 토큰에서 이메일 추출 예시)
    // const profile = await ApiClient.get(`${API_BASE_URL.DRIVER}/api/drivers/me`);
    // userEmail.textContent = profile.email;

    // 샘플: localStorage에서 이메일 추출 (실제로는 위의 API 호출 사용)
    userEmail.textContent = '기사 님';
  } catch (error) {
    console.error('프로필 로드 실패:', error);
    const userEmail = document.getElementById('user-email');
    userEmail.textContent = 'error';
  }
}

/**
 * 메뉴 이동
 * @param {string} page - 이동할 페이지 (profile, pending, location)
 */
function navigateTo(page) {
  const contentDiv = document.getElementById('content');

  switch (page) {
    case 'profile':
      contentDiv.innerHTML = `
        <h2>프로필</h2>
        <p>이곳은 기사 프로필 관리 영역입니다.</p>
        <p><a href="/samples/pages/driver/profile.html">프로필 페이지로 이동</a></p>
      `;
      contentDiv.classList.add('active');
      break;

    case 'pending':
      contentDiv.innerHTML = `
        <h2>Pending 배차</h2>
        <p>이곳은 배차 요청 대기 목록 영역입니다.</p>
        <p><a href="/samples/pages/driver/dispatch/pending.html">Pending 배차 페이지로 이동</a></p>
      `;
      contentDiv.classList.add('active');
      break;

    case 'location':
      contentDiv.innerHTML = `
        <h2>위치</h2>
        <p>이곳은 현재 위치 업데이트 영역입니다.</p>
        <p>기사의 실시간 위치를 업데이트합니다.</p>
      `;
      contentDiv.classList.add('active');
      break;

    default:
      contentDiv.innerHTML = '';
      contentDiv.classList.remove('active');
  }
}

/**
 * 로그아웃
 */
function handleLogout() {
  Auth.clearToken();
  window.location.href = '/samples/pages/auth/login.html';
}

// 페이지 로드 시 초기화
window.addEventListener('load', initDriverDashboard);
