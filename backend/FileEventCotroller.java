import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({ baseURL: API_BASE_URL });
api.interceptors.request.use((config) => {
  const token = (typeof sessionStorage !== 'undefined' && sessionStorage.getItem('token'))
    || (typeof localStorage !== 'undefined' && localStorage.getItem('token'))
    || null;
  if (token) {
    config.headers = config.headers || {};
    config.headers['Authorization'] = `Bearer ${token}`;
  }
  return config;
});

const notificationService = {
  async getForUser(userId) {
    const { data } = await api.get(`/notifications`, { params: { userId } });
    return Array.isArray(data) ? data : (data?.notifications || []);
  },

  async markRead(id) {
    await api.post(`/notifications/${encodeURIComponent(id)}/read`);
  },

  async markAllRead(userId) {
    await api.post(`/notifications/read-all`, { userId });
  },

  async remove(id) {
    await api.delete(`/notifications/${encodeURIComponent(id)}`);
  },

  async clearAll(userId) {
    await api.delete(`/notifications`, { params: { userId } });
  }
};

export default notificationService;
