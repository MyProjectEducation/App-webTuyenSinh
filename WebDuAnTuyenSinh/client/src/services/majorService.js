import api from './api';

export const majorService = {
    getAll: async () => {
        const response = await api.get('/majors');
        return response.data;
    },
    create: async (data) => {
        const response = await api.post('/majors', data);
        return response.data;
    },
    update: async (id, data) => {
        const response = await api.put(`/majors/${id}`, data);
        return response.data;
    },
    delete: async (id) => {
        const response = await api.delete(`/majors/${id}`);
        return response.data;
    },
    importMajors: async (formData) => {
        const response = await api.post('/majors/import', formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        });
        return response.data;
    }
};
