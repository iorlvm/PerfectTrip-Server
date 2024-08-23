<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<style>
    #alertPlaceholder {
        position: absolute;
        z-index: 99;
        top: 12px;
        left: 50%;
        transform: translateX(-50%);
    }

    #alertPlaceholder .alert {
        box-shadow: 0 2px 3px rgba(0, 0, 0, 0.1);
    }
</style>


<div id="alertPlaceholder"></div>

<script>
    const showAlert = (message, type = 'success') => {
        let alertPlaceholder = document.getElementById('alertPlaceholder');
        alertPlaceholder.innerHTML = `
        <div class="alert alert-\${type} alert-dismissible fade show" role="alert">
            \${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>`;
        let alertElement = alertPlaceholder.querySelector('.alert');
        let alert = new bootstrap.Alert(alertElement);

        setTimeout(() => {
            alert.close();
        }, 1200);
    }
</script>