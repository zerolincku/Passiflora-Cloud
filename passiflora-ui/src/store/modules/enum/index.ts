import { defineStore } from 'pinia';
import { EnumRecord, list } from '@/api/system/enum';

const useEnumStore = defineStore('enums', {
  state: () => ({
    cacheMap: new Map<string, EnumRecord[]>(),
  }),
  actions: {
    async getEnums(key: string) {
      if (this.cacheMap.has(key)) {
        return this.cacheMap.get(key);
      }
      const { data } = await list(key);
      this.cacheMap.set(key, data.data);
      return data.data;
    },
  },
});

export default useEnumStore;
