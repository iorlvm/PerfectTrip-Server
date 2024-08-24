document.addEventListener('DOMContentLoaded', function() {
    // 取得當前路徑
    const currentUrl = window.location.pathname;

    // 取得所有的link元素
    const links = document.querySelectorAll('.nav-link');

    links.forEach(link => {
        // 如果link的href跟路徑相同 添加上active
        if (link.getAttribute('href') === currentUrl) {
            link.classList.add('active');

            // 展開生效link所在的卷軸
            let parentCollapse = link.closest('.collapse');
            if (parentCollapse) {
                parentCollapse.classList.add('show');
            }
        } else {
            link.classList.remove('active');
        }
    });
});
