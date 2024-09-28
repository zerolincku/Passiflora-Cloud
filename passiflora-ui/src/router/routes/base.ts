import type { RouteRecordRaw } from 'vue-router';
import { REDIRECT_ROUTE_NAME } from '@/router/constants';

export const DEFAULT_LAYOUT = () => import('@/layout/default-layout.vue');

export const REDIRECT_MAIN: RouteRecordRaw = {
  path: '/redirect',
  name: 'redirectWrapper',
  component: DEFAULT_LAYOUT,
  meta: {
    requiresAuth: true,
    hideInMenu: true,
  },
  children: [
    {
      path: '/redirect/:path',
      name: REDIRECT_ROUTE_NAME,
      component: () => import('@/views/redirect/index.vue'),
      meta: {
        requiresAuth: true,
        hideInMenu: true,
      },
    },
  ],
};

export const NOT_FOUND_ROUTE: RouteRecordRaw = {
  path: '/:pathMatch(.*)*',
  name: 'notFound',
  component: () => import('@/views/not-found/index.vue'),
  meta: {
    requiresAuth: false,
    hideInMenu: true,
  },
};

export const NOT_PERMISSION_ROUTE: RouteRecordRaw = {
  path: '/not-permission',
  name: 'notPermission',
  component: () => import('@/views/not-permission/index.vue'),
  meta: {
    requiresAuth: false,
    hideInMenu: true,
  },
};

export const NOT_AUTH_ROUTE: RouteRecordRaw = {
  path: '/not-auth',
  name: 'notAuth',
  component: () => import('@/views/not-auth/index.vue'),
  meta: {
    requiresAuth: false,
    hideInMenu: true,
  },
};
