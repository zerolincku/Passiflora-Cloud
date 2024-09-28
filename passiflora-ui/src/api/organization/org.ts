import axios from 'axios';
import qs from 'query-string';
import { Result, BaseEntity, BasePageParam } from '@/types/global';

export type OrgRecord = {
  orgId?: string;
  orgName?: string;
  orgCode?: string;
  orgType?: number;
  orgLevel?: string;
  parentOrgId?: string;
  orgIdPath?: string;
  children?: OrgRecord[] | undefined;
} & BaseEntity;

export interface orgPageParams extends Partial<OrgRecord>, BasePageParam {}

export function orgPage(params: orgPageParams) {
  return axios.get<Result<OrgRecord[]>>('/iam-api/iam-org/page', {
    params,
    paramsSerializer: (obj) => {
      return qs.stringify(obj);
    },
  });
}

export function orgAdd(data: OrgRecord) {
  return axios.post<Result<string>>('/iam-api/iam-org/add', data);
}

export function orgUpdate(data: OrgRecord) {
  return axios.post<Result<string>>('/iam-api/iam-org/update', data);
}

export function orgDetail(data: string) {
  return axios.post<Result<OrgRecord>>('/iam-api/iam-org/detail', data);
}

export function orgDelete(data: string[]) {
  return axios.post<Result<OrgRecord>>('/iam-api/iam-org/delete', data);
}

export function orgTree(orgName: string) {
  return axios.get<Result<OrgRecord[]>>(
    `/iam-api/iam-org/org-tree?orgName=${orgName}`
  );
}
