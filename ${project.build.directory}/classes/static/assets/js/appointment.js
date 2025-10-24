document.addEventListener('DOMContentLoaded', () => {
    console.log("✅ appointment.js loaded");

    document.querySelectorAll('.list-group-item').forEach(item => {
        item.addEventListener('click', e => {
            e.preventDefault();
            const type = item.id.replace('filter-', ''); // current, history, submitted, cancelled

            // change active sidebar
            document.querySelectorAll('.list-group-item').forEach(i => i.classList.remove('active'));
            item.classList.add('active');

            // container thật sự hiển thị danh sách
            const container = document.getElementById('appointment-container');
            if (!container) {
                console.error("❌ appointment-container not found!");
                return;
            }

            // loading effect
            container.innerHTML = `
                <div class="text-center p-5 text-secondary">
                    <div class="spinner-border text-primary" role="status"></div>
                    <p class="mt-3">Loading...</p>
                </div>`;

            // fetch fragment
            fetch(`/patient/appointment/view?type=${type}`)
                .then(res => {
                    if (!res.ok) throw new Error(res.statusText);
                    return res.text();
                })
                .then(html => {
                    container.innerHTML = html;
                    container.style.opacity = 0;
                    setTimeout(() => {
                        container.style.transition = 'opacity 0.3s';
                        container.style.opacity = 1;
                    }, 50);
                })
                .catch(err => {
                    container.innerHTML = `
                        <div class="alert alert-danger text-center">Error loading appointments.</div>`;
                    console.error("Fetch error:", err);
                });
        });
    });
});
