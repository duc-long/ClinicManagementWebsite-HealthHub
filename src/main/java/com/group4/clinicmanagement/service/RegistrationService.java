package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.dto.registerpatient.PatientRegisterDTO;
import com.group4.clinicmanagement.entity.PasswordResetToken;
import com.group4.clinicmanagement.entity.Patient;
import com.group4.clinicmanagement.entity.User;
import com.group4.clinicmanagement.enums.Gender;
import com.group4.clinicmanagement.enums.UserStatus;
import com.group4.clinicmanagement.repository.PasswordResetTokenRepository;
import com.group4.clinicmanagement.repository.PatientRepository;
import com.group4.clinicmanagement.repository.RoleRepository;
import com.group4.clinicmanagement.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
@Service
public class RegistrationService {
    private final UserRepository userRepository;

    private final PasswordResetTokenRepository tokenRepository;

    private final EmailService emailService;

    private final PasswordEncoder passwordEncoder;

    private final RoleRepository roleRepository;

    private final PatientRepository patientRepository;

    private static final int MAX_ATTEMPTS = 5;

    public  RegistrationService(UserRepository userRepository,
                                PasswordResetTokenRepository tokenRepository,
                                EmailService emailService,
                                PasswordEncoder passwordEncoder,
                                RoleRepository roleRepository,
                                PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
    }

    @Transactional
    public void createPendingAccount(PatientRegisterDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Username already has an account.");
        }
        Optional<User> existing = userRepository.findByEmail(dto.getEmail());

        if (existing.isPresent()) {
            User user = existing.get();

            // Nếu user tồn tại nhưng chưa kích hoạt, gửi lại OTP
            if (user.getStatus() == UserStatus.INACTIVE) {
                resendOtp(user.getEmail());
                throw new RuntimeException("This email is already registered but not verified. OTP has been resent.");
            }

            // Nếu user đã kích hoạt -> chặn đăng ký trùng
            throw new RuntimeException("This email has already been registered and activated.");
        }

        // ➕ Tạo user mới (inactive)
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setGender(dto.getGender() != null ? dto.getGender() : Gender.UNKNOWN);
        user.setStatus(UserStatus.INACTIVE);
        user.setPasswordHash("PENDING");
        user.setRole(roleRepository.findByName("Patient")
                .orElseThrow(() -> new RuntimeException("Patient role not found.")));
        userRepository.save(user);

        // ➕ Tạo patient record
        Patient patient = new Patient();
        patient.setUser(user);
        patient.setAddress(dto.getAddress());
        patient.setDateOfBirth(dto.getDateOfBirth());
        patientRepository.save(patient);

        // ➕ Tạo và gửi OTP
        createAndSendOtp(dto.getEmail());
    }

//    @Transactional
//    public boolean verifyOtp(String email, String otp) {
//        PasswordResetToken token = tokenRepository.findByEmail(email)
//                .orElseThrow(() -> new RuntimeException("OTP not found"));
//
//        if (token.getAttempts() >= MAX_ATTEMPTS) {
//            tokenRepository.deleteByEmail(email);
//            throw new RuntimeException("Too many incorrect attempts. Please request a new OTP.");
//        }
//
//        if (token.isExpired()) {
//            tokenRepository.delete(token);
//            throw new RuntimeException("OTP expired");
//        }
//        // Kiểm tra OTP
//        if (token.getOtpCode().equals(otp)) {
//            // Reset số lần thử nếu OTP đúng
//            token.setAttempts(0);
//            tokenRepository.saveAndFlush(token);
//            tokenRepository.flush();
//            return true;
//        } else {
//            // Tăng số lần thử nếu OTP sai
//            token.setAttempts(token.getAttempts() + 1);
//            tokenRepository.saveAndFlush(token);
//            tokenRepository.flush();
//
//            // Nếu đã vượt quá số lần thử, xóa OTP
//            if (token.getAttempts() >= MAX_ATTEMPTS) {
//                System.out.println(token.getAttempts());
//                System.out.println(email);
//                tokenRepository.deleteByEmail(email);  // Xóa OTP nếu sai quá 5 lần
//                throw new RuntimeException("OTP has been deleted due to too many incorrect attempts.");
//            }
//            return false;
//        }
//    }

    @Transactional
    public int verifyOtp(String email, String otp) {
        PasswordResetToken token = tokenRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("OTP not found")); // Lỗi này OK vì nó là trường hợp ngoại lệ

        if (token.getAttempts() >= MAX_ATTEMPTS) {
            tokenRepository.deleteByEmail(email);
            // THAY ĐỔI 1: Không ném lỗi, chỉ trả về mã -1
            return -1;
        }

        if (token.isExpired()) {
            tokenRepository.deleteByEmail(email);
            return -2;
//            throw new RuntimeException("OTP expired"); // Lỗi này OK
        }

        // Kiểm tra OTP
        if (token.getOtpCode().equals(otp)) {
            // Reset số lần thử nếu OTP đúng
            token.setAttempts(0);
            tokenRepository.saveAndFlush(token);
            tokenRepository.flush();
            // THAY ĐỔI 2: Trả về mã 1
            return 1;
        } else {
            // Tăng số lần thử nếu OTP sai
            token.setAttempts(token.getAttempts() + 1);
            tokenRepository.saveAndFlush(token);
            tokenRepository.flush();

            // Nếu đã vượt quá số lần thử, xóa OTP
            if (token.getAttempts() >= MAX_ATTEMPTS) {
                System.out.println(token.getAttempts());
                System.out.println(email);
                tokenRepository.deleteByEmail(email);  // Xóa OTP nếu sai quá 5 lần

                // THAY ĐỔI 3: Không ném lỗi, chỉ trả về mã -1
                return -1;
            }
            // THAY ĐỔI 4: Trả về mã 0
            return 0;
        }
    }

    @Transactional
    public void createAccountAfterOtp(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found. Please register again."));

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        tokenRepository.deleteByEmail(email);
    }

    @Transactional
    public void resendOtp(String email) {
        tokenRepository.deleteByEmail(email);
        createAndSendOtp(email);
    }


    @Transactional
    public void createAndSendOtp(String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not found"));
        tokenRepository.deleteByEmail(email);

        String otp = String.format("%06d", new Random().nextInt(999999));

        PasswordResetToken token = new PasswordResetToken();
        token.setEmail(email);
        token.setOtpCode(otp);
        token.setToken(UUID.randomUUID().toString());
        token.setExpirationDate(LocalDateTime.now().plusMinutes(15));
        token.setAttempts(0);  // Khởi tạo số lần thử là 0
        tokenRepository.save(token);

        String subject = "HealthHub - Verify your registration";
        String body = """
                Hello %s,
                
                Thank you for registering at HealthHub!
                
                👉 Your OTP Code: %s
                (Valid for 15 minutes)
                
                Please enter this OTP on the verification page to complete your registration.
                
                Regards,
                HealthHub Support
                """.formatted(user.getFullName(), otp);

        emailService.sendEmail(email, subject, body);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found for email: " + email));
    }
}
