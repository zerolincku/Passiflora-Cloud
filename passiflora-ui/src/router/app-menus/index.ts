import { RouteRecordNormalized } from 'vue-router';
import { appRoutes, appExternalRoutes } from '../routes';

const mixinRoutes = [...appRoutes, ...appExternalRoutes];

const appClientMenus = mixinRoutes.map((el) => {
  const { name, path, meta, redirect, children } = el;
  return {
    name,
    path,
    meta,
    redirect,
    children,
  };
});

export const menuNameLocaleMap = new Map();

const loop = (menus: RouteRecordNormalized[], map: Map<string, string>) => {
  if (!menus || !menus.length) {
    return;
  }
  menus.forEach((menu) => {
    if (menu.name && menu.meta?.locale) {
      map.set(menu.name as string, menu.meta?.locale as string);
    }
    loop(menu.children as RouteRecordNormalized[], map);
  });
};

loop(appClientMenus as RouteRecordNormalized[], menuNameLocaleMap);
export default appClientMenus;
