
    document.body.addEventListener("click", function(event) {
    const target = event.target.closest(".logout-link");
    if (!target) return;

    event.preventDefault(); // Ngăn redirect ngay lập tức

    Swal.fire({
    title: 'Bạn có chắc muốn đăng xuất?',
    icon: 'warning',
    showCancelButton: true,
    confirmButtonText: 'Đăng xuất',
    cancelButtonText: 'Hủy',
    reverseButtons: true
}).then((result) => {
    if (result.isConfirmed) {
    window.location.href = target.getAttribute("href");
}
});
});

