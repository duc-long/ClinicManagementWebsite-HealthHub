document.addEventListener("DOMContentLoaded", function () {
    console.log("✅ Toast script loaded");

    const toastEl = document.getElementById("message");
    if (!toastEl) {
        console.warn("❌ Không tìm thấy #message trong DOM");
        return;
    }

    const toastBody = toastEl.querySelector(".toast-body").innerText.trim();
    const messageType = toastEl.dataset.type;
    console.log("📩 messageType:", messageType);
    console.log("📨 message:", toastBody);

    // Nếu có message thì show
    if (toastBody !== "") {
        const toast = new bootstrap.Toast(toastEl, { delay: 3000, autohide: true});
        toast.show();
        console.log("🎉 Toast hiển thị thành công");
    } else {
        console.warn("⚠️ Không có nội dung message để hiển thị");
    }
});
