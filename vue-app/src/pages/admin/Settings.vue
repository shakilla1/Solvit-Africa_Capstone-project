<template>
  <div class="space-y-6">
    <div>
      <h2 class="text-2xl font-serif text-[#0f172a]" :style="{ fontFamily: 'var(--font-serif)' }">Settings</h2>
      <p class="text-[#475569]">Manage your account settings</p>
    </div>

    <div class="border-b border-[#e2e8f0]">
      <div class="flex gap-6">
        <button v-for="tab in tabs" :key="tab" @click="activeTab = tab"
          :class="`pb-3 px-1 text-sm font-medium border-b-2 transition-colors ${activeTab === tab ? 'border-[#2563eb] text-[#2563eb]' : 'border-transparent text-[#475569] hover:text-[#0f172a]'}`">
          {{ capitalize(tab) }}
        </button>
      </div>
    </div>

    <Card v-if="activeTab === 'profile'">
      <h3 class="text-lg font-medium text-[#0f172a] mb-6">Profile Information</h3>
      <div class="space-y-4 max-w-md">
        <Input label="Full Name" model-value="Admin User" />
        <Input label="Email" type="email" model-value="admin@company.com" />
        <Input label="Organisation" model-value="Acme Inc" />
        <Button>Save Changes</Button>
      </div>
    </Card>

    <Card v-if="activeTab === 'security'">
      <h3 class="text-lg font-medium text-[#0f172a] mb-6">Security Settings</h3>
      <div class="space-y-6 max-w-md">
        <div>
          <h4 class="text-sm font-medium text-[#0f172a] mb-4">Change Password</h4>
          <div class="space-y-3">
            <Input label="Current Password" type="password" />
            <Input label="New Password" type="password" />
            <Input label="Confirm New Password" type="password" />
            <Button>Update Password</Button>
          </div>
        </div>
        <div class="pt-6 border-t border-[#e2e8f0]">
          <h4 class="text-sm font-medium text-[#0f172a] mb-2">Two-Factor Authentication</h4>
          <p class="text-sm text-[#475569] mb-4">Add an extra layer of security to your account</p>
          <Button variant="secondary">Enable 2FA</Button>
        </div>
      </div>
    </Card>

    <Card v-if="activeTab === 'notifications'">
      <h3 class="text-lg font-medium text-[#0f172a] mb-6">Notification Preferences</h3>
      <div class="space-y-4">
        <div v-for="notification in notifications" :key="notification" class="flex items-center justify-between">
          <span class="text-sm text-[#0f172a]">{{ notification }}</span>
          <label class="relative inline-flex items-center cursor-pointer">
            <input type="checkbox" class="sr-only peer" checked />
            <div class="w-11 h-6 bg-[#cbd5e1] peer-focus:ring-2 peer-focus:ring-[#2563eb]/20 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-[#2563eb]"></div>
          </label>
        </div>
      </div>
    </Card>

    <Card v-if="activeTab === 'organisation'">
      <h3 class="text-lg font-medium text-[#0f172a] mb-6">Organisation Settings</h3>
      <div class="space-y-4 max-w-md">
        <Input label="Organisation Name" model-value="Acme Inc" />
        <div>
          <label class="block text-sm font-medium text-[#0f172a] mb-1">Timezone</label>
          <select class="w-full px-4 py-3 border border-[#e2e8f0] rounded-lg">
            <option>UTC+00:00 (London)</option>
            <option>UTC+01:00 (Lagos)</option>
            <option>UTC-05:00 (New York)</option>
          </select>
        </div>
        <Button>Save Changes</Button>
      </div>
    </Card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import Card from '../../components/Card.vue'
import Input from '../../components/Input.vue'
import Button from '../../components/Button.vue'

const activeTab = ref('profile')
const tabs = ['profile', 'security', 'notifications', 'organisation']
const capitalize = (s: string) => s.charAt(0).toUpperCase() + s.slice(1)

const notifications = [
  'Campaign launched', 'Target clicked a link', 'Education module completed',
  'Weekly summary report', 'New re-test scheduled',
]
</script>
