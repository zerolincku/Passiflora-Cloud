import type {AxiosRequestConfig, AxiosResponse} from 'axios';
import axios from 'axios';
import {Message} from '@arco-design/web-vue';
import {clearToken, getToken} from '@/utils/auth';
import {Result} from '@/types/global';
import router from '@/router';

const toFiniteNumberOrNull = (value: unknown): number | null => {
  if (typeof value === 'number' && Number.isFinite(value)) return value;
  if (typeof value !== 'string') return null;
  const trimmed = value.trim();
  if (!trimmed) return null;
  const num = Number(trimmed);
  if (!Number.isFinite(num)) return null;
  return num;
};

const coerceTotalFields = (node: unknown) => {
  if (!node || typeof node !== 'object') return;
  if (Array.isArray(node)) {
    node.forEach(coerceTotalFields);
    return;
  }
  Object.entries(node).forEach(([key, value]) => {
    if (key === 'total') {
      const num = toFiniteNumberOrNull(value);
      if (num !== null) (node as Record<string, unknown>)[key] = num;
      return;
    }
    coerceTotalFields(value);
  });
};

if (import.meta.env.VITE_API_BASE_URL) {
  axios.defaults.baseURL = import.meta.env.VITE_API_BASE_URL;
}

axios.interceptors.request.use(
  (config: AxiosRequestConfig) => {
    const token = getToken();
    if (token) {
      if (!config.headers) {
        config.headers = {};
      }
      config.headers.Authorization = `${token}`;
    }
    return config;
  },
  (error) => {
    // do something
    return Promise.reject(error);
  }
);
// add response interceptors
axios.interceptors.response.use(
  async (response: AxiosResponse<Result>) => {
    const res = response.data;
    // if the custom code is not 20000, it is judged as an error.
    if (res.code !== 200) {
      // 50008: Illegal token; 50012: Other clients logged in; 50014: Token expired;
      if (res.code === 401) {
        clearToken();
        await router.push({ name: 'notAuth' });
      } else {
        Message.error({
          content: res.message || '系统错误',
          duration: 5 * 1000,
        });
      }
      return Promise.reject(new Error(res.message || 'Error'));
    }
    coerceTotalFields(res);
    return response;
  },
  (error) => {
    Message.error({
      content: error.response?.data?.message || 'Request Error',
      duration: 5 * 1000,
    });
    return Promise.reject(error);
  }
);
