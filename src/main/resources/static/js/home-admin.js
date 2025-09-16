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
    document.addEventListener("DOMContentLoaded", function () {
    const searchInput = document.querySelector("#searchForm input[name='keyword']");
    const statusFilter = document.querySelector("#statusFilter");
    const searchForm = document.querySelector("#searchForm");

    let typingTimer;
    const doneTypingInterval = 900; // 2 giây

    if (searchInput) {
        searchInput.addEventListener("keyup", function () {
            clearTimeout(typingTimer);
            typingTimer = setTimeout(() => {
                searchForm.submit();
            }, doneTypingInterval);
        });

        searchInput.addEventListener("keydown", function () {
            clearTimeout(typingTimer);
        });
    }

    // Khi chọn trạng thái thì submit luôn
    if (statusFilter) {
        statusFilter.addEventListener("change", function () {
            searchForm.submit();
        });
    }
});

