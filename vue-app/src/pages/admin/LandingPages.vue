<template>
  <div class="space-y-6">
    <div>
      <h2 class="text-2xl font-serif text-[#0f172a]" :style="{ fontFamily: 'var(--font-serif)' }">Landing Pages</h2>
      <p class="text-[#475569]">Fake landing pages used in simulations</p>
    </div>
    <div class="grid grid-cols-3 gap-6">
      <Card v-for="page in landingPages" :key="page.id">
        <!-- Scaled preview thumbnail -->
        <div class="aspect-video bg-[#f8fafc] rounded-lg mb-4 overflow-hidden relative cursor-pointer" @click="openPreview(page)">
          <div class="absolute inset-0 overflow-hidden pointer-events-none">
            <div style="transform: scale(0.3); transform-origin: top left; width: 333%; height: 333%;">
              <div v-html="page.previewHtml" class="bg-white"></div>
            </div>
          </div>
          <div class="absolute inset-0 bg-black/0 hover:bg-black/10 transition-colors flex items-center justify-center opacity-0 hover:opacity-100">
            <div class="bg-white rounded-lg px-3 py-1.5 text-xs font-medium text-[#0f172a] shadow flex items-center gap-1.5">
              <Eye :size="13" /> Preview
            </div>
          </div>
        </div>
        <h3 class="font-medium text-[#0f172a] mb-1">{{ page.name }}</h3>
        <p class="text-sm text-[#475569] mb-4">{{ page.type }}</p>
        <div class="flex gap-2">
          <Button variant="secondary" size="sm" class="flex-1" @click="openPreview(page)"><template #icon><Eye :size="16" /></template>Preview</Button>
          <Button size="sm" class="flex-1" @click="assignToCampaign(page)">Assign to Campaign</Button>
        </div>
      </Card>
    </div>

    <!-- Preview Modal -->
    <div v-if="previewPage" class="fixed inset-0 bg-black/60 flex items-center justify-center z-50 p-4" @click.self="previewPage = null">
      <div class="bg-white rounded-2xl shadow-2xl w-full max-w-sm overflow-hidden">
        <div class="flex items-center justify-between px-4 py-3 border-b border-[#e2e8f0]">
          <div>
            <h3 class="text-sm font-semibold text-[#0f172a]">{{ previewPage.name }}</h3>
            <p class="text-xs text-[#94a3b8]">Fake URL: {{ previewPage.fakeUrl }}</p>
          </div>
          <button @click="previewPage = null" class="p-1.5 hover:bg-[#f8fafc] rounded-lg"><X :size="16" class="text-[#475569]" /></button>
        </div>
        <div class="p-3 bg-[#fef3c7] border-b border-[#fde68a]">
          <p class="text-xs text-[#92400e]"><strong>⚠️ Simulation page:</strong> This is a fake login page used in phishing simulations. Credentials entered here are captured for training purposes.</p>
        </div>
        <div v-html="previewPage.previewHtml" class="overflow-auto" style="max-height: 400px;"></div>
        <div class="px-4 py-3 border-t border-[#e2e8f0] flex justify-between">
          <Button variant="secondary" size="sm" @click="previewPage = null">Close</Button>
          <Button size="sm" @click="assignToCampaign(previewPage!)">Assign to Campaign</Button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Eye, X } from '@lucide/vue'
import { useRouter } from 'vue-router'
import { toast } from 'vue-sonner'
import Card from '../../components/Card.vue'
import Button from '../../components/Button.vue'

const router = useRouter()
const previewPage = ref<any>(null)

