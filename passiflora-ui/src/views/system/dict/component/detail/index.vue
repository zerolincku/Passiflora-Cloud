<template>
  <a-modal
    v-model:visible="detailVisible"
    draggable
    simple
    width="auto"
    hide-cancel
    ok-text="关闭"
  >
    <a-card :bordered="false" :title="dictRecord.dictName">
      <a-row>
        <a-col :flex="1">
          <a-form
            :model="searchForm"
            :label-col-props="{ span: 6 }"
            :wrapper-col-props="{ span: 10 }"
            label-align="left"
          >
            <a-row :gutter="16">
              <a-col :span="8">
                <a-form-item class="m-0" field="label" :hide-label="true">
                  <a-input
                    v-model="searchForm['like[label]']"
                    placeholder="请输入标签"
                    @press-enter="search"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="8">
                <a-form-item class="m-0" field="value" :hide-label="true">
                  <a-input
                    v-model="searchForm['like[value]']"
                    placeholder="请输入值"
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
      <a-divider class="my-4" direction="horizontal" />
      <a-row class="flex mb-4 items-center">
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
              <a-button status="danger" :disabled="!(selectedKeys.length > 0)">
                <template #icon>
                  <icon-delete />
                </template>
                <template #default>批量删除</template>
              </a-button>
            </a-popconfirm>
          </a-space>
        </a-col>
        <a-col class="flex items-center justify-end" :span="12">
          <a-tooltip content="刷新">
            <div class="action-icon flex items-center mr-2" @click="search"
              ><icon-refresh size="18"
            /></div>
          </a-tooltip>
          <a-dropdown @select="handleSelectDensity">
            <a-tooltip content="密度">
              <div class="action-icon flex items-center mr-2"
                ><icon-line-height size="18"
              /></div>
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
              <div class="action-icon flex items-center"
                ><icon-settings size="18"
              /></div>
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
                        @change="handleChange($event, item, index)"
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
        row-key="dictItemId"
        :loading="loading"
        :pagination="pagination"
        :columns="cloneColumns"
        :data="renderData"
        :bordered="false"
        :size="size"
        :row-selection="rowSelection as TableRowSelection"
        @page-change="onPageChange"
        @page-size-change="onPageSizeChange"
      >
        <template #operations="{ record }">
          <a-space>
            <a-button
              type="text"
              size="small"
              :disabled="record.isSystem === 1"
              @click="updateButton(record)"
              >编辑</a-button
            >
            <a-popconfirm
              content="确认删除吗？"
              @ok="batchDelete([record.dictItemId])"
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
    <a-drawer
      :width="490"
      :visible="visible"
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
          <a-form-item field="label" label="标签" required>
            <a-input v-model="editFormModel.label" placeholder="请输入标签" />
          </a-form-item>
          <a-form-item field="value" label="值" required>
            <a-input v-model="editFormModel.value" placeholder="请输入值" />
          </a-form-item>
          <a-form-item field="remark" label="描述">
            <a-textarea
              v-model="editFormModel.remark"
              placeholder="请输入描述"
            />
          </a-form-item>
        </a-form>
      </div>
    </a-drawer>
  </a-modal>
</template>

<script lang="ts" setup>
  import { ref, reactive, computed, watch, nextTick } from 'vue';
  import {
    dictItemAdd,
    dictItemUpdate,
    dictItemDelete,
    dictItemPage,
    dictItemPageParams,
    DictItemRecord,
    DictRecord,
  } from '@/api/system/dict';
  import { cloneDeep, isEmpty } from 'lodash';
  import useLoading from '@/hooks/loading';
  import { Column, Pagination, SizeProps } from '@/types/global';
  import {
    TableColumnData,
    TableRowSelection,
  } from '@arco-design/web-vue/es/table/interface';
  import { densityList, rowSelection } from '@/utils';
  import { Message } from '@arco-design/web-vue';
  import Sortable from 'sortablejs';

  const detailVisible = ref(false);
  const dictRecord = ref<DictItemRecord>({});

  const { loading, setLoading } = useLoading(true);
  const selectedKeys = ref<string[]>([]);
  const renderData = ref<DictItemRecord[]>([]);
  const cloneColumns = ref<Column[]>([]);
  const showColumns = ref<Column[]>([]);
  const size = ref<SizeProps>('large');

  const generateSearchFormModel = () => {
    return {
      'like[label]': '',
      'like[value]': '',
    };
  };
  const searchForm = ref(generateSearchFormModel());

  const basePagination: Pagination = {
    current: 1,
    pageSize: 10,
  };
  const pagination = reactive({
    ...basePagination,
    showPageSize: true,
    pageSizeOptions: [10],
    showTotal: true,
  });
  const columns = computed<TableColumnData[]>(() => [
    {
      title: '标签',
      dataIndex: 'label',
      width: 160,
    },
    {
      title: '值',
      dataIndex: 'value',
      width: 160,
    },
    {
      title: '描述',
      dataIndex: 'remark',
      width: 200,
    },
    {
      title: '操作',
      dataIndex: 'operations',
      slotName: 'operations',
      headerCellStyle: { paddingLeft: '16px' },
    },
  ]);

  const fetchData = async (
    params: dictItemPageParams = {
      current: 1,
      pageSize: 10,
      dictId: dictRecord.value.dictId,
    }
  ) => {
    setLoading(true);
    try {
      const { data } = await dictItemPage(params);
      renderData.value = data.data.list;
      pagination.current = params.current;
      pagination.total = data.data.total;
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
      'eq[dictId]': dictRecord.value.dictId,
    } as unknown as dictItemPageParams);
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

  const handleSelectDensity = (
    val: string | number | Record<string, any> | undefined
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

  const batchDelete = async (ids: string[]) => {
    const { data } = await dictItemDelete(ids);
    selectedKeys.value = [];
    if (data.code === 200) {
      Message.success({
        content: '删除成功',
        duration: 5 * 1000,
      });
      search();
    }
  };

  const clickOpen = (r: DictRecord) => {
    dictRecord.value = cloneDeep(r);
    detailVisible.value = true;
    searchForm.value = generateSearchFormModel();
    search();
  };

  const reset = () => {
    searchForm.value = generateSearchFormModel();
    search();
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
  const visible = ref(false);
  const editFormModelTitle = ref<string>('');
  const editFormModel = ref<Partial<DictItemRecord>>({});

  const addButton = async () => {
    editFormModelTitle.value = '新增';
    if (!isEmpty(editFormModel.value.dictItemId)) {
      editFormModel.value = {};
    }
    editFormModel.value.dictId = dictRecord.value.dictId;
    visible.value = true;
  };

  const updateButton = async (recode: DictItemRecord) => {
    editFormModelTitle.value = '编辑';
    editFormModel.value = cloneDeep(recode);
    visible.value = true;
  };

  const rules = {
    label: [
      {
        required: true,
        message: '请输入标签',
      },
    ],
    value: [
      {
        required: true,
        message: '请输入值',
      },
    ],
  };

  const handleCancel = () => {
    visible.value = false;
  };
  const handleOk = async () => {
    const err = await editFormRef.value.validate();
    if (!err) {
      const saveAction =
        editFormModelTitle.value === '新增' ? dictItemAdd : dictItemUpdate;
      const { data } = await saveAction(editFormModel.value);

      if (data.code === 200) {
        Message.success({
          content: `${editFormModelTitle.value}成功`,
          duration: 5 * 1000,
        });
        visible.value = false;
        editFormRef.value.resetFields();
        search();
      }
    }
  };

  defineExpose({ clickOpen });
</script>

<style scoped lang="less"></style>
