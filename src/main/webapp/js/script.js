document.addEventListener('DOMContentLoaded', function() {
    // Get the current URL
    const currentUrl = window.location.pathname.replace(/^\/|\/$/g, '');

    // Select all nav-link elements
    const links = document.querySelectorAll('.nav-link');

    links.forEach(link => {
        // If the link href matches the current URL, add the 'active' class
        if (link.getAttribute('href') === currentUrl) {
            link.classList.add('active');

            // Expand the parent collapse menu if it exists
            let parentCollapse = link.closest('.collapse');
            if (parentCollapse) {
                let parentCollapseId = parentCollapse.getAttribute('id');
                let collapseTrigger = document.querySelector(`[data-bs-target="#${parentCollapseId}"]`);
                if (collapseTrigger) {
                    collapseTrigger.classList.add('show');
                }
            }
        } else {
            link.classList.remove('active');
        }
    });
});