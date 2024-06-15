<template>
  <a-spin style="display: block" :loading="loading">
    <a-tabs v-model:activeKey="messageType" type="rounded" destroy-on-hide>
      <a-tab-pane v-for="item in tabList" :key="item.key">
        <template #title>
          <span> {{ item.title }}{{ formatUnreadLength(item.key) }} </span>
        </template>
        <a-result v-if="!renderList.length" status="404">
          <template #subtitle> {{ $t('messageBox.noContent') }} </template>
        </a-result>
        <List
          :render-list="renderList"
          :unread-count="unreadCount"
          @item-click="handleItemClick"
        />
      </a-tab-pane>
      <template #extra>
        <a-button type="text" @click="emptyList">
          {{ $t('messageBox.tab.button') }}
        </a-button>
      </template>
    </a-tabs>
  </a-spin>
</template>

<script lang="ts" setup>
  import { ref, reactive, toRefs, computed } from 'vue';
  import { useI18n } from 'vue-i18n';
  import { MessageRecord, MessageListType } from '@/api/message';
  import useLoading from '@/hooks/loading';
  import List from './list.vue';

  interface TabItem {
    key: string;
    title: string;
    avatar?: string;
  }
  const { loading, setLoading } = useLoading(true);
  const messageType = ref('message');
  const { t } = useI18n();
  const messageData = reactive<{
    renderList: MessageRecord[];
    messageList: MessageRecord[];
  }>({
    renderList: [],
    messageList: [],
  });
  toRefs(messageData);
  const tabList: TabItem[] = [
    {
      key: 'message',
      title: t('messageBox.tab.title.message'),
    },
    {
      key: 'notice',
      title: t('messageBox.tab.title.notice'),
    },
    {
      key: 'todo',
      title: t('messageBox.tab.title.todo'),
    },
  ];
  async function fetchSourceData() {
    setLoading(true);
    try {
      messageData.messageList = [
        {
          id: 1,
          type: 'message',
          title: '郑曦月',
          subTitle: '的私信',
          avatar:
            '//p1-arco.byteimg.com/tos-cn-i-uwbnlip3yd/8361eeb82904210b4f55fab888fe8416.png~tplv-uwbnlip3yd-webp.webp',
          content: '审批请求已发送，请查收',
          time: '今天 12:30:01',
          status: 1,
        },
        {
          id: 2,
          type: 'message',
          title: '宁波',
          subTitle: '的回复',
          avatar:
            '//p1-arco.byteimg.com/tos-cn-i-uwbnlip3yd/3ee5f13fb09879ecb5185e440cef6eb9.png~tplv-uwbnlip3yd-webp.webp',
          content: '此处 bug 已经修复',
          time: '今天 12:30:01',
          status: 1,
        },
        {
          id: 3,
          type: 'message',
          title: '宁波',
          subTitle: '的回复',
          avatar:
            '//p1-arco.byteimg.com/tos-cn-i-uwbnlip3yd/3ee5f13fb09879ecb5185e440cef6eb9.png~tplv-uwbnlip3yd-webp.webp',
          content: '此处 bug 已经修复',
          time: '今天 12:20:01',
          status: 1,
        },
        {
          id: 4,
          type: 'notice',
          title: '续费通知',
          subTitle: '',
          avatar: '',
          content: '您的产品使用期限即将截止，如需继续使用产品请前往购…',
          time: '今天 12:20:01',
          messageType: 3,
          status: 1,
        },
        {
          id: 5,
          type: 'notice',
          title: '规则开通成功',
          subTitle: '',
          avatar: '',
          content: '内容屏蔽规则于 2021-12-01 开通成功并生效',
          time: '今天 12:20:01',
          messageType: 1,
          status: 1,
        },
        {
          id: 6,
          type: 'todo',
          title: '质检队列变更',
          subTitle: '',
          avatar: '',
          content: '内容质检队列于 2021-12-01 19:50:23 进行变更，请重新…',
          time: '今天 12:20:01',
          messageType: 0,
          status: 1,
        },
      ];
    } catch (err) {
      // you can report use errorHandler or other
    } finally {
      setLoading(false);
    }
  }
  async function readMessage(data: MessageListType) {
    fetchSourceData();
  }
  const renderList = computed(() => {
    return messageData.messageList.filter(
      (item) => messageType.value === item.type
    );
  });
  const unreadCount = computed(() => {
    return renderList.value.filter((item) => !item.status).length;
  });
  const getUnreadList = (type: string) => {
    const list = messageData.messageList.filter(
      (item) => item.type === type && !item.status
    );
    return list;
  };
  const formatUnreadLength = (type: string) => {
    const list = getUnreadList(type);
    return list.length ? `(${list.length})` : ``;
  };
  const handleItemClick = (items: MessageListType) => {
    if (renderList.value.length) readMessage([...items]);
  };
  const emptyList = () => {
    messageData.messageList = [];
  };
  fetchSourceData();
</script>

<style scoped lang="less">
  :deep(.arco-popover-popup-content) {
    padding: 0;
  }

  :deep(.arco-list-item-meta) {
    align-items: flex-start;
  }
  :deep(.arco-tabs-nav) {
    padding: 14px 0 12px 16px;
    border-bottom: 1px solid var(--color-neutral-3);
  }
  :deep(.arco-tabs-content) {
    padding-top: 0;
    .arco-result-subtitle {
      color: rgb(var(--gray-6));
    }
  }
</style>
