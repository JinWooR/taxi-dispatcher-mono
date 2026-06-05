import { Auth } from './common/auth.js';

function initApp() {
  const statusSection = document.getElementById('status-section');
  const dashboardSection = document.getElementById('dashboard-section');
  const dashboardButton = document.getElementById('dashboard-button');
  const logoutButton = document.getElementById('logout-button');

  if (Auth.isAuthenticated()) {
    statusSection.style.display = 'none';
    dashboardSection.style.display = 'block';

    const role = Auth.getRole();
    if (role === 'USER') {
      dashboardButton.textContent = '고객 대시보드';
      dashboardButton.href = '/pages/customer/index.html';
    } else if (role === 'DRIVER') {
      dashboardButton.textContent = '기사 대시보드';
      dashboardButton.href = '/pages/driver/index.html';
    } else {
      dashboardButton.textContent = '프로필 등록';
      if (role === 'BASIC') {
        dashboardButton.href = '#';
        dashboardButton.style.display = 'none';
      }
    }

    logoutButton.addEventListener('click', logout);
  } else {
    statusSection.style.display = 'block';
    dashboardSection.style.display = 'none';
  }
}

function logout() {
  Auth.clearToken();
  window.location.reload();
}

window.addEventListener('load', initApp);
