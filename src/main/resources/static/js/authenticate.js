// Toggle password visibility bằng checkbox
document.addEventListener('DOMContentLoaded', function () {
    const pwd = document.getElementById('password');
    const showPassword = document.getElementById('showPassword'); // checkbox
    const yearEl = document.getElementById('currentYear');

    if (yearEl) yearEl.textContent = new Date().getFullYear();

    if (showPassword && pwd) {
        showPassword.addEventListener('change', function () {
            pwd.type = this.checked ? 'text' : 'password';
        });
    }

    // Bootstrap-like client validation (simple)
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
