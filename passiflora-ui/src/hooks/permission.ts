import { RouteLocationNormalized, RouteRecordRaw } from 'vue-router';
import { useUserStore } from '@/store';

export default function usePermission() {
  const userStore = useUserStore();
  return {
    accessRouter(route: RouteLocationNormalized | RouteRecordRaw) {
      return (
        !route.meta?.requiresAuth ||
        userStore.menu?.some(
          (item) => item.toLowerCase() === (route.name as string).toLowerCase()
        )
      );
    },
    // You can add any rules you want
  };
}
