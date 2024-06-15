import 'vue-router';

declare module 'vue-router' {
  interface RouteMeta {
    requiresAuth: boolean; // Whether login is required to access the current page (every route must declare)
    icon?: string; // The icon show in the side permission
    locale?: string; // The locale name show in side permission and breadcrumb
    hideInMenu?: boolean; // If true, it is not displayed in the side permission
    hideChildrenInMenu?: boolean; // if set true, the children are not displayed in the side permission
    activeMenu?: string; // if set name, the permission will be highlighted according to the name you set
    order?: number; // Sort routing permission items. If set key, the higher the value, the more forward it is
    noAffix?: boolean; // if set true, the tag will not affix in the tab-bar
    ignoreCache?: boolean; // if set true, the page will not be cached
  }
}
