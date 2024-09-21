import axios from 'axios';
import qs from 'query-string';
import { Result, Page, BaseEntity, BasePageParam } from '@/types/global';

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
  return axios.get<Result<Page<DictRecord>>>('/iam-api/sysDict/page', {
    params,
    paramsSerializer: (obj) => {
      return qs.stringify(obj);
    },
  });
}

export function dictAdd(data: DictRecord) {
  return axios.post<Result<string>>('/iam-api/sysDict/add', data);
}

export function dictUpdate(data: DictRecord) {
  return axios.post<Result<string>>('/iam-api/sysDict/update', data);
}

export function dictDetail(data: string) {
  return axios.post<Result<DictRecord>>('/iam-api/sysDict/detail', data);
}

export function dictDelete(data: string[]) {
  return axios.post<Result<string>>('/iam-api/sysDict/delete', data);
}

export function dictItemPage(params: dictPageParams) {
  return axios.get<Result<Page<DictItemRecord>>>(
    '/iam-api/sysDictItem/page',
    {
      params,
      paramsSerializer: (obj) => {
        return qs.stringify(obj);
      },
    }
  );
}

export function dictItemListByDictTag(params: string) {
  return axios.get<Result<DictItemRecord[]>>(
    `/iam-api/sysDictItem/listByDictTag?dictTag=${params}`
  );
}

export function dictItemAdd(data: DictItemRecord) {
  return axios.post<Result<string>>('/iam-api/sysDictItem/add', data);
}

export function dictItemUpdate(data: DictItemRecord) {
  return axios.post<Result<string>>('/iam-api/sysDictItem/update', data);
}

export function dictItemDetail(data: string) {
  return axios.post<Result<DictItemRecord>>(
    '/iam-api/sysDictItem/update',
    data
  );
}

export function dictItemDelete(data: string[]) {
  return axios.post<Result<string>>('/iam-api/sysDictItem/delete', data);
}

export function listByDictId(data: string) {
  return axios.post<Result<DictItemRecord[]>>(
    '/iam-api/sysDictItem/listByDictId',
    data
  );
}

export function listByDictName(data: string) {
  return axios.post<Result<DictItemRecord[]>>(
    '/iam-api/sysDictItem/listByDictName',
    data
  );
}

export function listByDictTag(data: string) {
  return axios.post<Result<DictItemRecord[]>>(
    '/iam-api/sysDictItem/listByDictTag',
    data
  );
}
