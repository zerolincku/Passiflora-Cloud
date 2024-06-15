import type { Router, RouteRecordNormalized } from 'vue-router';
import NProgress from 'nprogress'; // progress bar

import usePermission from '@/hooks/permission';
import { appRoutes } from '../routes';
import { NOT_FOUND, NOT_PERMISSION } from '../constants';

export default function setupPermissionGuard(router: Router) {
  router.beforeEach(async (to, from, next) => {
    const Permission = usePermission();
    const permissionsAllow = Permission.accessRouter(to);
    if (!to.meta.requiresAuth) {
      next();
      NProgress.done();
      return;
    }

    const serverMenuConfig = [...appRoutes];

    let exist = false;
    while (serverMenuConfig.length && !exist) {
      const element = serverMenuConfig.shift();
      if (element?.name === to.name) exist = true;

      if (element?.children) {
        serverMenuConfig.push(
          ...(element.children as unknown as RouteRecordNormalized[])
        );
      }
    }
    if (exist && permissionsAllow) {
      next();
    } else if (!exist) {
      next(NOT_FOUND);
    } else {
      next(NOT_PERMISSION);
    }
    NProgress.done();
  });
}
