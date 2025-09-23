let chart;

document.addEventListener("DOMContentLoaded", () => {
    const yearSelect = document.getElementById("yearSelect");
    const monthSelect = document.getElementById("monthSelect");
    const ctx = document.getElementById("multiRevenueChart").getContext("2d");

    async function loadData() {
        const year = yearSelect.value;
        const month = monthSelect.value;
        let urlData = `/admin/revenue/data?year=${year}`;
        let urlSummary = `/admin/revenue/summary?year=${year}`;
        if (month) {
            urlData += `&month=${month}`;
            urlSummary += `&month=${month}`;
        }

        try {
            // 1️⃣ Lấy dữ liệu chart
            const resData = await fetch(urlData);
            const data = await resData.json();

            if (chart) chart.destroy();
            chart = new Chart(ctx, {
                type: "line",
                data: {
                    labels: data.labels,
                    datasets: [{
                        label: "Doanh thu",
                        data: data.values,
                        borderColor: "#28a745",
                        backgroundColor: "rgba(40,167,69,0.15)",
                        fill: true,
                        tension: 0.4
                    }]
                },
                options: {
                    responsive: true,
                    plugins: { legend: { display: false } },
                    scales: {
                        y: {
                            ticks: {
                                callback: v =>
                                    new Intl.NumberFormat("vi-VN", {
                                        style: "currency",
                                        currency: "VND"
                                    }).format(v)
                            }
                        }
                    }
                }
            });

            // 2️⃣ Lấy dữ liệu card
            const resSummary = await fetch(urlSummary);
            const summary = await resSummary.json();

            document.getElementById("todayRevenueCard").textContent =
                summary.todayRevenue.toLocaleString("vi-VN") + " VND";

            document.getElementById("totalRevenueCard").textContent =
                summary.totalRevenue.toLocaleString("vi-VN") + " VND";

            document.getElementById("ordersCard").textContent = summary.orders;
            document.getElementById("buyersCard").textContent = summary.buyers;

            document.getElementById("growthPercentCard").textContent =
                summary.growthPercent + "%";
        } catch (e) {
            console.error(e);
        }
    }

    yearSelect.addEventListener("change", loadData);
    monthSelect.addEventListener("change", loadData);

    loadData();
});
