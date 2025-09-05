// src/main/resources/static/js/login.js  (nội dung file)
document.addEventListener('DOMContentLoaded', function () {
    const eyeBtn = document.querySelector('.btn-icon-eye');
    const pwdInput = document.getElementById('password');

    if (eyeBtn && pwdInput) {
        eyeBtn.addEventListener('click', function () {
            const isPwd = pwdInput.type === 'password';
            pwdInput.type = isPwd ? 'text' : 'password';
            // đổi icon
            const icon = this.querySelector('i');
            if (icon) {
                icon.classList.toggle('bi-eye-fill');
                icon.classList.toggle('bi-eye-slash-fill');
            }
        });
    }
});
