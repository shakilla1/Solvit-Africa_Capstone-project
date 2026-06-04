<template>
  <div class="space-y-6">
    <div class="flex justify-between items-center">
      <div>
        <h2 class="text-2xl font-serif text-[#0f172a]" :style="{ fontFamily: 'var(--font-serif)' }">Event Logs</h2>
        <p class="text-[#475569]">Track all campaign events</p>
      </div>
      <Button variant="secondary"><template #icon><FileText :size="18" /></template>Export CSV</Button>
    </div>
    <Card>
      <div class="flex gap-4 mb-6">
        <Input placeholder="Search..." class="flex-1" />
        <select class="px-4 py-2 border border-[#e2e8f0] rounded-lg">
          <option>All Events</option><option>Clicked</option><option>Submitted</option><option>Opened</option>
        </select>
        <input type="date" class="px-4 py-2 border border-[#e2e8f0] rounded-lg" />
      </div>
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead class="bg-[#f8fafc] border-b border-[#e2e8f0]">
            <tr>
              <th class="text-left px-6 py-3 text-xs font-medium text-[#94a3b8] uppercase">Timestamp</th>
              <th class="text-left px-6 py-3 text-xs font-medium text-[#94a3b8] uppercase">User</th>
              <th class="text-left px-6 py-3 text-xs font-medium text-[#94a3b8] uppercase">Campaign</th>
              <th class="text-left px-6 py-3 text-xs font-medium text-[#94a3b8] uppercase">Event</th>
              <th class="text-left px-6 py-3 text-xs font-medium text-[#94a3b8] uppercase">IP Address</th>
              <th class="text-left px-6 py-3 text-xs font-medium text-[#94a3b8] uppercase">Device</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-[#e2e8f0]">
            <tr v-for="(event, idx) in events" :key="idx"
              :class="`hover:bg-[#f8fafc] ${event.event === 'Clicked' ? 'bg-[#dbeafe]/20' : event.event === 'Submitted' ? 'bg-[#fee2e2]/20' : ''}`">
              <td class="px-6 py-4 text-sm text-[#475569]">{{ event.timestamp }}</td>
              <td class="px-6 py-4 text-[#0f172a]">{{ event.user }}</td>
              <td class="px-6 py-4 text-[#475569]">{{ event.campaign }}</td>
              <td class="px-6 py-4">
                <span :class="`px-2 py-1 rounded text-xs font-medium ${event.event === 'Clicked' ? 'bg-[#dbeafe] text-[#2563eb]' : event.event === 'Submitted' ? 'bg-[#fee2e2] text-[#dc2626]' : 'bg-[#f1f5f9] text-[#475569]'}`">
                  {{ event.event }}
                </span>
              </td>
              <td class="px-6 py-4 text-sm text-[#475569]">{{ event.ip }}</td>
              <td class="px-6 py-4 text-sm text-[#475569]">{{ event.device }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </Card>
  </div>
</template>

<script setup lang="ts">
import { FileText } from '@lucide/vue'
import Card from '../../components/Card.vue'
import Button from '../../components/Button.vue'
import Input from '../../components/Input.vue'

const events = [
  { timestamp: '2025-05-28 14:32:15', user: 'John Doe', campaign: 'Q2 Awareness', event: 'Clicked', ip: '192.168.1.105', device: 'Chrome on Windows' },
  { timestamp: '2025-05-28 14:28:42', user: 'Jane Smith', campaign: 'Q2 Awareness', event: 'Submitted', ip: '192.168.1.142', device: 'Safari on macOS' },
  { timestamp: '2025-05-28 14:15:03', user: 'Bob Wilson', campaign: 'Q2 Awareness', event: 'Opened', ip: '192.168.1.89', device: 'Firefox on Linux' },
  { timestamp: '2025-05-28 13:45:21', user: 'Alice Brown', campaign: 'Q2 Awareness', event: 'Clicked', ip: '192.168.1.173', device: 'Edge on Windows' },
]
</script>
