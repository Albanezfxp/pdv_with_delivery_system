import axios from "axios";

const API = process.env.REACT_APP_API_URL

const api = axios.create({
    baseURL: API,
    timeout: 10000,
    headers: {
        'Content-Type': 'application/json',
    },
})

  
// Interceptor para tratar erros globais
api.interceptors.response.use(
    (response) => response,
    (error) => {
      if (error.response?.status === 401) {
        // Token inválido/expirou - redirecionar para login
        window.location.href = '/login';
      }
      return Promise.reject(error);
    }
  );
  
  export default api;