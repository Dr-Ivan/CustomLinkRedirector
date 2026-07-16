// КОНФИГУРАЦИЯ
const HOST = "http://192.168.0.105:8080"
const API_BASE = HOST + "/link";

const fullLinkInput = document.getElementById('fullLinkInput');
const shortLinkInput = document.getElementById('shortLinkInput');
const resultLinkField = document.getElementById('resultLink');
const submitBtn = document.getElementById('submitBtn');
const randomBtn = document.getElementById('randomBtn');
const copyBtn = document.getElementById('copyBtn');
const fullLinkError = document.getElementById('fullLinkError');
const shortLinkError = document.getElementById('shortLinkError');
const toast = document.getElementById('toast');

function showToast(message, isError = false) {
    toast.textContent = message || (isError ? "Ошибка" : "Скопировано!");
    toast.style.background = isError ? "#e53e3e" : "#2d3748";
    toast.classList.add('show');
    setTimeout(() => {
        toast.classList.remove('show');
        // восстанавливаем цвет
        toast.style.background = "#2d3748";
    }, 2000);
}

function copyToClipboard(text) {
    if (!text || text === "Здесь появится короткая ссылка") {
        showToast("Нет ссылки для копирования", true);
        return;
    }
    navigator.clipboard.writeText(text).then(() => {
        showToast("Скопировано!");
    }).catch(() => {
        showToast("Не удалось скопировать", true);
    });
}

function copyResult() {
    copyToClipboard(resultLinkField.value);
}

// Генерация случайной строки
function generateRandomShortLink(length = 10) {
    const chars = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
    let result = '';
    for (let i = 0; i < length; i++) {
        result += chars.charAt(Math.floor(Math.random() * chars.length));
    }
    return result;
}

function clearErrors() {
    fullLinkError.classList.remove('show');
    shortLinkError.classList.remove('show');
    fullLinkError.style.display = 'none';
    shortLinkError.style.display = 'none';
}

function showFieldError(element, message) {
    element.textContent = message;
    element.style.display = 'block';
    element.classList.add('show');
}

// Валидация ссылки на стороне клиента
function isValidUrl(url) {
    try {
        const u = new URL(url);
        return u.protocol === "http:" || u.protocol === "https:";
    } catch(e) {
        return false;
    }
}

// Запрос к бекенду
async function createShortLink() {
    clearErrors();

    const fullLink = fullLinkInput.value.trim();
    let shortLink = shortLinkInput.value.trim();

    if (!fullLink) {
        showFieldError(fullLinkError, "Введите длинную ссылку");
        return;
    }
    if (!isValidUrl(fullLink)) {
        showFieldError(fullLinkError, "⚠️ Некорректная ссылка. Должна начинаться с http:// или https://");
        return;
    }

    if (!shortLink) {
        shortLink = generateRandomShortLink(10);
        shortLinkInput.value = shortLink;
    }

    if (shortLink.length > 255) {
        showFieldError(shortLinkError, "Имя слишком длинное (максимум 255 символов)");
        return;
    }

    // Только латиница, цифры, дефис, подчеркивание
    const validShortPattern = /^[a-zA-Z0-9_-]+$/;
    if (!validShortPattern.test(shortLink)) {
        showFieldError(shortLinkError, "Имя может содержать только буквы, цифры, _ и -");
        return;
    }

    // Отключить кнопку на время запроса
    submitBtn.disabled = true;
    submitBtn.textContent = "Создаём...";

    try {
        const response = await fetch(API_BASE, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                shortLink: shortLink,
                fullLink: fullLink
            })
        });

        if (response.status === 201) {
            const data = await response.json();
            const fullShortUrl = `${HOST}/link/${data.shortLink}`;
            resultLinkField.value = fullShortUrl;
            showToast("Ссылка успешно создана!");
        }
        else if (response.status === 409) {
            const errorData = await response.json();
            showFieldError(shortLinkError, `Имя "${shortLink}" уже занято. Попробуйте другое или нажмите "Случайно".`);
            showToast("Ошибка: имя занято", true);
        }
        else if (response.status === 400) {
            const errorData = await response.json();
            let msg = "Неверные данные. Проверьте ссылку или имя.";
            if (errorData.details) msg = errorData.details;
            showFieldError(shortLinkError, msg);
            showToast("Ошибка валидации", true);
        }
        else {
            const errorData = await response.json();
            showFieldError(shortLinkError, errorData.details || "Неизвестная ошибка");
            showToast("Ошибка сервера", true);
        }
    } catch (error) {
        console.error("Network error:", error);
        showFieldError(shortLinkError, "Не удалось соединиться с сервером. Проверьте, запущен ли бекенд на порту 8080.");
        showToast("Ошибка соединения", true);
    } finally {
        submitBtn.disabled = false;
        submitBtn.textContent = "СОКРАТИТЬ ССЫЛКУ";
    }
}

randomBtn.addEventListener('click', () => {
    const randomName = generateRandomShortLink(10);
    shortLinkInput.value = randomName;
    shortLinkError.classList.remove('show');
    showToast("Случайное имя сгенерировано", false);
});

copyBtn.addEventListener('click', copyResult);
submitBtn.addEventListener('click', createShortLink);

// Enter в любом поле = отправка
const inputs = [fullLinkInput, shortLinkInput];
inputs.forEach(inp => {
    inp.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            e.preventDefault();
            createShortLink();
        }
    });
});

console.log("Фронтенд готов. Бекенд ожидается на", HOST);