<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<nav aria-label="Page navigation">
    <ul class="pagination justify-content-center" id="pagination"></ul>
</nav>

<script>
    const renderPagination = (total, limit, offset) => {
        const pagination = document.getElementById('pagination');
        pagination.innerHTML = '';

        const totalPages = Math.ceil(total / limit);
        const currentPage = Math.floor(offset / limit) + 1;

        // 上一頁按鈕
        const prevButton = `
        <li class="page-item \${currentPage === 1 ? 'disabled' : ''}">
            <a class="page-link" href="#" data-page="\${currentPage - 1}">上一頁</a>
        </li>`;
        pagination.insertAdjacentHTML('beforeend', prevButton);

        for (let i = 1; i <= totalPages; i++) {
            const activeClass = i === currentPage ? 'active' : '';
            const pageItem = `
            <li class="page-item \${activeClass}">
                <a class="page-link" href="#" data-page="\${i}">\${i}</a>
            </li>`;
            pagination.insertAdjacentHTML('beforeend', pageItem);
        }

        // 下一頁按鈕
        const nextButton = `
        <li class="page-item \${currentPage === totalPages ? 'disabled' : ''}">
            <a class="page-link" href="#" data-page="\${currentPage + 1}">下一頁</a>
        </li>`;
        pagination.insertAdjacentHTML('beforeend', nextButton);

        // 添加事件监听器
        document.querySelectorAll('.page-link').forEach(pageLink => {
            pageLink.addEventListener('click', (e) => {
                e.preventDefault();
                const page = parseInt(e.target.getAttribute('data-page'));
                const newOffset = (page - 1) * limit;
                loadCustomers(newOffset);
            });
        });
    };
</script>