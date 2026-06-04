<template>
  <div class="min-h-screen bg-[#f8fafc] py-8">
    <div class="max-w-3xl mx-auto px-4">

      <!-- Header -->
      <div class="flex items-center gap-3 mb-6">
        <button @click="router.back()" class="p-2 rounded-lg hover:bg-[#e2e8f0] transition-colors">
          <ArrowLeft :size="18" class="text-[#475569]" />
        </button>
        <div class="flex-1">
          <h1 class="text-lg font-semibold text-[#0f172a]">{{ topic.title }}</h1>
          <p class="text-xs text-[#94a3b8]">{{ topic.duration }} · {{ topic.questions }} quiz questions</p>
        </div>
        <div class="flex items-center gap-2 text-xs text-[#475569]">
          <span :class="phase === 'lesson' ? 'text-[#2563eb] font-semibold' : ''">Lesson</span>
          <span class="text-[#e2e8f0]">›</span>
          <span :class="phase === 'quiz' || phase === 'complete' ? 'text-[#2563eb] font-semibold' : ''">Quiz</span>
          <span class="text-[#e2e8f0]">›</span>
          <span :class="phase === 'complete' ? 'text-[#2563eb] font-semibold' : ''">Results</span>
        </div>
      </div>

      <!-- ── LESSON PHASE ── -->
      <div v-if="phase === 'lesson'" class="space-y-4">

        <!-- Hero card -->
        <div class="rounded-2xl p-6 text-white" :style="{ background: `linear-gradient(135deg, ${topic.color}, ${topic.color}cc)` }">
          <div class="flex items-center gap-3 mb-3">
            <div class="w-12 h-12 rounded-xl bg-white/20 flex items-center justify-center">
              <component :is="topic.icon" :size="24" class="text-white" />
            </div>
            <div>
              <h2 class="text-xl font-bold">{{ topic.title }}</h2>
              <p class="text-sm text-white/80">{{ topic.desc }}</p>
            </div>
          </div>
        </div>

        <!-- Lesson content sections -->
        <div v-for="(section, idx) in topic.content" :key="idx"
          class="bg-white rounded-2xl border border-[#e2e8f0] overflow-hidden">
          <!-- Section header -->
          <div class="flex items-center gap-3 px-6 py-4 border-b border-[#f1f5f9]"
            :style="{ backgroundColor: topic.color + '10' }">
            <div class="w-8 h-8 rounded-lg flex items-center justify-center text-white text-sm font-bold"
              :style="{ backgroundColor: topic.color }">{{ idx + 1 }}</div>
            <h3 class="font-semibold text-[#0f172a]">{{ section.title }}</h3>
          </div>

          <div class="p-6 space-y-4">
            <!-- Main text -->
            <p class="text-sm text-[#475569] leading-relaxed">{{ section.body }}</p>

            <!-- Example box -->
            <div v-if="section.example" class="bg-[#f8fafc] border-l-4 rounded-r-lg p-4"
              :style="{ borderColor: topic.color }">
              <p class="text-xs font-semibold text-[#94a3b8] uppercase mb-2">Real Example</p>
              <p class="text-sm text-[#0f172a]">{{ section.example }}</p>
            </div>

            <!-- Warning box -->
            <div v-if="section.warning" class="bg-[#fef2f2] border border-[#fecaca] rounded-xl p-4 flex gap-3">
              <AlertTriangle :size="18" class="text-[#dc2626] flex-shrink-0 mt-0.5" />
              <p class="text-sm text-[#dc2626]">{{ section.warning }}</p>
            </div>

            <!-- Tip box -->
            <div v-if="section.tip" class="bg-[#dbeafe] border border-[#bfdbfe] rounded-xl p-4 flex gap-3">
              <Lightbulb :size="18" class="text-[#2563eb] flex-shrink-0 mt-0.5" />
              <p class="text-sm text-[#1d4ed8]">{{ section.tip }}</p>
            </div>

            <!-- Checklist -->
            <div v-if="section.checklist" class="space-y-2">
              <div v-for="(item, i) in section.checklist" :key="i" class="flex items-start gap-2">
                <div class="w-5 h-5 rounded-full flex items-center justify-center flex-shrink-0 mt-0.5"
                  :style="{ backgroundColor: topic.color + '20' }">
                  <Check :size="11" :style="{ color: topic.color }" />
                </div>
                <p class="text-sm text-[#475569]">{{ item }}</p>
              </div>
            </div>

            <!-- Comparison table (real vs fake) -->
            <div v-if="section.comparison" class="grid grid-cols-2 gap-3">
              <div class="bg-[#dcfce7] border border-[#86efac] rounded-xl p-4">
                <p class="text-xs font-bold text-[#16a34a] mb-2">✓ Legitimate</p>
                <ul class="space-y-1">
                  <li v-for="(item, i) in section.comparison.good" :key="i" class="text-xs text-[#166534]">• {{ item }}</li>
                </ul>
              </div>
              <div class="bg-[#fee2e2] border border-[#fca5a5] rounded-xl p-4">
                <p class="text-xs font-bold text-[#dc2626] mb-2">✗ Phishing</p>
                <ul class="space-y-1">
                  <li v-for="(item, i) in section.comparison.bad" :key="i" class="text-xs text-[#991b1b]">• {{ item }}</li>
                </ul>
              </div>
            </div>
          </div>
        </div>

        <button @click="startQuiz"
          class="w-full py-4 rounded-2xl text-white font-semibold text-sm transition-all hover:opacity-90 flex items-center justify-center gap-2"
          :style="{ backgroundColor: topic.color }">
          <BookOpen :size="18" />
          I've read the lesson — Start Quiz
        </button>
      </div>

      <!-- ── QUIZ PHASE ── -->
      <div v-if="phase === 'quiz'">
        <!-- Progress -->
        <div class="mb-6">
          <div class="flex justify-between text-xs text-[#475569] mb-2">
            <span>Question {{ currentQuestion + 1 }} of {{ topicQuestions.length }}</span>
            <span class="font-medium text-[#0f172a]">{{ score }} pts</span>
          </div>
          <div class="h-2 bg-[#e2e8f0] rounded-full overflow-hidden">
            <div class="h-full rounded-full transition-all duration-300"
              :style="{ width: ((currentQuestion + 1) / topicQuestions.length) * 100 + '%', backgroundColor: topic.color }">
            </div>
          </div>
        </div>

        <!-- Question -->
        <div v-if="!answered" class="bg-white rounded-2xl border border-[#e2e8f0] p-6">
          <div class="flex items-center gap-2 mb-4">
            <span class="px-2 py-1 rounded text-xs font-medium text-white"
              :style="{ backgroundColor: currentQ.level === 1 ? '#16a34a' : currentQ.level === 2 ? '#d97706' : '#dc2626' }">
              {{ currentQ.levelName }}
            </span>
          </div>
          <h3 class="text-lg font-medium text-[#0f172a] mb-5">{{ currentQ.question }}</h3>
          <div class="space-y-3">
            <button v-for="(option, idx) in currentQ.options" :key="idx" @click="selectedAnswer = idx"
              :class="`w-full p-4 border-2 rounded-xl text-left transition-all ${selectedAnswer === idx ? 'border-[#2563eb] bg-[#dbeafe]/30' : 'border-[#e2e8f0] hover:border-[#cbd5e1]'}`">
              <div class="flex items-center gap-3">
                <div :class="`w-6 h-6 rounded-full border-2 flex items-center justify-center flex-shrink-0 ${selectedAnswer === idx ? 'border-[#2563eb] bg-[#2563eb]' : 'border-[#cbd5e1]'}`">
                  <div v-if="selectedAnswer === idx" class="w-2 h-2 rounded-full bg-white"></div>
                </div>
                <span class="text-sm text-[#0f172a]">{{ option }}</span>
              </div>
            </button>
          </div>
          <button @click="handleSubmitAnswer" :disabled="selectedAnswer === null"
            class="w-full mt-5 py-3 rounded-xl text-white font-semibold text-sm transition-all disabled:opacity-40"
            :style="{ backgroundColor: topic.color }">
            Submit Answer
          </button>
        </div>

        <!-- Feedback -->
        <div v-if="answered" class="bg-white rounded-2xl border border-[#e2e8f0] p-6 space-y-4">
          <div :class="`p-4 rounded-xl border ${selectedAnswer === currentQ.correct ? 'bg-[#dcfce7] border-[#86efac]' : 'bg-[#fee2e2] border-[#fca5a5]'}`">
            <p class="font-semibold" :style="{ color: selectedAnswer === currentQ.correct ? '#16a34a' : '#dc2626' }">
              {{ selectedAnswer === currentQ.correct ? '✓ Correct! +10 points' : '✗ Incorrect' }}
            </p>
          </div>
          <div class="bg-[#dbeafe] border border-[#bfdbfe] rounded-xl p-4 flex gap-3">
            <Brain :size="20" class="text-[#2563eb] flex-shrink-0 mt-0.5" />
            <div>
              <p class="text-sm font-semibold text-[#1e40af] mb-1">Explanation</p>
              <p class="text-sm text-[#1d4ed8]">{{ currentQ.explanation }}</p>
            </div>
          </div>
          <button @click="handleNextQuestion"
            class="w-full py-3 rounded-xl text-white font-semibold text-sm"
            :style="{ backgroundColor: topic.color }">
            {{ currentQuestion < topicQuestions.length - 1 ? 'Next Question →' : 'View My Results' }}
          </button>
        </div>
      </div>

      <!-- ── COMPLETE PHASE ── -->
      <div v-if="phase === 'complete'" class="space-y-4">
        <div class="bg-white rounded-2xl border border-[#e2e8f0] p-8 text-center">
          <div class="text-6xl mb-4">{{ score >= topicQuestions.length * 7 ? '🏆' : score >= topicQuestions.length * 4 ? '🎯' : '📚' }}</div>
          <h2 class="text-3xl font-serif text-[#0f172a] mb-1">{{ score }} / {{ topicQuestions.length * 10 }}</h2>
          <p class="text-sm text-[#475569] mb-4">{{ scoreLabel }}</p>
          <div class="flex justify-center gap-1 mb-6">
            <span v-for="star in 3" :key="star" class="text-2xl">{{ score >= star * (topicQuestions.length * 10 / 3) ? '⭐' : '☆' }}</span>
          </div>
          <div class="grid grid-cols-3 gap-3 mb-6">
            <div class="p-3 bg-[#f8fafc] rounded-xl text-center">
              <div class="text-xl font-serif text-[#16a34a]">{{ Math.floor(score / 10) }}</div>
              <div class="text-xs text-[#94a3b8]">Correct</div>
            </div>
            <div class="p-3 bg-[#f8fafc] rounded-xl text-center">
              <div class="text-xl font-serif text-[#dc2626]">{{ topicQuestions.length - Math.floor(score / 10) }}</div>
              <div class="text-xs text-[#94a3b8]">Wrong</div>
            </div>
            <div class="p-3 bg-[#f8fafc] rounded-xl text-center">
              <div class="text-xl font-serif" :style="{ color: topic.color }">{{ Math.round(score / (topicQuestions.length * 10) * 100) }}%</div>
              <div class="text-xs text-[#94a3b8]">Score</div>
            </div>
          </div>

          <!-- Badge earned -->
          <div v-if="score >= topicQuestions.length * 7" class="border-2 rounded-2xl p-4 mb-6"
            :style="{ borderColor: topicId === 'final-assessment' ? '#7c3aed' : '#d97706' }">
            <div class="w-14 h-14 rounded-full flex items-center justify-center mx-auto mb-2"
              :style="{ backgroundColor: topicId === 'final-assessment' ? '#ede9fe' : '#fef3c7' }">
              <Trophy :size="28" :style="{ color: topicId === 'final-assessment' ? '#7c3aed' : '#d97706' }" />
            </div>
            <p class="font-semibold text-[#0f172a]">{{ topicId === 'final-assessment' ? 'Phishing Detective' : 'Security Aware' }}</p>
            <p class="text-xs text-[#475569]">{{ topicId === 'final-assessment' ? 'Master badge earned! You completed the full training programme.' : 'Badge earned for completing this topic!' }}</p>
          </div>

          <div class="flex gap-3">
            <button @click="router.back()"
              class="flex-1 py-3 rounded-xl border-2 border-[#e2e8f0] text-sm font-semibold text-[#475569] hover:bg-[#f8fafc]">
              ← Back to Topics
            </button>
            <button @click="router.push('/leaderboard')"
              class="flex-1 py-3 rounded-xl text-white text-sm font-semibold"
              :style="{ backgroundColor: topic.color }">
              View Leaderboard
            </button>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ArrowLeft, Brain, Trophy, AlertTriangle, Lightbulb, Check, BookOpen, Mail, Link, Eye, Shield, Award } from '@lucide/vue'
