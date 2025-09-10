document.addEventListener("DOMContentLoaded", () => {
    // Kích hoạt tooltip Bootstrap cho các phần tử có title
    document.querySelectorAll("[title]").forEach(el => {
        new bootstrap.Tooltip(el);
    });

    // Xác nhận khi toggle trạng thái tài khoản
    document.querySelectorAll(".toggle-btn").forEach(btn => {
        btn.addEventListener("click", e => {
            const isActive = btn.dataset.status === "true";
            const action = isActive ? "khóa" : "mở khóa";
            if (!confirm(`Bạn có chắc chắn muốn ${action} tài khoản này không?`)) {
                e.preventDefault();
            }
        });
    });

    // Tìm kiếm với debounce
    const searchInput = document.getElementById("searchInput");
    const searchForm = document.getElementById("searchForm");
    let typingTimer;

    if (searchInput && searchForm) {
        searchInput.addEventListener("keyup", () => {
            clearTimeout(typingTimer);
            typingTimer = setTimeout(() => searchForm.submit(), 800); // delay 800ms
        });

        searchInput.addEventListener("keydown", () => clearTimeout(typingTimer));
    }
});

// Xem trước ảnh avatar
function previewImage(event) {
    const preview = document.getElementById("avatarPreview");
    const file = event.target.files[0];
    if (file && preview) {
        preview.src = URL.createObjectURL(file);
    }
}
