import { DEFAULT_LAYOUT } from '../base';
import { AppRouteRecordRaw } from '../types';

const SYSTEM: AppRouteRecordRaw = {
  path: '/organization',
  name: 'organization',
  component: DEFAULT_LAYOUT,
  meta: {
    locale: 'menu.organization',
    icon: 'icon-user-group',
    requiresAuth: true,
  },
  children: [
    {
      path: 'user',
      name: 'user',
      component: () => import('@/views/organization/user/index.vue'),
      meta: {
        locale: 'menu.organization.user',
        requiresAuth: true,
      },
    },
    {
      path: 'org',
      name: 'org',
      component: () => import('@/views/organization/org/index.vue'),
      meta: {
        locale: 'menu.organization.org',
        requiresAuth: true,
      },
    },
    {
      path: 'position',
      name: 'position',
      component: () => import('@/views/organization/position/index.vue'),
      meta: {
        locale: 'menu.organization.position',
        requiresAuth: true,
      },
    },
  ],
};

export default SYSTEM;
