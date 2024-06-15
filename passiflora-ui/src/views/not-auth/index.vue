<template>
  <div class="content">
    <a-result
      class="result"
      status="403"
      :subtitle="'登录凭证已过期，请重新登录'"
    >
    </a-result>
    <div class="operation-row">
      <a-button key="back" type="primary" @click="back"> back </a-button>
    </div>
  </div>
</template>

<script lang="ts" setup>
  import { useRouter } from 'vue-router';
  import { clearToken } from '@/utils/auth';

  const router = useRouter();
  const back = () => {
    clearToken();
    router.push({ name: 'login' });
  };
</script>

<style scoped lang="less">
  .content {
    // padding-top: 100px;
    position: absolute;
    top: 50%;
    left: 50%;
    margin-left: -95px;
    margin-top: -121px;
    text-align: center;
  }
</style>
