import { DEFAULT_LAYOUT } from '../base';
import { AppRouteRecordRaw } from '../types';

const SYSTEM: AppRouteRecordRaw = {
  path: '/system',
  name: 'system',
  component: DEFAULT_LAYOUT,
  meta: {
    title: '系统管理-local优先',
    locale: 'menu.system',
    icon: 'icon-settings',
    requiresAuth: true,
  },
  children: [
    {
      path: 'dict',
      name: 'dict',
      component: () => import('@/views/system/dict/index.vue'),
      meta: {
        locale: 'menu.system.dict',
        requiresAuth: true,
      },
    },
    {
      path: 'permission',
      name: 'permission',
      component: () => import('@/views/system/permission/index.vue'),
      meta: {
        locale: 'menu.system.permission',
        requiresAuth: true,
      },
    },
  ],
};

export default SYSTEM;
