<template>
  <div class="max-w-3xl mx-auto space-y-5">
    <!-- Stepper -->
    <div class="flex items-center">
      <template v-for="(s, idx) in steps" :key="s.num">
        <div class="flex items-center">
          <div :class="['w-9 h-9 rounded-full flex items-center justify-center text-sm font-medium transition-colors',
            step > s.num ? 'bg-[#2563eb] text-white' : step === s.num ? 'bg-[#2563eb] text-white' : 'bg-[#e2e8f0] text-[#94a3b8]']">
            <Check v-if="step > s.num" :size="16" />
            <span v-else>{{ s.num }}</span>
          </div>
          <div class="ml-2">
            <div :class="['text-xs font-medium', step >= s.num ? 'text-[#0f172a]' : 'text-[#94a3b8]']">{{ s.label }}</div>
          </div>
        </div>
        <div v-if="idx < steps.length - 1" :class="['flex-1 h-0.5 mx-3', step > s.num ? 'bg-[#2563eb]' : 'bg-[#e2e8f0]']"></div>
      </template>
    </div>

    <!-- Step 1: Campaign Details -->
    <Card v-if="step === 1">
      <h2 class="text-lg font-semibold text-[#0f172a] mb-4">Campaign Details</h2>
      <div class="space-y-4">
        <Input label="Campaign Name *" placeholder="e.g., Q3 Security Awareness" v-model="form.name" :error="errors.name" />
        <div>
          <label class="block text-sm font-medium text-[#0f172a] mb-2">Difficulty Level *</label>
          <div class="grid grid-cols-3 gap-3">
            <button v-for="diff in difficulties" :key="diff.value" @click="form.difficulty = diff.value; errors.difficulty = ''"
              :class="['p-3 border-2 rounded-lg text-left transition-all', form.difficulty === diff.value ? diff.activeCls : 'border-[#e2e8f0] hover:border-[#cbd5e1]']">
              <div class="font-medium text-sm text-[#0f172a]">{{ diff.label }}</div>
              <div class="text-xs text-[#475569] mt-0.5">{{ diff.desc }}</div>
            </button>
          </div>
          <p v-if="errors.difficulty" class="text-xs text-[#dc2626] mt-1">{{ errors.difficulty }}</p>
        </div>
        <div>
          <label class="block text-sm font-medium text-[#0f172a] mb-1">Description <span class="text-[#94a3b8] font-normal">(optional)</span></label>
          <textarea class="w-full px-3 py-2.5 border border-[#e2e8f0] rounded-lg resize-none text-sm focus:border-[#2563eb] focus:outline-none focus:ring-2 focus:ring-[#2563eb]/20" rows="3" v-model="form.description" placeholder="Add a description..."></textarea>
        </div>
        <div class="flex justify-end">
          <Button @click="validateStep1">Next: Choose Template →</Button>
        </div>
      </div>
    </Card>

    <!-- Step 2: Choose Template -->
    <Card v-if="step === 2">
      <h2 class="text-lg font-semibold text-[#0f172a] mb-4">Choose Email Template</h2>
      <p v-if="errors.template" class="text-xs text-[#dc2626] mb-3">{{ errors.template }}</p>
      <div class="grid grid-cols-3 gap-3 mb-4">
        <button v-for="template in templates" :key="template.id" @click="form.selectedTemplate = template.id; errors.template = ''"
          :class="['p-3 border-2 rounded-lg text-left transition-all', form.selectedTemplate === template.id ? 'border-[#2563eb] bg-[#dbeafe]/20' : 'border-[#e2e8f0] hover:border-[#cbd5e1]']">
          <div class="aspect-video bg-[#f8fafc] rounded mb-2 flex items-center justify-center">
            <Mail :size="20" class="text-[#94a3b8]" />
          </div>
          <div class="font-medium text-xs text-[#0f172a] mb-1">{{ template.name }}</div>
          <Badge :variant="template.difficulty === 'easy' ? 'green' : template.difficulty === 'medium' ? 'amber' : 'red'" class="text-xs">{{ template.difficulty }}</Badge>
          <div v-if="form.selectedTemplate === template.id" class="mt-1.5 flex items-center gap-1 text-[#2563eb] text-xs font-medium">
            <Check :size="12" /> Selected
          </div>
        </button>
      </div>
      <div class="flex justify-between">
        <Button variant="secondary" @click="step = 1">← Back</Button>
        <Button @click="validateStep2">Next: Add Targets →</Button>
      </div>
    </Card>

    <!-- Step 3: Add Targets -->
    <Card v-if="step === 3">
      <h2 class="text-lg font-semibold text-[#0f172a] mb-1">Add Targets</h2>
      <p class="text-sm text-[#475569] mb-4">Upload a CSV file or add individual email addresses</p>

      <!-- Tab toggle -->
      <div class="flex border border-[#e2e8f0] rounded-lg p-1 mb-4 w-fit">
        <button @click="targetMode = 'csv'"
          :class="['px-4 py-1.5 rounded-md text-sm font-medium transition-colors', targetMode === 'csv' ? 'bg-[#2563eb] text-white' : 'text-[#475569] hover:text-[#0f172a]']">
          Upload CSV
        </button>
        <button @click="targetMode = 'single'"
          :class="['px-4 py-1.5 rounded-md text-sm font-medium transition-colors', targetMode === 'single' ? 'bg-[#2563eb] text-white' : 'text-[#475569] hover:text-[#0f172a]']">
          Add Single Email
        </button>
      </div>

      <!-- CSV Upload -->
      <div v-if="targetMode === 'csv'">
        <div
          @dragover.prevent="dragOver = true"
          @dragleave="dragOver = false"
          @drop.prevent="handleDrop"
          :class="['border-2 border-dashed rounded-lg p-8 text-center mb-3 transition-colors cursor-pointer', dragOver ? 'border-[#2563eb] bg-[#dbeafe]/10' : 'border-[#e2e8f0] hover:border-[#cbd5e1]']"
          @click="fileInput?.click()"
        >
          <UploadCloud :size="36" class="mx-auto text-[#94a3b8] mb-2" />
          <p class="text-sm font-medium text-[#0f172a] mb-1">Drag & drop your CSV here, or click to browse</p>
          <p class="text-xs text-[#475569]">Format: name, email, department (one per row)</p>
          <input ref="fileInput" type="file" accept=".csv" class="hidden" @change="handleFileSelect" />
        </div>
        <div class="flex items-center justify-between mb-3">
          <button @click="downloadSampleCSV" class="text-xs text-[#2563eb] hover:underline flex items-center gap-1">
            <Download :size="12" /> Download sample CSV
          </button>
          <span v-if="csvFileName" class="text-xs text-[#16a34a] flex items-center gap-1">
            <CheckCircle :size="12" /> {{ csvFileName }}
          </span>
        </div>
      </div>

      <!-- Single Email -->
      <div v-if="targetMode === 'single'" class="mb-4">
        <div class="flex gap-2 mb-3">
          <div class="flex-1 space-y-2">
            <Input label="Full Name" placeholder="e.g., John Doe" v-model="singleTarget.name" />
          </div>
          <div class="flex-1 space-y-2">
            <Input label="Email Address *" type="email" placeholder="john@company.com" v-model="singleTarget.email" :error="singleError" />
          </div>
          <div class="flex-1 space-y-2">
            <Input label="Department" placeholder="e.g., Sales" v-model="singleTarget.dept" />
          </div>
        </div>
        <Button variant="secondary" size="sm" @click="addSingleTarget">
          <template #icon><Plus :size="15" /></template>Add to List
        </Button>
      </div>

      <!-- Target List Preview -->
      <div v-if="targets.length > 0" class="border border-[#e2e8f0] rounded-lg overflow-hidden mb-4">
        <div class="bg-[#f8fafc] px-4 py-2 border-b border-[#e2e8f0] flex justify-between items-center">
          <span class="text-sm font-medium text-[#0f172a]">{{ targets.length }} target{{ targets.length !== 1 ? 's' : '' }} added</span>
          <button @click="targets = []" class="text-xs text-[#dc2626] hover:underline">Clear all</button>
        </div>
        <div class="max-h-40 overflow-y-auto">
          <div v-for="(t, idx) in targets" :key="idx" class="flex items-center justify-between px-4 py-2 border-b border-[#f1f5f9] last:border-0 hover:bg-[#f8fafc]">
            <div class="flex items-center gap-3">
              <div class="w-6 h-6 rounded-full bg-[#dbeafe] flex items-center justify-center text-xs text-[#2563eb] font-medium">
                {{ t.name ? t.name[0].toUpperCase() : t.email[0].toUpperCase() }}
              </div>
              <div>
                <div class="text-sm text-[#0f172a]">{{ t.name || '—' }}</div>
                <div class="text-xs text-[#475569]">{{ t.email }}</div>
              </div>
            </div>
            <div class="flex items-center gap-3">
              <span class="text-xs text-[#94a3b8]">{{ t.dept || '—' }}</span>
              <button @click="targets.splice(idx, 1)" class="text-[#dc2626] hover:text-[#b91c1c]">
                <X :size="14" />
              </button>
            </div>
          </div>
        </div>
      </div>

      <p v-if="errors.targets" class="text-xs text-[#dc2626] mb-3">{{ errors.targets }}</p>

      <div class="flex justify-between">
        <Button variant="secondary" @click="step = 2">← Back</Button>
        <Button @click="validateStep3">Next: Review & Launch →</Button>
      </div>
    </Card>

    <!-- Step 4: Review & Launch -->
    <Card v-if="step === 4">
      <h2 class="text-lg font-semibold text-[#0f172a] mb-4">Review & Launch</h2>
      <div class="space-y-3 mb-5 bg-[#f8fafc] rounded-lg p-4">
        <div class="flex justify-between text-sm">
          <span class="text-[#94a3b8]">Campaign Name</span>
          <span class="font-medium text-[#0f172a]">{{ form.name }}</span>
        </div>
        <div class="flex justify-between text-sm">
          <span class="text-[#94a3b8]">Difficulty</span>
          <Badge :variant="form.difficulty === 'easy' ? 'green' : form.difficulty === 'medium' ? 'amber' : 'red'">{{ form.difficulty }}</Badge>
        </div>
        <div class="flex justify-between text-sm">
          <span class="text-[#94a3b8]">Template</span>
          <span class="text-[#0f172a]">{{ templates.find(t => t.id === form.selectedTemplate)?.name }}</span>
        </div>
        <div class="flex justify-between text-sm">
          <span class="text-[#94a3b8]">Targets</span>
          <span class="font-medium text-[#0f172a]">{{ targets.length }} recipient{{ targets.length !== 1 ? 's' : '' }}</span>
        </div>
        <div v-if="form.description" class="flex justify-between text-sm">
          <span class="text-[#94a3b8]">Description</span>
          <span class="text-[#0f172a] max-w-xs text-right">{{ form.description }}</span>
        </div>
      </div>

      <!-- Target preview -->
      <div class="mb-5">
        <div class="text-sm font-medium text-[#0f172a] mb-2">Target List Preview</div>
        <div class="border border-[#e2e8f0] rounded-lg overflow-hidden max-h-32 overflow-y-auto">
          <div v-for="(t, idx) in targets" :key="idx" class="flex items-center gap-3 px-4 py-2 border-b border-[#f1f5f9] last:border-0 text-sm">
            <span class="text-[#0f172a]">{{ t.name || '—' }}</span>
            <span class="text-[#475569]">{{ t.email }}</span>
            <span class="text-[#94a3b8] ml-auto">{{ t.dept || '—' }}</span>
          </div>
        </div>
      </div>

      <div class="bg-[#fef3c7] border border-[#d97706] rounded-lg p-3 mb-5 flex gap-2">
        <span class="text-[#d97706] text-base">⚠️</span>
        <div class="text-sm text-[#475569]">
          <span class="font-medium text-[#0f172a]">Warning: </span>
          Once launched, phishing emails will be sent to all {{ targets.length }} target(s). This cannot be undone.
        </div>
      </div>

      <div class="flex justify-between">
        <Button variant="secondary" @click="step = 3">← Back</Button>
        <div class="flex gap-2">
          <Button variant="secondary" @click="saveDraft">Save as Draft</Button>
          <Button @click="handleLaunch" :disabled="launching">
            <template #icon><Rocket :size="16" /></template>
            {{ launching ? 'Launching...' : 'Launch Campaign' }}
          </Button>
        </div>
      </div>
    </Card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { Check, UploadCloud, Rocket, Plus, X, Download, CheckCircle, Mail } from '@lucide/vue'
