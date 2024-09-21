import axios from 'axios';
import qs from 'query-string';
import type { RouteRecordNormalized } from 'vue-router';
import { UserState } from '@/store/modules/user/types';
import { Result, Page, BaseEntity, BasePageParam } from '@/types/global';
import { OrgRecord } from '@/api/organization/org';

export interface LoginData {
  userName: string;
  userPassword: string;
}

export type UserRecord = {
  userId?: string;
  realName?: string;
  userName?: string;
  idCardNo?: string;
  dateOfBirth?: string;
  gender?: 0 | 1;
  phoneNum?: string;
  email?: string;
  remark?: string;
  userPassword?: string;
  salt?: string;
  avatarFile?: string;
  orgId?: string;
  positionIds?: string;
  roleIds?: string;
  orgName?: string;
} & BaseEntity;

export interface userPageParams extends Partial<UserRecord>, BasePageParam {}

export interface UserPage {
  list: UserRecord[];
  total: number;
}

export function userPage(params: userPageParams) {
  return axios.get<Result<Page<UserRecord>>>('/iam-api/sysUser/page', {
    params,
    paramsSerializer: (obj) => {
      return qs.stringify(obj);
    },
  });
}

export function userAdd(data: UserRecord) {
  return axios.post<Result<string>>('/iam-api/sysUser/add', data);
}

export function login(data: LoginData) {
  return axios.post<Result<string>>('/iam-api/sysUser/login', data);
}

export function logout() {
  return axios.get<Result<string>>('/iam-api/sysUser/logout');
}

export function currentUserInfo() {
  return axios.get<Result<UserState>>('/iam-api/sysUser/currentUserInfo');
}

export function userUpdate(data: OrgRecord) {
  return axios.post<Result<string>>('/iam-api/sysUser/update', data);
}

export function userDetail(data: string) {
  return axios.post<Result<OrgRecord>>('/iam-api/sysUser/detail', data);
}

export function userDelete(data: string[]) {
  return axios.post<Result<OrgRecord>>('/iam-api/sysUser/delete', data);
}

export function getMenuList() {
  return axios.get<Result<RouteRecordNormalized[]>>(
    '/iam-api/sysPermission/menuTree'
  );
}
