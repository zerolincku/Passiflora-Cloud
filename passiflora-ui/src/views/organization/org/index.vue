<template>
  <div class="container">
    <Breadcrumb :items="['menu.organization', 'menu.organization.org']" />
    <a-grid class="inner-container" :cols="24" :col-gap="16" :row-gap="16">
      <a-grid-item class="h-full" :span="5">
        <a-card class="general-card h-full" title="组织架构">
          <a-row class="items-center" :gutter="16">
            <a-col :span="3">
              <a-tooltip
                :content="treeExpandContent"
                @click="treeExpandStatusChange"
              >
                <div class="ml-1.5 mt-2 mr-2">
                  <icon :name="treeExpandIcon" />
                </div>
              </a-tooltip>
            </a-col>
            <a-col :span="21">
              <a-input-search class="mt-2 max-w-60" v-model="searchKey" />
            </a-col>
          </a-row>
          <a-tree
            class="h-full"
            size="large"
            v-model:expanded-keys="treeExpandedKeys"
            :data="treeData"
            :field-names="treeFieldNames"
            :show-line="true"
            @select="selectOrg"
          >
            <template #switcher-icon="node, { isLeaf }">
              <IconDown v-if="!isLeaf" />
              <IconDriveFile v-if="isLeaf" />
            </template>
            <template #title="nodeData">
              <template v-if="(index = getMatchIndex(nodeData?.orgName)) < 0">{{
                nodeData?.orgName
              }}</template>
              <span v-else>
                {{ nodeData?.orgName?.substring(0, index) }}
                <span style="color: var(--color-primary-light-4)">
                  {{
                    nodeData?.orgName?.substring(
                      index,
                      index + searchKey.trim().length
                    )
                  }}
                </span>
                {{
                  nodeData?.orgName?.substring(index + searchKey.trim().length)
                }}
              </span>
            </template>
            <template #extra="nodeData">
              <IconPlus
                style="
                  position: absolute;
                  right: 8px;
                  font-size: 12px;
                  top: 10px;
                  color: #3370ff;
                "
                @click="() => onIconClick(nodeData)"
              />
            </template>
          </a-tree>
        </a-card>
      </a-grid-item>
      <a-grid-item class="h-full" :span="19">
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
                    <a-form-item field="orgName" :hide-label="true">
                      <a-input
                        v-model="searchForm['like[orgName]']"
                        placeholder="请输入机构名称"
                        @press-enter="search"
                      />
                    </a-form-item>
                  </a-col>
                  <a-col :span="6">
                    <a-form-item field="userName" :hide-label="true">
                      <a-input
                        v-model="searchForm['like[orgCode]']"
                        placeholder="请输入机构编码"
                        @press-enter="search"
                      />
                    </a-form-item>
                  </a-col>
                  <a-col :span="6">
                    <a-form-item field="orgType" :hide-label="true">
                      <a-select
                        v-model="searchForm['eq[orgType]']"
                        :options="orgTypeOptions"
                        :allow-clear="true"
                        :default-active-first-option="false"
                        placeholder="请选择机构类型"
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
                    :disabled="!selectedKeys.length > 0"
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
            row-key="orgId"
            :loading="loading"
            :pagination="pagination"
            :columns="(cloneColumns as TableColumnData[])"
            :data="renderData"
            :bordered="false"
            :size="size"
            :row-selection="rowSelection"
            @page-change="onPageChange"
            @page-size-change="onPageSizeChange"
          >
            <template #orgType="{ record }">
              {{ getLabelByValue(orgTypeOptions, record.orgType) }}
            </template>
            <template #operations="{ record }">
              <a-space>
                <a-button type="text" size="small">查看</a-button>
                <a-button type="text" size="small" @click="updateButton(record)"
                  >编辑</a-button
                >
                <a-popconfirm
                  content="确认删除吗？"
                  @ok="batchDelete([record.orgId])"
                >
                  <a-button type="text" size="small" status="danger"
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
      <template #title>{{ editFormModelTitle }}机构</template>
      <div>
        <a-form
          ref="editFormRef"
          :rules="rules"
          :model="editFormModel"
          layout="vertical"
          scroll-to-first-error
        >
          <a-form-item field="orgName" label="机构名称" required>
            <a-input
              v-model="editFormModel.orgName"
              placeholder="请输入机构名称"
            />
          </a-form-item>
          <a-form-item field="orgCode" label="机构编码" required>
            <a-input
              v-model="editFormModel.orgCode"
              placeholder="请输入机构编码"
            />
          </a-form-item>
          <a-form-item field="parentOrgId" label="上级机构">
            <a-tree-select
              v-model="editFormModel.parentOrgId"
              placeholder="请选择上级机构"
              size="large"
              :filter-tree-node="filterTreeNode"
              :data="orgTreeModel"
              :field-names="treeFieldNames"
              :allow-clear="true"
              :allow-search="true"
              :fallback-option="
                () => {
                  return { orgName: '无', orgId: '0' };
                }
              "
            />
          </a-form-item>
          <a-form-item field="orgType" label="机构类型" required>
            <a-select
              v-model="editFormModel.orgType"
              :options="orgTypeOptions"
              placeholder="请选择机构类型"
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
    orgPage,
    OrgRecord,
    orgPageParams,
    orgTree,
    orgDelete,
    orgAdd,
    orgUpdate,
  } from '@/api/organization/org';
  import { Pagination } from '@/types/global';
  import type { TableColumnData } from '@arco-design/web-vue/es/table/interface';
  import cloneDeep from 'lodash/cloneDeep';
  import Sortable from 'sortablejs';
  import { useDictStore } from '@/store';
  import { getLabelByValue } from '@/utils/dict';
  import { IconPlus } from '@arco-design/web-vue/es/icon';
  import { densityList, rowSelection } from '@/utils';
  import { isEmpty } from 'lodash';
  import { Message } from '@arco-design/web-vue';
  import { DictItemRecord } from '@/api/system/dict';
  import icon from '@/components/icon';

  type SizeProps = 'mini' | 'small' | 'medium' | 'large';
  type Column = TableColumnData & { checked?: true };

  const treeExpandContent = ref<string>('展开');
  const treeExpandIcon = ref<string>('icon-expand');
  const treeExpandedKeys = ref<string[]>([]);
  const treeAllKeys = ref<string[]>([]);

  const treeExpandStatusChange = () => {
    if (treeExpandIcon.value === 'icon-expand') {
      treeExpandIcon.value = 'icon-shrink';
      treeExpandContent.value = '收起';
      treeExpandedKeys.value = treeAllKeys.value;
    } else {
      treeExpandIcon.value = 'icon-expand';
      treeExpandContent.value = '展开';
      treeExpandedKeys.value = [];
    }
  };

  const generateSearchFormModel = () => {
    return {
      'like[orgIdPath]': '',
      'like[orgName]': '',
      'like[orgCode]': '',
      'eq[orgType]': '',
    };
  };
  const { loading, setLoading } = useLoading(true);
  const renderData = ref<OrgRecord[]>([]);
  const searchForm = ref(generateSearchFormModel());
  const cloneColumns = ref<Column[]>([]);
  const showColumns = ref<Column[]>([]);
  const selectedKeys = ref<string[]>([]);

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
      title: '机构名称',
      dataIndex: 'orgName',
    },
    {
      title: '机构编码',
      dataIndex: 'orgCode',
    },
    {
      title: '机构类型',
      dataIndex: 'orgType',
      slotName: 'orgType',
    },
    {
      title: '操作',
      dataIndex: 'operations',
      slotName: 'operations',
      headerCellStyle: { paddingLeft: '16px' },
    },
  ]);

  const orgTypeOptions = ref<DictItemRecord[] | undefined>([]);
  const orgTreeModel = ref<OrgRecord[]>([]);

  const treeData = computed(() => {
    return searchData(searchKey.value.trim());
  });

  function searchData(keyword: string) {
    const loop = (data: OrgRecord[]) => {
      const result: OrgRecord[] = [];
      data.forEach((item) => {
        if (
          (item.orgName as string)
            .toLowerCase()
            .indexOf(keyword.trim().toLowerCase()) > -1
        ) {
          result.push({ ...item });
        } else if (item.children) {
          const filterData = loop(item.children);
          if (filterData.length) {
            result.push({
              ...item,
              children: filterData,
            });
          }
        }
      });
      return result;
    };

    return loop(orgTreeModel.value);
  }

  function getMatchIndex(orgName: string) {
    if (!searchKey.value.trim()) {
      return -1;
    }
    return orgName.toLowerCase().indexOf(searchKey.value.trim().toLowerCase());
  }

  const searchKey = ref('');
  const treeFieldNames = {
    key: 'orgId',
    title: 'orgName',
  };
  onMounted(async () => {
    orgTypeOptions.value = await useDictStore().getDictItems('orgType', true);
    await refreshOrgTree();
  });

  const refreshOrgTree = async () => {
    const { data } = await orgTree('');
    orgTreeModel.value = data.data;
    calculateOrg(orgTreeModel.value);
  };

  const calculateOrg = (orgs: OrgRecord[] | undefined) => {
    if (!orgs) {
      return;
    }
    orgs.forEach((org) => {
      treeAllKeys.value.push(org.orgId as string);
      calculateOrg(org.children);
    });
  };

  const selectOrg = async (orgIds: string[]) => {
    const orgId = orgIds[0];
    searchForm.value['like[orgIdPath]'] = orgId;
    search();
  };

  const onIconClick = (nodeData: OrgRecord) => {
    addButton();
    editFormModel.parentOrgId = nodeData.orgId;
  };

  const fetchData = async (
    params: orgPageParams = { current: 1, pageSize: 10 }
  ) => {
    setLoading(true);
    try {
      const { data } = await orgPage(params);
      renderData.value = data.data.list;
      pagination.current = params.current;
      pagination.total = data.data.total;
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
    } as unknown as orgPageParams);
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
  let editFormModel = reactive<Partial<OrgRecord>>({});

  const filterTreeNode = (inputText: string, node: OrgRecord) => {
    return (
      (node.orgName as string).toLowerCase().indexOf(inputText.toLowerCase()) >
      -1
    );
  };

  const rules = {
    orgName: [
      {
        required: true,
        message: '请输入机构名称',
      },
    ],
    orgCode: [
      {
        required: true,
        message: '请输入机构编码',
      },
    ],
  };

  const addButton = () => {
    editFormModelTitle.value = '新增';
    if (!isEmpty(editFormModel.orgId)) {
      editFormModel = reactive<Partial<OrgRecord>>({});
    }
    editFormVisible.value = true;
  };

  const updateButton = async (recode: OrgRecord) => {
    editFormModelTitle.value = '编辑';
    editFormModel = reactive<OrgRecord>(cloneDeep(recode));
    editFormVisible.value = true;
  };

  const batchDelete = async (ids: string[]) => {
    const { data } = await orgDelete(ids);
    selectedKeys.value = [];
    if (data.code === 200) {
      Message.success({
        content: '删除成功',
        duration: 5 * 1000,
      });
      search();
      await refreshOrgTree();
    }
  };
  const handleCancel = () => {
    editFormVisible.value = false;
  };
  const handleOk = async () => {
    const err = await editFormRef.value.validate();
    if (!err) {
      const saveAction =
        editFormModelTitle.value === '新增' ? orgAdd : orgUpdate;
      const { data } = await saveAction(editFormModel);

      if (data.code === 200) {
        Message.success({
          content: `${editFormModelTitle.value}成功`,
          duration: 5 * 1000,
        });
        editFormVisible.value = false;
        editFormRef.value.resetFields();
        search();
        await refreshOrgTree();
      }
    }
  };
</script>

<script lang="ts">
  export default {
    name: 'SystemUser',
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
