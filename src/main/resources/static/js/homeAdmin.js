(function () {
    const sidebar = document.getElementById('sidebar');
    const toggleBtn = document.getElementById('toggleSidebar'); // ← đổi tên này cho khớp
    const LS_COLLAPSED = 'sidebar_collapsed';
    const savedCollapsed = localStorage.getItem(LS_COLLAPSED) === '1';

    if (savedCollapsed) sidebar.classList.add('collapsed');

    toggleBtn.addEventListener('click', () => {
        sidebar.classList.toggle('collapsed');
        const collapsed = sidebar.classList.contains('collapsed');
        localStorage.setItem(LS_COLLAPSED, collapsed ? '1' : '0');
    });
})();
