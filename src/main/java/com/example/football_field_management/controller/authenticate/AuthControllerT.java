    package com.example.football_field_management.controller.authenticate;


    import com.example.football_field_management.dto.LoginRequest;
    import com.example.football_field_management.model.Account;
    import com.example.football_field_management.model.PasswordResetToken;
    import com.example.football_field_management.model.Role;
    import com.example.football_field_management.repository.RoleRepository;
    import com.example.football_field_management.service.EmailService;
    import com.example.football_field_management.service.PasswordResetService;
    import com.example.football_field_management.service.admin.users.IAccountService;
    import jakarta.servlet.http.HttpSession;
    import lombok.RequiredArgsConstructor;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.core.context.SecurityContextHolder;
    import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.validation.BindingResult;
    import org.springframework.web.bind.annotation.*;
    import org.springframework.web.servlet.mvc.support.RedirectAttributes;

    import java.time.LocalDateTime;
    import java.util.HashSet;
    import java.util.List;

    @Controller("mvcAuthController")
    @RequiredArgsConstructor
    @RequestMapping("/auth")
    public class AuthControllerT {
        private final IAccountService accountService;
        private final RoleRepository roleRepository;
        private final BCryptPasswordEncoder passwordEncoder;
        private final PasswordResetService passwordResetService;
        private final EmailService emailService;

        @GetMapping("/login")
        public String loginPage(Model model) {
            model.addAttribute("loginRequest", new LoginRequest());
            return "auth/login";
        }

        @GetMapping("/logout")
        public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
            SecurityContextHolder.clearContext();
            session.invalidate();
            redirectAttributes.addFlashAttribute("successMessage", "Bạn đã đăng xuất thành công!");
            return "redirect:/auth/login";
        }


        @GetMapping("/register")
        public String registerPage(Model model) {
            model.addAttribute("account", new Account());
            return "auth/register";
        }

        @PostMapping("/register")
        public String registerAccount(@ModelAttribute("account") Account account,
                                      BindingResult result,
                                      Model model) {
            if (result.hasErrors()) {
                return "auth/register";
            }
            if (!account.getPassword().equals(account.getConfirmPassword())) {
                model.addAttribute("errorMessage", "Mật khẩu xác nhận không khớp!");
                return "auth/register";
            }

            if (accountService.existsByEmail(account.getEmail())) {
                model.addAttribute("errorMessage", "Email này đã tồn tại!");
                return "auth/register";
            }

            Role userRole = roleRepository.findByRoleName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("Role USER không tồn tại"));
            account.setRoles(new HashSet<>(List.of(userRole)));

            // MÃ HÓA MẬT KHẨU
            account.setPassword(passwordEncoder.encode(account.getPassword()));

            accountService.register(account);
            model.addAttribute("successMessage", "Đăng ký thành công, mời bạn đăng nhập!");
            return "redirect:/auth/login";
        }

        @GetMapping("/forgot-password")
        public String forgotPasswordPage(Model model) {
            model.addAttribute("email", "");
            return "auth/forgot-password";
        }

        @PostMapping("/forgot-password")
        public String handleForgotPassword(@RequestParam("email") String email, Model model) {
            Account account = accountService.findByEmail(email);

            // trả về giống Gmail: không báo email đúng hay sai
            if (account == null) {
                model.addAttribute("successMessage", "Nếu email hợp lệ, bạn sẽ nhận được mail chứa link reset mật khẩu!");
                return "auth/forgot-password";
            }

            // Lấy token theo account
            PasswordResetToken existingToken = passwordResetService.getTokenByAccount(account);

            // Nếu token tồn tại → kiểm tra thời gian gửi gần nhất
            if (existingToken != null && existingToken.getLastPasswordResetEmailSentAt() != null) {
                LocalDateTime lastSent = existingToken.getLastPasswordResetEmailSentAt();
                LocalDateTime now = LocalDateTime.now();

                // Giới hạn 1 phút
                if (lastSent.plusMinutes(1).isAfter(now)) {
                    model.addAttribute("errorMessage", "Vui lòng chờ ít nhất 1 phút trước khi gửi lại email!");
                    return "auth/forgot-password";
                }
            }

            // Tạo token mới hoặc cập nhật token cũ
            String token = passwordResetService.createPasswordResetToken(account);
            String resetLink = "http://localhost:8080/auth/reset-password?token=" + token;

            // Gửi email thật
            emailService.sendSimpleMail(account.getEmail(),
                    "Reset mật khẩu Sanbong247",
                    "Nhấn vào link này để reset mật khẩu: " + resetLink);

            // Cập nhật thời gian gửi email
            passwordResetService.updateLastEmailSentTime(account);

            model.addAttribute("successMessage", "Link reset mật khẩu đã được gửi tới email của bạn!");
            return "auth/forgot-password";
        }




        @GetMapping("/reset-password")
        public String resetPasswordPage(@RequestParam("token") String token, Model model) {
            if (!passwordResetService.isPasswordResetTokenValid(token)) {
                model.addAttribute("errorMessage", "Token không hợp lệ hoặc đã hết hạn!");
                return "auth/forgot-password";
            }
            model.addAttribute("token", token);
            return "auth/reset-password";
        }

        @PostMapping("/reset-password")
        public String handleResetPassword(@RequestParam("token") String token,
                                          @RequestParam("password") String password,
                                          @RequestParam("confirmPassword") String confirmPassword,
                                          Model model) {
            if (!password.equals(confirmPassword)) {
                model.addAttribute("errorMessage", "Mật khẩu xác nhận không khớp!");
                model.addAttribute("token", token);
                return "auth/reset-password";
            }
            passwordResetService.updatePassword(token, password);
            model.addAttribute("successMessage", "Mật khẩu đã được cập nhật thành công!");
            return "redirect:/auth/login";
        }


    }
