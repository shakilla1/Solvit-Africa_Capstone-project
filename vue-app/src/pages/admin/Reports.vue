<template>
  <div class="space-y-6">
    <div class="flex justify-between items-center">
      <div>
        <h2 class="text-2xl font-serif text-[#0f172a]" :style="{ fontFamily: 'var(--font-serif)' }">Reports</h2>
        <p class="text-[#475569]">Generated campaign reports</p>
      </div>
      <Button @click="showModal = true"><template #icon><Plus :size="18" /></template>Generate New Report</Button>
    </div>
    <Card class="p-0 overflow-hidden">
      <table class="w-full">
        <thead class="bg-[#f8fafc] border-b border-[#e2e8f0]">
          <tr>
            <th class="text-left px-6 py-3 text-xs font-medium text-[#94a3b8] uppercase">Report Name</th>
            <th class="text-left px-6 py-3 text-xs font-medium text-[#94a3b8] uppercase">Campaign</th>
            <th class="text-left px-6 py-3 text-xs font-medium text-[#94a3b8] uppercase">Generated</th>
            <th class="text-left px-6 py-3 text-xs font-medium text-[#94a3b8] uppercase">Type</th>
            <th class="text-left px-6 py-3 text-xs font-medium text-[#94a3b8] uppercase">Actions</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-[#e2e8f0]">
          <tr v-for="report in reports" :key="report.id" class="hover:bg-[#f8fafc]">
            <td class="px-6 py-4 text-[#0f172a] font-medium">{{ report.name }}</td>
            <td class="px-6 py-4 text-[#475569]">{{ report.campaign }}</td>
            <td class="px-6 py-4 text-[#475569]">{{ report.date }}</td>
            <td class="px-6 py-4"><span class="px-2 py-1 bg-[#dbeafe] text-[#2563eb] rounded text-xs font-medium">{{ report.type }}</span></td>
            <td class="px-6 py-4">
              <Button variant="secondary" size="sm" @click="downloadReport(report)"><template #icon><Download :size="16" /></template>Download</Button>
            </td>
          </tr>
        </tbody>
      </table>
    </Card>

    <!-- Generate Report Modal -->
    <div v-if="showModal" class="fixed inset-0 bg-black/40 flex items-center justify-center z-50" @click.self="showModal = false">
      <div class="bg-white rounded-xl p-6 w-full max-w-md shadow-xl">
        <div class="flex justify-between items-center mb-5">
          <h3 class="text-lg font-semibold text-[#0f172a]">Generate New Report</h3>
          <button @click="showModal = false" class="p-1.5 hover:bg-[#f8fafc] rounded-lg"><X :size="16" class="text-[#475569]" /></button>
        </div>
        <div class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-[#0f172a] mb-1">Campaign</label>
            <select v-model="modalForm.campaign" class="w-full px-3 py-2.5 border border-[#e2e8f0] rounded-lg text-sm focus:border-[#2563eb] focus:outline-none">
              <option value="">Select a campaign...</option>
              <option>Q2 Security Awareness</option>
              <option>IT Department Test</option>
              <option>New Hire Training</option>
              <option>Executive Phishing Test</option>
            </select>
          </div>
          <div>
            <label class="block text-sm font-medium text-[#0f172a] mb-2">Report Type</label>
            <div class="grid grid-cols-2 gap-2">
              <button v-for="t in reportTypes" :key="t" @click="modalForm.type = t"
                :class="['px-3 py-2 border-2 rounded-lg text-sm text-left transition-all', modalForm.type === t ? 'border-[#2563eb] bg-[#dbeafe]/20 text-[#2563eb]' : 'border-[#e2e8f0] text-[#475569] hover:border-[#cbd5e1]']">
                {{ t }}
              </button>
            </div>
          </div>
          <div>
            <label class="block text-sm font-medium text-[#0f172a] mb-2">Format</label>
            <div class="flex gap-2">
              <button v-for="f in ['PDF', 'CSV', 'Both']" :key="f" @click="modalForm.format = f"
                :class="['px-4 py-2 border-2 rounded-lg text-sm transition-all', modalForm.format === f ? 'border-[#2563eb] bg-[#dbeafe]/20 text-[#2563eb]' : 'border-[#e2e8f0] text-[#475569] hover:border-[#cbd5e1]']">
                {{ f }}
              </button>
            </div>
          </div>
        </div>
        <div class="flex gap-3 mt-6">
          <Button variant="secondary" class="flex-1" @click="showModal = false">Cancel</Button>
          <Button class="flex-1" @click="generateReport" :disabled="generating || !modalForm.campaign">
            <span v-if="generating" class="flex items-center justify-center gap-2">
              <svg class="animate-spin w-4 h-4" fill="none" viewBox="0 0 24 24"><circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"/><path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"/></svg>
              Generating...
            </span>
            <span v-else>Generate Report</span>
          </Button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Plus, Download, X } from '@lucide/vue'
import { toast } from 'vue-sonner'
import Card from '../../components/Card.vue'
import Button from '../../components/Button.vue'

const showModal = ref(false)
const generating = ref(false)
const modalForm = ref({ campaign: '', type: 'Full Report', format: 'PDF' })
const reportTypes = ['Full Report', 'Click Summary', 'Education Summary', 'Risk Overview']

const reports = ref([
  { id: 1, name: 'Q2 Security Awareness Report', campaign: 'Q2 Awareness', date: 'May 20, 2025', type: 'PDF' },
  { id: 2, name: 'IT Department Test Summary', campaign: 'IT Test', date: 'Apr 15, 2025', type: 'CSV' },
  { id: 3, name: 'New Hire Training Results', campaign: 'New Hire', date: 'Mar 10, 2025', type: 'PDF' },
])

function downloadReport(report: any) {
  toast.success(`Downloading "${report.name}"...`)
}

async function generateReport() {
  generating.value = true
  await new Promise(r => setTimeout(r, 2000))
  const newReport = {
    id: Date.now(),
    name: `${modalForm.value.campaign} — ${modalForm.value.type}`,
    campaign: modalForm.value.campaign,
    date: new Date().toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' }),
    type: modalForm.value.format === 'Both' ? 'PDF + CSV' : modalForm.value.format,
  }
  reports.value.unshift(newReport)
  generating.value = false
  showModal.value = false
  toast.success('Report generated successfully!')
}
</script>