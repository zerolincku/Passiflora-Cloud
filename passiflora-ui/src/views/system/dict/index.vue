<template>
  <div class="container">
    <Breadcrumb :items="['menu.system', 'menu.system.dict']" />
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
                  <a-col :span="8">
                    <a-form-item field="dictName" :hide-label="true">
                      <a-input
                        v-model="searchForm['like[dictName]']"
                        placeholder="请输入名称"
                        @press-enter="search"
                      />
                    </a-form-item>
                  </a-col>
                  <a-col :span="6">
                    <a-form-item field="dictTag" :hide-label="true">
                      <a-input
                        v-model="searchForm['like[dictTag]']"
                        placeholder="请输入标签"
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
                <a-button type="primary" @click="addButton">
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
                  position="bl"
                  @popup-visible-change="popupVisibleChange"
                >
                  <div class="action-icon"><icon-settings size="18" /></div>
                  <template #content>
                    <div id="tableSetting">
                      <a-row
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
                      </a-row>
                    </div>
                  </template>
                </a-popover>
              </a-tooltip>
            </a-col>
          </a-row>
          <a-table
            v-model:selectedKeys="selectedKeys"
            row-key="dictId"
            :loading="loading"
            :pagination="pagination"
            :columns="(cloneColumns as TableColumnData[])"
            :data="renderData"
            :bordered="false"
            :size="size"
            :row-selection="rowSelection as TableRowSelection"
            @page-change="onPageChange"
            @page-size-change="onPageSizeChange"
          >
            <template #valueIsOnly="{ record }">
              {{
                getLabelByValue(
                  valueIsOnlyOptions as EnumRecord[],
                  record.valueIsOnly
                )
              }}
            </template>
            <template #operations="{ record }">
              <a-space>
                <a-button type="text" size="small" @click="detailClick(record)"
                  >查看</a-button
                >
                <a-button
                  type="text"
                  size="small"
                  :disabled="record.isSystem === 1"
                  @click="updateButton(record)"
                  >编辑</a-button
                >
                <a-popconfirm
                  content="确认删除吗？"
                  @ok="batchDelete([record.dictId])"
                >
                  <a-button
                    type="text"
                    size="small"
                    status="danger"
                    :disabled="record.isSystem === 1"
                    ><template #icon> <icon-delete /> </template
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
      <template #title>{{ editFormModelTitle }}字典</template>
      <div>
        <a-form
          ref="editFormRef"
          :rules="rules"
          :model="editFormModel"
          layout="vertical"
          scroll-to-first-error
        >
          <a-form-item field="dictName" label="名称" required>
            <a-input
              v-model="editFormModel.dictName"
              placeholder="请输入名称"
            />
          </a-form-item>
          <a-form-item field="dictTag" label="标签" required>
            <a-input v-model="editFormModel.dictTag" placeholder="请输入标签" />
          </a-form-item>
          <a-form-item field="remark" label="描述">
            <a-textarea
              v-model="editFormModel.remark"
              placeholder="请输入描述"
            />
          </a-form-item>
          <a-form-item field="valueIsOnly" label="字典项值可重复" required>
            <a-radio-group
              v-model="editFormModel.valueIsOnly"
              :options="valueIsOnlyOptions"
            ></a-radio-group>
          </a-form-item>
        </a-form>
      </div>
    </a-drawer>
    <Detail ref="detailRef"></Detail>
  </div>
</template>

