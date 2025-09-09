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
