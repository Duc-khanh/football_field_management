document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll("[title]").forEach(el => {
        new bootstrap.Tooltip(el);
    });

    const searchInput = document.getElementById("searchInput");
    const searchForm = document.getElementById("searchForm");
    let typingTimer;

    if (searchInput && searchForm) {
        searchInput.addEventListener("keyup", () => {
            clearTimeout(typingTimer);
            typingTimer = setTimeout(() => searchForm.submit(), 800);
        });

        searchInput.addEventListener("keydown", () => clearTimeout(typingTimer));
    }
});

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
    const doneTypingInterval = 900;

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

    if (statusFilter) {
        statusFilter.addEventListener("change", function () {
            searchForm.submit();
        });
    }
});

