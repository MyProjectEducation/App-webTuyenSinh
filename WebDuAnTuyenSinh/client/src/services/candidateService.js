import api from './api';

export const candidateService = {
    getAllCandidates: async () => {
        const response = await api.get('/candidates');
        return response.data;
    },
    
    getCandidateById: async (id) => {
        const response = await api.get(`/candidates/${id}`);
        return response.data;
    },
    
    create: async (data) => {
        const response = await api.post('/candidates', data);
        return response.data;
    },

    update: async (id, data) => {
        const response = await api.put(`/candidates/${id}`, data);
        return response.data;
    },

    delete: async (id) => {
        const response = await api.delete(`/candidates/${id}`);
        return response.data;
    }
};

export const authService = {
    login: async (username, password) => {
        const response = await api.post('/auth/login', { username, password });
        return response.data; // Server sẽ trả về { token, user }
    }
}
