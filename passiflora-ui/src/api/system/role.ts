import axios from 'axios';
import qs from 'query-string';
import { Result, BaseEntity, BasePageParam } from '@/types/global';

export type RoleRecord = {
  roleId?: string;
  roleName?: string;
  roleCode?: string;
  roleStatus?: number;
} & BaseEntity;

export type rolePermissionSaveDto = {
  roleId?: string;
  permissionIds?: string[];
};

export interface rolePageParams extends Partial<RoleRecord>, BasePageParam {}

export function rolePage(params: rolePageParams) {
  return axios.get<Result<RoleRecord[]>>('/iam-api/iam-role/page', {
    params,
    paramsSerializer: (obj) => {
      return qs.stringify(obj);
    },
  });
}

export function roleList(params: rolePageParams) {
  return axios.get<Result<RoleRecord[]>>('/iam-api/iam-role/list', {
    params,
    paramsSerializer: (obj) => {
      return qs.stringify(obj);
    },
  });
}

export function roleAdd(data: RoleRecord) {
  return axios.post<Result<string>>('/iam-api/iam-role/add', data);
}

export function roleUpdate(data: RoleRecord) {
  return axios.post<Result<string>>('/iam-api/iam-role/update', data);
}

export function roleDetail(data: string) {
  return axios.post<Result<RoleRecord>>('/iam-api/iam-role/detail', data);
}

export function roleDelete(data: string[]) {
  return axios.post<Result<RoleRecord>>('/iam-api/iam-role/delete', data);
}

export function roleDisable(data: string[]) {
  return axios.post<Result<RoleRecord>>('/iam-api/iam-role/disable', data);
}

export function roleEnable(data: string[]) {
  return axios.post<Result<RoleRecord>>('/iam-api/iam-role/enable', data);
}

export function permissionIdsByRoleIds(data: string[]) {
  return axios.post<Result<string[]>>(
    '/iam-api/iam-role/permission-ids-by-role-ids',
    data
  );
}

export function saveRolePermission(data: rolePermissionSaveDto) {
  return axios.post<Result<string>>(
    '/iam-api/iam-role/save-role-permission',
    data
  );
}
