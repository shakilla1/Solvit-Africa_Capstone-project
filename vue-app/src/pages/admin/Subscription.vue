<template>
  <div class="space-y-6">
    <div>
      <h2 class="text-2xl font-serif text-[#0f172a]" :style="{ fontFamily: 'var(--font-serif)' }">Subscription & Billing</h2>
      <p class="text-[#475569]">Manage your subscription and billing</p>
    </div>

    <Card class="border-2 border-[#2563eb]">
      <div class="flex justify-between items-start mb-6">
        <div>
          <div class="flex items-center gap-3 mb-2">
            <h3 class="text-xl font-medium text-[#0f172a]">Starter Plan</h3>
            <Badge variant="blue">Active</Badge>
          </div>
          <p class="text-[#475569]">Renews on June 15, 2025</p>
        </div>
        <div class="text-right">
          <div class="text-3xl font-serif text-[#0f172a] mb-1" :style="{ fontFamily: 'var(--font-serif)' }">$49<span class="text-lg text-[#94a3b8]">/month</span></div>
        </div>
      </div>
      <div class="mb-6">
        <div class="flex justify-between items-center mb-2">
          <span class="text-sm text-[#475569]">Users</span>
          <span class="text-sm font-medium text-[#0f172a]">23 / 50</span>
        </div>
        <div class="h-2 bg-[#f1f5f9] rounded-full overflow-hidden">
          <div class="h-full bg-[#2563eb] rounded-full" style="width: 46%"></div>
        </div>
      </div>
      <div class="flex gap-3">
        <Button>Upgrade Plan</Button>
        <Button variant="secondary">Manage Billing</Button>
      </div>
    </Card>

    <div class="grid grid-cols-5 gap-4">
      <Card v-for="plan in plans" :key="plan.name" :class="plan.current ? 'border-2 border-[#2563eb]' : ''">
        <div class="text-center mb-4">
          <h3 class="font-medium text-[#0f172a] mb-2">{{ plan.name }}</h3>
          <div class="text-2xl font-serif text-[#0f172a] mb-1" :style="{ fontFamily: 'var(--font-serif)' }">{{ plan.price }}</div>
          <div class="text-sm text-[#94a3b8]">{{ plan.users }} users</div>
        </div>
        <Button v-if="plan.current" variant="secondary" disabled class="w-full">Current Plan</Button>
        <Button v-else-if="plan.name === 'Enterprise'" variant="secondary" class="w-full">Contact Sales</Button>
        <Button v-else class="w-full">Upgrade</Button>
      </Card>
    </div>

    <Card>
      <h3 class="text-lg font-medium text-[#0f172a] mb-4">Payment Method</h3>
      <div class="flex items-center justify-between p-4 bg-[#f8fafc] rounded-lg mb-4">
        <div class="flex items-center gap-3">
          <div class="w-12 h-8 bg-[#2563eb] rounded flex items-center justify-center">
            <CreditCard :size="20" class="text-white" />
          </div>
          <div>
            <div class="text-sm font-medium text-[#0f172a]">•••• •••• •••• 4242</div>
            <div class="text-xs text-[#94a3b8]">Expires 12/26</div>
          </div>
        </div>
        <Button variant="secondary" size="sm">Update</Button>
      </div>
    </Card>
  </div>
</template>

<script setup lang="ts">
import { CreditCard } from '@lucide/vue'
import Card from '../../components/Card.vue'
import Button from '../../components/Button.vue'
import Badge from '../../components/Badge.vue'

const plans = [
  { name: 'Free', price: '$0', users: 1, current: false },
  { name: 'Personal', price: '$5', users: 1, current: false },
  { name: 'Starter', price: '$49', users: 50, current: true },
  { name: 'Professional', price: '$149', users: 500, current: false },
  { name: 'Enterprise', price: 'Custom', users: '∞', current: false },
]
</script>
