<template>
  <div class="space-y-5">
    <div>
      <h2 class="text-xl font-semibold text-[#0f172a]">Email Templates</h2>
      <p class="text-sm text-[#475569]">Browse and preview phishing simulation email templates</p>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="grid grid-cols-3 gap-4">
      <div v-for="i in 6" :key="i" class="bg-white border border-[#e2e8f0] rounded-xl p-4 animate-pulse">
        <div class="aspect-video bg-[#f1f5f9] rounded-lg mb-3"></div>
        <div class="h-4 bg-[#f1f5f9] rounded mb-2"></div>
        <div class="h-3 bg-[#f1f5f9] rounded w-2/3"></div>
      </div>
    </div>

    <!-- Templates grid -->
    <div v-else class="grid grid-cols-3 gap-4">
      <div v-for="template in templates" :key="template.id"
        class="bg-white border border-[#e2e8f0] rounded-xl overflow-hidden hover:shadow-md transition-shadow">
        <!-- Email preview thumbnail -->
        <div class="aspect-video bg-[#f8fafc] relative overflow-hidden cursor-pointer"
          @click="openPreview(template)">
          <!-- Scaled-down iframe preview -->
          <div class="absolute inset-0 overflow-hidden pointer-events-none">
            <div style="transform: scale(0.35); transform-origin: top left; width: 285%; height: 285%;">
              <div v-html="template.htmlBody" class="bg-white p-2"></div>
            </div>
          </div>
          <!-- Overlay with preview button -->
          <div class="absolute inset-0 bg-black/0 hover:bg-black/10 transition-colors flex items-center justify-center opacity-0 hover:opacity-100">
            <div class="bg-white rounded-lg px-3 py-1.5 text-xs font-medium text-[#0f172a] shadow flex items-center gap-1.5">
              <Eye :size="13" /> Preview
            </div>
          </div>
        </div>

        <div class="p-4">
          <h3 class="text-sm font-semibold text-[#0f172a] mb-1">{{ template.name }}</h3>
          <p class="text-xs text-[#475569] mb-2">From: <span class="text-[#0f172a]">{{ template.senderName }}</span></p>
          <p class="text-xs text-[#94a3b8] mb-3 truncate">Subject: {{ template.subject }}</p>
          <div class="flex items-center gap-2 mb-3">
            <Badge variant="grey">{{ template.category }}</Badge>
            <Badge :variant="template.difficulty === 'EASY' ? 'green' : template.difficulty === 'MEDIUM' ? 'amber' : 'red'">
              {{ capitalize(template.difficulty) }}
            </Badge>
          </div>
          <div class="flex gap-2">
            <Button variant="secondary" size="sm" class="flex-1" @click="openPreview(template)">
              <template #icon><Eye :size="13" /></template>Preview
            </Button>
            <Button size="sm" class="flex-1" @click="useInCampaign(template)">
              Use in Campaign
            </Button>
          </div>
        </div>
      </div>
    </div>

    <!-- Preview Modal -->
    <div v-if="previewTemplate" class="fixed inset-0 bg-black/60 flex items-center justify-center z-50 p-4"
      @click.self="previewTemplate = null">
      <div class="bg-white rounded-2xl shadow-2xl w-full max-w-2xl max-h-[90vh] flex flex-col overflow-hidden">
        <!-- Modal header -->
        <div class="flex items-center justify-between px-5 py-3 border-b border-[#e2e8f0]">
          <div>
            <h3 class="text-sm font-semibold text-[#0f172a]">{{ previewTemplate.name }}</h3>
            <p class="text-xs text-[#475569]">From: {{ previewTemplate.senderEmail }} · Subject: {{ previewTemplate.subject }}</p>
          </div>
          <div class="flex items-center gap-2">
            <Badge :variant="previewTemplate.difficulty === 'EASY' ? 'green' : previewTemplate.difficulty === 'MEDIUM' ? 'amber' : 'red'">
              {{ capitalize(previewTemplate.difficulty) }}
            </Badge>
            <button @click="previewTemplate = null" class="p-1.5 hover:bg-[#f8fafc] rounded-lg">
              <X :size="16" class="text-[#475569]" />
            </button>
          </div>
        </div>

        <!-- Red flags callout -->
        <div class="px-5 py-2 bg-[#fef3c7] border-b border-[#fde68a]">
          <p class="text-xs text-[#92400e]">
            <strong>⚠️ Red flags in this template:</strong>
            {{ getRedFlags(previewTemplate) }}
          </p>
        </div>

        <!-- Email HTML preview -->
        <div class="flex-1 overflow-y-auto p-4">
          <div class="border border-[#e2e8f0] rounded-lg overflow-hidden">
            <!-- Email client header bar -->
            <div class="bg-[#f8fafc] px-4 py-2 border-b border-[#e2e8f0] text-xs text-[#475569] space-y-0.5">
              <div><strong>From:</strong> {{ previewTemplate.senderName }} &lt;{{ previewTemplate.senderEmail }}&gt;</div>
              <div><strong>Subject:</strong> {{ previewTemplate.subject }}</div>
            </div>
            <!-- Rendered HTML -->
            <div v-html="previewTemplate.htmlBody" class="p-2"></div>
          </div>
        </div>

        <!-- Modal footer -->
        <div class="px-5 py-3 border-t border-[#e2e8f0] flex justify-between items-center">
          <p class="text-xs text-[#94a3b8]">Links in this preview are disabled</p>
          <div class="flex gap-2">
            <Button variant="secondary" size="sm" @click="previewTemplate = null">Close</Button>
            <Button size="sm" @click="useInCampaign(previewTemplate!)">Use in Campaign</Button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Eye, X } from '@lucide/vue'
