document.getElementById('emailForm').addEventListener('submit', async function (event) {
    event.preventDefault();
    const loadingEl = document.getElementById('loading');
    const resultEl = document.getElementById('result');
    const outputEl = document.getElementById('output');

    resultEl.classList.remove('show');
    loadingEl.classList.add('show');
    outputEl.value = '';

    const template = document.getElementById('template').value;

    const formData = {
        template,
        recipientName: document.getElementById('recipientName').value,
        event: document.getElementById('event').value,
        reason: document.getElementById('reason').value,
        senderName: document.getElementById('senderName').value,
        senderRole: document.getElementById('senderRole').value,
        tone: document.getElementById('tone').value
    };

    if (template === 'MISSED') {
        formData.date = document.getElementById('date').value;
    } else if (template === 'LATE') {
        formData.arrivalTime = document.getElementById('arrivalTime').value;
    }

    try {
        const response = await fetch('/generate-email', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(formData)
        });

        resultEl.classList.add('show');
        outputEl.value = await response.text();
        autoResizeTextarea(outputEl);
    } catch (err) {
        outputEl.value = 'Error generating email. Please try again.';
    } finally {
        loadingEl.classList.remove('show');
    }
});

document.addEventListener('DOMContentLoaded', () => {
    const templateSelect = document.getElementById('template');
    const dateGroup = document.getElementById('dateGroup');
    const arrivalGroup = document.getElementById('arrivalTimeGroup');
    const dateInput = document.getElementById('date');
    const arrivalInput = document.getElementById('arrivalTime');

    function updateVisibility() {
        const value = templateSelect.value;

        if (value === 'MISSED') {
            dateGroup.classList.add('show');
            dateInput.required = true;

            arrivalGroup.classList.remove('show');
            arrivalInput.required = false;
            arrivalInput.value = '';
        } else if (value === 'LATE') {
            arrivalGroup.classList.add('show');
            arrivalInput.required = true;

            dateGroup.classList.remove('show');
            dateInput.required = false;
            dateInput.value = '';
        } else {
            dateGroup.classList.remove('show');
            dateInput.required = false;
            arrivalGroup.classList.remove('show');
            arrivalInput.required = false;
        }
    }

    templateSelect.addEventListener('change', updateVisibility);
    updateVisibility();
});

function autoResizeTextarea(textarea) {
    textarea.style.height = 'auto';
    textarea.style.height = textarea.scrollHeight + 'px';
}