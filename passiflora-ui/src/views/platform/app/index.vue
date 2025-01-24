<template>
  <div class="container">
    <Breadcrumb :items="['menu.platform', 'menu.platform.app']" />
    <a-grid class="inner-container" :cols="24" :col-gap="16" :row-gap="16">
      <a-grid-item class="h-full" :span="24">
        <a-card class="general-card h-full" title="查询表格">
          <a-row>
            <a-col :flex="1">
              <a-form
                :model="searchForm"
                :label-col-props="{ span: 6 }"
                :wrapper-col-props="{ span: 18 }"
                label-align="left"
              >
                <a-row :gutter="16">
                  <a-col :span="6">
                    <a-form-item field="dictName" :hide-label="true">
                      <a-input
                        v-model="searchForm['like[appName]']"
                        placeholder="请输入应用名称"
                        @press-enter="search"
                      />
                    </a-form-item>
                  </a-col>
                  <a-col :span="6">
                    <a-form-item field="dictTag" :hide-label="true">
                      <a-input
                        v-model="searchForm['like[appKey]']"
                        placeholder="请输入应用标签"
                        @press-enter="search"
                      />
                    </a-form-item>
                  </a-col>
                </a-row>
              </a-form>
            </a-col>
            <a-divider class="h-9" direction="vertical" />
            <a-col :flex="'36px'" class="text-right">
              <a-space direction="horizontal" :size="18">
                <a-button type="primary" @click="search">
                  <template #icon>
                    <icon-search />
                  </template>
                  查询
                </a-button>
                <a-button @click="reset">
                  <template #icon>
                    <icon-refresh />
                  </template>
                  重置
                </a-button>
              </a-space>
            </a-col>
          </a-row>
          <a-divider class="mt-0" />
          <a-row class="mb-4">
            <a-col :span="12">
              <a-space>
                <a-button type="primary" @click="addButton({})">
                  <template #icon>
                    <icon-plus />
                  </template>
                  新增
                </a-button>
                <a-popconfirm
                  content="确认删除吗？"
                  @ok="batchDelete(selectedKeys)"
                >
                  <a-button
                    status="danger"
                    :disabled="!(selectedKeys.length > 0)"
                  >
                    <template #icon>
                      <icon-delete />
                    </template>
                    <template #default>批量删除</template>
                  </a-button>
                </a-popconfirm>
              </a-space>
            </a-col>
            <a-col :span="12" class="flex items-center justify-end">
              <a-tooltip content="刷新">
                <div class="action-icon" @click="search"
                  ><icon-refresh size="18"
                /></div>
              </a-tooltip>
              <a-dropdown @select="handleSelectDensity">
                <a-tooltip content="密度">
                  <div class="action-icon"><icon-line-height size="18" /></div>
                </a-tooltip>
                <template #content>
                  <a-doption
                    v-for="item in densityList"
                    :key="item.value"
                    :value="item.value"
                    :class="{ active: item.value === size }"
                  >
                    <span>{{ item.name }}</span>
                  </a-doption>
                </template>
              </a-dropdown>
              <a-tooltip content="列设置">
                <a-popover
                  trigger="click"
                  app="bl"
                  @popup-visible-change="popupVisibleChange"
                >
                  <div class="action-icon"><icon-settings size="18" /></div>
                  <template #content>
                    <div id="tableSetting">
                      <div
                        v-for="(item, index) in showColumns"
                        :key="item.dataIndex"
                        class="setting"
                      >
                        <div class="mr-1 cursor-move">
                          <icon-drag-arrow />
                        </div>
                        <div>
                          <a-checkbox
                            v-model="item.checked"
                            @change="
                              handleChange(
                                $event,
                                item as TableColumnData,
                                index
                              )
                            "
                          >
                          </a-checkbox>
                        </div>
                        <div class="title">
                          {{ item.title }}
                        </div>
                      </div>
                    </div>
                  </template>
                </a-popover>
              </a-tooltip>
            </a-col>
          </a-row>
          <a-table
            v-model:selectedKeys="selectedKeys"
            row-key="appId"
            :loading="loading"
            :pagination="pagination"
            :columns="(cloneColumns as TableColumnData[])"
            :data="renderData"
            :bordered="false"
            :size="size"
            :row-selection="rowSelection"
            :hide-expand-button-on-empty="true"
            @page-change="onPageChange"
            @page-size-change="onPageSizeChange"
          >
            <template #appStatus="{ record }">
              <span v-if="record.appStatus === 1" class="circle pass"></span>
              <span v-else class="circle err"></span>
              {{
                getLabelByValue(
                  appStatusOptions as EnumRecord[],
                  record.appStatus
                )
              }}
            </template>
            <template #operations="{ record }">
              <a-space>
                <a-button type="text" size="small" @click="updateButton(record)"
                  >编辑</a-button
                >
                <a-popconfirm
                  v-if="record.appStatus === 1"
                  content="确认禁用吗？"
                  @ok="batchDisable([record.appId])"
                >
                  <a-button
                    v-if="record.appStatus === 1"
                    type="text"
                    size="small"
                    status="warning"
                    >禁用</a-button
                  >
                </a-popconfirm>
                <a-popconfirm
                  v-if="record.appStatus === 0"
                  content="确认启用吗？"
                  @ok="batchEnable([record.appId])"
                >
                  <a-button
                    v-if="record.appStatus === 0"
                    type="text"
                    size="small"
                    status="success"
                    >启用</a-button
                  >
                </a-popconfirm>
                <a-popconfirm
                  content="确认删除吗？"
                  @ok="batchDelete([record.appId])"
                >
                  <a-button type="text" size="small" status="danger">
                    <template #icon> <icon-delete /> </template
                    ><template #default>删除</template></a-button
                  >
                </a-popconfirm>
              </a-space>
            </template>
          </a-table>
        </a-card>
      </a-grid-item>
    </a-grid>
    <a-drawer
      :width="490"
      :visible="editFormVisible"
      unmount-on-close
      @ok="handleOk"
      @cancel="handleCancel"
    >
      <template #title>{{ editFormModelTitle }}应用</template>
      <div>
        <a-form
          ref="editFormRef"
          :rules="rules"
          :model="editFormModel"
          layout="vertical"
          scroll-to-first-error
        >
          <a-form-item field="appName" label="应用名称" required>
            <a-input
              v-model="editFormModel.appName"
              placeholder="请输入应用名称"
            />
          </a-form-item>
          <a-form-item field="appUrl" label="应用地址" required>
            <a-input
                v-model="editFormModel.appUrl"
                placeholder="请输入应用地址"
            />
          </a-form-item>
          <a-form-item field="appIcon" label="应用图标" required>
            <a-input
                v-model="editFormModel.appIcon"
                placeholder="请输入应用图标"
            />
          </a-form-item>
          <a-form-item field="appType" label="应用类型" required>
            <a-input
                v-model="editFormModel.appType"
                placeholder="请输入应用类型"
            />
          </a-form-item>
          <a-form-item field="appRemark" label="应用描述" required>
            <a-input
                v-model="editFormModel.appRemark"
                placeholder="请输入应用描述"
            />
          </a-form-item>
          <a-form-item field="appStatus" label="状态" required>
            <a-radio-group
              v-model="editFormModel.appStatus"
              :options="appStatusOptions"
            />
          </a-form-item>
        </a-form>
      </div>
    </a-drawer>
  </div>
