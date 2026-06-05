/**
 * 고객 대시보드 로직
 *
 * 흐름:
 * 1. 페이지 로드 시 인증 확인
 * 2. 사용자 정보 조회 및 표시
 * 3. 메뉴 선택에 따라 콘텐츠 영역 업데이트
 */

function initCustomerDashboard() {
  checkAuthentication();
  loadUserProfile();
}

/**
 * 인증 확인 (로그인 필수)
 */
function checkAuthentication() {
  if (!Auth.isAuthenticated()) {
    window.location.href = '/samples/pages/auth/login.html';
    return;
  }

  if (!Auth.isCustomer()) {
    // 고객이 아닌 경우 기사 대시보드로 이동
    window.location.href = '/samples/pages/driver/index.html';
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
    // const profile = await ApiClient.get(`${API_BASE_URL.CUSTOMER}/api/customers/me`);
    // userEmail.textContent = profile.email;

    // 샘플: localStorage에서 이메일 추출 (실제로는 위의 API 호출 사용)
    userEmail.textContent = '고객 님';
  } catch (error) {
    console.error('프로필 로드 실패:', error);
    const userEmail = document.getElementById('user-email');
    userEmail.textContent = 'error';
  }
}

/**
 * 메뉴 이동
 * @param {string} page - 이동할 페이지 (profile, request, list)
 */
function navigateTo(page) {
  const contentDiv = document.getElementById('content');

  switch (page) {
    case 'profile':
      contentDiv.innerHTML = `
        <h2>프로필</h2>
        <p>이곳은 고객 프로필 관리 영역입니다.</p>
        <p><a href="/samples/pages/customer/profile.html">프로필 페이지로 이동</a></p>
      `;
      contentDiv.classList.add('active');
      break;

    case 'request':
      contentDiv.innerHTML = `
        <h2>배차 요청</h2>
        <p>이곳은 새로운 배차 요청 영역입니다.</p>
        <p><a href="/samples/pages/customer/dispatch/request.html">배차 요청 페이지로 이동</a></p>
      `;
      contentDiv.classList.add('active');
      break;

    case 'list':
      contentDiv.innerHTML = `
        <h2>내 배차 목록</h2>
        <p>이곳은 배차 요청 목록 영역입니다.</p>
        <p><a href="/samples/pages/customer/dispatch/list.html">배차 목록 페이지로 이동</a></p>
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
window.addEventListener('load', initCustomerDashboard);
