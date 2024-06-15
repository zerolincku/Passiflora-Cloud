import { h, compile } from 'vue';

type Props = {
  name: string;
};
const icon = (props: Props) => {
  return props.name ? h(compile(`<${props.name}/>`)) : null;
};

export default icon;
