import api from './api';

export const bonusService = {
  getAll: async () => {
    const response = await api.get('/bonus');
    return response.data;
  },
  create: async (data) => {
    const response = await api.post('/bonus', data);
    return response.data;
  },
  update: async (id, data) => {
    const response = await api.put(`/bonus/${id}`, data);
    return response.data;
  },
  delete: async (id) => {
    const response = await api.delete(`/bonus/${id}`);
    return response.data;
  }
};
