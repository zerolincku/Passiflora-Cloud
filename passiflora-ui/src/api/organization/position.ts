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

export type positionPermissionSaveDto = {
  positionId?: string;
  permissionIds?: string[];
};

export interface positionPageParams
  extends Partial<PositionRecord>,
    BasePageParam {}

export function positionPage(params: positionPageParams) {
  return axios.get<Result<Page<PositionRecord>>>(
    '/iam-api/iam-position/page',
    {
      params,
      paramsSerializer: (obj) => {
        return qs.stringify(obj);
      },
    }
  );
}

export function positionAdd(data: PositionRecord) {
  return axios.post<Result<string>>('/iam-api/iam-position/add', data);
}

export function positionUpdate(data: PositionRecord) {
  return axios.post<Result<string>>('/iam-api/iam-position/update', data);
}

export function positionDetail(data: string) {
  return axios.post<Result<PositionRecord>>(
    '/iam-api/iam-position/detail',
    data
  );
}

export function positionDelete(data: string[]) {
  return axios.post<Result<PositionRecord>>(
    '/iam-api/iam-position/delete',
    data
  );
}

export function positionTree(positionName: string) {
  return axios.get<Result<PositionRecord[]>>(
    `/iam-api/iam-position/position-tree?positionName=${positionName}`
  );
}

export function positionUpdateOrder(data: PositionRecord[]) {
  return axios.post<Result<string>>(
    '/iam-api/iam-position/update-order',
    data
  );
}

export function positionDisable(data: string[]) {
  return axios.post<Result<PositionRecord>>(
    '/iam-api/iam-position/disable',
    data
  );
}

export function positionEnable(data: string[]) {
  return axios.post<Result<PositionRecord>>(
    '/iam-api/iam-position/enable',
    data
  );
}

export function permissionIdsByPositionIds(data: string[]) {
  return axios.post<Result<string[]>>(
    '/iam-api/iam-position/permission-ids-by-position-ids',
    data
  );
}

export function savePositionPermission(data: positionPermissionSaveDto) {
  return axios.post<Result<string>>(
    '/iam-api/iam-position/save-position-permission',
    data
  );
}
