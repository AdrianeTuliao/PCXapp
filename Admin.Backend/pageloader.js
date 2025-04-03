document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll("a").forEach(link => {
        link.addEventListener("click", function (e) {
            e.preventDefault();
            let href = this.getAttribute("href");

            let content = document.querySelector(".content");

            // Fade out content smoothly before loading new content
            content.style.transition = "opacity 0.3s ease-out";
            content.style.opacity = "0";

            fetch(href)
                .then(response => response.text())
                .then(data => {
                    let parser = new DOMParser();
                    let newDoc = parser.parseFromString(data, "text/html");

                    setTimeout(() => {
                        content.innerHTML = newDoc.querySelector(".content").innerHTML;

                        // Fade in the new content smoothly
                        content.style.transition = "opacity 0.3s ease-in";
                        content.style.opacity = "1";
                    }, 300); // Wait for fade-out animation to complete
                });
        });
    });
});
