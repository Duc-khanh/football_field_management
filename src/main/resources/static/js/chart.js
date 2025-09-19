document.addEventListener("DOMContentLoaded", function () {
    const jsonEl = document.getElementById('revenueData');
    const revenueData = jsonEl ? JSON.parse(jsonEl.textContent || '{}') : {};

    if (!revenueData || Object.keys(revenueData).length === 0) {
        console.warn("Không có dữ liệu doanh thu");
        return;
    }

    const months = [
        "Tháng 1","Tháng 2","Tháng 3","Tháng 4","Tháng 5","Tháng 6",
        "Tháng 7","Tháng 8","Tháng 9","Tháng 10","Tháng 11","Tháng 12"
    ];

    const datasets = [{
        label: 'Doanh thu',
        data: revenueData['COMPLETE'].map(Number),
        borderColor: '#008FFB',
        backgroundColor: '#008FFB33',
        fill: true,
        tension: 0.4
    }];


    const ctx = document.getElementById('multiRevenueChart').getContext('2d');
    new Chart(ctx, {
        type: 'line',
        data: { labels: months, datasets: datasets },
        options: {
            responsive: true,
            plugins: {
                legend: { position: 'right' },
                tooltip: {
                    callbacks: {
                        label: (context) =>
                            new Intl.NumberFormat('vi-VN', {
                                style: 'currency',
                                currency: 'VND'
                            }).format(context.raw)
                    }
                }
            },
            scales: {
                x: { stacked: true },
                y: {
                    stacked: true,
                    ticks: {
                        callback: (value) =>
                            new Intl.NumberFormat('vi-VN', {
                                style: 'currency',
                                currency: 'VND'
                            }).format(value)
                    }
                }
            }
        }
    });
});
