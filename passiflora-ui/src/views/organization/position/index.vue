<template>
  <div class="container">
    <Breadcrumb :items="['menu.organization', 'menu.organization.position']" />
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
            row-key="positionId"
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
            <template #dataScopeType="{ record }">
              {{
                getLabelByValue(
                  dataScopeTypeOptions as EnumRecord[],
                  record.dataScopeType
                )
              }}
            </template>
            <template #positionStatus="{ record }">
              <span
                v-if="record.positionStatus === 1"
                class="circle pass"
              ></span>
              <span v-else class="circle err"></span>
              {{
                getLabelByValue(
                  positionStatusOptions as EnumRecord[],
                  record.positionStatus
                )
              }}
            </template>
            <template #positionName="{ record }">
              {{ record.positionName }}
              <a-button
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
                <a-button
                  type="text"
                  size="small"
                  @click="permissionButton(record)"
                  >权限</a-button
                >
                <a-popconfirm
                  v-if="record.positionStatus === 1"
                  content="禁用会自动禁用下级职位，确认禁用吗？"
                  @ok="batchDisable([record.positionId])"
                >
                  <a-button
                    v-if="record.positionStatus === 1"
                    type="text"
                    size="small"
                    status="warning"
                    >禁用</a-button
                  >
                </a-popconfirm>
                <a-popconfirm
                  v-if="record.positionStatus === 0"
                  content="启用会自动启用上级职位，确认启用吗？"
                  @ok="batchEnable([record.positionId])"
                >
                  <a-button
                    v-if="record.positionStatus === 0"
                    type="text"
                    size="small"
                    status="success"
                    >启用</a-button
                  >
                </a-popconfirm>
                <a-popconfirm
                  content="确认删除吗？"
                  @ok="batchDelete([record.positionId])"
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
      <template #title>{{ editFormModelTitle }}职位</template>
      <div>
        <a-form
          ref="editFormRef"
          :rules="rules"
          :model="editFormModel"
          layout="vertical"
          scroll-to-first-error
        >
          <a-form-item field="positionName" label="职位名称" required>
            <a-input
              v-model="editFormModel.positionName"
              placeholder="请输入职位名称"
            />
          </a-form-item>
          <a-form-item field="dataScopeType" label="数据权限范围" required>
            <a-radio-group
              v-model="editFormModel.dataScopeType"
              direction="vertical"
              :options="dataScopeTypeOptions"
            />
          </a-form-item>
          <a-form-item field="positionStatus" label="状态" required>
            <a-radio-group
              v-model="editFormModel.positionStatus"
              :options="positionStatusOptions"
            />
          </a-form-item>
          <a-form-item field="parentPositionId" label="上级职位">
            <a-tree-select
              v-model="editFormModel.parentPositionId"
              placeholder="请选择上级职位"
              size="large"
              :data="positionTreeModel"
              :field-names="treeFieldName"
              :filter-tree-node="filterTreeNode"
              :allow-clear="true"
              :allow-search="true"
              :fallback-option="
                () => {
                  return { positionName: '无', positionId: '0' };
                }
              "
            />
          </a-form-item>
        </a-form>
      </div>
    </a-drawer>
    <a-drawer
      :width="490"
      :visible="permissionFormVisible"
      unmount-on-close
      @ok="permissionHandleOk"
      @cancel="permissionHandleCancel"
    >
      <template #title>权限</template>
      <div>
        <a-tree
          v-model:checked-keys="permissionCheckedKeys"
          :field-names="{
            key: 'permissionId',
            title: 'permissionTitle',
          }"
          :check-strictly="true"
          :checkable="true"
          :data="permissionTreeModel"
          @expandAll="
            () => {
              return true;
            }
          "
        />
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
  } from '@/api/system/permission';
  import {
    positionPermissionSaveDto,
    PositionRecord,
    positionTree,
    positionDelete,
    positionAdd,
    positionUpdate,
    positionUpdateOrder,
    positionDisable,
    positionEnable,
    permissionIdsByPositionIds,
    savePositionPermission,
  } from '@/api/organization/position';
  import {
    TableColumnData,
    TableData,
  } from '@arco-design/web-vue/es/table/interface';
  import cloneDeep from 'lodash/cloneDeep';
  import Sortable from 'sortablejs';
  import { IconPlus, IconDown, IconUp } from '@arco-design/web-vue/es/icon';
  import { densityList, rowSelection } from '@/utils';
  import { Message } from '@arco-design/web-vue';
  import { useAppStore, useEnumStore } from '@/store';
  import { EnumRecord } from '@/api/system/enum';
  import { getLabelByValue } from '@/utils/enums';

  type SizeProps = 'mini' | 'small' | 'medium' | 'large';
  type Column = TableColumnData & { checked?: true };

  const { loading, setLoading } = useLoading(true);
  const renderData = ref<PositionRecord[]>([]);
  const positionTreeModel = ref<PositionRecord[]>([]);
  const cloneColumns = ref<Column[]>([]);
  const showColumns = ref<Column[]>([]);
  const selectedKeys = ref<string[]>([]);
  const permissionCheckedKeys = ref<string[]>([]);
  const permissionTreeModel = ref<PermissionRecord[]>([]);

  const size = ref<SizeProps>('large');

  const columns = computed<TableColumnData[]>(() => [
    {
      title: '职位名称',
      dataIndex: 'positionName',
      slotName: 'positionName',
    },
    {
      title: '数据权限范围',
      dataIndex: 'dataScopeType',
      slotName: 'dataScopeType',
    },
    {
      title: '状态',
      dataIndex: 'positionStatus',
      slotName: 'positionStatus',
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
    key: 'positionId',
    title: 'positionName',
  };
  const fetchData = async () => {
    setLoading(true);
    try {
      const { data } = await positionTree('');
      renderData.value = data.data;
      positionTreeModel.value = [];
      dealPositionTreeOption(data.data, positionTreeModel.value);
    } catch (err) {
      // you can report use errorHandler or other
    } finally {
      setLoading(false);
    }
  };

  const expandable = {
    icon: (expanded: boolean, record: TableData) => {
      if (expanded) {
        return h(IconUp);
      }
      return h(IconDown);
    },
  };

  const dealPositionTreeOption = (
    list: PositionRecord[],
    container: PositionRecord[]
  ) => {
    if (!list) {
      return;
    }
    list.forEach((item) => {
      const t = {
        positionId: item.positionId,
        positionName: item.positionName,
        disabled: false,
        children: [],
      };
      container.push(t);
      dealPositionTreeOption(item.children as PositionRecord[], t.children);
    });
  };

  const filterTreeNode = (inputText: string, node: PositionRecord) => {
    return (
      (node.positionName as string)
        .toLowerCase()
        .indexOf(inputText.toLowerCase()) > -1
    );
  };

  const tableChange = async (a: PositionRecord[]) => {
    renderData.value = a;
    assignOrder(renderData.value);
    await positionUpdateOrder(renderData.value);
  };

  const assignOrder = (positions: PositionRecord[]) => {
    let order = 1;
    positions.forEach((position) => {
      position.order = order;
      order += 1;
      if (position.children && position.children.length > 0) {
        assignOrder(position.children);
      }
    });
  };

  const search = () => {
    fetchData();
  };

  const positionStatusOptions = ref<EnumRecord[] | undefined>([]);
  const dataScopeTypeOptions = ref<EnumRecord[] | undefined>([]);
  onMounted(async () => {
    positionStatusOptions.value = await useEnumStore().getEnums('StatusEnum');
    dataScopeTypeOptions.value = await useEnumStore().getEnums(
      'PositionDataScopeTypeEnum'
    );
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
  let editFormModel = reactive<Partial<PositionRecord>>({
    positionStatus: 1,
    dataScopeType: 0,
  });

  const rules = {};

  const addButton = (record: PositionRecord) => {
    editFormModelTitle.value = '新增';
    editFormModel = reactive<Partial<PositionRecord>>({
      positionStatus: 1,
      dataScopeType: 0,
    });
    if (record.positionId) {
      editFormModel.parentPositionId = record.positionId;
    }
    editFormVisible.value = true;
  };

  const updateButton = async (recode: PositionRecord) => {
    editFormModelTitle.value = '编辑';
    editFormModel = reactive<PositionRecord>(cloneDeep(recode));
    editFormVisible.value = true;
  };

  const permissionButton = async (recode: PositionRecord) => {
    const { data } = await permissionTableTree();
    permissionTreeModel.value = data.data;
    const req = await permissionIdsByPositionIds([recode.positionId as string]);
    permissionCheckedKeys.value = req.data.data;
    positionPermission.positionId = recode.positionId;
    permissionFormVisible.value = true;
  };

  const batchDisable = async (ids: string[]) => {
    const { data } = await positionDisable(ids);
    if (data.code === 200) {
      Message.success({
        content: '禁用成功',
        duration: 5 * 1000,
      });
      search();
    }
  };

  const batchEnable = async (ids: string[]) => {
    const { data } = await positionEnable(ids);
    if (data.code === 200) {
      Message.success({
        content: '启用成功',
        duration: 5 * 1000,
      });
      search();
    }
  };

  const batchDelete = async (ids: string[]) => {
    const { data } = await positionDelete(ids);
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
        editFormModelTitle.value === '新增' ? positionAdd : positionUpdate;
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
    editFormModel = reactive<Partial<PositionRecord>>({
      positionStatus: 1,
    });
  };

  const positionPermission: positionPermissionSaveDto = {
    positionId: '',
    permissionIds: [],
  };
  // 权限抽屉
  const permissionFormVisible = ref(false);
  const permissionHandleCancel = () => {
    permissionFormVisible.value = false;
  };
  const permissionHandleOk = async () => {
    positionPermission.permissionIds = permissionCheckedKeys.value;
    const { data } = await savePositionPermission(positionPermission);
    if (data.code === 200) {
      Message.success({
        content: `保存成功`,
        duration: 5 * 1000,
      });
      permissionFormVisible.value = false;
    }
  };
</script>

<script lang="ts">
  export default {
    name: 'SysPosition',
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