import { toast } from 'vue-sonner'
import Card from '../../components/Card.vue'
import Button from '../../components/Button.vue'
import Input from '../../components/Input.vue'
import Badge from '../../components/Badge.vue'
import { campaignApi } from '../../api'

const router = useRouter()
const step = ref(1)
const launching = ref(false)
const targetMode = ref<'csv' | 'single'>('csv')
const dragOver = ref(false)
const csvFileName = ref('')
const fileInput = ref<HTMLInputElement | null>(null)
const singleError = ref('')

const form = ref({ name: '', difficulty: '', description: '', selectedTemplate: null as number | null })
const errors = ref({ name: '', difficulty: '', template: '', targets: '' })

interface Target { name: string; email: string; dept: string }
const targets = ref<Target[]>([])
const singleTarget = ref<Target>({ name: '', email: '', dept: '' })

const steps = [
  { num: 1, label: 'Details' },
  { num: 2, label: 'Template' },
  { num: 3, label: 'Targets' },
  { num: 4, label: 'Launch' },
]

const difficulties = [
  { value: 'easy', label: 'Easy', desc: 'Obvious red flags', activeCls: 'border-[#16a34a] bg-[#dcfce7]/30' },
  { value: 'medium', label: 'Medium', desc: 'Subtle cues', activeCls: 'border-[#d97706] bg-[#fef3c7]/30' },
  { value: 'hard', label: 'Hard', desc: 'Near-perfect spoof', activeCls: 'border-[#dc2626] bg-[#fee2e2]/30' },
]

