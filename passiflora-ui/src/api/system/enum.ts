import axios from 'axios';
import { Result } from '@/types/global';

export type EnumRecord = {
  label: string;
  value: number | string;
};

export function list(param: string) {
  return axios.get<Result<EnumRecord[]>>(`/system-api/enum/${param}`);
}
