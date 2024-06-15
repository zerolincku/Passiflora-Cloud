import { DictItemRecord } from '@/api/system/dict';

export function getLabelByValue(items: DictItemRecord[], value: number) {
  let label = '';
  items.forEach((item) => {
    if (
      item.value !== undefined &&
      item.label !== undefined &&
      item.value.toString() === value.toString()
    ) {
      label = item.label;
    }
  });
  return label;
}
export default null;