const templates = [
  { id: 1, name: 'IT Password Reset', difficulty: 'medium' },
  { id: 2, name: 'Lottery Winner', difficulty: 'easy' },
  { id: 3, name: 'Bank Security Alert', difficulty: 'hard' },
  { id: 4, name: 'WhatsApp Suspended', difficulty: 'medium' },
  { id: 5, name: 'Job Offer Letter', difficulty: 'easy' },
  { id: 6, name: 'University Portal', difficulty: 'medium' },
  { id: 7, name: 'MTN Data Reward', difficulty: 'easy' },
  { id: 8, name: 'Package Delivery', difficulty: 'medium' },
  { id: 9, name: 'Google/MS Alert', difficulty: 'hard' },
]

function validateStep1() {
  errors.value.name = form.value.name.trim() ? '' : 'Campaign name is required'
  errors.value.difficulty = form.value.difficulty ? '' : 'Please select a difficulty level'
  if (!errors.value.name && !errors.value.difficulty) step.value = 2
}

function validateStep2() {
  errors.value.template = form.value.selectedTemplate ? '' : 'Please select a template'
  if (!errors.value.template) step.value = 3
}

function validateStep3() {
  errors.value.targets = targets.value.length > 0 ? '' : 'Please add at least one target'
  if (!errors.value.targets) step.value = 4
}

