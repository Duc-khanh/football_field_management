(function () {
    const sidebar = document.getElementById('sidebar');
    const toggleBtn = document.getElementById('toggleSidebar');
    const LS_COLLAPSED = 'sidebar_collapsed';

    if (!sidebar || !toggleBtn) {
        console.warn('Sidebar or toggle button not found. Sidebar script disabled.');
        return;
    }

    // Luôn mặc định collapsed khi load lần đầu
    if (!localStorage.getItem(LS_COLLAPSED)) {
        localStorage.setItem(LS_COLLAPSED, '1');
    }

    const savedCollapsed = localStorage.getItem(LS_COLLAPSED) === '1';
    if (savedCollapsed) sidebar.classList.add('collapsed');

    toggleBtn.addEventListener('click', () => {
        sidebar.classList.toggle('collapsed');
        const collapsed = sidebar.classList.contains('collapsed');
        localStorage.setItem(LS_COLLAPSED, collapsed ? '1' : '0');
    });
})();
