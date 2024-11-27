document.addEventListener("DOMContentLoaded", () => {
    const simbolos = ["🍒", "🍋", "🍉", "⭐", "🔔", "🥙", "🌟", "🎁"];
    const probabilidades = [30, 30, 20, 10, 5, 4, 3, 3];
    let saldo = 100;
    let multiplicador = 1;

    const tableroDiv = document.getElementById("tablero");
    const mensajesDiv = document.getElementById("mensajes");
    const apuestaInput = document.getElementById("apuesta");
    const jugarBtn = document.getElementById("jugar");
    const multiplicadorDiv = document.getElementById("multiplicador");

    function actualizarMensajes(mensaje) {
        mensajesDiv.innerHTML += `<p>${mensaje}</p>`;
        mensajesDiv.scrollTop = mensajesDiv.scrollHeight;
    }

    function generarSimboloAleatorio() {
        const total = probabilidades.reduce((acc, prob) => acc + prob, 0);
        const rand = Math.floor(Math.random() * total);
        let acumulado = 0;

        for (let i = 0; i < simbolos.length; i++) {
            acumulado += probabilidades[i];
            if (rand < acumulado) return simbolos[i];
        }
        return simbolos[0];
    }

    function actualizarTablero(tablero) {
        const celdas = tableroDiv.getElementsByClassName("celda");
        for (let i = 0; i < 3; i++) {
            for (let j = 0; j < 3; j++) {
                celdas[i * 3 + j].textContent = tablero[i][j];
            }
        }
    }

    function esLineaGanadora(s1, s2, s3, comodin = "🌟") {
        return (s1 === s2 && s2 === s3) || 
               (s1 === comodin || s2 === comodin || s3 === comodin);
    }

    function evaluarGanancia(tablero, apuesta) {
        let ganancia = 0;
        const comodin = "🌟";

        // Evaluar solo las filas (horizontal)
        for (let i = 0; i < 3; i++) {
            if (esLineaGanadora(tablero[i][0], tablero[i][1], tablero[i][2], comodin)) {
                ganancia += calcularPremio(tablero[i][0], apuesta);
            }
        }

        return ganancia;
    }

    function calcularPremio(simbolo, apuesta) {
        switch (simbolo) {
            case "🍒": return apuesta * 3;
            case "🍋": return apuesta * 4;
            case "🍉": return apuesta * 5;
            case "⭐": return apuesta * 8;
            case "🔔": return apuesta * 10;
            case "🥙": return apuesta * 15;
            case "🌟": return apuesta * 30; // Premio especial para comodín
            default: return 0;
        }
    }

    function esBonusActivado(tablero) {
        for (let i = 0; i < 3; i++) {
            if (tablero[i][0] === "🎁" && tablero[i][1] === "🎁" && tablero[i][2] === "🎁") {
                return true;
            }
        }
        return false;
    }

    function jugarMinijuego() {
        const multiplicadores = [100, 75, 50, 25, 10, 5];
        const probabilidadesMult = [5, 10, 15, 25, 30, 15];

        const totalProb = probabilidadesMult.reduce((acc, prob) => acc + prob, 0);
        const rand = Math.floor(Math.random() * totalProb);
        let acumulado = 0;

        for (let i = 0; i < multiplicadores.length; i++) {
            acumulado += probabilidadesMult[i];
            if (rand < acumulado) return multiplicadores[i];
        }
        return 5; // Valor por defecto
    }

    function jugarTiradasGratis(multiplicador) {
        actualizarMensajes("¡Comienzan las tiradas gratuitas! 🎁");

        let gananciaTotal = 0;
        for (let t = 1; t <= 10; t++) {
            const tablero = Array.from({ length: 3 }, () =>
                Array.from({ length: 3 }, generarSimboloAleatorio)
            );
            actualizarTablero(tablero);

            const ganancia = evaluarGanancia(tablero, 1); // 1 moneda de apuesta base
            gananciaTotal += ganancia;

            actualizarMensajes(`Tirada gratuita ${t}: Ganaste ${ganancia} monedas.`);
        }

        gananciaTotal *= multiplicador;
        actualizarMensajes(`¡Las tiradas gratuitas terminaron! Ganaste un total de ${gananciaTotal} monedas.`);
        return gananciaTotal;
    }

    function mostrarMultiplicador() {
        multiplicadorDiv.textContent = `¡Tu multiplicador es x${multiplicador}!`;
        multiplicadorDiv.style.display = "block"; // Asegurarse de que se vea
    }

    jugarBtn.addEventListener("click", () => {
        const apuesta = parseInt(apuestaInput.value, 10);

        if (isNaN(apuesta) || apuesta <= 0 || apuesta > saldo) {
            actualizarMensajes("Introduce una apuesta válida.");
            return;
        }

        saldo -= apuesta;
        actualizarMensajes(`Apostaste ${apuesta} monedas. Saldo restante: ${saldo}.`);

        // Generar tablero
        const tablero = Array.from({ length: 3 }, () => 
            Array.from({ length: 3 }, generarSimboloAleatorio)
        );

        actualizarTablero(tablero);

        // Evaluar ganancia
        const ganancia = evaluarGanancia(tablero, apuesta);
        if (ganancia > 0) {
            saldo += ganancia;
            actualizarMensajes(`¡Ganaste ${ganancia} monedas! Nuevo saldo: ${saldo}.`);
        } else {
            actualizarMensajes("No ganaste esta vez. ¡Sigue intentando!");
        }

        // Verificar bonus
        if (esBonusActivado(tablero)) {
            actualizarMensajes("¡Activaste el minijuego de bonificación! 🎁🎁🎁");
            multiplicador = jugarMinijuego();
            mostrarMultiplicador(); // Mostrar multiplicador
            const gananciaBonus = jugarTiradasGratis(multiplicador);
            saldo += gananciaBonus;
        }

        // Verificar saldo
        if (saldo <= 0) {
            actualizarMensajes("Te quedaste sin monedas. Fin del juego.");
            jugarBtn.disabled = true;
        }
    });
});