<script lang="ts" setup>
  import { computed, onMounted, reactive, ref, watch, nextTick } from 'vue';
  import useLoading from '@/hooks/loading';
  import {
    dictAdd,
    dictUpdate,
    dictDelete,
    dictPage,
    dictPageParams,
    DictRecord,
  } from '@/api/system/dict';
  import { Pagination, SizeProps, Column } from '@/types/global';
  import type {
    TableColumnData,
    TableRowSelection,
  } from '@arco-design/web-vue/es/table/interface';
  import cloneDeep from 'lodash/cloneDeep';
  import { Message } from '@arco-design/web-vue';
  import { useEnumStore } from '@/store';
  import { EnumRecord } from '@/api/system/enum';
  import { densityList, rowSelection } from '@/utils';
  import { getLabelByValue } from '@/utils/enums';
  import { isEmpty } from 'lodash';
  import Sortable from 'sortablejs';
  import Detail from '@/views/system/dict/component/detail/index.vue';

  const generateSearchFormModel = () => {
    return {
      'like[dictName]': '',
      'like[dictTag]': '',
    };
  };
  const { loading, setLoading } = useLoading(true);
  const selectedKeys = ref<string[]>([]);
  const renderData = ref<DictRecord[]>([]);
  const searchForm = ref(generateSearchFormModel());
  const cloneColumns = ref<Column[]>([]);
  const showColumns = ref<Column[]>([]);
  const size = ref<SizeProps>('large');

  const basePagination: Pagination = {
    current: 1,
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
      title: '名称',
      dataIndex: 'dictName',
    },
    {
      title: '标签',
      dataIndex: 'dictTag',
    },
    {
      title: '字典项值可重复',
      dataIndex: 'valueIsOnly',
      slotName: 'valueIsOnly',
    },
    {
      title: '描述',
      dataIndex: 'remark',
    },
    {
      title: '操作',
      dataIndex: 'operations',
      slotName: 'operations',
      headerCellStyle: { paddingLeft: '16px' },
    },
  ]);
  const fetchData = async (
    params: dictPageParams = { current: 1, pageSize: 10 }
  ) => {
    setLoading(true);
    try {
      const { data } = await dictPage(params);
      renderData.value = data.data;
      pagination.current = params.current;
      pagination.total = data.total;
      renderData.value.forEach((item) => {
        if (item.isSystem === 1) {
          item.disabled = true;
        }
      });
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
    } as unknown as dictPageParams);
  };
  const onPageChange = (current: number) => {
    basePagination.current = current;
    search();
  };

  const onPageSizeChange = (pageSize: number) => {
    basePagination.pageSize = pageSize;
    pagination.pageSize = pageSize;
    search();
  };

  fetchData();
  const reset = () => {
    searchForm.value = generateSearchFormModel();
    search();
  };

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

  // 新增抽屉
  const editFormRef = ref();
  const editFormVisible = ref(false);
  const editFormModelTitle = ref<string>('');
  let editFormModel = reactive<Partial<DictRecord>>({
    valueIsOnly: 0,
  });

  const rules = {
    dictName: [
      {
        required: true,
        message: '请输入名称',
      },
    ],
    dictTag: [
      {
        required: true,
        message: '请输入标签',
      },
    ],
  };

  const valueIsOnlyOptions = ref<EnumRecord[] | undefined>([]);
  onMounted(async () => {
    valueIsOnlyOptions.value = await useEnumStore().getEnums('yes-or-no-enum');
  });

  const addButton = async () => {
    editFormModelTitle.value = '新增';
    if (!isEmpty(editFormModel.dictId)) {
      editFormModel = reactive<Partial<DictRecord>>({
        valueIsOnly: 0,
      });
    }
    editFormVisible.value = true;
  };

  const updateButton = async (recode: DictRecord) => {
    editFormModelTitle.value = '编辑';
    editFormModel = reactive<DictRecord>(cloneDeep(recode));
    editFormVisible.value = true;
  };

  const batchDelete = async (ids: string[]) => {
    const { data } = await dictDelete(ids);
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
        editFormModelTitle.value === '新增' ? dictAdd : dictUpdate;
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
  };

  const detailRef = ref();
  const detailClick = (recode: DictRecord) => {
    detailRef.value.clickOpen(recode);
  };
</script>

<script lang="ts">
  export default {
    name: 'SystemDict',
  };
</script>

<style scoped lang="less">
  .container {
    padding: 0 20px 20px 20px;
    height: calc(100% - 40px);
    :deep(.content) {
      position: relative;
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
</style>
