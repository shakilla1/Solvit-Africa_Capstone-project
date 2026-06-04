import { Mail, Link, Eye, Brain, Award } from '@lucide/vue'

export const topics = [
  { id: 'email-red-flags', title: 'Spotting Email Red Flags', desc: 'Learn to identify suspicious sender addresses, urgency language, and unexpected requests', duration: '3 min read', questions: 4, icon: Mail, color: '#2563eb' },
  { id: 'suspicious-links', title: 'Identifying Suspicious Links', desc: 'How to hover-check URLs, spot lookalike domains, and avoid malicious redirects', duration: '3 min read', questions: 4, icon: Link, color: '#d97706' },
  { id: 'domain-spoofing', title: 'Domain Spoofing & Lookalikes', desc: 'Understand how attackers create convincing fake domains like "g00gle.com"', duration: '3 min read', questions: 4, icon: Eye, color: '#dc2626' },
  { id: 'social-engineering', title: 'Social Engineering Tactics', desc: 'Recognise manipulation techniques: authority, urgency, fear, and pretexting', duration: '3 min read', questions: 4, icon: Brain, color: '#7c3aed' },
  { id: 'credential-safety', title: 'Protecting Your Credentials', desc: "Best practices for passwords, MFA, and what to do if you think you've been phished", duration: '3 min read', questions: 4, icon: Award, color: '#16a34a' },
]
