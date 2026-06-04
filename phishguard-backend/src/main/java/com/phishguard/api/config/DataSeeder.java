package com.phishguard.api.config;

import com.phishguard.api.entity.AdminUser;
import com.phishguard.api.entity.EmailTemplate;
import com.phishguard.api.enums.Difficulty;
import com.phishguard.api.enums.Plan;
import com.phishguard.api.enums.Role;
import com.phishguard.api.repository.AdminUserRepository;
import com.phishguard.api.repository.EmailTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final EmailTemplateRepository templateRepo;
    private final AdminUserRepository adminRepo;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Value("${app.admin.fullName}")
    private String adminFullName;

    @Value("${app.admin.organisationName}")
    private String adminOrgName;

    @Override
    public void run(String... args) {
        if (!adminRepo.existsByEmail(adminEmail)) {
            adminRepo.save(AdminUser.builder()
                    .fullName(adminFullName)
                    .organisationName(adminOrgName)
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .role(Role.ROLE_ADMIN)
                    .plan(Plan.ENTERPRISE)
                    .build());
            log.info("Default admin account created: {}", adminEmail);
        }

        if (templateRepo.count() > 0) return; // already seeded

        log.info("Seeding default email templates...");
        templateRepo.saveAll(List.of(
            buildTemplate(
                "IT Password Reset",
                "IT",
                Difficulty.MEDIUM,
                "IT Support Team",
                "it-support@company-helpdesk.com",
                "Action Required: Your Password Expires in 24 Hours",
                "gmail",
                """
                <div style="font-family:Arial,sans-serif;max-width:600px;margin:0 auto;background:#fff;border:1px solid #e0e0e0;border-radius:8px;overflow:hidden">
                  <div style="background:#1a73e8;padding:20px;text-align:center">
                    <h2 style="color:#fff;margin:0">IT Support Portal</h2>
                  </div>
                  <div style="padding:30px">
                    <p style="color:#333">Dear {{NAME}},</p>
                    <p style="color:#333">Our system has detected that your company password will expire in <strong>24 hours</strong>. To avoid being locked out of your account, please reset your password immediately.</p>
                    <div style="background:#fff3cd;border:1px solid #ffc107;border-radius:4px;padding:15px;margin:20px 0">
                      <strong>⚠️ Warning:</strong> Failure to update your password will result in account suspension.
                    </div>
                    <div style="text-align:center;margin:30px 0">
                      <a href="{{TRACKING_URL}}" style="background:#1a73e8;color:#fff;padding:14px 32px;border-radius:4px;text-decoration:none;font-weight:bold;display:inline-block">Reset Password Now</a>
                    </div>
                    <p style="color:#666;font-size:13px">If you did not request this, please contact IT Support immediately at support@company.com</p>
                  </div>
                  <div style="background:#f5f5f5;padding:15px;text-align:center;font-size:12px;color:#999">
                    © 2025 Company IT Support · This is an automated message
                  </div>
                </div>
                """
            ),
            buildTemplate(
                "Bank Security Alert",
                "Financial",
                Difficulty.HARD,
                "SecureBank Security Team",
                "security@securebank-alerts.com",
                "URGENT: Suspicious Activity Detected on Your Account",
                "bank",
                """
                <div style="font-family:Arial,sans-serif;max-width:600px;margin:0 auto;background:#fff;border:1px solid #e0e0e0;border-radius:8px;overflow:hidden">
                  <div style="background:#006400;padding:20px;text-align:center">
                    <h2 style="color:#fff;margin:0">🔒 SecureBank</h2>
                    <p style="color:#90ee90;margin:5px 0 0">Security Alert</p>
                  </div>
                  <div style="padding:30px">
                    <p style="color:#333">Dear Valued Customer,</p>
                    <p style="color:#333">We have detected <strong>unusual login activity</strong> on your account from an unrecognised device in Lagos, Nigeria at 03:42 AM.</p>
                    <div style="background:#fee;border-left:4px solid #c00;padding:15px;margin:20px 0">
                      <strong style="color:#c00">Account Status: AT RISK</strong><br>
                      <span style="color:#666;font-size:13px">Your account has been temporarily limited. Verify your identity to restore full access.</span>
                    </div>
                    <div style="text-align:center;margin:30px 0">
                      <a href="{{TRACKING_URL}}" style="background:#006400;color:#fff;padding:14px 32px;border-radius:4px;text-decoration:none;font-weight:bold;display:inline-block">Verify My Identity</a>
                    </div>
                    <p style="color:#666;font-size:12px">You have <strong>12 hours</strong> to verify before your account is suspended for security reasons.</p>
                  </div>
                </div>
                """
            ),
            buildTemplate(
                "WhatsApp Account Suspended",
                "Social",
                Difficulty.MEDIUM,
                "WhatsApp Support",
                "support@whatsapp-verify.com",
                "Your WhatsApp account has been suspended",
                "whatsapp",
                """
                <div style="font-family:Arial,sans-serif;max-width:600px;margin:0 auto;background:#fff;border:1px solid #e0e0e0;border-radius:8px;overflow:hidden">
                  <div style="background:#25d366;padding:20px;text-align:center">
                    <h2 style="color:#fff;margin:0">WhatsApp</h2>
                  </div>
                  <div style="padding:30px">
                    <p style="color:#333">Hello {{NAME}},</p>
                    <p style="color:#333">Your WhatsApp account has been <strong>temporarily suspended</strong> due to a violation of our Terms of Service.</p>
                    <p style="color:#333">To appeal this decision and restore your account, please verify your identity within <strong>48 hours</strong>.</p>
                    <div style="text-align:center;margin:30px 0">
                      <a href="{{TRACKING_URL}}" style="background:#25d366;color:#fff;padding:14px 32px;border-radius:4px;text-decoration:none;font-weight:bold;display:inline-block">Verify Account</a>
                    </div>
                    <p style="color:#999;font-size:12px">If you do not verify within 48 hours, your account will be permanently deleted.</p>
                  </div>
                </div>
                """
            ),
            buildTemplate(
                "MTN Data Reward",
                "Telecom",
                Difficulty.EASY,
                "MTN Rewards",
                "rewards@mtn-promo.net",
                "Congratulations! You've won 10GB FREE Data 🎉",
                "mtn",
                """
                <div style="font-family:Arial,sans-serif;max-width:600px;margin:0 auto;background:#fff;border:1px solid #e0e0e0;border-radius:8px;overflow:hidden">
                  <div style="background:#ffcb05;padding:20px;text-align:center">
                    <h2 style="color:#000;margin:0">MTN</h2>
                    <p style="color:#000;margin:5px 0 0;font-weight:bold">Everywhere You Go</p>
                  </div>
                  <div style="padding:30px;text-align:center">
                    <div style="font-size:48px">🎉</div>
                    <h2 style="color:#333">Congratulations {{NAME}}!</h2>
                    <p style="color:#333">You have been selected to receive <strong>10GB FREE Data</strong> as part of our loyal customer appreciation programme.</p>
                    <div style="background:#fff9e6;border:2px solid #ffcb05;border-radius:8px;padding:20px;margin:20px 0">
                      <div style="font-size:36px;font-weight:bold;color:#000">10 GB</div>
                      <div style="color:#666">Valid for 30 days</div>
                    </div>
                    <a href="{{TRACKING_URL}}" style="background:#ffcb05;color:#000;padding:14px 32px;border-radius:4px;text-decoration:none;font-weight:bold;display:inline-block">Claim Your Free Data</a>
                    <p style="color:#999;font-size:12px;margin-top:20px">Offer expires in 24 hours. Limited to one claim per customer.</p>
                  </div>
                </div>
                """
            ),
            buildTemplate(
                "Google Account Alert",
                "Tech",
                Difficulty.HARD,
                "Google Security",
                "no-reply@google-security-alert.com",
                "Critical security alert for your Google Account",
                "gmail",
                """
                <div style="font-family:Arial,sans-serif;max-width:600px;margin:0 auto;background:#fff;border:1px solid #e0e0e0;border-radius:8px;overflow:hidden">
                  <div style="padding:20px;border-bottom:1px solid #e0e0e0;display:flex;align-items:center">
                    <span style="font-size:24px;font-weight:bold;color:#4285f4">G</span>
                    <span style="font-size:24px;font-weight:bold;color:#ea4335">o</span>
                    <span style="font-size:24px;font-weight:bold;color:#fbbc05">o</span>
                    <span style="font-size:24px;font-weight:bold;color:#4285f4">g</span>
                    <span style="font-size:24px;font-weight:bold;color:#34a853">l</span>
                    <span style="font-size:24px;font-weight:bold;color:#ea4335">e</span>
                  </div>
                  <div style="padding:30px">
                    <h2 style="color:#202124">Critical security alert</h2>
                    <p style="color:#3c4043">Someone just used your password to try to sign in to your account from a new device.</p>
                    <div style="background:#f8f9fa;border-radius:8px;padding:15px;margin:20px 0">
                      <div style="color:#5f6368;font-size:13px">Device: Windows PC</div>
                      <div style="color:#5f6368;font-size:13px">Location: Johannesburg, South Africa</div>
                      <div style="color:#5f6368;font-size:13px">Time: Just now</div>
                    </div>
                    <p style="color:#3c4043">If this was you, you can ignore this message. If not, we'll help you secure your account.</p>
                    <div style="margin:25px 0">
                      <a href="{{TRACKING_URL}}" style="background:#1a73e8;color:#fff;padding:12px 24px;border-radius:4px;text-decoration:none;font-weight:500;display:inline-block;margin-right:10px">Check activity</a>
                    </div>
                  </div>
                </div>
                """
            ),
            buildTemplate(
                "Job Offer Letter",
                "HR",
                Difficulty.EASY,
                "HR Department - TechCorp",
                "hr@techcorp-careers.net",
                "Congratulations! Job Offer - Software Developer Position",
                "university",
                """
                <div style="font-family:Arial,sans-serif;max-width:600px;margin:0 auto;background:#fff;border:1px solid #e0e0e0;border-radius:8px;overflow:hidden">
                  <div style="background:#1e40af;padding:20px;text-align:center">
                    <h2 style="color:#fff;margin:0">TechCorp International</h2>
                    <p style="color:#93c5fd;margin:5px 0 0">Human Resources Department</p>
                  </div>
                  <div style="padding:30px">
                    <p style="color:#333">Dear {{NAME}},</p>
                    <p style="color:#333">We are pleased to offer you the position of <strong>Software Developer</strong> at TechCorp International. After careful consideration of your application, we believe you are an excellent fit for our team.</p>
                    <div style="background:#eff6ff;border:1px solid #bfdbfe;border-radius:8px;padding:20px;margin:20px 0">
                      <strong>Offer Details:</strong><br>
                      <span style="color:#666">Salary: R45,000/month</span><br>
                      <span style="color:#666">Start Date: 1 August 2025</span><br>
                      <span style="color:#666">Location: Sandton, Johannesburg</span>
                    </div>
                    <p style="color:#333">Please review and accept your offer by clicking the button below. You will need to log in with your email to access the full offer letter and complete onboarding documents.</p>
                    <div style="text-align:center;margin:30px 0">
                      <a href="{{TRACKING_URL}}" style="background:#1e40af;color:#fff;padding:14px 32px;border-radius:4px;text-decoration:none;font-weight:bold;display:inline-block">View & Accept Offer</a>
                    </div>
                    <p style="color:#999;font-size:12px">This offer expires in 72 hours. Please do not share this link.</p>
                  </div>
                </div>
                """
            )
        ));

        log.info("Seeded {} email templates", templateRepo.count());
    }

    private EmailTemplate buildTemplate(String name, String category, Difficulty difficulty,
                                         String senderName, String senderEmail,
                                         String subject, String landingType, String html) {
        return EmailTemplate.builder()
                .name(name).category(category).difficulty(difficulty)
                .senderName(senderName).senderEmail(senderEmail)
                .subject(subject).landingPageType(landingType)
                .htmlBody(html.strip()).isDefault(true)
                .build();
    }
}
