document.addEventListener("DOMContentLoaded", () => {
    const simbolos = ["🍒", "🍋", "🍉", "⭐", "🔔", "7", "🌟", "🎁"];
    const probabilidades = [30, 30, 20, 10, 5, 4, 1, 1];
    let saldo = 100;

    const saldoElemento = document.getElementById("saldo");
    const resultadosElemento = document.getElementById("resultados");
    const mensajeElemento = document.getElementById("mensaje");
    const apuestaInput = document.getElementById("apuesta");
    const jugarBoton = document.getElementById("jugar");

    const obtenerSimboloAleatorio = () => {
        const total = probabilidades.reduce((a, b) => a + b, 0);
        const rand = Math.random() * total;
        let acumulado = 0;
        for (let i = 0; i < simbolos.length; i++) {
            acumulado += probabilidades[i];
            if (rand < acumulado) return simbolos[i];
        }
        return simbolos[0];
    };

    const generarTablero = () => {
        const tablero = [];
        for (let i = 0; i < 9; i++) {
            tablero.push(obtenerSimboloAleatorio());
        }
        return tablero;
    };

    const mostrarTablero = (tablero) => {
        resultadosElemento.innerHTML = "";
        tablero.forEach((simbolo) => {
            const celda = document.createElement("div");
            celda.textContent = simbolo;
            resultadosElemento.appendChild(celda);
        });
    };

    jugarBoton.addEventListener("click", () => {
        const apuesta = parseInt(apuestaInput.value);
        if (!apuesta || apuesta <= 0 || apuesta > saldo) {
            mensajeElemento.textContent = "Ingresa una apuesta válida.";
            return;
        }

        saldo -= apuesta;
        saldoElemento.textContent = `Saldo: ${saldo} monedas`;

        const tablero = generarTablero();
        mostrarTablero(tablero);

        // Evaluación básica (aquí puedes agregar más lógica)
        const premio = Math.random() < 0.1 ? apuesta * 2 : 0; // 10% de probabilidad de ganar el doble
        saldo += premio;

        if (premio > 0) {
            mensajeElemento.textContent = `¡Ganaste ${premio} monedas!`;
        } else {
            mensajeElemento.textContent = "No ganaste esta vez. ¡Sigue intentando!";
        }

        saldoElemento.textContent = `Saldo: ${saldo} monedas`;

        if (saldo <= 0) {
            mensajeElemento.textContent = "¡Te has quedado sin monedas! Fin del juego.";
            jugarBoton.disabled = true;
        }
    });
});
