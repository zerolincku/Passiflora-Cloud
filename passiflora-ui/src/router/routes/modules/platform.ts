import { DEFAULT_LAYOUT } from '../base';
import { AppRouteRecordRaw } from '../types';

const PLATFORM: AppRouteRecordRaw = {
  path: '/platform',
  name: 'platform',
  component: DEFAULT_LAYOUT,
  meta: {
    title: '平台管理',
    locale: 'menu.platform',
    icon: 'icon-settings',
    requiresAuth: true,
  },
  children: [
    {
      path: 'app',
      name: 'app',
      component: () => import('@/views/platform/app/index.vue'),
      meta: {
        title: '应用管理',
        locale: 'menu.platform.app',
        requiresAuth: true,
      },
    },
  ],
};

export default PLATFORM;
