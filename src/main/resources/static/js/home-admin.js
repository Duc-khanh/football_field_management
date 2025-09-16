document.addEventListener("DOMContentLoaded", () => {
    // Kích hoạt tooltip Bootstrap cho các phần tử có title
    document.querySelectorAll("[title]").forEach(el => {
        new bootstrap.Tooltip(el);
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
