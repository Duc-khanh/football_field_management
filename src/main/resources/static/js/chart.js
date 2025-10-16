let chart;

document.addEventListener("DOMContentLoaded", () => {
    const yearSelect = document.getElementById("yearSelect");
    const monthSelect = document.getElementById("monthSelect");
    const ctx = document.getElementById("multiRevenueChart").getContext("2d");

    async function loadData(page = 0) {
        const year = yearSelect.value || "";
        const month = monthSelect.value;

        // API đồng bộ filter
        let query = `year=${year}`;
        if (month && month !== "") {
            query += `&month=${month}`;
        }        const urlData = `/admin/revenue/data?${query}`;
        const urlSummary = `/admin/revenue/summary?${query}`;
        const urlOrders = `/admin/revenue/orders?page=${page}&size=5&${query}`;
        const urlCustomers = `/admin/revenue/customers?${query}`;

        try {
            // 1️⃣ Chart
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
                                callback: v => formatCurrency(v)
                            }
                        }
                    }
                }
            });

            // 2️⃣ Summary
            const summary = await (await fetch(urlSummary)).json();
            document.getElementById("todayRevenueCard").textContent = formatCurrency(summary.todayRevenue);
            document.getElementById("totalRevenueCard").textContent = formatCurrency(summary.totalRevenue);
            document.getElementById("ordersCard").textContent = summary.orders;
            document.getElementById("buyersCard").textContent = summary.buyers;

            updateGrowth("growthTodayCard", summary.growthToday);
            updateGrowth("growthTotalRevenueCard", summary.growthTotalRevenue);
            updateGrowth("growthOrdersCard", summary.growthOrders);
            updateGrowth("growthBuyersCard", summary.growthBuyers);

            // 3️⃣ Orders table
            const ordersPage = await (await fetch(urlOrders)).json();
            const tbody = document.querySelector("#ordersTable tbody");
            tbody.innerHTML = "";

            if (!ordersPage.content || ordersPage.content.length === 0) {
                tbody.innerHTML = `<tr><td colspan="5" class="text-center text-muted">Không có đơn hàng</td></tr>`;
            } else {
                ordersPage.content.forEach(o => {
                    tbody.innerHTML += `
                        <tr>
                            <td><span class="badge ${getStatusClass(o.status)}">${o.status}</span></td>
                            <td>${o.paymentMethod ?? ""}</td>
                            <td class="fw-bold">${formatCurrency(o.totalAmount)}</td>
                            <td>${formatDate(o.paidAt)}</td>
                            <td>${o.account ? o.account.fullName : ""}</td>
                        </tr>`;
                });
            }
            renderOrdersPagination(ordersPage);

            // 4️⃣ Top customers
            const customers = await (await fetch(urlCustomers)).json();
            const ul = document.getElementById("customerList");
            ul.innerHTML = "";

            if (!customers || customers.length === 0) {
                ul.innerHTML = `<li class="list-group-item text-muted">Không có khách hàng</li>`;
            } else {
                customers.forEach(c => {
                    ul.innerHTML += `
                        <li class="list-group-item d-flex align-items-center">
                            <img src="${c.avatar ? '/uploads/avatars/' + c.avatar : '/images/default-avatar.png'}"
                                 class="rounded-circle me-3" width="48" height="48" alt="avatar">
                            <div class="flex-grow-1">
                                <div class="fw-bold">${c.fullName}</div>
                                <small class="text-muted">${c.email}</small>
                            </div>
                            <div class="text-end">
                                <div class="fw-bold text-primary">${formatCurrency(c.totalSpent)}</div>
                                <small class="text-muted">${c.address ?? ""}</small>
                            </div>
                        </li>`;
                });
            }

        } catch (e) {
            console.error("Error loading revenue data:", e);
        }
    }

    // 📌 Helpers
    function updateGrowth(id, value) {
        const el = document.getElementById(id);
        if (!el) return;

        el.textContent = (value ?? 0) + "%";
        el.className = value > 0
            ? "text-success fw-bold"
            : (value < 0 ? "text-danger fw-bold" : "text-secondary");
    }

    function getStatusClass(status) {
        switch (status) {
            case "COMPLETE": return "bg-success";
            case "PAID": return "bg-primary";
            case "CANCELLED": return "bg-danger";
            case "REFUNDED": return "bg-warning text-dark";
            default: return "bg-secondary";
        }
    }

    function formatCurrency(v) {
        if (!v) return "0 VND";
        return new Intl.NumberFormat("vi-VN", {
            style: "currency",
            currency: "VND"
        }).format(v);
    }

    function formatDate(dateStr) {
        if (!dateStr) return "";
        return new Date(dateStr).toLocaleDateString("vi-VN");
    }

    function renderOrdersPagination(page) {
        const pagination = document.getElementById("ordersPagination");
        pagination.innerHTML = "";

        if (!page || page.totalPages <= 1) return;

        for (let i = 0; i < page.totalPages; i++) {
            pagination.innerHTML += `
                <li class="page-item ${i === page.number ? 'active' : ''}">
                    <button class="page-link" data-page="${i}">${i + 1}</button>
                </li>`;
        }

        pagination.querySelectorAll("button").forEach(btn => {
            btn.addEventListener("click", e => {
                loadData(parseInt(e.target.dataset.page));
            });
        });
    }

    // 🎯 Event listeners
    yearSelect.addEventListener("change", () => loadData(0));
    monthSelect.addEventListener("change", () => loadData(0));

    // Load lần đầu
    loadData();
});
