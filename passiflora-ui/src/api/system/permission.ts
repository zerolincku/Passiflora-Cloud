import axios from 'axios';
import qs from 'query-string';
import { Result, BaseEntity, BasePageParam } from '@/types/global';

export type PermissionRecord = {
  permissionId?: string;
  permissionTitle?: string;
  permissionName?: string;
  permissionIcon?: string;
  permissionParentId?: string;
  permissionPath?: string;
  order?: number;
  remark?: string;
  permissionLevel?: number;
  permissionStatus?: number;
  permissionType?: number;
  children?: PermissionRecord[] | undefined;
  [key: string]: unknown;
} & BaseEntity;

export interface permissionPageParams
  extends Partial<PermissionRecord>,
    BasePageParam {}

export function permissionPage(params: permissionPageParams) {
  return axios.get<Result<PermissionRecord[]>>(
    '/iam-api/iam-permission/page',
    {
      params,
      paramsSerializer: (obj) => {
        return qs.stringify(obj);
      },
    }
  );
}

export function permissionTableTree() {
  return axios.get<Result<PermissionRecord[]>>(
    '/iam-api/iam-permission/permission-table-tree'
  );
}

export function permissionUpdateOrder(data: PermissionRecord[]) {
  return axios.post<Result<string>>(
    '/iam-api/iam-permission/update-order',
    data
  );
}

export function permissionAdd(data: PermissionRecord) {
  return axios.post<Result<string>>('/iam-api/iam-permission/add', data);
}

export function permissionUpdate(data: PermissionRecord) {
  return axios.post<Result<string>>('/iam-api/iam-permission/update', data);
}

export function permissionDetail(data: string) {
  return axios.post<Result<PermissionRecord>>(
    '/iam-api/iam-permission/detail',
    data
  );
}

export function permissionDelete(data: string[]) {
  return axios.post<Result<PermissionRecord>>(
    '/iam-api/iam-permission/delete',
    data
  );
}

export function permissionDisable(data: string[]) {
  return axios.post<Result<PermissionRecord>>(
    '/iam-api/iam-permission/disable',
    data
  );
}

export function permissionEnable(data: string[]) {
  return axios.post<Result<PermissionRecord>>(
    '/iam-api/iam-permission/enable',
    data
  );
}
