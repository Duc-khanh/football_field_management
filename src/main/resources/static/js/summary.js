// summary.js
let yearSelect, monthSelect;

document.addEventListener("DOMContentLoaded", () => {
    yearSelect = document.getElementById("yearSelect");
    monthSelect = document.getElementById("monthSelect");

    // Gọi lần đầu khi trang load
    loadSummary();

    // Lắng nghe khi đổi filter
    yearSelect.addEventListener("change", loadSummary);
    monthSelect.addEventListener("change", loadSummary);
});

async function loadSummary() {
    const year = yearSelect.value;
    const month = monthSelect.value;
    let url = `/admin/revenue/summary?year=${year}`;
    if (month) url += `&month=${month}`;

    try {
        const res = await fetch(url);
        const data = await res.json();

        // Doanh thu hôm nay
        document.getElementById("todayRevenue").textContent =
            new Intl.NumberFormat("vi-VN").format(data.todayRevenue) + " VND";

        // Tăng / giảm %
        const growth = document.getElementById("growthPercent");
        const upIcon = document.getElementById("growthUp");
        const downIcon = document.getElementById("growthDown");

        growth.textContent = (data.growthPercent > 0 ? "+" : "") + data.growthPercent + "%";
        growth.className = "fw-bold " + (data.isUp ? "text-success" : data.isDown ? "text-danger" : "text-secondary");
        upIcon.style.display = data.isUp ? "inline" : "none";
        downIcon.style.display = data.isDown ? "inline" : "none";

        // Tổng doanh thu
        document.getElementById("totalRevenue").textContent =
            new Intl.NumberFormat("vi-VN").format(data.totalRevenue) + " VND";

        // Tổng đơn hàng
        document.getElementById("totalOrders").textContent = data.orders;

        // Người mua
        document.getElementById("buyers").textContent = data.buyers;
    } catch (e) {
        console.error("Summary error:", e);
    }
}
