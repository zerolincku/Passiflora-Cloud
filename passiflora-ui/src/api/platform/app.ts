import axios from 'axios';
import qs from 'query-string';
import { Result, BaseEntity, BasePageParam } from '@/types/global';

export type AppRecord = {
  appId?: string;
  appName?: string;
  appKey?: string;
  appSecret?: string;
  appIcon?: string;
  appUrl?: string;
  appStatus?: number;
  appType?: number;
  appRemark?: number;
  appPeriod?: number;
} & BaseEntity;

export type appPermissionSaveDto = {
  appId?: string;
  permissionIds?: string[];
};

export interface appPageParams extends Partial<AppRecord>, BasePageParam {}

export function appPage(params: appPageParams) {
  return axios.get<Result<AppRecord[]>>('/iam-api/iam-app/page', {
    params,
    paramsSerializer: (obj) => {
      return qs.stringify(obj);
    },
  });
}

export function appAdd(data: AppRecord) {
  return axios.post<Result<string>>('/iam-api/iam-app/add', data);
}

export function appUpdate(data: AppRecord) {
  return axios.post<Result<string>>('/iam-api/iam-app/update', data);
}

export function appDetail(data: string) {
  return axios.post<Result<AppRecord>>('/iam-api/iam-app/detail', data);
}

export function appDelete(data: string[]) {
  return axios.post<Result<AppRecord>>('/iam-api/iam-app/delete', data);
}

export function appDisable(data: string[]) {
  return axios.post<Result<AppRecord>>('/iam-api/iam-app/disable', data);
}

export function appEnable(data: string[]) {
  return axios.post<Result<AppRecord>>('/iam-api/iam-app/enable', data);
}
