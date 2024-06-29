<template>
  <div class="container">
    <Breadcrumb :items="['menu.system', 'menu.system.permission']" />
    <a-grid class="inner-container" :cols="24" :col-gap="16" :row-gap="16">
      <a-grid-item class="h-full" :span="24">
        <a-card class="general-card h-full" title="查询表格">
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
            row-key="permissionId"
            :loading="loading"
            :pagination="false"
            :columns="(cloneColumns as TableColumnData[])"
            :data="renderData"
            :bordered="false"
            :size="size"
            :row-selection="rowSelection"
            :hide-expand-button-on-empty="true"
            :draggable="{}"
            :expandable="expandable"
            @change="tableChange"
          >
            <template #permissionType="{ record }">
              <span v-if="record.permissionType === 0" class="circle"></span>
              <span v-else class="circle warn"></span>
              {{
                getLabelByValue(
                  permissionTypeEnumOptions as EnumRecord[],
                  record.permissionType
                )
              }}
            </template>
            <template #permissionStatus="{ record }">
              <span
                v-if="record.permissionStatus === 1"
                class="circle pass"
              ></span>
              <span v-else class="circle err"></span>
              {{
                getLabelByValue(
                  statusOptions as EnumRecord[],
                  record.permissionStatus
                )
              }}
            </template>
            <template #permissionTitle="{ record }">
              <icon :name="record.permissionIcon" />
              {{ record.permissionTitle }}
              <a-button
                v-if="record.permissionType !== 2"
                class="text-xl"
                type="text"
                size="mini"
                @click="addButton(record)"
                >+</a-button
              >
            </template>
            <template #operations="{ record }">
              <a-space>
                <a-button type="text" size="small" @click="updateButton(record)"
                  >编辑</a-button
                >
                <a-popconfirm
                  v-if="record.permissionStatus === 1"
                  content="禁用会自动禁用下级权限，确认禁用吗？"
                  @ok="batchDisable([record.permissionId])"
                >
                  <a-button
                    v-if="record.permissionStatus === 1"
                    type="text"
                    size="small"
                    status="warning"
                    >禁用</a-button
                  >
                </a-popconfirm>
                <a-popconfirm
                  v-if="record.permissionStatus === 0"
                  content="启用会自动启用上级权限，确认启用吗？"
                  @ok="batchEnable([record.permissionId])"
                >
                  <a-button
                    v-if="record.permissionStatus === 0"
                    type="text"
                    size="small"
                    status="success"
                    >启用</a-button
                  >
                </a-popconfirm>
                <a-popconfirm
                  content="确认删除吗？"
                  @ok="batchDelete([record.permissionId])"
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
      <template #title
        >{{ editFormModelTitle
        }}{{
          getLabelByValue(
            permissionTypeEnumOptions as EnumRecord[],
            editFormModel.permissionType
          )
        }}</template
      >
      <div>
        <a-form
          ref="editFormRef"
          :rules="rules"
          :model="editFormModel"
          layout="vertical"
          scroll-to-first-error
        >
          <a-form-item
            field="permissionTitle"
            :label="getLabel('', '名称', editFormModel.permissionType as number)"
            required
          >
            <a-input
              v-model="editFormModel.permissionTitle"
              :placeholder="getLabel('请输入', '名称', editFormModel.permissionType as number)"
            />
          </a-form-item>
          <a-form-item
            field="permissionName"
            :label="getLabel('', '标识', editFormModel.permissionType as number)"
            required
          >
            <a-input
              v-model="editFormModel.permissionName"
              :placeholder="getLabel('请输入', '标识', editFormModel.permissionType as number)"
            />
          </a-form-item>
          <a-form-item field="permissionType" label="类型" required>
            <a-radio-group
              v-model="editFormModel.permissionType"
              :options="permissionTypeEnumOptions"
              @change="typeChange"
            />
          </a-form-item>
          <a-form-item field="permissionStatus" label="状态" required>
            <a-radio-group
              v-model="editFormModel.permissionStatus"
              :options="statusOptions"
            />
          </a-form-item>
          <a-form-item
            field="permissionParentId"
            :label="getLabel('上级', '', editFormModel.permissionType as number > 0 ? (editFormModel.permissionType as number - 1) : 0)"
            :required="editFormModel.permissionType === 2"
          >
            <a-tree-select
              v-model="editFormModel.permissionParentId"
              :placeholder="getLabel('请选择上级', '', editFormModel.permissionType as number > 0 ? (editFormModel.permissionType as number - 1) : 0)"
              size="large"
              :data="menuTree"
              :field-names="treeFieldName"
              :filter-tree-node="filterTreeNode"
              :allow-clear="true"
              :allow-search="true"
              :fallback-option="
                () => {
                  return { title: '无', permission: '0' };
                }
              "
            />
          </a-form-item>
          <a-form-item field="permissionIcon" label="图标">
            <icon-select
              v-model="editFormModel.permissionIcon"
              :disabled="editFormModel.permissionType === 2"
            ></icon-select>
          </a-form-item>
        </a-form>
      </div>
    </a-drawer>
  </div>
