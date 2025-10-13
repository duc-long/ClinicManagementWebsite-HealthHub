document.addEventListener("DOMContentLoaded", function () {
    const toastEl = document.getElementById("toastMessage");
    const toastBody = toastEl.querySelector(".toast-body").innerText.trim();

    // if message not null -> show toast
    if (toastBody !== "") {
        const toast = new bootstrap.Toast(toastEl, {delay: 3000});
        toast.show();
    }
})