</template>

<script lang="ts" setup>
  import { computed, ref, reactive, watch, nextTick, onMounted } from 'vue';
  import useLoading from '@/hooks/loading';
  import {
    AppRecord,
    appDelete,
    appAdd,
    appUpdate,
    appDisable,
    appEnable,
    appPage,
    appPageParams,
  } from '@/api/platform/app';
  import { TableColumnData } from '@arco-design/web-vue/es/table/interface';
  import cloneDeep from 'lodash/cloneDeep';
  import { densityList, rowSelection } from '@/utils';
  import { Message } from '@arco-design/web-vue';
  import { useEnumStore } from '@/store';
  import { EnumRecord } from '@/api/system/enum';
  import { getLabelByValue } from '@/utils/enums';
  import { Pagination } from '@/types/global';
  import Sortable from 'sortablejs';

  type SizeProps = 'mini' | 'small' | 'medium' | 'large';
  type Column = TableColumnData & { checked?: true };

  const generateSearchFormModel = () => {
    return {
      'like[appName]': '',
      'like[appKey]': '',
    };
  };

  const { loading, setLoading } = useLoading(true);
  const renderData = ref<AppRecord[]>([]);
  const searchForm = ref(generateSearchFormModel());
  const cloneColumns = ref<Column[]>([]);
  const showColumns = ref<Column[]>([]);
  const selectedKeys = ref<string[]>([]);

  const size = ref<SizeProps>('large');

  const basePagination: Pagination = {
    pageNum: 1,
    pageSize: 10,
  };
  const pagination = reactive({
    ...basePagination,
    showPageSize: true,
    pageSizeOptions: [10, 20, 50],
    showTotal: true,
  });

  const columns = computed<TableColumnData[]>(() => [
    {
      title: '应用名称',
      dataIndex: 'appName',
      slotName: 'appName',
    },
    {
      title: '应用令牌',
      dataIndex: 'appKey',
      slotName: 'appKey',
    },
    {
      title: '应用秘钥',
      dataIndex: 'appSecret',
      slotName: 'appSecret',
    },
    {
      title: '应用地址',
      dataIndex: 'appUrl',
      slotName: 'appUrl',
    },
    {
      title: '应用类型',
      dataIndex: 'appType',
      slotName: 'appType',
    },
    {
      title: '应用令牌有效期',
      dataIndex: 'appPeriod',
      slotName: 'appPeriod',
    },
    {
      title: '状态',
      dataIndex: 'appStatus',
      slotName: 'appStatus',
    },
    {
      title: '操作',
      dataIndex: 'operations',
      slotName: 'operations',
      headerCellStyle: { paddingLeft: '16px' },
    },
  ]);

  watch(
    () => columns.value,
    (val) => {
      cloneColumns.value = cloneDeep(val);
      cloneColumns.value.forEach((item, index) => {
        item.checked = true;
      });
      showColumns.value = cloneDeep(cloneColumns.value);
    },
    { deep: true, immediate: true }
  );

  const fetchData = async (
    params: appPageParams = { pageNum: 1, pageSize: 10 }
  ) => {
    setLoading(true);
    try {
      const { data } = await appPage(params);
      renderData.value = data.data;
      pagination.pageNum = params.pageNum;
      pagination.total = data.total;
    } catch (err) {
      // you can report use errorHandler or other
    } finally {
      setLoading(false);
    }
  };

  const search = () => {
    fetchData({
      ...basePagination,
      ...searchForm.value,
    } as unknown as appPageParams);
  };

  const onPageChange = (pageNum: number) => {
    basePagination.pageNum = pageNum;
    search();
  };

  const onPageSizeChange = (pageSize: number) => {
    basePagination.pageSize = pageSize;
    pagination.pageSize = pageSize;
    search();
  };

  const appStatusOptions = ref<EnumRecord[] | undefined>([]);
  const appTypeOptions = ref<EnumRecord[] | undefined>([]);
  onMounted(async () => {
    appStatusOptions.value = await useEnumStore().getEnums('status-enum');
    appTypeOptions.value = await useEnumStore().getEnums('app-type-enum');
    await fetchData();
  });

  const handleSelectDensity = (
    val: string | number | Record<string, any> | undefined,
    e: Event
  ) => {
    size.value = val as SizeProps;
  };

  const handleChange = (
    checked: boolean | (string | boolean | number)[],
    column: Column,
    index: number
  ) => {
    if (!checked) {
      cloneColumns.value = showColumns.value.filter(
        (item) => item.dataIndex !== column.dataIndex
      );
    } else {
      cloneColumns.value.splice(index, 0, column);
    }
  };

  // 新增抽屉
  const editFormRef = ref();
  const editFormVisible = ref(false);
  const editFormModelTitle = ref<string>('');
  let editFormModel = reactive<Partial<AppRecord>>({
    appStatus: 1,
  });

  const rules = {};

  const addButton = (record: AppRecord) => {
    editFormModelTitle.value = '新增';
    editFormModel = reactive<Partial<AppRecord>>({
      appStatus: 1,
    });
    editFormVisible.value = true;
  };

  const updateButton = async (recode: AppRecord) => {
    editFormModelTitle.value = '编辑';
    editFormModel = reactive<AppRecord>(cloneDeep(recode));
    editFormVisible.value = true;
  };

  const batchDisable = async (ids: string[]) => {
    const { data } = await appDisable(ids);
    if (data.code === 200) {
      Message.success({
        content: '禁用成功',
        duration: 5 * 1000,
      });
      search();
    }
  };

  const batchEnable = async (ids: string[]) => {
    const { data } = await appEnable(ids);
    if (data.code === 200) {
      Message.success({
        content: '启用成功',
        duration: 5 * 1000,
      });
      search();
    }
  };

  const batchDelete = async (ids: string[]) => {
    const { data } = await appDelete(ids);
    selectedKeys.value = [];
    if (data.code === 200) {
      Message.success({
        content: '删除成功',
        duration: 5 * 1000,
      });
      search();
    }
  };
  const handleCancel = () => {
    editFormVisible.value = false;
  };
  const handleOk = async () => {
    const err = await editFormRef.value.validate();
    if (!err) {
      const saveAction =
        editFormModelTitle.value === '新增' ? appAdd : appUpdate;
      const { data } = await saveAction(editFormModel);

      if (data.code === 200) {
        Message.success({
          content: `${editFormModelTitle.value}成功`,
          duration: 5 * 1000,
        });
        editFormVisible.value = false;
        editFormRef.value.resetFields();
        search();
      }
    }
    editFormModel = reactive<Partial<AppRecord>>({
      appStatus: 1,
    });
  };

  const reset = () => {
    searchForm.value = generateSearchFormModel();
    search();
  };

  const popupVisibleChange = (val: boolean) => {
    if (val) {
      nextTick(() => {
        const el = document.getElementById('tableSetting') as HTMLElement;
        const sortable = new Sortable(el, {
          onEnd(e: any) {
            const { oldIndex, newIndex } = e;
            exchangeArray(cloneColumns.value, oldIndex, newIndex);
            exchangeArray(showColumns.value, oldIndex, newIndex);
          },
        });
      });
    }
  };

  const exchangeArray = <T extends Array<any>>(
    array: T,
    beforeIdx: number,
    newIdx: number,
    isDeep = false
  ): T => {
    const newArray = isDeep ? cloneDeep(array) : array;
    if (beforeIdx > -1 && newIdx > -1) {
      // 先替换后面的，然后拿到替换的结果替换前面的
      newArray.splice(
        beforeIdx,
        1,
        newArray.splice(newIdx, 1, newArray[beforeIdx]).pop()
      );
    }
    return newArray;
  };
</script>

<script lang="ts">
  export default {
    name: 'SysApp',
  };
</script>

<style scoped lang="less">
  .container {
    padding: 0 20px 20px 20px;
    height: calc(100% - 40px);
    :deep(.content) {
      app: relative;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      height: 100%;
      text-align: center;
      background-color: var(--color-bg-1);
      border-radius: 4px;
    }
  }

  :deep(.arco-table-th) {
    &:last-child {
      .arco-table-th-item-title {
        margin-left: 16px;
      }
    }
  }
  .action-icon {
    margin-left: 12px;
    cursor: pointer;
  }
  .active {
    color: #0960bd;
    background-color: #e3f4fc;
  }
  .setting {
    display: flex;
    align-items: center;
    width: 200px;
    .title {
      margin-left: 12px;
      cursor: pointer;
    }
  }

  :deep .arco-table-expand-btn {
    background-color: transparent;
  }

  :deep .arco-table-expand-btn:hover {
    background-color: var(--color-neutral-3);
  }
</style>