</template>

<script lang="ts" setup>
  import { computed, ref, reactive, watch, nextTick, onMounted, h } from 'vue';
  import useLoading from '@/hooks/loading';
  import {
    PermissionRecord,
    permissionTableTree,
    permissionDelete,
    permissionAdd,
    permissionUpdate,
    permissionUpdateOrder,
    permissionDisable,
    permissionEnable,
  } from '@/api/system/permission';
  import {
    TableColumnData,
    TableData,
  } from '@arco-design/web-vue/es/table/interface';
  import cloneDeep from 'lodash/cloneDeep';
  import Sortable from 'sortablejs';
  import { IconPlus, IconDown, IconUp } from '@arco-design/web-vue/es/icon';
  import { densityList, rowSelection } from '@/utils';
  import { Message } from '@arco-design/web-vue';
  import icon from '@/components/icon';
  import iconSelect from '@/components/icon-select';
  import { useAppStore, useEnumStore } from '@/store';
  import { EnumRecord } from '@/api/system/enum';
  import { getLabelByValue } from '@/utils/enums';

  type SizeProps = 'mini' | 'small' | 'medium' | 'large';
  type Column = TableColumnData & { checked?: true };

  const { loading, setLoading } = useLoading(true);
  const renderData = ref<PermissionRecord[]>([]);
  const menuTree = ref<PermissionRecord[]>([]);
  const cloneColumns = ref<Column[]>([]);
  const showColumns = ref<Column[]>([]);
  const selectedKeys = ref<string[]>([]);

  const size = ref<SizeProps>('large');

  const columns = computed<TableColumnData[]>(() => [
    {
      title: '权限名称',
      dataIndex: 'permissionTitle',
      slotName: 'permissionTitle',
    },
    {
      title: '权限标识',
      dataIndex: 'permissionName',
      width: 230,
      ellipsis: true,
      tooltip: true,
    },
    {
      title: '类型',
      dataIndex: 'permissionType',
      slotName: 'permissionType',
    },
    {
      title: '状态',
      dataIndex: 'permissionStatus',
      slotName: 'permissionStatus',
    },
    {
      title: '备注',
      dataIndex: 'remark',
    },
    {
      title: '操作',
      dataIndex: 'operations',
      slotName: 'operations',
      headerCellStyle: { paddingLeft: '16px' },
    },
  ]);

  const treeFieldName = {
    key: 'permission',
    title: 'title',
  };
  const fetchData = async () => {
    setLoading(true);
    try {
      const { data } = await permissionTableTree();
      renderData.value = data.data;
      menuTree.value = [];
      dealMenuTreeOption(data.data, menuTree.value);
      computedMenuTree(menuTree.value);
    } catch (err) {
      // you can report use errorHandler or other
    } finally {
      setLoading(false);
    }
  };

  const getLabel = (prefix: string, suffix: string, permissionType: number) => {
    return (
      prefix +
      getLabelByValue(
        permissionTypeEnumOptions.value as EnumRecord[],
        permissionType
      ) +
      suffix
    );
  };

  const expandable = {
    icon: (expanded: boolean, record: TableData) => {
      if (expanded) {
        return h(IconUp);
      }
      return h(IconDown);
    },
  };

  const dealMenuTreeOption = (
    list: PermissionRecord[],
    container: PermissionRecord[]
  ) => {
    if (!list) {
      return;
    }
    list.forEach((item) => {
      // 权限不添加到上级目录选择树种
      if (item.permissionType === 2) {
        return;
      }
      const t = {
        permission: item.permissionId,
        title: item.permissionTitle,
        disabled: false,
        permissionType: item.permissionType,
        children: [],
      };
      container.push(t);
      dealMenuTreeOption(item.children as PermissionRecord[], t.children);
    });
  };

  const computedMenuTree = (list: PermissionRecord[]) => {
    if (!list) {
      return;
    }
    list.forEach((item) => {
      let disabled = false;
      if (editFormModel.permissionType === 0) {
        if (item.permissionType === 1) {
          disabled = true;
        }
      }
      if (editFormModel.permissionType === 1) {
        if (item.permissionType === 1) {
          disabled = true;
        }
      }
      if (editFormModel.permissionType === 2) {
        if (item.permissionType === 0) {
          disabled = true;
        }
      }
      item.disabled = disabled;
      computedMenuTree(item.children as PermissionRecord[]);
    });
  };

  const filterTreeNode = (inputText: string, node: PermissionRecord) => {
    return (
      (node.permissionTitle as string)
        .toLowerCase()
        .indexOf(inputText.toLowerCase()) > -1
    );
  };

  const appStore = useAppStore();
  const tableChange = async (a: PermissionRecord[]) => {
    renderData.value = a;
    assignOrder(renderData.value);
    await permissionUpdateOrder(renderData.value);
    await appStore.fetchServerMenuConfig();
  };

  const assignOrder = (permissions: PermissionRecord[]) => {
    let order = 1;
    permissions.forEach((permission) => {
      permission.order = order;
      order += 1;
      if (permission.children && permission.children.length > 0) {
        assignOrder(permission.children);
      }
    });
  };

  const search = () => {
    fetchData();
  };

  const statusOptions = ref<EnumRecord[] | undefined>([]);
  const permissionTypeEnumOptions = ref<EnumRecord[] | undefined>([]);
  onMounted(async () => {
    statusOptions.value = await useEnumStore().getEnums('StatusEnum');
    permissionTypeEnumOptions.value = await useEnumStore().getEnums(
      'PermissionTypeEnum'
    );
    await fetchData();
  });

  const typeChange = (value: number) => {
    editFormModel.permissionParentId = '';
    if (value === 2) {
      editFormModel.permissionIcon = '';
    }
    computedMenuTree(menuTree.value);
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
  let editFormModel = reactive<Partial<PermissionRecord>>({
    permissionIcon: '',
    permissionStatus: 1,
    permissionType: 0,
  });

  const rules = {};

  const addButton = (record: PermissionRecord) => {
    editFormModelTitle.value = '新增';
    editFormModel = reactive<Partial<PermissionRecord>>({
      permissionIcon: '',
      permissionStatus: 1,
      permissionType: 0,
    });
    if (record.permissionId) {
      editFormModel.permissionParentId = record.permissionId;
    }
    if (record.permissionType !== undefined) {
      editFormModel.permissionType = record.permissionType + 1;
    }
    editFormVisible.value = true;
  };

  const updateButton = async (recode: PermissionRecord) => {
    editFormModelTitle.value = '编辑';
    editFormModel = reactive<PermissionRecord>(cloneDeep(recode));
    editFormVisible.value = true;
  };

  const batchDisable = async (ids: string[]) => {
    const { data } = await permissionDisable(ids);
    if (data.code === 200) {
      Message.success({
        content: '禁用成功',
        duration: 5 * 1000,
      });
      search();
      await appStore.fetchServerMenuConfig();
    }
  };

  const batchEnable = async (ids: string[]) => {
    const { data } = await permissionEnable(ids);
    if (data.code === 200) {
      Message.success({
        content: '启用成功',
        duration: 5 * 1000,
      });
      search();
      await appStore.fetchServerMenuConfig();
    }
  };

  const batchDelete = async (ids: string[]) => {
    const { data } = await permissionDelete(ids);
    selectedKeys.value = [];
    if (data.code === 200) {
      Message.success({
        content: '删除成功',
        duration: 5 * 1000,
      });
      search();
      await appStore.fetchServerMenuConfig();
    }
  };
  const handleCancel = () => {
    editFormVisible.value = false;
  };
  const handleOk = async () => {
    const err = await editFormRef.value.validate();
    if (!err) {
      const saveAction =
        editFormModelTitle.value === '新增' ? permissionAdd : permissionUpdate;
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
    editFormModel = reactive<Partial<PermissionRecord>>({
      permissionIcon: '',
      permissionStatus: 1,
      permissionType: 0,
    });
    await appStore.fetchServerMenuConfig();
  };
</script>

<script lang="ts">
  export default {
    name: 'SysPermission',
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

  :deep .arco-table-expand-btn {
    background-color: transparent;
  }

  :deep .arco-table-expand-btn:hover {
    background-color: var(--color-neutral-3);
  }
</style>
