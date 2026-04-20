import axios from 'axios';

// Khởi tạo một đối tượng Axios dùng chung
const api = axios.create({
    baseURL: import.meta.env.VITE_API_URL || 'http://localhost:5000/api',
    // Cờ này bắt buộc nếu dùng CORS credentials: true ở Server
    withCredentials: true 
});

// Interceptor Request: Tự động đính kèm Token JWT trước khi gửi
api.interceptors.request.use(
    (config) => {
        // Lấy token từ LocalStorage (Sẽ lưu lúc Đăng Nhập)
        const token = localStorage.getItem('tuyensinh_token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// Interceptor Response: Tự động bắt lỗi Unauthorized nếu token hết hạn
api.interceptors.response.use(
    (response) => {
        return response;
    },
    (error) => {
        if (error.response && error.response.status === 401) {
            // Có thể tự động dọn dẹp localStorage và chuyển hướng về Login
            console.error("Lỗi xác thực (401): Vui lòng đăng nhập lại", error);
            // window.location.href = '/login'; 
        }
        return Promise.reject(error);
    }
);

export default api;
