// Elementos del DOM
const saldoElemento = document.getElementById("saldo");
const apuestaInput = document.getElementById("apuesta");
const jugarBoton = document.getElementById("jugar");
const tableroElemento = document.getElementById("tablero");
const mensajeElemento = document.getElementById("mensaje");

// Configuración inicial
let saldo = 100;
saldoElemento.textContent = `Saldo: ${saldo} monedas`;

const simbolos = ["🍒", "🍋", "🍉", "⭐", "🔔", "7", "🌟"]; // Símbolos posibles
const probabilidades = [30, 30, 20, 10, 5, 4, 1]; // Probabilidades de aparición

// Función para generar un tablero 3x3
const generarTablero = () => {
    const tablero = [];
    for (let i = 0; i < 9; i++) {
        tablero.push(obtenerSimboloAleatorio());
    }
    return tablero;
};

// Función para obtener un símbolo aleatorio basado en probabilidades
const obtenerSimboloAleatorio = () => {
    const totalProbabilidades = probabilidades.reduce((acc, curr) => acc + curr, 0);
    const aleatorio = Math.floor(Math.random() * totalProbabilidades);

    let acumulado = 0;
    for (let i = 0; i < simbolos.length; i++) {
        acumulado += probabilidades[i];
        if (aleatorio < acumulado) {
            return simbolos[i];
        }
    }
    return simbolos[0]; // Valor por defecto
};

// Función para mostrar el tablero en el DOM
const mostrarTablero = (tablero) => {
    tableroElemento.innerHTML = ""; // Limpiar el tablero previo
    for (let i = 0; i < 9; i += 3) {
        const fila = document.createElement("div");
        fila.classList.add("fila");
        fila.innerHTML = `
            <span>${tablero[i]}</span>
            <span>${tablero[i + 1]}</span>
            <span>${tablero[i + 2]}</span>
        `;
        tableroElemento.appendChild(fila);
    }
};

// Función para evaluar las ganancias en filas horizontales
const evaluarGanancia = (tablero, apuesta) => {
    let ganancia = 0;
    const comodin = "🌟";

    // Revisar filas horizontales
    for (let i = 0; i < 3; i++) {
        const fila = [tablero[i * 3], tablero[i * 3 + 1], tablero[i * 3 + 2]];
        if (esLineaGanadora(fila, comodin)) {
            ganancia += calcularPremio(fila[0], apuesta, comodin);
        }
    }

    return ganancia;
};

// Función para verificar si una línea es ganadora
const esLineaGanadora = (linea, comodin) => {
    const [s1, s2, s3] = linea;
    return (
        (s1 === s2 && s2 === s3) || // Tres símbolos iguales
        (s1 === comodin || s2 === comodin || s3 === comodin) // Al menos un comodín
    );
};

// Función para calcular el premio basado en el símbolo
const calcularPremio = (simbolo, apuesta, comodin) => {
    if (simbolo === comodin) {
        return apuesta * 30; // Premio especial para 3 comodines
    }

    switch (simbolo) {
        case "🍒": return apuesta * 3;
        case "🍋": return apuesta * 4;
        case "🍉": return apuesta * 5;
        case "⭐": return apuesta * 8;
        case "🔔": return apuesta * 10;
        case "7": return apuesta * 15;
        default: return 0;
    }
};

// Evento para jugar
jugarBoton.addEventListener("click", () => {
    const apuesta = parseInt(apuestaInput.value);
    if (!apuesta || apuesta <= 0 || apuesta > saldo) {
        mensajeElemento.textContent = "Ingresa una apuesta válida.";
        return;
    }

    // Restar apuesta del saldo
    saldo -= apuesta;
    saldoElemento.textContent = `Saldo: ${saldo} monedas`;

    // Generar y mostrar el tablero
    const tablero = generarTablero();
    mostrarTablero(tablero);

    // Evaluar ganancias
    const ganancia = evaluarGanancia(tablero, apuesta);
    saldo += ganancia;

    if (ganancia > 0) {
        mensajeElemento.textContent = `¡Ganaste ${ganancia} monedas!`;
    } else {
        mensajeElemento.textContent = "No ganaste esta vez. ¡Sigue intentando!";
    }

    saldoElemento.textContent = `Saldo: ${saldo} monedas`;

    // Verificar si el jugador se quedó sin saldo
    if (saldo <= 0) {
        mensajeElemento.textContent = "¡Te has quedado sin monedas! Fin del juego.";
        jugarBoton.disabled = true;
    }
});
