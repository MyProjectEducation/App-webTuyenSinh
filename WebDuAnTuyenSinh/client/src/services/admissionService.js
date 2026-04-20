import api from './api';

export const admissionService = {
  getAll: async () => {
    const response = await api.get('/admissions');
    return response.data;
  },
  create: async (data) => {
    const response = await api.post('/admissions', data);
    return response.data;
  },
  update: async (id, data) => {
    const response = await api.put(`/admissions/${id}`, data);
    return response.data;
  },
  delete: async (id) => {
    const response = await api.delete(`/admissions/${id}`);
    return response.data;
  },
  saveResults: async (data) => {
    const response = await api.post('/admissions/save-results', data);
    return response.data;
  }
};
