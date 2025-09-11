document.addEventListener('DOMContentLoaded', function () {
    const pwd = document.getElementById('password');
    const togglePassword = document.getElementById('togglePassword');

    if (togglePassword && pwd) {
        togglePassword.addEventListener('click', function () {
            const type = pwd.getAttribute('type') === 'password' ? 'text' : 'password';
            pwd.setAttribute('type', type);
            this.classList.toggle('bi-eye');
            this.classList.toggle('bi-eye-slash');
        });
    }

    // Bootstrap-like client validation
    const forms = document.querySelectorAll('.needs-validation');
    Array.prototype.slice.call(forms).forEach(function (form) {
        form.addEventListener('submit', function (event) {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
                form.classList.add('was-validated');
            }
        }, false);
    });
});
// Xử lý autofill (email, password khi trình duyệt tự điền)
const inputs = document.querySelectorAll('.input-field input');
inputs.forEach(input => {
    // Nếu có giá trị sẵn thì thêm class filled
    if (input.value.trim() !== '') {
        input.classList.add('filled');
    }
    // Khi người dùng nhập
    input.addEventListener('input', function () {
        if (this.value.trim() !== '') {
            this.classList.add('filled');
        } else {
            this.classList.remove('filled');
        }
    });
    // Check autofill sau khi load trang
    window.addEventListener('load', () => {
        const inputs = document.querySelectorAll('.input-field input');
        inputs.forEach(input => {
            if (input.value.trim() !== '') {
                input.classList.add('filled');
            }
        });
    });
});

