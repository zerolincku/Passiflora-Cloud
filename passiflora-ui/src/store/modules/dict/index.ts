import { defineStore } from 'pinia';
import { DictItemRecord, dictItemListByDictTag } from '@/api/system/dict';

const useDictStore = defineStore('dict', {
  state: () => ({
    cacheMap: new Map<string, DictItemRecord[]>(),
  }),
  actions: {
    async getDictItems(dictTag: string, isNumber: boolean) {
      if (this.cacheMap.has(dictTag)) {
        return this.cacheMap.get(dictTag);
      }
      const { data } = await dictItemListByDictTag(dictTag);
      if (isNumber && data.data) {
        const items = data.data as unknown as DictItemRecord[];
        items.forEach((item) => {
          item.value = parseInt(item.value as string, 10);
        });
      }
      this.cacheMap.set(dictTag, data.data);
      return data.data;
    },
  },
});

export default useDictStore;
