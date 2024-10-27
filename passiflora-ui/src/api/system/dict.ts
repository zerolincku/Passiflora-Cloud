import axios from 'axios';
import qs from 'query-string';
import { Result, BaseEntity, BasePageParam } from '@/types/global';

export type DictRecord = {
  dictId?: string;
  dictName?: string;
  dictTag?: string;
  remark?: string;
  isSystem?: 0 | 1;
  valueIsOnly?: 0 | 1;
} & BaseEntity;

export type DictItemRecord = {
  dictItemId?: string;
  dictId?: string;
  label?: string;
  value?: string | number;
  remark?: string;
  isSystem?: 0 | 1;
} & BaseEntity;

export interface dictPageParams extends Partial<DictRecord>, BasePageParam {
  [key: string]: any;
}

export interface dictItemPageParams
  extends Partial<DictItemRecord>,
    BasePageParam {
  [key: string]: any;
}

export interface DictPage {
  list: DictRecord[];
  total: number;
}

export function dictPage(params: dictPageParams) {
  return axios.get<Result<DictRecord[]>>('/iam-api/iam-dict/page', {
    params,
    paramsSerializer: (obj) => {
      return qs.stringify(obj);
    },
  });
}

export function dictAdd(data: DictRecord) {
  return axios.post<Result<string>>('/iam-api/iam-dict/add', data);
}

export function dictUpdate(data: DictRecord) {
  return axios.post<Result<string>>('/iam-api/iam-dict/update', data);
}

export function dictDetail(data: string) {
  return axios.post<Result<DictRecord>>('/iam-api/iam-dict/detail', data);
}

export function dictDelete(data: string[]) {
  return axios.post<Result<string>>('/iam-api/iam-dict/delete', data);
}

export function dictItemPage(params: dictPageParams) {
  return axios.get<Result<DictItemRecord[]>>('/iam-api/iam-dict-item/page', {
    params,
    paramsSerializer: (obj) => {
      return qs.stringify(obj);
    },
  });
}

export function dictItemListByDictTag(params: string) {
  return axios.get<Result<DictItemRecord[]>>(
    `/iam-api/iam-dict-item/list-by-dict-tag?dictTag=${params}`
  );
}

export function dictItemAdd(data: DictItemRecord) {
  return axios.post<Result<string>>('/iam-api/iam-dict-item/add', data);
}

export function dictItemUpdate(data: DictItemRecord) {
  return axios.post<Result<string>>('/iam-api/iam-dict-item/update', data);
}

export function dictItemDetail(data: string) {
  return axios.post<Result<DictItemRecord>>(
    '/iam-api/iam-dict-item/update',
    data
  );
}

export function dictItemDelete(data: string[]) {
  return axios.post<Result<string>>('/iam-api/iam-dict-item/delete', data);
}

export function listByDictId(data: string) {
  return axios.post<Result<DictItemRecord[]>>(
    '/iam-api/iam-dict-item/list-by-dict-id',
    data
  );
}

export function listByDictName(data: string) {
  return axios.post<Result<DictItemRecord[]>>(
    '/iam-api/iam-dict-item/list-by-dict-name',
    data
  );
}

export function listByDictTag(data: string) {
  return axios.post<Result<DictItemRecord[]>>(
    '/iam-api/iam-dict-item/list-by-dict-tag',
    data
  );
}
