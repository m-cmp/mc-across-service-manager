import { AxiosRequestConfig } from 'axios';

export const axiosBaseOptions: AxiosRequestConfig = {
  baseURL: import.meta.env.VITE_API_BASE_URL,
  // timeout: 8000,
  headers: {
    'Content-Type': 'application/json',
    'Access-Control-Allow-Origin': `*`,
    'Access-Control-Allow-Credentials': 'true',
  },
};
