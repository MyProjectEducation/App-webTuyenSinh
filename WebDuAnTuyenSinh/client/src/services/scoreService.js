import api from './api';

export const scoreService = {
  getAll: async () => {
    const response = await api.get('/scores');
    return response.data;
  },
  create: async (data) => {
    const response = await api.post('/scores', data);
    return response.data;
  },
  update: async (id, data) => {
    const response = await api.put(`/scores/${id}`, data);
    return response.data;
  },
  delete: async (id) => {
    const response = await api.delete(`/scores/${id}`);
    return response.data;
  }
};