import confetti from 'canvas-confetti'
import axios from 'axios'

const router = useRouter()
const route = useRoute()
const phishToken = route.query.token as string || ''
const topicId = route.params.id as string
const BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080'

const phase = ref<'lesson' | 'quiz' | 'complete'>('lesson')
const currentQuestion = ref(0)
const selectedAnswer = ref<number | null>(null)
const score = ref(0)
const answered = ref(false)
const answers = ref<Record<number, number>>({})

const allTopics: Record<string, any> = {
  'final-assessment': {
    id: 'final-assessment',
    title: 'Final Assessment',
    desc: 'Comprehensive test across all 5 phishing awareness topics',
    duration: '10 min', questions: 15, icon: Trophy, color: '#7c3aed',
    content: [
      {
        title: 'Final Assessment — Are You Ready?',
        body: 'This assessment covers all 5 topics you have studied: Email Red Flags, Suspicious Links, Domain Spoofing, Social Engineering, and Credential Safety. You will face 15 scenario-based questions drawn from all topics.',
        tip: 'Take your time on each question. Read all options carefully before selecting your answer.',
        checklist: [
          '15 questions across all 5 topics',
          'Real-world scenario-based questions',
          'Score 70% or above to earn the Phishing Detective badge',
          'You can retake as many times as you need',
        ],
      },
    ],
    questions: [
      { level: 1, levelName: 'Email Red Flags', question: 'An email from "support@paypa1.com" asks you to verify your account. What is the red flag?', options: ['The email asks you to verify', 'The domain uses "1" instead of "l" — it is a lookalike domain', 'The email is from support', 'The email has no attachment'], correct: 1, explanation: 'paypa1.com uses the number 1 instead of the letter l to impersonate paypal.com — a classic typosquatting attack.' },
      { level: 1, levelName: 'Email Red Flags', question: 'Which phrase in an email should immediately raise suspicion?', options: ['Thank you for your purchase', 'Your account will be locked in 24 hours unless you act now', 'Here is your monthly newsletter', 'Your order has shipped'], correct: 1, explanation: 'Urgency and threat of account loss are the primary psychological tactics used in phishing emails.' },
      { level: 1, levelName: 'Suspicious Links', question: 'Before clicking a link in an email, what should you do first?', options: ['Click it to see where it goes', 'Hover over it to see the real destination URL', 'Copy and paste it', 'Check the email subject'], correct: 1, explanation: 'Hovering reveals the actual URL in the browser status bar without triggering the link.' },
      { level: 2, levelName: 'Suspicious Links', question: 'A link shows "https://microsoft.com.login-verify.ru/account". What is the real domain?', options: ['microsoft.com', 'login-verify.ru', 'account', 'https'], correct: 1, explanation: 'The real domain is always the last part before the first single slash — login-verify.ru. microsoft.com here is just a subdomain of the attacker\'s site.' },
      { level: 2, levelName: 'Domain Spoofing', question: 'What technique uses "rn" to look like "m" in a domain name?', options: ['Typosquatting', 'Homograph attack', 'Subdomain spoofing', 'URL shortening'], correct: 1, explanation: 'Homograph attacks use visually similar characters — "rn" next to each other looks like "m" at a quick glance.' },
      { level: 2, levelName: 'Domain Spoofing', question: 'An email displays "PayPal Support" as the sender name. How do you verify the real sender?', options: ['Trust the display name', 'Look at the email subject', 'Click the sender name to reveal the actual email address', 'Check the email signature'], correct: 2, explanation: 'Display names can be set to anything. Always click the sender name to reveal the actual email address behind it.' },
      { level: 2, levelName: 'Social Engineering', question: 'Someone calls claiming to be your IT department and asks for your password. You should:', options: ['Give it — they are IT', 'Give only your username', 'Refuse — no legitimate IT team needs your password', 'Ask them to hold while you think'], correct: 2, explanation: 'No legitimate IT team, bank, or company ever needs your password. This is a classic pretexting attack.' },
      { level: 2, levelName: 'Social Engineering', question: 'What is spear phishing?', options: ['Sending phishing emails to thousands of people', 'A targeted attack using personal information researched about the specific victim', 'Phishing via phone calls', 'Phishing using social media only'], correct: 1, explanation: 'Spear phishing is highly targeted — attackers research the victim to craft convincing personalised messages.' },
      { level: 2, levelName: 'Credential Safety', question: 'Why is reusing the same password across multiple sites dangerous?', options: ['It makes passwords easier to remember', 'If one site is breached, all accounts with that password are exposed', 'Reusing passwords makes them stronger', 'It is only dangerous on banking sites'], correct: 1, explanation: 'Credential stuffing attacks use stolen passwords from one breach to try logging into other sites.' },
      { level: 3, levelName: 'Email Red Flags', question: 'You receive an unexpected salary PDF from HR asking you to log in to view it. What do you do?', options: ['Open the PDF immediately', 'Log in as requested', 'Contact HR directly via a known number to verify', 'Forward it to colleagues to check'], correct: 2, explanation: 'Unexpected attachments requiring login are a classic phishing vector. Always verify through a known, trusted channel.' },
      { level: 3, levelName: 'Suspicious Links', question: 'Does HTTPS mean a website is safe?', options: ['Yes, always', 'Yes, if it has a padlock', 'No — HTTPS only encrypts the connection, not that the site is legitimate', 'Only on .com domains'], correct: 2, explanation: 'Phishing sites routinely use HTTPS. It only means data is encrypted in transit — not that the site is trustworthy.' },
      { level: 3, levelName: 'Domain Spoofing', question: 'An attacker registers "bankofamerica-secure.net". This is called:', options: ['Homograph attack', 'Typosquatting', 'Subdomain spoofing', 'Adding words to create a lookalike domain'], correct: 3, explanation: 'Adding words like "secure" or "login" to a brand name is a common domain spoofing technique.' },
      { level: 3, levelName: 'Social Engineering', question: 'Your CEO emails urgently asking for a wire transfer to a new vendor before end of day. Best action?', options: ['Process it immediately — it is the CEO', 'Ignore it', 'Call the CEO on a known number to verify before acting', 'Reply to the email asking for details'], correct: 2, explanation: 'CEO fraud (whaling) is common. Always verify unusual financial requests through a second channel — a phone call.' },
      { level: 3, levelName: 'Credential Safety', question: 'You just submitted your password on a fake site. What is your FIRST action?', options: ['Wait to see if anything bad happens', 'Tell a colleague', 'Immediately change your password on the real website', 'Delete the phishing email'], correct: 2, explanation: 'Attackers access accounts within minutes. Changing your password immediately is the most critical first step.' },
      { level: 3, levelName: 'All Topics', question: 'Which combination best defends against phishing attacks?', options: ['Strong password only', 'Antivirus software only', 'MFA + unique passwords + phishing awareness training', 'Firewall + VPN'], correct: 2, explanation: 'The strongest defence combines a strong unique password, MFA, and the ability to recognise phishing attempts.' },
    ],
  },
  'email-red-flags': {
    id: 'email-red-flags',
    title: 'Spotting Email Red Flags',
    desc: 'Learn to identify suspicious sender addresses, urgency language, and unexpected requests',
    duration: '4 min read', questions: 4, icon: Mail, color: '#2563eb',
    content: [
      {
        title: 'What is a Phishing Email?',
        body: 'A phishing email is a fraudulent message designed to trick you into revealing sensitive information — like passwords, credit card numbers, or personal data. Attackers disguise themselves as trusted organisations such as your bank, employer, or popular services like Google or WhatsApp.',
        example: 'You receive an email from "security@gooogle-support.com" saying your Gmail account has been compromised and you must verify immediately.',
        warning: 'Phishing is responsible for over 90% of all data breaches worldwide. One click can compromise your entire organisation.',
      },
      {
        title: 'Suspicious Sender Addresses',
        body: 'The sender\'s email address is the most reliable red flag. Attackers use domains that look almost identical to real ones but with subtle differences — extra letters, wrong extensions, or hyphens added.',
        comparison: {
          good: ['support@paypal.com', 'no-reply@google.com', 'alerts@amazon.com'],
          bad: ['support@paypa1.com', 'no-reply@google-security.net', 'alerts@amazon-verify.com'],
        },
        tip: 'Always hover over or click the sender name to reveal the full email address before trusting any email.',
      },
      {
        title: 'Urgency & Fear Tactics',
        body: 'Phishing emails create panic to stop you from thinking clearly. Common tactics include threatening account closure, legal action, missed payments, or saying you have "24 hours" to act.',
        checklist: [
          '"Your account will be permanently deleted in 24 hours"',
          '"Immediate action required — unusual sign-in detected"',
          '"Your payment has failed — update your details now"',
          '"You have been selected for a refund — claim immediately"',
        ],
        warning: 'Legitimate companies never threaten you via email or demand immediate action on sensitive matters.',
      },
      {
        title: 'Unexpected Attachments & Requests',
        body: 'If you were not expecting a document, invoice, or link — be suspicious. Attackers send fake invoices, HR documents, or delivery notifications to get you to open malicious files or enter credentials.',
        example: 'You receive a "payslip" PDF from HR but it asks you to log in with your company credentials to view it.',
        tip: 'When in doubt, contact the sender directly through a known phone number or official website — never reply to the suspicious email.',
      },
    ],
    questions: [
      { level: 1, levelName: 'Basics', question: 'Which email address is most likely from a phisher?', options: ['support@paypal.com', 'support@paypa1-secure.com', 'noreply@paypal.com', 'help@paypal.com'], correct: 1, explanation: '"paypa1-secure.com" is a lookalike domain — the "l" is replaced with "1". Always examine the full domain carefully.' },
      { level: 1, levelName: 'Basics', question: 'A phishing email typically tries to create a sense of:', options: ['Excitement', 'Urgency or fear', 'Curiosity about discounts', 'Trust through long explanations'], correct: 1, explanation: 'Urgency and fear are the primary psychological tools phishers use to stop you from thinking critically before acting.' },
      { level: 2, levelName: 'Intermediate', question: 'You receive an unexpected invoice PDF from "accounts@company-hr.net". What should you do?', options: ['Open it immediately', 'Forward it to colleagues', 'Contact the real sender via a known number to verify', 'Reply asking if it is legitimate'], correct: 2, explanation: 'Never open unexpected attachments. Contact the sender through a verified channel — not by replying to the email.' },
      { level: 3, levelName: 'Advanced', question: 'Which of these is NOT a typical red flag in phishing emails?', options: ['Generic greeting like "Dear Customer"', 'Request to click a link urgently', 'Email from your manager\'s exact company address', 'Threat of account suspension'], correct: 2, explanation: 'An email from the exact correct company address is not a red flag. However, even this can be spoofed in advanced attacks — always check context.' },
    ],
  },

  'suspicious-links': {
    id: 'suspicious-links',
    title: 'Identifying Suspicious Links',
    desc: 'How to hover-check URLs, spot lookalike domains, and avoid malicious redirects',
    duration: '4 min read', questions: 4, icon: Link, color: '#d97706',
    content: [
      {
        title: 'Why Links Are Dangerous',
        body: 'A hyperlink can display friendly text like "Click here to reset your password" while actually pointing to a completely different malicious website. Clicking such a link can lead to credential theft, malware installation, or session hijacking — all without you realising.',
        example: 'A button labelled "Verify Your PayPal Account" links to http://paypal-secure-login.ru/verify instead of paypal.com.',
        warning: 'Never trust the visible text of a link. Always check where it actually goes before clicking.',
      },
      {
        title: 'How to Check a Link Before Clicking',
        body: 'On desktop, hover your mouse over a link and look at the bottom of your browser — the actual URL will appear. On mobile, press and hold the link to see a preview of the destination URL.',
        checklist: [
          'Hover over the link — does the URL match the expected site?',
          'Look for HTTPS — but note: HTTPS does NOT mean the site is safe, only that it is encrypted',
          'Check the domain name carefully for typos or extra words',
          'Be suspicious of URL shorteners (bit.ly, tinyurl) in unexpected emails',
          'Look for redirect patterns like "google.com/url?q=evil-site.com"',
        ],
        tip: 'Use a URL scanner like virustotal.com to check suspicious links before visiting them.',
      },
      {
        title: 'Lookalike Domains',
        body: 'Attackers register domains that look almost identical to real ones. They use techniques like homograph attacks (replacing letters with similar-looking characters), adding words, or using wrong extensions.',
        comparison: {
          good: ['paypal.com', 'amazon.com', 'microsoft.com', 'bankofamerica.com'],
          bad: ['paypa1.com', 'amazon-orders.com', 'micros0ft.com', 'bankofamerica-secure.net'],
        },
      },
      {
        title: 'Malicious Redirects',
        body: 'Some phishing links first go through a legitimate-looking URL before redirecting you to a malicious site. This is done to bypass email filters. The initial URL may even contain the word "google" or "microsoft" to appear trustworthy.',
        example: 'A link goes to: https://docs.google.com/forms/redirect?url=http://steal-your-password.com — it uses Google\'s domain but redirects elsewhere.',
        warning: 'Watch the browser address bar after clicking — if the URL changes to something unexpected, leave immediately.',
      },
    ],
    questions: [
      { level: 1, levelName: 'Basics', question: 'How can you check where a link goes before clicking it on a desktop?', options: ['Click it and see', 'Hover over it and read the URL at the bottom of the browser', 'Copy the link text', 'Check the email subject'], correct: 1, explanation: 'Hovering shows the actual destination URL in the browser status bar without triggering the link.' },
      { level: 2, levelName: 'Intermediate', question: 'Which URL is most suspicious?', options: ['https://paypal.com/login', 'https://paypal-secure-login.net/verify', 'https://www.paypal.com/account', 'https://paypal.com/help'], correct: 1, explanation: '"paypal-secure-login.net" is not PayPal\'s domain. The real domain is always "paypal.com" — anything added around it is a red flag.' },
      { level: 2, levelName: 'Intermediate', question: 'Does HTTPS in a URL mean a website is safe to use?', options: ['Yes, always', 'No — HTTPS only means the connection is encrypted, not that the site is legitimate', 'Only on .com domains', 'Yes, if it also has a padlock'], correct: 1, explanation: 'HTTPS only encrypts the connection. Phishing sites can and do use HTTPS. Always verify the domain itself.' },
      { level: 3, levelName: 'Advanced', question: 'What is a homograph attack?', options: ['Sending the same email to many people', 'Using look-alike characters in domain names (e.g. "rn" instead of "m")', 'Copying a company\'s logo', 'Redirecting through Google'], correct: 1, explanation: 'Homograph attacks replace letters with visually similar characters — like using "rnicrosoft.com" (rn) instead of "microsoft.com" (m).' },
    ],
  },

  'domain-spoofing': {
    id: 'domain-spoofing',
    title: 'Domain Spoofing & Lookalikes',
    desc: 'Understand how attackers create convincing fake domains',
    duration: '4 min read', questions: 4, icon: Eye, color: '#dc2626',
    content: [
      {
        title: 'What is Domain Spoofing?',
        body: 'Domain spoofing is when an attacker creates or uses a domain name that closely resembles a legitimate organisation\'s domain. The goal is to make emails or websites appear to come from a trusted source when they do not.',
        example: 'An attacker registers "arnazon.com" (using "rn" which looks like "m") to impersonate Amazon and send fake order confirmation emails.',
        warning: 'Even security-savvy users can be fooled by domain spoofing — especially in small mobile screens where the full URL is hidden.',
      },
      {
        title: 'Common Spoofing Techniques',
        body: 'Attackers have many tricks for creating convincing fake domains. Understanding these patterns helps you spot them faster.',
        checklist: [
          'Typosquatting: misspelling the domain (googIe.com with a capital i instead of L)',
          'Adding words: secure-paypal.com, paypal-support.com, amazon-helpdesk.com',
          'Wrong TLD: paypal.net, google.info, microsoft.org instead of .com',
          'Subdomain tricks: paypal.com.evil-site.ru (the real domain is evil-site.ru)',
          'Character substitution: g00gle.com, micros0ft.com, arnazon.com',
        ],
      },
      {
        title: 'Email Header Spoofing',
        body: 'Beyond domain names, attackers can spoof the "From" display name so an email appears to come from "PayPal Support" even if the actual sending address is completely different. Most email clients show the display name prominently and hide the actual address.',
        comparison: {
          good: ['Display: PayPal Support | Address: service@paypal.com', 'Display: Google | Address: no-reply@google.com'],
          bad: ['Display: PayPal Support | Address: verify@paypal-alerts.net', 'Display: Google Security | Address: admin@g00gle-alert.com'],
        },
        tip: 'Always click on the sender name in your email client to reveal the actual email address behind the display name.',
      },
      {
        title: 'How to Verify a Real Domain',
        body: 'When unsure, do not use the link in the email. Instead, type the company\'s known address directly into your browser, search for it on Google, or call their official support number.',
        checklist: [
          'Type the website address directly in the browser bar',
          'Search the company on Google and use the official result',
          'Check the domain registration using whois.domaintools.com',
          'When in doubt, call the company on their official number',
          'Never trust a link or phone number provided in a suspicious email',
        ],
        warning: 'Once you enter credentials on a fake site, your account is compromised even if you realise the mistake immediately.',
      },
    ],
    questions: [
      { level: 1, levelName: 'Basics', question: 'What does "arnazon.com" demonstrate?', options: ['A legitimate Amazon domain', 'A homograph spoofing attack using "rn" to mimic "m"', 'An Amazon subdomain', 'A safe redirect'], correct: 1, explanation: '"rn" next to each other looks like "m" — so "arnazon" is read as "amazon" at a glance. This is a classic homograph attack.' },
      { level: 2, levelName: 'Intermediate', question: 'An email says it is from "PayPal Support" but you want to verify. What do you do?', options: ['Trust the display name', 'Click the link to check', 'Click the sender name to see the actual email address', 'Forward it to a friend'], correct: 2, explanation: 'Always click the display name to reveal the actual sending address. Display names can say anything — only the address matters.' },
      { level: 2, levelName: 'Intermediate', question: 'Which of these is a subdomain spoofing attack?', options: ['paypal.com/login', 'login.paypal.com', 'paypal.com.verify-account.ru', 'secure.paypal.com'], correct: 2, explanation: 'In "paypal.com.verify-account.ru", the actual domain is "verify-account.ru". The "paypal.com" part is just a subdomain of the attackers site.' },
      { level: 3, levelName: 'Advanced', question: 'What is typosquatting?', options: ['Squatting on someone\'s trademark', 'Registering misspelled versions of popular domains to catch typing mistakes', 'Sending emails with typos', 'Using the wrong email template'], correct: 1, explanation: 'Typosquatting registers domains like "gooogle.com" or "faceboook.com" to catch users who mistype URLs and redirect them to phishing sites.' },
    ],
  },

  'social-engineering': {
    id: 'social-engineering',
    title: 'Social Engineering Tactics',
    desc: 'Recognise manipulation techniques: authority, urgency, fear, and pretexting',
    duration: '4 min read', questions: 4, icon: Shield, color: '#7c3aed',
    content: [
      {
        title: 'What is Social Engineering?',
        body: 'Social engineering is the art of manipulating people into giving up confidential information or performing actions they normally would not. Instead of hacking systems, attackers hack human psychology — exploiting trust, fear, curiosity, and authority.',
        example: 'An attacker calls pretending to be IT support and says your account has been hacked. They urgently need your password to "fix" it before you lose access.',
        warning: 'No IT department, bank, or government agency will ever ask for your password over email, phone, or text.',
      },
      {
        title: 'The 6 Influence Triggers',
        body: 'Attackers exploit well-known psychological principles to bypass your rational thinking. Knowing these triggers helps you recognise when you are being manipulated.',
        checklist: [
          'Authority: "This is the CEO — I need you to transfer funds immediately"',
          'Urgency: "You have 1 hour before your account is permanently deleted"',
          'Fear: "Legal action will be taken if you do not respond now"',
          'Scarcity: "Only 3 spots left — claim your prize immediately"',
          'Social proof: "Your colleagues have already verified their accounts"',
          'Reciprocity: "We gave you a free upgrade — now we need to verify your details"',
        ],
      },
      {
        title: 'Pretexting & Impersonation',
        body: 'Pretexting involves creating a fabricated scenario (a pretext) to extract information. Attackers research their targets on LinkedIn, social media, and company websites to make their story convincing.',
        comparison: {
          good: ['IT asks you to reset via the official IT portal', 'HR sends documents through the official HR system', 'Bank contacts you via the number on your card'],
          bad: ['"IT support" emails asking for your password directly', '"HR" sends urgent salary forms via personal Gmail', '"Bank" calls from unknown number asking for OTP'],
        },
        tip: 'If something feels off, it probably is. Trust your instincts and verify through official channels before taking any action.',
      },
      {
        title: 'Spear Phishing & Whaling',
        body: 'Regular phishing targets many people at once. Spear phishing is highly targeted — attackers research a specific person and craft a personalised message. Whaling targets senior executives (the "big fish") with high-value attacks.',
        example: 'An attacker finds on LinkedIn that you work in finance at Acme Corp. They email you from "CEO@acme-corp.co" (not the real domain) requesting an urgent wire transfer to a new vendor.',
        warning: 'Targeted attacks are harder to detect. Always verify unusual financial or access requests through a second channel — call the person directly.',
      },
    ],
    questions: [
      { level: 1, levelName: 'Basics', question: 'What is social engineering?', options: ['Hacking into computer systems', 'Manipulating people psychologically to give up information', 'Writing malicious code', 'Sending bulk emails'], correct: 1, explanation: 'Social engineering exploits human psychology rather than technical vulnerabilities — it is often easier to trick a person than to hack a system.' },
      { level: 2, levelName: 'Intermediate', question: 'Someone calls claiming to be IT support and asks for your password to fix an issue. What do you do?', options: ['Give it — they are IT', 'Refuse — no legitimate IT staff need your password', 'Give only your username', 'Ask them to email you instead'], correct: 1, explanation: 'Legitimate IT support never needs your password. They can reset it without knowing it. This is a classic pretexting attack.' },
      { level: 2, levelName: 'Intermediate', question: 'What makes spear phishing more dangerous than regular phishing?', options: ['It uses more emails', 'It is personalised and targeted using researched information about the victim', 'It only targets executives', 'It uses phone calls'], correct: 1, explanation: 'Spear phishing uses personal details (name, job title, colleagues, recent activity) to make the attack highly convincing and harder to detect.' },
      { level: 3, levelName: 'Advanced', question: 'Which psychological trigger is used in: "Only 2 hours left before your account is locked"?', options: ['Authority', 'Reciprocity', 'Urgency and fear', 'Social proof'], correct: 2, explanation: 'Time pressure (urgency) combined with account loss (fear) are classic triggers designed to panic you into acting without thinking.' },
    ],
  },

  'credential-safety': {
    id: 'credential-safety',
    title: 'Protecting Your Credentials',
    desc: 'Best practices for passwords, MFA, and what to do if you\'ve been phished',
    duration: '4 min read', questions: 4, icon: Award, color: '#16a34a',
    content: [
      {
        title: 'Why Credentials Are the Primary Target',
        body: 'Your username and password are the keys to your digital life. With your email credentials alone, an attacker can reset passwords for every other account you have — banking, social media, work systems, and more. This is why credential theft is the most common goal in phishing attacks.',
        example: 'An attacker gets your email password. They immediately use "Forgot Password" on your bank\'s website, receive the reset link in your email, and access your bank account — all within minutes.',
        warning: 'Once an attacker has your email password, they can own every account connected to that email address.',
      },
      {
        title: 'Strong Password Practices',
        body: 'Weak or reused passwords are a major vulnerability. If you use the same password on multiple sites and one is breached, all your accounts are at risk — a technique called credential stuffing.',
        checklist: [
          'Use at least 12 characters with a mix of letters, numbers, and symbols',
          'Never reuse passwords across different sites',
          'Use a password manager (Bitwarden, 1Password) to generate and store unique passwords',
          'Avoid obvious patterns: name+year, company+123, qwerty, password1',
          'Change passwords immediately if you suspect a breach',
        ],
        tip: 'A passphrase like "Coffee-Table-River-42!" is long, memorable, and far stronger than "P@ssw0rd".',
      },
      {
        title: 'Multi-Factor Authentication (MFA)',
        body: 'MFA adds a second layer of protection. Even if an attacker steals your password, they still cannot log in without the second factor — usually a code from an app on your phone.',
        comparison: {
          good: ['Authenticator app (Google Authenticator, Authy) — strongest', 'SMS one-time code — better than nothing', 'Hardware key (YubiKey) — most secure'],
          bad: ['No MFA — password alone is one point of failure', 'Security questions — easily guessed from social media', 'Email OTP — compromised if email is breached'],
        },
        tip: 'Enable MFA on every account that offers it — especially email, banking, and work accounts.',
      },
      {
        title: 'What to Do If You\'ve Been Phished',
        body: 'If you realise you have submitted credentials to a fake site, act immediately. Speed is critical — attackers often access accounts within minutes of receiving stolen credentials.',
        checklist: [
          '1. Change your password immediately on the real website',
          '2. Enable MFA if not already active',
          '3. Check for any unauthorised activity or sent emails',
          '4. Notify your IT/security team if it is a work account',
          '5. Check other accounts that use the same password and change those too',
          '6. Report the phishing site to your email provider and the organisation being impersonated',
        ],
        warning: 'Do not wait to see if anything bad happens. Assume your account is compromised and act immediately.',
      },
    ],
    questions: [
      { level: 1, levelName: 'Basics', question: 'What is credential stuffing?', options: ['Creating very long passwords', 'Using stolen username/password pairs from one breach to access other sites', 'Encrypting your credentials', 'Storing passwords in a browser'], correct: 1, explanation: 'Credential stuffing exploits password reuse — if you use the same password on multiple sites, one breach exposes them all.' },
      { level: 1, levelName: 'Basics', question: 'Which is the strongest form of MFA?', options: ['SMS one-time code', 'Security question', 'Authenticator app or hardware key', 'Email OTP'], correct: 2, explanation: 'Authenticator apps and hardware keys are not tied to your phone number or email — they cannot be intercepted like SMS codes.' },
      { level: 2, levelName: 'Intermediate', question: 'You realise you just submitted your work email password on a fake site. What is your FIRST action?', options: ['Wait to see if anything happens', 'Immediately change your password on the real site', 'Delete the phishing email', 'Tell a colleague'], correct: 1, explanation: 'Speed is critical. Change your password immediately — attackers can access accounts within minutes of receiving credentials.' },
      { level: 3, levelName: 'Advanced', question: 'Which password is strongest?', options: ['P@ssw0rd!', 'Company2024', 'xK9#mP2$vQ!nL7', 'JohnSmith1990!'], correct: 2, explanation: 'Random character combinations with mixed types are strongest. Predictable patterns like names, years, and common substitutions (@ for a) are easily cracked.' },
    ],
  },
}

