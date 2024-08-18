import type { TableColumnData } from '@arco-design/web-vue/es/table/interface';

export interface AnyObject {
  [key: string]: unknown;
}

export interface Options {
  value: unknown;
  label: string;
}

export interface NodeOptions extends Options {
  children?: NodeOptions[];
}

export interface GetParams {
  body: null;
  type: string;
  url: string;
}

export interface PostData {
  body: string;
  type: string;
  url: string;
}

export interface Pagination {
  current: number;
  pageSize: number;
  total?: number;
}

export type TimeRanger = [string, string];

export interface GeneralChart {
  xAxis: string[];
  data: Array<{ name: string; value: number[] }>;
}

export interface Result<T = unknown> {
  code: number;
  message: string;
  data: T;
}

export interface Page<T> {
  list: T[];
  total: number;
}

export interface BaseEntity {
  createBy?: string;
  updateBy?: string;
  createTime?: string;
  updateTime?: string;
  delFlag?: number;
  version?: number;
  disabled?: boolean;
}

export interface BasePageParam {
  current?: number;
  pageSize?: number;
}

export type SizeProps = 'mini' | 'small' | 'medium' | 'large';
export type Column = TableColumnData & { checked?: true };
