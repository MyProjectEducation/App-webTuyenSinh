import api from './api';

export const majorComboService = {
    getAll: async () => {
        const response = await api.get('/major-combos');
        return response.data;
    },
    create: async (data) => {
        const response = await api.post('/major-combos', data);
        return response.data;
    },
    delete: async (id) => {
        const response = await api.delete(`/major-combos/${id}`);
        return response.data;
    }
};
