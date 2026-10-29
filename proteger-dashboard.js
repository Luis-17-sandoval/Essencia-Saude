(function () {
    let usuario = null;

    try {
        usuario = JSON.parse(
            localStorage.getItem("mediturno_usuario") || "null"
        );
    } catch (error) {
        usuario = null;
    }

    if (!usuario || usuario.rol !== "ADMIN") {
        window.location.replace("login.html");
    }
})();