import api from './api';

export const conversionService = {
    getAll: async () => {
        const response = await api.get('/conversions');
        return response.data;
    },
    create: async (data) => {
        const response = await api.post('/conversions', data);
        return response.data;
    },
    update: async (id, data) => {
        const response = await api.put(`/conversions/${id}`, data);
        return response.data;
    },
    delete: async (id) => {
        const response = await api.delete(`/conversions/${id}`);
        return response.data;
    }
};
