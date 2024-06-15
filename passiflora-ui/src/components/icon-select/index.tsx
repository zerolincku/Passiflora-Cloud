import { defineComponent, PropType } from 'vue';
import Icon from '@arco-design/web-vue/es/icon';
import IconUi from '@/components/icon';
import { Select } from '@arco-design/web-vue';

interface Option {
  label: string;
  value: string;
}

interface Props {
  disabled: boolean;
  modelValue: string;
}

export default defineComponent({
  name: 'IconSelect',
  props: {
    modelValue: {
      type: String as PropType<string>,
      required: true,
    },
    disabled: {
      type: Boolean as PropType<boolean>,
      required: true,
    },
  },
  emits: ['update:modelValue'],
  setup(props: Props, { emit }) {
    const handleChange = (value: string) => {
      emit('update:modelValue', value);
    };

    const formattedIcons: Option[] = Object.keys(Icon)
      .filter((item) => item.startsWith('Icon'))
      .map((item) => item.replace(/([a-z])([A-Z])/g, '$1-$2').toLowerCase())
      .map((item) => ({
        label: item,
        value: item,
      }));

    return () => (
      <Select
        value={props.modelValue}
        disabled={props.disabled}
        options={formattedIcons}
        placeholder="图标"
        onChange={handleChange}
        allowClear={true}
        defaultValue={props.modelValue}
        allowSearch={true}
      >
        {{
          label: () => (
            <>
              <IconUi name={props.modelValue} />
              &nbsp;{props.modelValue}
            </>
          ),
          option: ({ data }: { data: Option }) => (
            <>
              <IconUi name={data.label} />
              &nbsp;{data.label}
            </>
          ),
        }}
      </Select>
    );
  },
});
