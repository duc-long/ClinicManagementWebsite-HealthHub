document.addEventListener('DOMContentLoaded', function () {
    const content = document.getElementById('content');

    // 🩵 Load main section (overview, appointments, profile,...)
    async function loadSection(name, pushState = true) {
        try {
            const res = await fetch(`/doctor/view/${name}`);
            if (!res.ok) throw new Error("Load Failed: " + res.status);

            const html = await res.text();
            content.innerHTML = html;

            // 🔹 Chỉ update URL khi người dùng click (pushState = true)
            if (pushState) history.pushState({ section: name }, '', `/doctor/${name}`);
        } catch (err) {
            content.innerHTML = `<div class="alert alert-danger">${err.message}</div>`;
        }
    }

    // 🩷 Load detail page (e.g., appointment/detail/{id})
    async function loadDetail(type, id, pushState = true) {
        try {
            const res = await fetch(`/doctor/${type}/detail/${id}`);
            if (!res.ok) throw new Error("Load Failed: " + res.status);

            const html = await res.text();
            content.innerHTML = html;

            if (pushState)
                history.pushState({ type, id }, '', `/doctor/${type}/detail/${id}`);
        } catch (err) {
            content.innerHTML = `<div class="alert alert-danger">${err.message}</div>`;
        }
    }

    // ✅ Gắn global để fragment có thể gọi
    window.loadSection = loadSection;
    window.loadDetail = loadDetail;

    // 🧭 Sidebar click
    document.querySelectorAll('.sidebar a[data-section]').forEach(a => {
        a.addEventListener('click', e => {
            e.preventDefault();
            document.querySelectorAll('.sidebar a').forEach(link => link.classList.remove('active'));
            a.classList.add('active');
            loadSection(a.dataset.section); // pushState mặc định = true
        });
    });

    // 🔙 Xử lý Back / Forward bằng popstate event
    window.addEventListener('popstate', (event) => {
        const state = event.state;

        if (!state) {
            // Nếu không có state → fallback về overview
            loadSection('overview', false);
            return;
        }

        if (state.section) {
            loadSection(state.section, false); // không pushState lần nữa
        } else if (state.type && state.id) {
            loadDetail(state.type, state.id, false);
        }
    });

    // 🔄 Khi F5 hoặc mở link trực tiếp → đọc URL hiện tại để xác định fragment
    function handleInitialLoad() {
        const path = location.pathname.replace(/^\/doctor\//, ''); // bỏ /doctor/
        console.log("🔹 Initial path:", path);

        if (path === '' || path === 'home') {
            loadSection('overview', false);
        } else if (path.startsWith('appointment/detail/')) {
            const id = path.split('/')[2];
            loadDetail('appointment', id, false);
        } else {
            loadSection(path, false);
        }
    }

    handleInitialLoad();
});