function addSingleTarget() {
  singleError.value = ''
  if (!singleTarget.value.email) { singleError.value = 'Email is required'; return }
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(singleTarget.value.email)) { singleError.value = 'Enter a valid email'; return }
  if (targets.value.find(t => t.email === singleTarget.value.email)) { singleError.value = 'Email already added'; return }
  targets.value.push({ ...singleTarget.value })
  singleTarget.value = { name: '', email: '', dept: '' }
  toast.success('Target added!')
}

function parseCSV(text: string) {
  const lines = text.trim().split('\n').filter(l => l.trim())
  const parsed: Target[] = []
  for (const line of lines) {
    const parts = line.split(',').map(p => p.trim().replace(/^"|"$/g, ''))
    if (parts.length >= 1) {
      const [first, second, third] = parts
      // detect if first column is email or name
      const isEmail = (s: string) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(s)
      if (isEmail(first)) {
        parsed.push({ name: '', email: first, dept: second || '' })
      } else if (second && isEmail(second)) {
        parsed.push({ name: first, email: second, dept: third || '' })
      }
    }
  }
  return parsed
}

function handleFileSelect(e: Event) {
  const file = (e.target as HTMLInputElement).files?.[0]
  if (!file) return
  csvFileName.value = file.name
  const reader = new FileReader()
  reader.onload = (ev) => {
    const parsed = parseCSV(ev.target?.result as string)
    if (parsed.length === 0) { toast.error('No valid emails found in CSV'); return }
    targets.value = [...targets.value, ...parsed.filter(p => !targets.value.find(t => t.email === p.email))]
    toast.success(`${parsed.length} target(s) imported from CSV`)
  }
  reader.readAsText(file)
}

