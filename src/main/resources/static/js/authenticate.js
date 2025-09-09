document.addEventListener('DOMContentLoaded', function () {
    const pwd = document.getElementById('password');
    const showPassword = document.getElementById('showPassword');

    if (showPassword && pwd) {
        showPassword.addEventListener('change', function () {
            pwd.type = this.checked ? 'text' : 'password';
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