const topic = computed(() => allTopics[topicId] || allTopics['email-red-flags'])
const topicQuestions = computed(() => topic.value.questions)
const currentQ = computed(() => topicQuestions.value[currentQuestion.value])

const scoreLabel = computed(() => {
  const pct = score.value / (topicQuestions.value.length * 10) * 100
  if (pct >= 80) return 'Excellent! You have a strong understanding of this topic.'
  if (pct >= 60) return 'Good effort! Review the sections you missed.'
  return 'Keep practising — revisit the lesson and try again.'
})

async function startQuiz() {
  phase.value = 'quiz'
  if (phishToken) {
    try {
      await axios.post(`${BASE_URL}/api/user/training/token/${phishToken}/start`, {
        topicId: topicId,
        topicTitle: topic.value.title
      })
    } catch (e) {
      console.warn('Could not start training on backend:', e)
    }
  }
}

function handleSubmitAnswer() {
  if (selectedAnswer.value === null) return
  answers.value[currentQuestion.value] = selectedAnswer.value
  if (selectedAnswer.value === currentQ.value.correct) score.value += 10
  answered.value = true
}

async function handleNextQuestion() {
  if (currentQuestion.value < topicQuestions.value.length - 1) {
    currentQuestion.value++
    selectedAnswer.value = null
    answered.value = false
  } else {
    phase.value = 'complete'
    confetti({ particleCount: 120, spread: 70, origin: { y: 0.6 } })

    // Save progress to localStorage so Training.vue can detect completion
    const PROGRESS_KEY = 'phishguard_training_progress'
    const existing = JSON.parse(localStorage.getItem(PROGRESS_KEY) || '[]')
    const finalPct = Math.round(score.value / (topicQuestions.value.length * 10) * 100)
    const idx = existing.findIndex((p: any) => p.topicId === topicId)
    const entry = { topicId, topicTitle: topic.value.title, status: 'completed', score: finalPct, completedAt: new Date().toISOString(), answers: answers.value }
    if (idx >= 0) existing[idx] = entry
    else existing.push(entry)
    localStorage.setItem(PROGRESS_KEY, JSON.stringify(existing))

    if (phishToken) {
      try {
        await axios.post(`${BASE_URL}/api/user/training/token/${phishToken}/submit`, {
          topicId: topicId,
          answers: answers.value
        })
      } catch (e) {
        console.warn('Could not submit training to backend:', e)
      }
    }
  }
}
</script>