function handleDrop(e: DragEvent) {
  dragOver.value = false
  const file = e.dataTransfer?.files[0]
  if (!file || !file.name.endsWith('.csv')) { toast.error('Please drop a .csv file'); return }
  csvFileName.value = file.name
  const reader = new FileReader()
  reader.onload = (ev) => {
    const parsed = parseCSV(ev.target?.result as string)
    if (parsed.length === 0) { toast.error('No valid emails found in CSV'); return }
    targets.value = [...targets.value, ...parsed.filter(p => !targets.value.find(t => t.email === p.email))]
    toast.success(`${parsed.length} target(s) imported`)
  }
  reader.readAsText(file)
}

function downloadSampleCSV() {
  const content = 'name,email,department\nJohn Doe,john@company.com,Sales\nJane Smith,jane@company.com,Marketing\nBob Wilson,bob@company.com,IT'
  const blob = new Blob([content], { type: 'text/csv' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url; a.download = 'sample_targets.csv'; a.click()
  URL.revokeObjectURL(url)
}

async function saveDraft() {
  try {
    await campaignApi.create({
      name: form.value.name,
      description: form.value.description,
      difficulty: form.value.difficulty,
      templateId: form.value.selectedTemplate,
      autoRetest: false,
      retestIntervalDays: 14,
      targets: targets.value.map(t => ({ name: t.name, email: t.email, department: t.dept }))
    })
    toast.success('Campaign saved as draft')
    router.push('/admin/campaigns')
  } catch (err: any) {
    toast.error(err.response?.data?.error || 'Failed to save campaign')
  }
}

async function handleLaunch() {
  launching.value = true
  try {
    const created = await campaignApi.create({
      name: form.value.name,
      description: form.value.description,
      difficulty: form.value.difficulty,
      templateId: form.value.selectedTemplate,
      autoRetest: false,
      retestIntervalDays: 14,
      targets: targets.value.map(t => ({ name: t.name, email: t.email, department: t.dept }))
    })
    await campaignApi.launch(created.data.id)
    toast.success(`Campaign "${form.value.name}" launched! Emails queued for ${targets.value.length} target(s).`)
    router.push('/admin/campaigns')
  } catch (err: any) {
    toast.error(err.response?.data?.error || 'Failed to launch campaign')
  } finally {
    launching.value = false
  }
}
</script>
