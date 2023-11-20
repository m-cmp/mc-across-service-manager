import axios from 'axios';
import type { AxiosInstance } from 'axios';
import { SnackbarKey, closeSnackbar } from 'notistack';
import { displaySnackbar } from '@/components/snackBar/snackbarController';
import { axiosBaseOptions } from '@/network/axios/axiosConfig';
import type { Upload } from '@/network/axios/type';

export interface CustomError {
  message: string;
  name: string;
  config: {
    timeout: number;
    baseURL: string;
    method: string;
    url: string;
  };
  code: string;
  status: number | null;
  response?: {
    data?: ApiErrorResponse | string;
  };
}

export interface ApiErrorResponse {
  from?: string;
  code?: number;
  status?: string;
  message?: string;
}

/**
 * 기본 API 요청, 응답
 */
console.log(import.meta.env);
const axiosInstance: AxiosInstance = axios.create(axiosBaseOptions);

const snackbarKeys: Record<string, SnackbarKey | null> = {};

/** axios request 인터셉터 (개발용 로그) **/
axiosInstance.interceptors.request.use(
  request => {
    if (request.method !== 'get') {
      const url = request.url;
      console.log(request);
      if (url) {
        snackbarKeys[url] = displaySnackbar(` ${request.url}`, 'info', null, null);
      }
    }
    return request;
  },
  error => {
    console.log(`axios reqeust rror`, error);
    const url = error.config.url;
    if (snackbarKeys[url] !== null && error.config.method !== 'get') {
      closeSnackbar(snackbarKeys[url] as SnackbarKey);
      delete snackbarKeys[url];
    }
    delete snackbarKeys[url];
    return Promise.reject(error);
  }
);

/** axios response 인터셉터 **/
axiosInstance.interceptors.response.use(
  response => {
    const url = response.config.url;

    if (url) {
      if (snackbarKeys[url] !== null && response.config.method !== 'get') {
        closeSnackbar(snackbarKeys[url] as SnackbarKey);
      }

      if (response.config.method !== 'get') {
        if (response.data instanceof Blob) {
          displaySnackbar(`BLOB`, 'success', null);
          return response;
        } else {
          console.log('es', response);
          displaySnackbar(`요청이 성공적으로 수행되었습니다.`, 'success', null);
          return response.data;
        }
      } else {
        return response.data instanceof Blob ? response : response.data;
      }
    }
  },
  (error: CustomError) => {
    let errorMessage = error.message;
    const url = error.config.url;
    let requestFrom = '';
    if (url) {
      if (snackbarKeys[url] !== null && error.config.method !== 'get') {
        closeSnackbar(snackbarKeys[url] as SnackbarKey);
        delete snackbarKeys[url];
        const urlSegments = url.split('/');
        if (urlSegments.length > 3) {
          requestFrom = urlSegments[3];
        }
      }
      delete snackbarKeys[url];
    }

    if (error.response) {
      if (typeof error.response.data === 'string') {
        errorMessage = error.response.data;
      } else {
        const apiErrorResponse: ApiErrorResponse = {
          from: error.response.data?.from,
          code: error.response.data?.code,
          status: error.response.data?.status,
          message: error.response.data?.message,
        };
        errorMessage = `${apiErrorResponse.from}-${apiErrorResponse.code} : ${apiErrorResponse.message}`;
      }
    }
    displaySnackbar(`${errorMessage} : ${requestFrom}`, 'error', error);

    return Promise.reject(error);
  }
);

export const get = <T>(url: string, data?: object): Promise<T> => {
  const baseUrl = url.split('/')[3];
  console.log('Request API: ', url);
  if (baseUrl === 'orchestrator') {
    axiosInstance.defaults.baseURL = import.meta.env.VITE_API_BASE_URL_ORCHESTRATOR;
  } else if (baseUrl === 'controller') {
    axiosInstance.defaults.baseURL = import.meta.env.VITE_API_BASE_URL_CONTROLLER;
  } else {
    axiosInstance.defaults.baseURL = axiosBaseOptions.baseURL;
  }
  return axiosInstance.get(url, { params: data });
};

export const post = <T>(url: string, data?: object, params?: object): Promise<T> => {
  const baseUrl = url.split('/')[3];
  console.log('Request API: ', url);
  if (baseUrl === 'orchestrator') {
    axiosInstance.defaults.baseURL = import.meta.env.VITE_API_BASE_URL_ORCHESTRATOR;
  } else if (baseUrl === 'controller') {
    axiosInstance.defaults.baseURL = import.meta.env.VITE_API_BASE_URL_CONTROLLER;
  } else {
    axiosInstance.defaults.baseURL = axiosBaseOptions.baseURL;
  }
  return axiosInstance.post(url, data, { params });
};

export const put = <T>(url: string, data?: object, params?: object): Promise<T> => {
  const baseUrl = url.split('/')[3];
  console.log('Request API: ', url);
  if (baseUrl === 'orchestrator') {
    axiosInstance.defaults.baseURL = import.meta.env.VITE_API_BASE_URL_ORCHESTRATOR;
  } else if (baseUrl === 'controller') {
    axiosInstance.defaults.baseURL = import.meta.env.VITE_API_BASE_URL_CONTROLLER;
  } else {
    axiosInstance.defaults.baseURL = axiosBaseOptions.baseURL;
  }

  return axiosInstance.put(url, data, { params });
};

export const deleteOne = <T>(url: string, data?: object): Promise<T> => {
  const baseUrl = url.split('/')[3];
  console.log('Request API: ', url);
  if (baseUrl === 'orchestrator') {
    axiosInstance.defaults.baseURL = import.meta.env.VITE_API_BASE_URL_ORCHESTRATOR;
  } else if (baseUrl === 'controller') {
    axiosInstance.defaults.baseURL = import.meta.env.VITE_API_BASE_URL_CONTROLLER;
  } else {
    axiosInstance.defaults.baseURL = axiosBaseOptions.baseURL;
  }
  return axiosInstance.delete(url, { params: data });
};

export const upload = <T>(data: Upload): Promise<T> => {
  const { url, formData, controller, onUploadProgress } = data;
  return axiosInstance.post(url, formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
    onUploadProgress,
    signal: controller ? controller.signal : undefined,
  });
};
