let chart;

document.addEventListener("DOMContentLoaded", () => {
    const yearSelect = document.getElementById("yearSelect");
    const monthSelect = document.getElementById("monthSelect");
    const ctx = document.getElementById("multiRevenueChart").getContext("2d");

    async function loadChart() {
        const year = yearSelect.value;
        const month = monthSelect.value;
        let url = `/admin/revenue/data?year=${year}`;
        if (month) url += `&month=${month}`;

        try {
            const res = await fetch(url);
            const data = await res.json();

            if (chart) chart.destroy();

            chart = new Chart(ctx, {
                type: "line",
                data: {
                    labels: data.labels, // ["Ngày 1", "Ngày 2", ... "Ngày 30"]
                    datasets: [{
                        label: "Doanh thu",
                        data: data.values, // [1000000, 0, 0, 2500000, ...]
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
        } catch (e) {
            console.error(e);
        }
    }

    // Gọi khi load trang & khi đổi filter
    yearSelect.addEventListener("change", loadChart);
    monthSelect.addEventListener("change", loadChart);

    loadChart();
});
