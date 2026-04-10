<template>
  <div class="page-container">
    <div class="page-header">
      <h2>桌台管理</h2>
      <div style="display:flex; gap:12px; align-items:center;">
        <!-- 状态图例 -->
        <div style="display:flex; gap:8px;">
          <span v-for="s in statusList" :key="s.key" style="display:flex; align-items:center; gap:4px; font-size:13px;">
            <span style="width:10px; height:10px; border-radius:50%; display:inline-block;" :style="{ background: s.dot }"></span>
            {{ s.label }}
          </span>
        </div>
        <el-button type="primary" @click="openDialog()">
          <el-icon><Plus /></el-icon> 添加桌台
        </el-button>
      </div>
    </div>

    <!-- 筛选 -->
    <el-card class="mb-16">
      <el-radio-group v-model="filterStatus" @change="fetchData">
        <el-radio-button :value="null">全部</el-radio-button>
        <el-radio-button v-for="s in statusList" :key="s.key" :value="s.key">
          {{ s.label }}
        </el-radio-button>
      </el-radio-group>
    </el-card>

    <!-- 桌台网格 -->
    <el-card v-loading="loading">
      <div class="table-grid">
        <div
          v-for="t in tables"
          :key="t.id"
          class="table-card"
          :class="t.status.toLowerCase()"
          @click="openStatusDialog(t)"
        >
          <div class="table-num">{{ t.tableNumber }}</div>
          <div class="table-cap">{{ t.capacity }}人桌</div>
          <div class="table-loc">{{ t.location || '' }}</div>
          <el-tag :type="statusTagMap[t.status]?.type" size="small" style="margin-top:6px;">
            {{ statusTagMap[t.status]?.label }}
          </el-tag>
          <div style="margin-top:8px; display:flex; gap:4px; justify-content:center;" @click.stop>
            <el-button link size="small" @click="openDialog(t)">编辑</el-button>
            <el-popconfirm title="确定删除此桌台？" @confirm="handleDelete(t.id)">
              <template #reference>
                <el-button link type="danger" size="small">删除</el-button>
              </template>
            </el-popconfirm>
          </div>
        </div>
      </div>
      <el-empty v-if="!loading && tables.length === 0" description="暂无桌台数据" />
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="editRow ? '编辑桌台' : '添加桌台'" width="460px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="桌号" prop="tableNumber">
          <el-input v-model="form.tableNumber" placeholder="如：A01" />
        </el-form-item>
        <el-form-item label="容纳人数" prop="capacity">
          <el-input-number v-model="form.capacity" :min="1" :max="50" />
        </el-form-item>
        <el-form-item label="区域位置">
          <el-input v-model="form.location" placeholder="如：一楼大厅、VIP包间" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.notes" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>

    <!-- 更新状态弹窗 -->
    <el-dialog v-model="statusDialogVisible" :title="`更新桌台状态 - ${statusTarget?.tableNumber}`" width="360px">
      <el-radio-group v-model="newStatus" style="display:flex; flex-direction:column; gap:12px;">
        <el-radio v-for="s in statusList" :key="s.key" :value="s.key" border>
          {{ s.label }}
        </el-radio>
      </el-radio-group>
      <template #footer>
        <el-button @click="statusDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleStatusUpdate">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getTables, createTable, updateTable, updateTableStatus, deleteTable } from '@/api/table'
import { ElMessage } from 'element-plus'

const tables = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const statusDialogVisible = ref(false)
const submitting = ref(false)
const editRow = ref(null)
const statusTarget = ref(null)
const newStatus = ref('')
const filterStatus = ref(null)
const formRef = ref()

const statusList = [
  { key: 'AVAILABLE', label: '空闲', dot: '#86efac', type: 'success' },
  { key: 'OCCUPIED',  label: '使用中', dot: '#fca5a5', type: 'danger' },
  { key: 'RESERVED',  label: '已预订', dot: '#fcd34d', type: 'warning' },
  { key: 'CLEANING',  label: '清洁中', dot: '#93c5fd', type: 'primary' }
]
const statusTagMap = Object.fromEntries(statusList.map(s => [s.key, s]))

const form = reactive({ tableNumber: '', capacity: 4, location: '', notes: '' })
const rules = {
  tableNumber: [{ required: true, message: '请输入桌号', trigger: 'blur' }],
  capacity: [{ required: true, message: '请输入容纳人数', trigger: 'blur' }]
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getTables(filterStatus.value ? { status: filterStatus.value } : {})
    tables.value = res.data || []
  } finally { loading.value = false }
}

const openDialog = (row = null) => {
  editRow.value = row
  if (row) {
    Object.assign(form, { tableNumber: row.tableNumber, capacity: row.capacity, location: row.location || '', notes: row.notes || '' })
  } else {
    Object.assign(form, { tableNumber: '', capacity: 4, location: '', notes: '' })
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitting.value = true
  try {
    if (editRow.value) {
      await updateTable(editRow.value.id, form)
      ElMessage.success('桌台已更新')
    } else {
      await createTable(form)
      ElMessage.success('桌台已添加')
    }
    dialogVisible.value = false
    fetchData()
  } finally { submitting.value = false }
}

const openStatusDialog = (t) => {
  statusTarget.value = t
  newStatus.value = t.status
  statusDialogVisible.value = true
}

const handleStatusUpdate = async () => {
  await updateTableStatus(statusTarget.value.id, newStatus.value)
  ElMessage.success('桌台状态已更新')
  statusDialogVisible.value = false
  fetchData()
}

const handleDelete = async (id) => {
  await deleteTable(id)
  ElMessage.success('已删除')
  fetchData()
}

onMounted(fetchData)
</script>
