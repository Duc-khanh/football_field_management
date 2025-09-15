// alert.js

// Lấy dữ liệu success/error từ thẻ <body> (Thymeleaf)
const body = document.querySelector('body');
const successMessage = body.dataset.successMessage;
const errorMessage = body.dataset.errorMessage;

// Nếu có thông báo thành công
if (successMessage) {
    Swal.fire({
        toast: true,
        position: 'top-end',
        icon: 'success',
        title: successMessage,
        showConfirmButton: false,
        timer: 2500,
        timerProgressBar: true,
        customClass: {
            popup: 'swal-toast-margin'
        }
    });
}

if (errorMessage) {
    Swal.fire({
        toast: true,
        position: 'top-end',
        icon: 'error',
        title: errorMessage,
        showConfirmButton: false,
        timer: 2500,
        timerProgressBar: true,
        customClass: {
            popup: 'swal-toast-margin'
        }
    });
}