const landingPages = [
  {
    id: 1, name: 'Gmail Login', type: 'Email Provider', fakeUrl: 'accounts.go0gle.com',
    previewHtml: `<div style="font-family:Arial,sans-serif;padding:32px;background:#fff;min-height:300px;display:flex;align-items:center;justify-content:center"><div style="width:100%;max-width:360px"><div style="text-align:center;margin-bottom:24px"><span style="font-size:28px;font-weight:700;color:#4285f4">G</span><span style="font-size:28px;font-weight:700;color:#ea4335">o</span><span style="font-size:28px;font-weight:700;color:#fbbc05">o</span><span style="font-size:28px;font-weight:700;color:#4285f4">g</span><span style="font-size:28px;font-weight:700;color:#34a853">l</span><span style="font-size:28px;font-weight:700;color:#ea4335">e</span><p style="font-size:24px;color:#202124;margin:8px 0">Sign in</p><p style="font-size:14px;color:#5f6368">Use your Google Account</p></div><input type="email" placeholder="Email or phone" style="width:100%;padding:12px;border:1px solid #dadce0;border-radius:4px;font-size:16px;box-sizing:border-box;margin-bottom:12px" /><input type="password" placeholder="Enter your password" style="width:100%;padding:12px;border:1px solid #dadce0;border-radius:4px;font-size:16px;box-sizing:border-box;margin-bottom:20px" /><button style="width:100%;padding:12px;background:#1a73e8;color:#fff;border:none;border-radius:4px;font-size:16px;cursor:pointer">Next</button></div></div>`,
  },
  {
    id: 2, name: 'MTN Customer Portal', type: 'Telecom', fakeUrl: 'mtn-customerportal.net',
    previewHtml: `<div style="font-family:Arial,sans-serif;background:#fff;min-height:300px"><div style="background:#ffcb05;padding:16px;text-align:center"><span style="font-size:24px;font-weight:900;color:#000">MTN</span></div><div style="padding:32px;max-width:360px;margin:0 auto"><h2 style="font-size:20px;color:#000;margin-bottom:20px">MTN Customer Portal</h2><input type="text" placeholder="Phone number or email" style="width:100%;padding:12px;border:1px solid #ddd;border-radius:4px;font-size:14px;box-sizing:border-box;margin-bottom:12px" /><input type="password" placeholder="Password" style="width:100%;padding:12px;border:1px solid #ddd;border-radius:4px;font-size:14px;box-sizing:border-box;margin-bottom:20px" /><button style="width:100%;padding:12px;background:#ffcb05;color:#000;border:none;border-radius:4px;font-size:16px;font-weight:700;cursor:pointer">Login</button></div></div>`,
  },
  {
    id: 3, name: 'University Student Portal', type: 'Education', fakeUrl: 'student-portal-sis.edu.ng',
    previewHtml: `<div style="font-family:Arial,sans-serif;background:#fff;min-height:300px"><div style="background:#1e40af;padding:16px;text-align:center"><span style="font-size:20px;font-weight:700;color:#fff">🎓 University of Technology</span></div><div style="padding:32px;max-width:360px;margin:0 auto"><h2 style="font-size:18px;color:#1e40af;margin-bottom:20px">Student Information System</h2><input type="text" placeholder="Student email" style="width:100%;padding:12px;border:1px solid #ddd;border-radius:4px;font-size:14px;box-sizing:border-box;margin-bottom:12px" /><input type="password" placeholder="Password" style="width:100%;padding:12px;border:1px solid #ddd;border-radius:4px;font-size:14px;box-sizing:border-box;margin-bottom:20px" /><button style="width:100%;padding:12px;background:#1e40af;color:#fff;border:none;border-radius:4px;font-size:16px;cursor:pointer">Login to Portal</button></div></div>`,
  },
  {
    id: 4, name: 'Generic Bank Login', type: 'Financial', fakeUrl: 'securebank-online.net',
    previewHtml: `<div style="font-family:Arial,sans-serif;background:#fff;min-height:300px"><div style="background:#059669;padding:16px;text-align:center"><span style="font-size:20px;font-weight:700;color:#fff">🔒 SecureBank</span></div><div style="padding:32px;max-width:360px;margin:0 auto"><h2 style="font-size:18px;color:#059669;margin-bottom:4px">Secure Online Banking</h2><p style="font-size:12px;color:#6b7280;margin-bottom:20px">Internet Banking</p><input type="email" placeholder="Account email" style="width:100%;padding:12px;border:1px solid #ddd;border-radius:4px;font-size:14px;box-sizing:border-box;margin-bottom:12px" /><input type="password" placeholder="Password" style="width:100%;padding:12px;border:1px solid #ddd;border-radius:4px;font-size:14px;box-sizing:border-box;margin-bottom:12px" /><label style="display:flex;align-items:center;gap:8px;font-size:12px;color:#6b7280;margin-bottom:20px"><input type="checkbox" /> Remember this device</label><button style="width:100%;padding:12px;background:#059669;color:#fff;border:none;border-radius:4px;font-size:16px;cursor:pointer">Sign In</button></div></div>`,
  },
  {
    id: 5, name: 'WhatsApp Web', type: 'Social', fakeUrl: 'web-whatsapp-login.com',
    previewHtml: `<div style="font-family:Arial,sans-serif;background:#fff;min-height:300px"><div style="background:#25d366;padding:16px;text-align:center"><span style="font-size:20px;font-weight:700;color:#fff">WhatsApp</span></div><div style="padding:32px;max-width:360px;margin:0 auto"><h2 style="font-size:18px;color:#25d366;margin-bottom:4px">WhatsApp Web</h2><p style="font-size:12px;color:#6b7280;margin-bottom:20px">Connect your phone</p><input type="tel" placeholder="+27 80 000 0000" style="width:100%;padding:12px;border:1px solid #ddd;border-radius:4px;font-size:14px;box-sizing:border-box;margin-bottom:12px" /><input type="text" placeholder="OTP Code" style="width:100%;padding:12px;border:1px solid #ddd;border-radius:4px;font-size:14px;box-sizing:border-box;margin-bottom:20px" /><button style="width:100%;padding:12px;background:#25d366;color:#fff;border:none;border-radius:4px;font-size:16px;cursor:pointer">Verify & Connect</button></div></div>`,
  },
]

function openPreview(page: any) { previewPage.value = page }

function assignToCampaign(page: any) {
  toast.success(`"${page.name}" assigned. Redirecting to new campaign...`)
  previewPage.value = null
  router.push('/admin/campaigns/new')
}
</script>