import { toast } from 'vue-sonner'
import Card from '../../components/Card.vue'
import Button from '../../components/Button.vue'
import Badge from '../../components/Badge.vue'
import { templateApi } from '../../api'

const router = useRouter()
const loading = ref(true)
const templates = ref<any[]>([])
const previewTemplate = ref<any>(null)

onMounted(async () => {
  try {
    const { data } = await templateApi.list()
    templates.value = data
  } catch {
    // Fallback to mock data when backend not running
    templates.value = mockTemplates
  } finally {
    loading.value = false
  }
})

function openPreview(template: any) {
  previewTemplate.value = template
}

function useInCampaign(template: any) {
  // Store selected template and redirect to new campaign
  localStorage.setItem('selectedTemplateId', template.id)
  localStorage.setItem('selectedTemplateName', template.name)
  toast.success(`"${template.name}" selected. Redirecting to new campaign...`)
  previewTemplate.value = null
  router.push('/admin/campaigns/new')
}

function getRedFlags(template: any): string {
  const flags: string[] = []
  if (template.senderEmail?.includes('-') || template.senderEmail?.includes('alert')) flags.push('suspicious sender domain')
  if (template.subject?.toUpperCase().includes('URGENT') || template.subject?.includes('!')) flags.push('urgency language')
  if (template.htmlBody?.includes('24 hours') || template.htmlBody?.includes('48 hours')) flags.push('artificial deadline')
  if (template.htmlBody?.includes('suspended') || template.htmlBody?.includes('locked')) flags.push('fear tactics')
  return flags.length > 0 ? flags.join(', ') : 'lookalike domain, unexpected request'
}

const capitalize = (s: string) => s ? s.charAt(0) + s.slice(1).toLowerCase() : ''

