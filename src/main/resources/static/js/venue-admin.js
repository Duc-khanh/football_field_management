document.addEventListener("DOMContentLoaded", function () {
    const statusFilter = document.getElementById("statusFilter");
    const searchForm = document.getElementById("searchForm");

    statusFilter.addEventListener("change", function () {
        // Submit form bằng AJAX
        const formData = new FormData(searchForm);
        const query = new URLSearchParams(formData).toString();

        fetch(`/admin/venue/list?${query}`, {
            headers: { "X-Requested-With": "XMLHttpRequest" }
        })
            .then(res => res.text())
            .then(html => {
                // Lấy phần tbody mới từ response
                const parser = new DOMParser();
                const doc = parser.parseFromString(html, "text/html");
                const newTableBody = doc.querySelector("tbody").innerHTML;
                document.querySelector("tbody").innerHTML = newTableBody;

                // Update phân trang nếu có
                const newPagination = doc.querySelector("nav");
                const paginationContainer = document.querySelector("nav");
                if (paginationContainer && newPagination) {
                    paginationContainer.innerHTML = newPagination.innerHTML;
                }
            });
    });
});
