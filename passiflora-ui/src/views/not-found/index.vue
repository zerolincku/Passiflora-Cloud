<template>
  <div class="content">
    <a-result class="result" status="404" :subtitle="'没有找到页面'">
    </a-result>
    <div class="operation-row">
      <a-button key="back" type="primary" @click="back"> back </a-button>
    </div>
  </div>
</template>

<script lang="ts" setup>
  import { useRouter } from 'vue-router';

  const router = useRouter();
  const back = () => {
    // warning： Go to the node that has the permission
    router.back();
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