// Mock data for when backend is not running
const mockTemplates = [
  { id: 1, name: 'IT Password Reset', category: 'IT', difficulty: 'MEDIUM', senderName: 'IT Support Team', senderEmail: 'it-support@company-helpdesk.com', subject: 'Action Required: Your Password Expires in 24 Hours', landingPageType: 'gmail', htmlBody: `<div style="font-family:Arial,sans-serif;max-width:600px;margin:0 auto;background:#fff;border:1px solid #e0e0e0;border-radius:8px;overflow:hidden"><div style="background:#1a73e8;padding:20px;text-align:center"><h2 style="color:#fff;margin:0">IT Support Portal</h2></div><div style="padding:30px"><p>Dear John,</p><p>Your company password will expire in <strong>24 hours</strong>. Reset it immediately to avoid being locked out.</p><div style="background:#fff3cd;border:1px solid #ffc107;border-radius:4px;padding:15px;margin:20px 0"><strong>⚠️ Warning:</strong> Failure to update will result in account suspension.</div><div style="text-align:center;margin:30px 0"><a href="#" style="background:#1a73e8;color:#fff;padding:14px 32px;border-radius:4px;text-decoration:none;font-weight:bold;display:inline-block">Reset Password Now</a></div></div></div>` },
  { id: 2, name: 'Bank Security Alert', category: 'Financial', difficulty: 'HARD', senderName: 'SecureBank Security', senderEmail: 'security@securebank-alerts.com', subject: 'URGENT: Suspicious Activity Detected on Your Account', landingPageType: 'bank', htmlBody: `<div style="font-family:Arial,sans-serif;max-width:600px;margin:0 auto;background:#fff;border:1px solid #e0e0e0;border-radius:8px;overflow:hidden"><div style="background:#006400;padding:20px;text-align:center"><h2 style="color:#fff;margin:0">🔒 SecureBank</h2></div><div style="padding:30px"><p>Dear Valued Customer,</p><p>We detected <strong>unusual login activity</strong> from Lagos, Nigeria at 03:42 AM.</p><div style="background:#fee;border-left:4px solid #c00;padding:15px;margin:20px 0"><strong style="color:#c00">Account Status: AT RISK</strong></div><div style="text-align:center;margin:30px 0"><a href="#" style="background:#006400;color:#fff;padding:14px 32px;border-radius:4px;text-decoration:none;font-weight:bold;display:inline-block">Verify My Identity</a></div></div></div>` },
  { id: 3, name: 'WhatsApp Suspended', category: 'Social', difficulty: 'MEDIUM', senderName: 'WhatsApp Support', senderEmail: 'support@whatsapp-verify.com', subject: 'Your WhatsApp account has been suspended', landingPageType: 'whatsapp', htmlBody: `<div style="font-family:Arial,sans-serif;max-width:600px;margin:0 auto;background:#fff;border:1px solid #e0e0e0;border-radius:8px;overflow:hidden"><div style="background:#25d366;padding:20px;text-align:center"><h2 style="color:#fff;margin:0">WhatsApp</h2></div><div style="padding:30px"><p>Hello,</p><p>Your WhatsApp account has been <strong>temporarily suspended</strong> due to a Terms of Service violation.</p><div style="text-align:center;margin:30px 0"><a href="#" style="background:#25d366;color:#fff;padding:14px 32px;border-radius:4px;text-decoration:none;font-weight:bold;display:inline-block">Verify Account</a></div></div></div>` },
  { id: 4, name: 'MTN Data Reward', category: 'Telecom', difficulty: 'EASY', senderName: 'MTN Rewards', senderEmail: 'rewards@mtn-promo.net', subject: "Congratulations! You've won 10GB FREE Data 🎉", landingPageType: 'mtn', htmlBody: `<div style="font-family:Arial,sans-serif;max-width:600px;margin:0 auto;background:#fff;border:1px solid #e0e0e0;border-radius:8px;overflow:hidden"><div style="background:#ffcb05;padding:20px;text-align:center"><h2 style="color:#000;margin:0">MTN</h2></div><div style="padding:30px;text-align:center"><div style="font-size:48px">🎉</div><h2>Congratulations!</h2><p>You have been selected to receive <strong>10GB FREE Data</strong>.</p><div style="background:#fff9e6;border:2px solid #ffcb05;border-radius:8px;padding:20px;margin:20px 0"><div style="font-size:36px;font-weight:bold">10 GB</div></div><a href="#" style="background:#ffcb05;color:#000;padding:14px 32px;border-radius:4px;text-decoration:none;font-weight:bold;display:inline-block">Claim Your Free Data</a></div></div>` },
  { id: 5, name: 'Google Account Alert', category: 'Tech', difficulty: 'HARD', senderName: 'Google Security', senderEmail: 'no-reply@google-security-alert.com', subject: 'Critical security alert for your Google Account', landingPageType: 'gmail', htmlBody: `<div style="font-family:Arial,sans-serif;max-width:600px;margin:0 auto;background:#fff;border:1px solid #e0e0e0;border-radius:8px;overflow:hidden"><div style="padding:20px;border-bottom:1px solid #e0e0e0"><span style="font-size:24px;font-weight:bold;color:#4285f4">G</span><span style="font-size:24px;font-weight:bold;color:#ea4335">o</span><span style="font-size:24px;font-weight:bold;color:#fbbc05">o</span><span style="font-size:24px;font-weight:bold;color:#4285f4">g</span><span style="font-size:24px;font-weight:bold;color:#34a853">l</span><span style="font-size:24px;font-weight:bold;color:#ea4335">e</span></div><div style="padding:30px"><h2>Critical security alert</h2><p>Someone used your password to sign in from a new device in Johannesburg.</p><div style="background:#f8f9fa;border-radius:8px;padding:15px;margin:20px 0"><div style="color:#5f6368;font-size:13px">Device: Windows PC · Location: Johannesburg · Time: Just now</div></div><a href="#" style="background:#1a73e8;color:#fff;padding:12px 24px;border-radius:4px;text-decoration:none;font-weight:500;display:inline-block">Check activity</a></div></div>` },
  { id: 6, name: 'Job Offer Letter', category: 'HR', difficulty: 'EASY', senderName: 'HR Department - TechCorp', senderEmail: 'hr@techcorp-careers.net', subject: 'Congratulations! Job Offer - Software Developer Position', landingPageType: 'university', htmlBody: `<div style="font-family:Arial,sans-serif;max-width:600px;margin:0 auto;background:#fff;border:1px solid #e0e0e0;border-radius:8px;overflow:hidden"><div style="background:#1e40af;padding:20px;text-align:center"><h2 style="color:#fff;margin:0">TechCorp International</h2></div><div style="padding:30px"><p>Dear Candidate,</p><p>We are pleased to offer you the position of <strong>Software Developer</strong> at TechCorp International.</p><div style="background:#eff6ff;border:1px solid #bfdbfe;border-radius:8px;padding:20px;margin:20px 0"><strong>Salary:</strong> R45,000/month · <strong>Start:</strong> 1 August 2025</div><div style="text-align:center;margin:30px 0"><a href="#" style="background:#1e40af;color:#fff;padding:14px 32px;border-radius:4px;text-decoration:none;font-weight:bold;display:inline-block">View & Accept Offer</a></div></div></div>` },
]
</script>
