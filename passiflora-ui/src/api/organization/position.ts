import axios from 'axios';
import qs from 'query-string';
import { Result, Page, BaseEntity, BasePageParam } from '@/types/global';

export type PositionRecord = {
  positionId?: string;
  positionName?: string;
  positionLevel?: string;
  parentPositionId?: string;
  positionIdPath?: string;
  dataScopeType?: number;
  positionStatus?: number;
  order?: number;
  children?: PositionRecord[] | undefined;
} & BaseEntity;

export interface positionPageParams
  extends Partial<PositionRecord>,
    BasePageParam {}

export function positionPage(params: positionPageParams) {
  return axios.get<Result<Page<PositionRecord>>>(
    '/system-api/sysPosition/page',
    {
      params,
      paramsSerializer: (obj) => {
        return qs.stringify(obj);
      },
    }
  );
}

export function positionAdd(data: PositionRecord) {
  return axios.post<Result<string>>('/system-api/sysPosition/add', data);
}

export function positionUpdate(data: PositionRecord) {
  return axios.post<Result<string>>('/system-api/sysPosition/update', data);
}

export function positionDetail(data: string) {
  return axios.post<Result<PositionRecord>>(
    '/system-api/sysPosition/detail',
    data
  );
}

export function positionDelete(data: string[]) {
  return axios.post<Result<PositionRecord>>(
    '/system-api/sysPosition/delete',
    data
  );
}

export function positionTree(positionName: string) {
  return axios.get<Result<PositionRecord[]>>(
    `/system-api/sysPosition/positionTree?positionName=${positionName}`
  );
}

export function positionUpdateOrder(data: PositionRecord[]) {
  return axios.post<Result<string>>(
    '/system-api/sysPosition/updateOrder',
    data
  );
}

export function positionDisable(data: string[]) {
  return axios.post<Result<PositionRecord>>(
    '/system-api/sysPosition/disable',
    data
  );
}

export function positionEnable(data: string[]) {
  return axios.post<Result<PositionRecord>>(
    '/system-api/sysPosition/enable',
    data
  );
}
