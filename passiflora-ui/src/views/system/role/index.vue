<template>
  <div class="container">
    <Breadcrumb :items="['menu.organization', 'menu.system.role']" />
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
                        v-model="searchForm['like[roleName]']"
                        placeholder="请输入角色名称"
                        @press-enter="search"
                      />
                    </a-form-item>
                  </a-col>
                  <a-col :span="6">
                    <a-form-item field="dictTag" :hide-label="true">
                      <a-input
                        v-model="searchForm['like[roleCode]']"
                        placeholder="请输入角色标签"
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
                  role="bl"
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
            row-key="roleId"
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
            <template #roleStatus="{ record }">
              <span v-if="record.roleStatus === 1" class="circle pass"></span>
              <span v-else class="circle err"></span>
              {{
                getLabelByValue(
                  roleStatusOptions as EnumRecord[],
                  record.roleStatus
                )
              }}
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
                  v-if="record.roleStatus === 1"
                  content="确认禁用吗？"
                  @ok="batchDisable([record.roleId])"
                >
                  <a-button
                    v-if="record.roleStatus === 1"
                    type="text"
                    size="small"
                    status="warning"
                    >禁用</a-button
                  >
                </a-popconfirm>
                <a-popconfirm
                  v-if="record.roleStatus === 0"
                  content="确认启用吗？"
                  @ok="batchEnable([record.roleId])"
                >
                  <a-button
                    v-if="record.roleStatus === 0"
                    type="text"
                    size="small"
                    status="success"
                    >启用</a-button
                  >
                </a-popconfirm>
                <a-popconfirm
                  content="确认删除吗？"
                  @ok="batchDelete([record.roleId])"
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
      <template #title>{{ editFormModelTitle }}角色</template>
      <div>
        <a-form
          ref="editFormRef"
          :rules="rules"
          :model="editFormModel"
          layout="vertical"
          scroll-to-first-error
        >
          <a-form-item field="roleName" label="角色名称" required>
            <a-input
              v-model="editFormModel.roleName"
              placeholder="请输入角色名称"
            />
          </a-form-item>
          <a-form-item field="roleCode" label="角色标识" required>
            <a-input
              v-model="editFormModel.roleCode"
              placeholder="请输入角色标识"
            />
          </a-form-item>
          <a-form-item field="roleStatus" label="状态" required>
            <a-radio-group
              v-model="editFormModel.roleStatus"
              :options="roleStatusOptions"
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
  import { computed, ref, reactive, watch, nextTick, onMounted } from 'vue';
  import useLoading from '@/hooks/loading';
  import {
    PermissionRecord,
    permissionTableTree,
  } from '@/api/system/permission';
  import {
    rolePermissionSaveDto,
    RoleRecord,
    roleDelete,
    roleAdd,
    roleUpdate,
    roleDisable,
    roleEnable,
    permissionIdsByRoleIds,
    saveRolePermission,
    rolePage,
    rolePageParams,
  } from '@/api/system/role';
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
      'like[roleName]': '',
      'like[roleCode]': '',
    };
  };

  const { loading, setLoading } = useLoading(true);
  const renderData = ref<RoleRecord[]>([]);
  const searchForm = ref(generateSearchFormModel());
  const cloneColumns = ref<Column[]>([]);
  const showColumns = ref<Column[]>([]);
  const selectedKeys = ref<string[]>([]);
  const permissionCheckedKeys = ref<string[]>([]);
  const permissionTreeModel = ref<PermissionRecord[]>([]);

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
      title: '角色名称',
      dataIndex: 'roleName',
      slotName: 'roleName',
    },
    {
      title: '角色标识',
      dataIndex: 'roleCode',
      slotName: 'roleCode',
    },
    {
      title: '状态',
      dataIndex: 'roleStatus',
      slotName: 'roleStatus',
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
    params: rolePageParams = { pageNum: 1, pageSize: 10 }
  ) => {
    setLoading(true);
    try {
      const { data } = await rolePage(params);
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
    } as unknown as rolePageParams);
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

  const roleStatusOptions = ref<EnumRecord[] | undefined>([]);
  onMounted(async () => {
    roleStatusOptions.value = await useEnumStore().getEnums('status-enum');
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
  let editFormModel = reactive<Partial<RoleRecord>>({
    roleStatus: 1,
  });

  const rules = {};

  const addButton = (record: RoleRecord) => {
    editFormModelTitle.value = '新增';
    editFormModel = reactive<Partial<RoleRecord>>({
      roleStatus: 1,
    });
    editFormVisible.value = true;
  };

  const updateButton = async (recode: RoleRecord) => {
    editFormModelTitle.value = '编辑';
    editFormModel = reactive<RoleRecord>(cloneDeep(recode));
    editFormVisible.value = true;
  };

  const permissionButton = async (recode: RoleRecord) => {
    const { data } = await permissionTableTree();
    permissionTreeModel.value = data.data;
    const req = await permissionIdsByRoleIds([recode.roleId as string]);
    permissionCheckedKeys.value = req.data.data;
    rolePermission.roleId = recode.roleId;
    permissionFormVisible.value = true;
  };

  const batchDisable = async (ids: string[]) => {
    const { data } = await roleDisable(ids);
    if (data.code === 200) {
      Message.success({
        content: '禁用成功',
        duration: 5 * 1000,
      });
      search();
    }
  };

  const batchEnable = async (ids: string[]) => {
    const { data } = await roleEnable(ids);
    if (data.code === 200) {
      Message.success({
        content: '启用成功',
        duration: 5 * 1000,
      });
      search();
    }
  };

  const reset = () => {
    searchForm.value = generateSearchFormModel();
    search();
  };

  const batchDelete = async (ids: string[]) => {
    const { data } = await roleDelete(ids);
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
        editFormModelTitle.value === '新增' ? roleAdd : roleUpdate;
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
    editFormModel = reactive<Partial<RoleRecord>>({
      roleStatus: 1,
    });
  };

  const rolePermission: rolePermissionSaveDto = {
    roleId: '',
    permissionIds: [],
  };
  // 权限抽屉
  const permissionFormVisible = ref(false);
  const permissionHandleCancel = () => {
    permissionFormVisible.value = false;
  };
  const permissionHandleOk = async () => {
    rolePermission.permissionIds = permissionCheckedKeys.value;
    const { data } = await saveRolePermission(rolePermission);
    if (data.code === 200) {
      Message.success({
        content: `保存成功`,
        duration: 5 * 1000,
      });
      permissionFormVisible.value = false;
    }
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
    name: 'SysRole',
  };
</script>

<style scoped lang="less">
  .container {
    padding: 0 20px 20px 20px;
    height: calc(100% - 40px);
    :deep(.content) {
      role: relative;
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
