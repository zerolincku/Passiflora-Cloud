import { createPinia } from 'pinia';
import useAppStore from './modules/app';
import useUserStore from './modules/user';
import useTabBarStore from './modules/tab-bar';
import useEnumStore from './modules/enum';
import useDictStore from './modules/dict';

const pinia = createPinia();

export {
  useAppStore,
  useUserStore,
  useTabBarStore,
  useEnumStore,
  useDictStore,
};
export default pinia;
