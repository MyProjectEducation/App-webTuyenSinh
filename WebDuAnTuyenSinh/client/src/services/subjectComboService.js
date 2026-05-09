import api from './api';

export const subjectComboService = {
    getAll: async () => {
        const response = await api.get('/subject-combos');
        return response.data;
    },
    create: async (data) => {
        const response = await api.post('/subject-combos', data);
        return response.data;
    },
    update: async (id, data) => {
        const response = await api.put(`/subject-combos/${id}`, data);
        return response.data;
    },
    delete: async (id) => {
        const response = await api.delete(`/subject-combos/${id}`);
        return response.data;
    },
    importSubjectCombos: async (formData) => {
        const response = await api.post('/subject-combos/import', formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        });
        return response.data;
    }
};
