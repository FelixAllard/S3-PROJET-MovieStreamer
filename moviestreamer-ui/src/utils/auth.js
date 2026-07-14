export function getLoggedUser() {
  const token = localStorage.getItem('token');
  if (!token) return null;

  try {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(atob(base64).split('').map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)).join(''));
    const payload = JSON.parse(jsonPayload);
    const storedDbUserId = localStorage.getItem('db_user_id');

    return {
      id: storedDbUserId || payload.userId || payload.db_id || payload.sub || null,
      username: payload.preferred_username || payload.name || 'User',
      roles: payload.realm_access?.roles || [],
      isAdmin: payload.realm_access?.roles?.includes('admin') || false
    };
  } catch (e) {
    console.error('Failed parsing access credentials payload:', e);
    return null;
  }
}

export function isLoggedIn() {
  return Boolean(localStorage.getItem('token'));
}

export async function resolveDbUserId(apiClient) {
  const storedDbUserId = localStorage.getItem('db_user_id');
  if (storedDbUserId) return storedDbUserId;

  const user = await apiClient.get('/user/me');
  if (user?.id) {
    localStorage.setItem('db_user_id', user.id);
    return user.id;
  }

  return null;
}
