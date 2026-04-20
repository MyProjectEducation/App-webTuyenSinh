import api from './api';

export const dashboardService = {
    getOverview: () => api.get('/dashboard/overview'),
    getTopMajors: () => api.get('/dashboard/top-majors'),
    getScoreDistribution: () => api.get('/dashboard/score-distribution')
};
