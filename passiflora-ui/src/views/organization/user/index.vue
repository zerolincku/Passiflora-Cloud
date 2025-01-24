<template>
  <div class="container">
    <Breadcrumb :items="['menu.organization', 'menu.organization.user']" />
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
                    <a-form-item field="realName" :hide-label="true">
                      <a-input
                        v-model="searchForm['like[realName]']"
                        placeholder="请输入用户名"
                        @press-enter="search"
                      />
                    </a-form-item>
                  </a-col>
                  <a-col :span="6">
                    <a-form-item field="userName" :hide-label="true">
                      <a-input
                        v-model="searchForm['like[userName]']"
                        placeholder="请输入账号"
                        @press-enter="search"
                      />
                    </a-form-item>
                  </a-col>
                  <a-col :span="6">
                    <a-form-item field="phoneNum" :hide-label="true">
                      <a-input
                        v-model="searchForm['like[phoneNum]']"
                        placeholder="请输入手机号"
                        @press-enter="search"
                      />
                    </a-form-item>
                  </a-col>
                  <a-col :span="6">
                    <a-form-item field="gender" :hide-label="true">
                      <a-select
                        v-model="searchForm['eq[gender]']"
                        :options="genderOptions"
                        :allow-clear="true"
                        :default-active-first-option="false"
                        placeholder="请选择性别"
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
              <a-button>
                <template #icon>
                  <icon-download />
                </template>
                下载
              </a-button>
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
            row-key="userId"
            :loading="loading"
            :pagination="pagination"
            :columns="(cloneColumns as TableColumnData[])"
            :data="renderData"
            :bordered="false"
            :scrollbar="true"
            :size="size"
            :row-selection="rowSelection as TableRowSelection"
            :scroll="scrollPercent"
            @page-change="onPageChange"
            @page-size-change="onPageSizeChange"
          >
            <template #positionsNames="{ record }">
              <a-tag v-if="record.positionNames.length">{{
                record.positionNames[0]
              }}</a-tag>
              <a-popover v-if="record.positionNames.length > 1">
                <a-tag class="ml-1.5"
                  >+{{ record.positionNames.length - 1 }}</a-tag
                >
                <template #content>
                  <div
                    v-for="(item, index) in record.positionNames"
                    :key="item"
                  >
                    <a-tag :class="index === 0 ? '' : 'mt-1.5'">{{
                      item
                    }}</a-tag>
                  </div>
                </template>
              </a-popover>
            </template>
            <template #roleNames="{ record }">
              <a-tag v-if="record.roleNames.length">{{
                record.roleNames[0]
              }}</a-tag>
              <a-popover v-if="record.roleNames.length > 1">
                <a-tag class="ml-1.5">+{{ record.roleNames.length - 1 }}</a-tag>
                <template #content>
                  <div v-for="(item, index) in record.roleNames" :key="item">
                    <a-tag :class="index === 0 ? '' : 'mt-1.5'">{{
                      item
                    }}</a-tag>
                  </div>
                </template>
              </a-popover>
            </template>
            <template #gender="{ record }">
              {{
                getLabelByValue(
                  genderOptions as DictItemRecord[],
                  record.gender
                )
              }}
            </template>
            <template #operations="{ record }">
              <a-space>
                <a-button type="text" size="small">查看</a-button>
                <a-button type="text" size="small" @click="updateButton(record)"
                  >编辑</a-button
                >
                <div>
                  <a-popconfirm
                    content="确认删除吗？"
                    @ok="batchDelete([record.userId])"
                  >
                    <a-button type="text" size="small" status="danger"
                      ><template #icon> <icon-delete /> </template
                      ><template #default>删除</template></a-button
                    >
                  </a-popconfirm>
                </div>
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
      <template #title>{{ editFormModelTitle }}用户</template>
      <div>
        <a-form
          ref="editFormRef"
          :rules="rules"
          :model="editFormModel"
          layout="vertical"
          scroll-to-first-error
        >
          <a-form-item field="userName" label="账户" required>
            <a-input
              v-model="editFormModel.userName"
              placeholder="请输入账号"
            />
          </a-form-item>
          <a-form-item field="realName" label="姓名" required>
            <a-input
              v-model="editFormModel.realName"
              placeholder="请输入姓名"
            />
          </a-form-item>
          <a-form-item field="orgId" label="所属机构" required>
            <a-tree-select
              v-model="editFormModel.orgId"
              placeholder="请选择所属机构"
              size="large"
              :filter-tree-node="filterOrgTreeNode"
              :data="orgTreeModel"
              :field-names="orgTreeFieldNames"
              :allow-clear="true"
              :allow-search="true"
            />
          </a-form-item>
          <a-form-item field="positionIds" label="职位" required>
            <a-tree-select
              v-model="editFormModel.positionIds"
              placeholder="请选择职位"
              size="large"
              :filter-tree-node="filterPositionTreeNode"
              :data="positionTreeModel"
              :field-names="positionTreeFieldNames"
              :allow-clear="true"
              :allow-search="true"
              :multiple="true"
            />
          </a-form-item>
          <a-form-item field="positionIds" label="角色" required>
            <a-select
              v-model="editFormModel.roleIds"
              placeholder="请选择角色"
              size="large"
              :options="roleListModel"
              :field-names="roleFieldNames"
              :allow-clear="true"
              :allow-search="true"
              :multiple="true"
            />
          </a-form-item>
          <a-form-item field="phoneNum" label="电话号码" required>
            <a-input
              v-model="editFormModel.phoneNum"
              placeholder="请输入电话号码"
            />
          </a-form-item>
          <a-form-item field="gender" label="性别">
            <a-select
              v-model="editFormModel.gender"
              :options="genderOptions"
              placeholder="请选择性别"
            />
          </a-form-item>
          <a-form-item field="idCardNo" label="身份证号">
            <a-input
              v-model="editFormModel.idCardNo"
              placeholder="请输入身份证号"
            />
          </a-form-item>
          <a-form-item field="email" label="电子邮箱">
            <a-input
              v-model="editFormModel.email"
              placeholder="请输入电子邮箱"
            />
          </a-form-item>
          <a-form-item field="dateOfBirth" label="出生日期">
            <a-date-picker
              v-model="editFormModel.dateOfBirth"
              placeholder="请输入出生日期"
            />
          </a-form-item>
          <a-form-item field="remark" label="备注">
            <a-textarea
              v-model="editFormModel.remark"
              placeholder="请输入备注"
            />
          </a-form-item>
        </a-form>
      </div>
    </a-drawer>
  </div>
