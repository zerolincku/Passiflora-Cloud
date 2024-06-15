import { EnumRecord } from '@/api/system/enum';

export function getLabelByValue(enums: EnumRecord[], value: number) {
  let label = '';
  enums.forEach((e) => {
    if (e.value === value) {
      label = e.label;
    }
  });
  return label;
}

export default null;
