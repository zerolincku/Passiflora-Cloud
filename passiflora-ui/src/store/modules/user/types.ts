export interface UserState {
  createBy?: string;
  updateBy?: string;
  createTime?: string;
  updateTime?: string;
  delFlag?: number;
  version?: number;
  userId?: string;
  userName?: string;
  gender?: number;
  phoneNum?: string;
  email?: string;
  remark?: string;
  avatarFile?: string;
  // TODO
  permission: string[];
  menu: string[];
}
