document.getElementById('messageForm').addEventListener('submit', async function (event) {
    event.preventDefault();
    const loadingEl = document.getElementById('loading');
    const resultEl = document.getElementById('result');
    const outputEl = document.getElementById('output');

    resultEl.classList.remove('show');
    loadingEl.classList.add('show');
    outputEl.value = '';

    const formData = {
        sender: document.getElementById('sender').value,
        receiver: document.getElementById('receiver').value,
        event: document.getElementById('event').value,
        reason: document.getElementById('reason').value,
        tone: document.getElementById('tone').value
    };

    try {
        const response = await fetch('/api/communications/generate', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(formData)
        });

        if (!response.ok) {
            const text = await response.text();
            throw new Error(text);
        }

        const data = await response.json();
        resultEl.classList.add('show');
        outputEl.value = `Subject: ${data.subject}\n\nTone: ${data.tone}\n\n${data.body}`;
        autoResizeTextarea(outputEl);
    } catch (err) {
        outputEl.value = 'Error generating message: ' + err.message;
        resultEl.classList.add('show');
    } finally {
        loadingEl.classList.remove('show');
    }
});

function autoResizeTextarea(textarea) {
    textarea.style.height = 'auto';
    textarea.style.height = textarea.scrollHeight + 'px';
}

// Debug panel
document.getElementById('debugToggle').addEventListener('click', function () {
    const panel = document.getElementById('debugPanel');
    panel.classList.toggle('collapsed');
    if (!panel.classList.contains('collapsed')) {
        loadDebugInfo();
    }
});

async function loadDebugInfo() {
    const container = document.getElementById('debugInfo');
    try {
        const response = await fetch('/api/debug/info');
        if (!response.ok) throw new Error('Failed to load debug info');
        const data = await response.json();
        container.innerHTML = Object.entries(data)
            .map(([key, value]) => `<div class="debug-row"><span class="debug-label">${formatLabel(key)}</span><span class="debug-value">${value}</span></div>`)
            .join('');
    } catch (err) {
        container.textContent = 'Error: ' + err.message;
    }
}

function formatLabel(camelCase) {
    return camelCase.replace(/([A-Z])/g, ' $1').replace(/^./, s => s.toUpperCase());
}

// Live health polling
const HEALTH_POLL_INTERVAL_MS = 4000;
let healthTimer = null;

async function pollHealthOnce() {
    const dot = document.getElementById('healthDot');
    const label = document.getElementById('healthLabel');
    if (!dot || !label) return;
    try {
        const res = await fetch('/api/debug/health', {cache: 'no-store'});
        if (!res.ok) throw new Error('HTTP ' + res.status);
        const data = await res.json();
        const status = (data.status || 'unknown').toString().toUpperCase();

        dot.classList.remove('up', 'down', 'unknown');
        switch (status) {
            case 'UP':
                dot.classList.add('up');
                break;
            case 'DOWN':
                dot.classList.add('down');
                break;
            default:
                dot.classList.add('unknown');
        }
        label.textContent = status.toLowerCase();
        dot.title = `Server health: ${status}`;
    } catch (e) {
        dot.classList.remove('up');
        dot.classList.add('down');
        const label = document.getElementById('healthLabel');
        if (label) label.textContent = 'down';
    }
}

function startHealthPolling() {
    if (healthTimer) clearInterval(healthTimer);
    pollHealthOnce();
    healthTimer = setInterval(pollHealthOnce, HEALTH_POLL_INTERVAL_MS);
}

// Start polling as soon as the page loads
document.addEventListener('DOMContentLoaded', startHealthPolling);
