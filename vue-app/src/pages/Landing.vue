<template>
  <div class="min-h-full bg-white">
    <!-- Navbar -->
    <nav class="sticky top-0 bg-white border-b border-[#e2e8f0] z-50">
      <div class="px-8 flex items-center justify-between" style="height: 52px;">
        <!-- Logo → always goes to / -->
        <RouterLink to="/" class="flex items-center gap-2 hover:opacity-80 transition-opacity">
          <Shield :size="20" class="text-[#2563eb]" />
          <span class="font-serif text-base text-[#0f172a]" :style="{ fontFamily: 'var(--font-serif)' }">
            PhishGuard <span class="text-[#2563eb]">Pro</span>
          </span>
        </RouterLink>
        <div class="flex items-center gap-5">
          <a href="#about" class="text-sm text-[#475569] hover:text-[#0f172a] transition-colors">About</a>
          <a href="#features" class="text-sm text-[#475569] hover:text-[#0f172a] transition-colors">Features</a>
          <a href="#how-it-works" class="text-sm text-[#475569] hover:text-[#0f172a] transition-colors">How It Works</a>
          <a href="#pricing" class="text-sm text-[#475569] hover:text-[#0f172a] transition-colors">Pricing</a>
          <RouterLink to="/login" class="text-sm text-[#475569] hover:text-[#0f172a] transition-colors">User Login</RouterLink>
          <RouterLink to="/admin/login"><Button variant="secondary" size="sm">Admin Login</Button></RouterLink>
          <RouterLink to="/admin/register"><Button size="sm">Get Started Free</Button></RouterLink>
        </div>
      </div>
    </nav>

    <!-- Hero -->
    <section class="px-8 pt-10 pb-8 text-center bg-gradient-to-b from-white to-[#f8fafc]">
      <Badge variant="blue" class="mb-4">Cybersecurity Awareness Platform</Badge>
      <h1 class="text-4xl font-serif leading-tight text-[#0f172a] mb-4 max-w-3xl mx-auto" :style="{ fontFamily: 'var(--font-serif)' }">
        Train Your Team to Spot Phishing.<br />Before It's Too Late.
      </h1>
      <p class="text-sm text-[#475569] max-w-lg mx-auto mb-6">
        PhishGuard Pro simulates real phishing attacks, catches who clicks, and immediately trains them with AI-powered education. Measurable results from day one.
      </p>
      <div class="flex items-center justify-center gap-3 mb-3">
        <RouterLink to="/admin/register"><Button size="md">Start Free Trial</Button></RouterLink>
        <a href="#how-it-works">
          <Button variant="secondary" size="md">
            <template #icon><Play :size="15" /></template>
            See How It Works
          </Button>
        </a>
      </div>
      <p class="text-xs text-[#94a3b8]">No credit card required · 1 free campaign forever</p>
    </section>

    <!-- Stats Bar -->
    <section class="bg-[#0f172a] py-5">
      <div class="px-8">
        <div class="grid grid-cols-3 gap-6 text-center">
          <div>
            <div class="text-xl font-serif text-white mb-0.5" :style="{ fontFamily: 'var(--font-serif)' }">9 Email Templates</div>
            <div class="text-xs text-[#64748b]">Ready to deploy</div>
          </div>
          <div>
            <div class="text-xl font-serif text-white mb-0.5" :style="{ fontFamily: 'var(--font-serif)' }">5 Fake Landing Pages</div>
            <div class="text-xs text-[#64748b]">Realistic simulations</div>
          </div>
          <div>
            <div class="text-xl font-serif text-white mb-0.5" :style="{ fontFamily: 'var(--font-serif)' }">Measurable Improvement</div>
            <div class="text-xs text-[#64748b]">Proven results</div>
          </div>
        </div>
      </div>
    </section>

    <!-- About -->
    <section id="about" class="px-8 py-10">
      <div class="max-w-4xl mx-auto">
        <div class="text-center mb-8">
          <Badge variant="grey" class="mb-3">About PhishGuard Pro</Badge>
          <h2 class="text-2xl font-serif text-[#0f172a] mb-3" :style="{ fontFamily: 'var(--font-serif)' }">What Is PhishGuard Pro?</h2>
          <p class="text-sm text-[#475569] max-w-2xl mx-auto">
            PhishGuard Pro is a cybersecurity awareness platform built for organisations that want to proactively protect their teams from phishing attacks.
            Instead of waiting for a real attack, we simulate one — then immediately educate the people who fell for it.
          </p>
        </div>
        <div class="grid grid-cols-2 gap-6">
          <div class="bg-[#f8fafc] rounded-xl p-5 border border-[#e2e8f0]">
            <div class="flex items-center gap-3 mb-3">
              <div class="w-9 h-9 rounded-lg bg-[#dbeafe] flex items-center justify-center">
                <Target :size="18" class="text-[#2563eb]" />
              </div>
              <h3 class="text-sm font-semibold text-[#0f172a]">For Security Admins</h3>
            </div>
            <p class="text-xs text-[#475569]">Launch phishing simulations, monitor who clicks, track risk scores across departments, and generate compliance reports — all from one dashboard.</p>
          </div>
          <div class="bg-[#f8fafc] rounded-xl p-5 border border-[#e2e8f0]">
            <div class="flex items-center gap-3 mb-3">
              <div class="w-9 h-9 rounded-lg bg-[#dcfce7] flex items-center justify-center">
                <Users :size="18" class="text-[#16a34a]" />
              </div>
              <h3 class="text-sm font-semibold text-[#0f172a]">For Employees</h3>
            </div>
            <p class="text-xs text-[#475569]">Complete interactive training sessions, earn badges, climb the leaderboard, and build real skills to recognise and avoid phishing attacks in the wild.</p>
          </div>
        </div>
      </div>
    </section>

    <!-- How It Works -->
    <section id="how-it-works" class="bg-[#f8fafc] px-8 py-10">
      <div class="max-w-4xl mx-auto">
        <div class="text-center mb-8">
          <Badge variant="blue" class="mb-3">Simple Process</Badge>
          <h2 class="text-2xl font-serif text-[#0f172a]" :style="{ fontFamily: 'var(--font-serif)' }">How It Works</h2>
        </div>
        <div class="grid grid-cols-4 gap-4">
          <div v-for="(step, idx) in steps" :key="idx" class="text-center relative">
            <!-- connector line -->
            <div v-if="idx < steps.length - 1" class="absolute top-5 left-1/2 w-full h-0.5 bg-[#e2e8f0] z-0"></div>
            <div class="relative z-10 w-10 h-10 rounded-full flex items-center justify-center mx-auto mb-3 text-white text-sm font-bold" :style="{ backgroundColor: step.color }">
              {{ idx + 1 }}
            </div>
            <div class="w-10 h-10 rounded-lg flex items-center justify-center mx-auto mb-3" :style="{ backgroundColor: step.color + '20' }">
              <component :is="step.icon" :size="18" :style="{ color: step.color }" />
            </div>
            <h3 class="text-xs font-semibold text-[#0f172a] mb-1">{{ step.title }}</h3>
            <p class="text-xs text-[#475569]">{{ step.desc }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- Features -->
    <section id="features" class="px-8 py-10">
      <div class="text-center mb-6">
        <Badge variant="blue" class="mb-3">Platform Features</Badge>
        <h2 class="text-2xl font-serif text-[#0f172a]" :style="{ fontFamily: 'var(--font-serif)' }">Everything You Need</h2>
      </div>
      <div class="grid grid-cols-3 gap-4">
        <div v-for="(feature, idx) in features" :key="idx" class="bg-white border border-[#e2e8f0] rounded-xl p-5 hover:shadow-md transition-shadow">
          <div class="w-10 h-10 rounded-lg bg-[#dbeafe] flex items-center justify-center mb-3">
            <component :is="feature.icon" :size="18" class="text-[#2563eb]" />
          </div>
          <h3 class="text-sm font-semibold text-[#0f172a] mb-1">{{ feature.title }}</h3>
          <p class="text-xs text-[#475569]">{{ feature.desc }}</p>
        </div>
      </div>
    </section>

    <!-- Pricing -->
    <section id="pricing" class="bg-[#f8fafc] px-8 py-10">
      <div class="text-center mb-6">
        <Badge variant="grey" class="mb-3">Transparent Pricing</Badge>
        <h2 class="text-2xl font-serif text-[#0f172a]" :style="{ fontFamily: 'var(--font-serif)' }">Choose Your Plan</h2>
      </div>
      <div class="grid grid-cols-5 gap-3">
        <div v-for="(plan, idx) in plans" :key="idx" :class="['bg-white border-2 rounded-xl p-4 relative transition-shadow hover:shadow-md', plan.popular ? 'border-[#2563eb] shadow-md' : 'border-[#e2e8f0]']">
          <Badge v-if="plan.popular" variant="blue" class="absolute -top-2.5 left-1/2 -translate-x-1/2 text-xs whitespace-nowrap">Most Popular</Badge>
          <div class="text-center mb-3">
            <h3 class="text-sm font-semibold text-[#0f172a] mb-1">{{ plan.name }}</h3>
            <div class="text-2xl font-serif text-[#0f172a]" :style="{ fontFamily: 'var(--font-serif)' }">{{ plan.price }}</div>
            <div class="text-xs text-[#94a3b8]">{{ plan.users }}</div>
          </div>
          <div class="space-y-1.5 mb-4">
            <div v-for="(has, i) in plan.features" :key="i" class="flex items-center gap-1.5 text-xs">
              <Check v-if="has" :size="12" class="text-[#16a34a] flex-shrink-0" />
              <X v-else :size="12" class="text-[#94a3b8] flex-shrink-0" />
              <span :class="has ? 'text-[#0f172a]' : 'text-[#94a3b8]'">{{ planFeatureLabels[i] }}</span>
            </div>
          </div>
          <RouterLink :to="plan.name === 'Enterprise' ? '#' : '/admin/register'">
            <Button :variant="plan.popular ? 'primary' : 'secondary'" size="sm" class="w-full">
              {{ plan.name === 'Enterprise' ? 'Contact Sales' : 'Get Started' }}
            </Button>
          </RouterLink>
        </div>
      </div>
    </section>

    <!-- Footer -->
    <footer class="bg-[#0f172a] text-white py-8">
      <div class="px-8">
        <div class="grid grid-cols-4 gap-8 mb-6">
          <!-- Brand -->
          <div>
            <RouterLink to="/" class="flex items-center gap-2 mb-3 hover:opacity-80 transition-opacity">
              <Shield :size="18" class="text-[#2563eb]" />
              <span class="font-serif text-sm" :style="{ fontFamily: 'var(--font-serif)' }">PhishGuard Pro</span>
            </RouterLink>
            <p class="text-xs text-[#64748b] leading-relaxed">
              Protecting organisations from phishing through simulation, education, and analytics.
            </p>
          </div>
          <!-- Platform -->
          <div>
            <h4 class="text-xs font-semibold text-[#94a3b8] uppercase tracking-wider mb-3">Platform</h4>
            <div class="space-y-2">
              <a href="#features" class="block text-xs text-[#64748b] hover:text-white transition-colors">Features</a>
              <a href="#how-it-works" class="block text-xs text-[#64748b] hover:text-white transition-colors">How It Works</a>
              <a href="#pricing" class="block text-xs text-[#64748b] hover:text-white transition-colors">Pricing</a>
              <a href="#about" class="block text-xs text-[#64748b] hover:text-white transition-colors">About</a>
            </div>
          </div>
          <!-- Access -->
          <div>
            <h4 class="text-xs font-semibold text-[#94a3b8] uppercase tracking-wider mb-3">Access</h4>
            <div class="space-y-2">
              <RouterLink to="/admin/login" class="block text-xs text-[#64748b] hover:text-white transition-colors">Admin Login</RouterLink>
              <RouterLink to="/admin/register" class="block text-xs text-[#64748b] hover:text-white transition-colors">Create Account</RouterLink>
              <RouterLink to="/login" class="block text-xs text-[#64748b] hover:text-white transition-colors">User Login</RouterLink>
              <RouterLink to="/forgot-password" class="block text-xs text-[#64748b] hover:text-white transition-colors">Forgot Password</RouterLink>
            </div>
          </div>
          <!-- Legal -->
          <div>
            <h4 class="text-xs font-semibold text-[#94a3b8] uppercase tracking-wider mb-3">Legal</h4>
            <div class="space-y-2">
              <a href="#" class="block text-xs text-[#64748b] hover:text-white transition-colors">Privacy Policy</a>
              <a href="#" class="block text-xs text-[#64748b] hover:text-white transition-colors">Terms of Service</a>
              <a href="#" class="block text-xs text-[#64748b] hover:text-white transition-colors">Cookie Policy</a>
              <a href="#" class="block text-xs text-[#64748b] hover:text-white transition-colors">Contact Us</a>
            </div>
          </div>
        </div>
        <div class="border-t border-[#1e293b] pt-4 flex items-center justify-between">
          <p class="text-xs text-[#475569]">© 2025 PhishGuard Pro. All rights reserved.</p>
          <div class="flex items-center gap-1.5">
            <div class="w-1.5 h-1.5 rounded-full bg-[#16a34a]"></div>
            <span class="text-xs text-[#475569]">All systems operational</span>
          </div>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { Shield, Zap, Brain, Trophy, BarChart2, RefreshCw, Play, Check, X, Target, Users, Mail, GraduationCap, MousePointer } from '@lucide/vue'
import Button from '../components/Button.vue'
import Badge from '../components/Badge.vue'

const steps = [
  { icon: Target, title: 'Launch a Campaign', desc: 'Choose a phishing template and upload your target list', color: '#2563eb' },
  { icon: Mail, title: 'Emails Are Sent', desc: 'Realistic phishing emails land in your team\'s inboxes', color: '#d97706' },
  { icon: MousePointer, title: 'Track Who Clicks', desc: 'Real-time alerts when someone clicks or submits credentials', color: '#dc2626' },
  { icon: GraduationCap, title: 'Educate Instantly', desc: 'Clickers are immediately redirected to training content', color: '#16a34a' },
]

const features = [
  { icon: Shield, title: 'Realistic Simulations', desc: '9 phishing templates targeting real-world scenarios' },
  { icon: Zap, title: 'Instant Awareness Pop-up', desc: 'Fires the second they click, before the fake login page' },
  { icon: Brain, title: 'AI-Powered Education', desc: 'Personalised coaching based on each user\'s answers' },
  { icon: Trophy, title: 'Gamification & Badges', desc: 'Leaderboards, achievement badges, and certificates' },
  { icon: BarChart2, title: 'Analytics Dashboard', desc: 'Click rates, department heatmaps, and risk scores' },
  { icon: RefreshCw, title: 'Progressive Re-Testing', desc: 'Adaptive difficulty increases every 2–4 weeks' },
]

const planFeatureLabels = ['Phishing Simulations', 'Education Modules', 'Analytics Dashboard', 'Gamification', 'Custom Templates']

const plans = [
  { name: 'Free', price: '$0', users: '1 simulation', popular: false, features: [true, false, false, false, false] },
  { name: 'Personal', price: '$5', users: 'individual', popular: false, features: [true, true, false, false, false] },
  { name: 'Starter', price: '$49', users: 'up to 50 users', popular: true, features: [true, true, true, true, false] },
  { name: 'Professional', price: '$149', users: 'up to 500 users', popular: false, features: [true, true, true, true, true] },
  { name: 'Enterprise', price: 'Custom', users: 'unlimited', popular: false, features: [true, true, true, true, true] },
]
</script>
