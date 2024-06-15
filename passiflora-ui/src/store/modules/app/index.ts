import { defineStore } from 'pinia';
import type { RouteRecordNormalized } from 'vue-router';
import defaultSettings from '@/config/settings.json';
import { getMenuList } from '@/api/organization/user';
import { menuNameLocaleMap } from '@/router/app-menus';
import { AppState } from './types';

const localSetting = localStorage.getItem('globalSettings');
const useSetting = localSetting ? JSON.parse(localSetting) : defaultSettings;

// 色弱模式配置
document.body.style.filter = useSetting.colorWeak ? 'invert(80%)' : 'none';

const useAppStore = defineStore('app', {
  state: (): AppState => ({ ...useSetting }),

  getters: {
    appCurrentSetting(state: AppState): AppState {
      return { ...state };
    },
    appDevice(state: AppState) {
      return state.device;
    },
    appAsyncMenus(state: AppState): RouteRecordNormalized[] {
      return state.serverMenu;
    },
  },

  actions: {
    // Update app settings
    updateSettings(partial: Partial<AppState>) {
      // @ts-ignore-next-line
      this.$patch(partial);
    },

    // Change theme color
    toggleTheme(dark: boolean) {
      if (dark) {
        this.theme = 'dark';
        document.body.setAttribute('arco-theme', 'dark');
      } else {
        this.theme = 'light';
        document.body.removeAttribute('arco-theme');
      }
    },
    toggleDevice(device: string) {
      this.device = device;
    },
    toggleMenu(value: boolean) {
      this.hideMenu = value;
    },
    async fetchServerMenuConfig() {
      const { data } = await getMenuList();
      this.serverMenu = data.data;
      await this.loop(this.serverMenu);
    },

    async loop(appClientMenus: RouteRecordNormalized[]) {
      if (!appClientMenus || !appClientMenus.length) {
        return;
      }
      appClientMenus.forEach((menu) => {
        if (menuNameLocaleMap.has(menu.name)) {
          menu.meta.locale = menuNameLocaleMap.get(menu.name);
        }
        this.loop(menu.children as RouteRecordNormalized[]);
      });
    },

    clearServerMenu() {
      this.serverMenu = [];
    },
  },
});

export default useAppStore;
