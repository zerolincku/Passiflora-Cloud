import { defineStore } from 'pinia';
import {
  login as userLogin,
  logout as userLogout,
  currentUserInfo,
  LoginData,
} from '@/api/organization/user';
import { setToken, clearToken } from '@/utils/auth';
import { removeRouteListener } from '@/utils/route-listener';
import { UserState } from './types';
import useAppStore from '../app';

const useUserStore = defineStore('user', {
  state: (): UserState => ({
    createBy: '',
    updateBy: '',
    createTime: '',
    updateTime: '',
    delFlag: 0,
    version: 0,
    userId: '',
    userName: '',
    gender: 0,
    phoneNum: '',
    email: '',
    remark: '',
    avatarFile: '',
    permission: [],
    menu: [],
  }),

  getters: {
    userInfo(state: UserState): UserState {
      return { ...state };
    },
  },

  actions: {
    // Set user's information
    setInfo(partial: Partial<UserState>) {
      this.$patch(partial);
    },

    // Reset user's information
    resetInfo() {
      this.$reset();
    },

    // Get user's information
    async info() {
      const appStore = useAppStore();
      const res = await currentUserInfo();
      await appStore.fetchServerMenuConfig();
      this.setInfo(res.data.data);
    },

    // Login
    async login(loginForm: LoginData) {
      try {
        const res = await userLogin(loginForm);
        setToken(res.data.data);
        await this.info();
      } catch (err) {
        clearToken();
        throw err;
      }
    },

    // Logout
    async logout() {
      await userLogout();
      const appStore = useAppStore();
      this.resetInfo();
      clearToken();
      removeRouteListener();
      appStore.clearServerMenu();
    },
  },
});

export default useUserStore;