</template>

<script lang="ts" setup>
  import { computed, nextTick, onMounted, reactive, ref, watch } from 'vue';
  import useLoading from '@/hooks/loading';
  import {
    userAdd,
    userDelete,
    userPage,
    userPageParams,
    UserRecord,
    userUpdate,
  } from '@/api/organization/user';
  import { Pagination } from '@/types/global';
  import type {
    TableColumnData,
    TableRowSelection,
  } from '@arco-design/web-vue/es/table/interface';
  import cloneDeep from 'lodash/cloneDeep';
  import Sortable from 'sortablejs';
  import { useDictStore } from '@/store';
  import { DictItemRecord } from '@/api/system/dict';
  import { getLabelByValue } from '@/utils/dict';
  import { OrgRecord, orgTree } from '@/api/organization/org';
  import { rowSelection } from '@/utils';
  import { IconPlus } from '@arco-design/web-vue/es/icon';
  import { Message } from '@arco-design/web-vue';
  import { isEmpty } from 'lodash';
  import icon from '@/components/icon';
  import { PositionRecord, positionTree } from '@/api/organization/position';
  import { roleList, rolePageParams, RoleRecord } from '@/api/system/role';

  type SizeProps = 'mini' | 'small' | 'medium' | 'large';
  type Column = TableColumnData & { checked?: true };

  const scrollPercent = {
    x: '120%',
  };

  const generateSearchFormModel = () => {
    return {
      'like[realName]': '',
      'like[userName]': '',
      'like[phoneNum]': '',
      'eq[gender]': '',
      'orgId': '',
    };
  };
  const selectedKeys = ref<string[]>([]);
  const { loading, setLoading } = useLoading(true);
  const renderData = ref<UserRecord[]>([]);
  const searchForm = ref(generateSearchFormModel());
  const cloneColumns = ref<Column[]>([]);
  const showColumns = ref<Column[]>([]);

  const size = ref<SizeProps>('large');

  const treeExpandContent = ref<string>('展开');
  const treeExpandIcon = ref<string>('icon-expand');
  const treeExpandedKeys = ref<string[]>([]);
  const treeAllKeys = ref<string[]>([]);

  const searchKey = ref('');
  const treeFieldNames = {
    key: 'orgId',
    title: 'orgName',
  };

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

  const selectOrg = async (orgIds: string[]) => {
    const orgId = orgIds[0];
    searchForm.value.orgId = orgId;
    search();
  };

  const orgTreeModel = ref<OrgRecord[]>([]);
  const positionTreeModel = ref<PositionRecord[]>([]);
  const roleListModel = ref<RoleRecord[]>([]);
  const orgTreeFieldNames = {
    key: 'orgId',
    title: 'orgName',
  };
  const positionTreeFieldNames = {
    key: 'positionId',
    title: 'positionName',
  };
  const roleFieldNames = {
    label: 'roleName',
    value: 'roleId',
  };
  const filterOrgTreeNode = (inputText: string, node: OrgRecord) => {
    return (
      (node.orgName as string).toLowerCase().indexOf(inputText.toLowerCase()) >
      -1
    );
  };

  const filterPositionTreeNode = (inputText: string, node: PositionRecord) => {
    return (
      (node.positionName as string)
        .toLowerCase()
        .indexOf(inputText.toLowerCase()) > -1
    );
  };

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
  const densityList = computed(() => [
    {
      name: '迷你',
      value: 'mini',
    },
    {
      name: '偏小',
      value: 'small',
    },
    {
      name: '中等',
      value: 'medium',
    },
    {
      name: '偏大',
      value: 'large',
    },
  ]);
  const columns = computed<TableColumnData[]>(() => [
    {
      title: '用户名',
      dataIndex: 'realName',
      width: 80,
    },
    {
      title: '账号',
      dataIndex: 'userName',
      width: 100,
    },
    {
      title: '性别',
      dataIndex: 'gender',
      slotName: 'gender',
      width: 60,
    },
    {
      title: '身份证号',
      dataIndex: 'idCardNo',
      width: 190,
    },
    {
      title: '所属机构',
      dataIndex: 'orgName',
      width: 130,
    },
    {
      title: '职位',
      dataIndex: 'positionNames',
      slotName: 'positionsNames',
      width: 150,
    },
    {
      title: '角色',
      dataIndex: 'roleNames',
      slotName: 'roleNames',
      width: 150,
    },
    {
      title: '出生日期',
      dataIndex: 'dateOfBirth',
      width: 120,
    },
    {
      title: '电话号码',
      dataIndex: 'phoneNum',
      width: 130,
    },
    {
      title: '电子邮件',
      dataIndex: 'email',
      ellipsis: true,
      tooltip: true,
      width: 130,
    },
    {
      title: '备注',
      dataIndex: 'remark',
      ellipsis: true,
      tooltip: true,
      width: 130,
    },
    {
      title: '操作',
      dataIndex: 'operations',
      slotName: 'operations',
      width: 240,
      headerCellStyle: { paddingLeft: '16px' },
    },
  ]);

  const genderOptions = ref<DictItemRecord[] | undefined>([]);
  onMounted(async () => {
    genderOptions.value = await useDictStore().getDictItems('gender', true);
    await refreshOrgTree();
    await refreshPositionTree();
    await refreshRoleList();
  });

  const refreshOrgTree = async () => {
    const { data } = await orgTree('');
    orgTreeModel.value = data.data;
    calculateOrg(orgTreeModel.value);
  };

  const refreshPositionTree = async () => {
    const { data } = await positionTree('');
    positionTreeModel.value = data.data;
  };

  const refreshRoleList = async () => {
    const param: rolePageParams = {};
    const { data } = await roleList(param);
    roleListModel.value = data.data;
  };

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

  const calculateOrg = (orgs: OrgRecord[] | undefined) => {
    if (!orgs) {
      return;
    }
    orgs.forEach((org) => {
      treeAllKeys.value.push(org.orgId as string);
      calculateOrg(org.children);
    });
  };

  const fetchData = async (
    params: userPageParams = { pageNum: 1, pageSize: 10 }
  ) => {
    setLoading(true);
    try {
      const { data } = await userPage(params);
      renderData.value = data.data;
      pagination.pageNum = params.pageNum as number;
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
    } as unknown as userPageParams);
  };
  const onPageChange = (pageNum: number) => {
    basePagination.pageNum = pageNum;
    search();
  };

  const batchDelete = async (ids: string[]) => {
    const { data } = await userDelete(ids);
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

  // 新增用户抽屉
  const editFormModelTitle = ref<string>('');
  const editFormRef = ref();
  const editFormVisible = ref(false);
  let editFormModel = reactive<Partial<UserRecord>>({});

  const rules = {
    userName: [
      {
        required: true,
        message: '请输入账号',
      },
    ],
    realName: [
      {
        required: true,
        message: '请输入姓名',
      },
    ],
  };

  const addButton = () => {
    editFormModelTitle.value = '新增';
    if (!isEmpty(editFormModel.orgId)) {
      editFormModel = reactive<Partial<UserRecord>>({});
    }
    editFormVisible.value = true;
  };

  const updateButton = async (recode: UserRecord) => {
    editFormModelTitle.value = '编辑';
    editFormModel = reactive<UserRecord>(cloneDeep(recode));
    editFormVisible.value = true;
  };

  const handleCancel = () => {
    editFormVisible.value = false;
  };
  const handleOk = async () => {
    const err = await editFormRef.value.validate();
    if (!err) {
      const saveAction =
        editFormModelTitle.value === '新增' ? userAdd : userUpdate;
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
