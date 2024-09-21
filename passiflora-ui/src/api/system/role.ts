import axios from 'axios';
import qs from 'query-string';
import { Result, Page, BaseEntity, BasePageParam } from '@/types/global';

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
  return axios.get<Result<Page<RoleRecord>>>('/iam-api/sysRole/page', {
    params,
    paramsSerializer: (obj) => {
      return qs.stringify(obj);
    },
  });
}

export function roleList(params: rolePageParams) {
  return axios.get<Result<RoleRecord[]>>('/iam-api/sysRole/list', {
    params,
    paramsSerializer: (obj) => {
      return qs.stringify(obj);
    },
  });
}

export function roleAdd(data: RoleRecord) {
  return axios.post<Result<string>>('/iam-api/sysRole/add', data);
}

export function roleUpdate(data: RoleRecord) {
  return axios.post<Result<string>>('/iam-api/sysRole/update', data);
}

export function roleDetail(data: string) {
  return axios.post<Result<RoleRecord>>('/iam-api/sysRole/detail', data);
}

export function roleDelete(data: string[]) {
  return axios.post<Result<RoleRecord>>('/iam-api/sysRole/delete', data);
}

export function roleDisable(data: string[]) {
  return axios.post<Result<RoleRecord>>('/iam-api/sysRole/disable', data);
}

export function roleEnable(data: string[]) {
  return axios.post<Result<RoleRecord>>('/iam-api/sysRole/enable', data);
}

export function permissionIdsByRoleIds(data: string[]) {
  return axios.post<Result<string[]>>(
    '/iam-api/sysRole/permissionIdsByRoleIds',
    data
  );
}

export function saveRolePermission(data: rolePermissionSaveDto) {
  return axios.post<Result<string>>(
    '/iam-api/sysRole/saveRolePermission',
    data
  );
}
