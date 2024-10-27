<template>
  <div v-if="!appStore.navbar" class="fixed-settings" @click="setVisible">
    <a-button type="primary">
      <template #icon>
        <icon-settings />
      </template>
    </a-button>
  </div>
  <a-drawer
    :width="300"
    unmount-on-close
    :visible="visible"
    :cancel-text="$t('settings.close')"
    :ok-text="$t('settings.save')"
    :esc-to-close="true"
    :hide-cancel="true"
    @ok="save"
    @cancel="cancel"
    @close="close"
  >
    <template #title> {{ $t('settings.title') }} </template>
    <Block :options="contentOpts" :title="$t('settings.content')" />
    <Block :options="othersOpts" :title="$t('settings.otherSettings')" />
    <!--    <a-alert>{{ $t('settings.alertContent') }}</a-alert>-->
  </a-drawer>
</template>

<script lang="ts" setup>
  import { computed } from 'vue';
  import { Message } from '@arco-design/web-vue';
  import { useAppStore } from '@/store';
  import Block from './block.vue';

  const emit = defineEmits(['cancel']);

  const appStore = useAppStore();
  const visible = computed(() => appStore.globalSettings);
  const contentOpts = computed(() => [
    {
      name: 'settings.topMenu',
      key: 'topMenu',
      defaultVal: appStore.topMenu,
    },
    { name: 'settings.footer', key: 'footer', defaultVal: appStore.footer },
    {
      name: 'settings.menuWidth',
      key: 'menuWidth',
      defaultVal: appStore.menuWidth,
      type: 'number',
    },
  ]);
  const othersOpts = computed(() => [
    {
      name: 'settings.colorWeak',
      key: 'colorWeak',
      defaultVal: appStore.colorWeak,
    },
  ]);

  const cancel = () => {
    appStore.updateSettings({ globalSettings: false });
    const text = JSON.stringify(appStore.$state, null, 2);
    localStorage.setItem('globalSettings', text);
    emit('cancel');
  };

  const close = () => {
    cancel();
  };

  const save = async () => {
    cancel();
    Message.success('保存成功');
  };
  const setVisible = () => {
    appStore.updateSettings({ globalSettings: true });
  };
</script>

<style scoped lang="less">
  .fixed-settings {
    position: fixed;
    top: 280px;
    right: 0;

    svg {
      font-size: 18px;
      vertical-align: -4px;
    }
  }
</style>
