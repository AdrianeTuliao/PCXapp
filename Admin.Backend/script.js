// Para sa login page
const container = document.querySelector('.container');
const registerBtn = document.querySelector('.register-btn');
const loginBtn = document.querySelector('.login-btn');

// Event Listeners
registerBtn.addEventListener('click', () => {
    container.classList.add('active');
});

loginBtn.addEventListener('click', () => {
    container.classList.remove('active');
});

// Para sa pag toggle sa password
function togglePassword(inputId, iconElement) {
    let passwordField = document.getElementById(inputId);

    if (passwordField.type === "password") {
        passwordField.type = "text";
        iconElement.classList.remove("bxs-lock-alt");
        iconElement.classList.add("bx-lock-open-alt");
    } else {
        passwordField.type = "password";
        iconElement.classList.remove("bx-lock-open-alt");
        iconElement.classList.add("bxs-lock-alt");
    }
}

document.addEventListener("DOMContentLoaded", function () {
    setTimeout(() => {
        document.querySelector(".container").classList.add("show");
    }, 100); // Slight delay for a smooth effect
});

