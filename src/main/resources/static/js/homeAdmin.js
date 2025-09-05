(function () {
    const sidebar = document.getElementById('sidebar');
    const toggleBtn = document.getElementById('toggleSidebar');
    const LS_COLLAPSED = 'sidebar_collapsed';

    if (!sidebar || !toggleBtn) {
        console.warn('Sidebar or toggle button not found. Sidebar script disabled.');
        return;
    }

    // Load trạng thái thu gọn từ localStorage
    const savedCollapsed = localStorage.getItem(LS_COLLAPSED) === '1';
    if (savedCollapsed) {
        sidebar.classList.add('collapsed');
    }

    // Toggle sidebar
    toggleBtn.addEventListener('click', () => {
        sidebar.classList.toggle('collapsed');
        const collapsed = sidebar.classList.contains('collapsed');
        localStorage.setItem(LS_COLLAPSED, collapsed ? '1' : '0');
    });
})();

function toggleTheme() {
    const current = document.documentElement.getAttribute("data-theme");
    const newTheme = current === "dark" ? "light" : "dark";

    document.documentElement.setAttribute("data-theme", newTheme);
    localStorage.setItem("theme", newTheme);
    updateThemeIcon();
}

function updateThemeIcon() {
    const themeIcon = document.getElementById('themeIcon');
    if (!themeIcon) return;

    const currentTheme = document.documentElement.getAttribute("data-theme");

    themeIcon.className = currentTheme === "dark"
        ? "fa-solid fa-sun"
        : "fa-solid fa-moon";
}

window.addEventListener("DOMContentLoaded", () => {
    const savedTheme = localStorage.getItem("theme") || "light";
    document.documentElement.setAttribute("data-theme", savedTheme);
    updateThemeIcon();
});
