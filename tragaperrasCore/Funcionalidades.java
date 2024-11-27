package Slot;

import java.util.Random;
import java.util.Scanner;

public class Funcionalidades {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();

        // Array de símbolos posibles (el último es el bonus 🎁)
        String[] simbolos = {"🍒", "🍋", "🍉", "⭐", "🔔", "7", "🌟", "🎁"};

        // Probabilidades ajustadas (el bonus 🎁 es más raro que el comodín 🌟)
        int[] probabilidades = {30, 30, 20, 10, 5, 4, 1, 1};

        int saldo = 100;
        int apuesta;

        System.out.println("¡Bienvenido a la tragaperras 3x3!");
        System.out.println("Comienzas con un saldo de: " + saldo + " monedas");

        while (saldo > 0) {
            System.out.println("\nTu saldo actual es: " + saldo + " monedas");
            System.out.print("Ingresa tu apuesta (o 0 para salir): ");
            apuesta = scanner.nextInt();

            if (apuesta == 0) {
                System.out.println("¡Gracias por jugar! Te llevas " + saldo + " monedas.");
                break;
            }

            if (apuesta > saldo) {
                System.out.println("No tienes suficientes monedas. Intenta con una cantidad menor.");
                continue;
            }

            // Reducir el saldo por la apuesta
            saldo -= apuesta;

            // Generar la cuadrícula 3x3
            String[][] tablero = new String[3][3];
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    tablero[i][j] = obtenerSimboloAleatorio(simbolos, probabilidades, random);
                }
            }

            // Mostrar el tablero
            System.out.println("\n🎰 Resultados:");
            for (String[] fila : tablero) {
                for (String simbolo : fila) {
                    System.out.print(simbolo + " ");
                }
                System.out.println();
            }

            // Evaluar premios
            int ganancia = evaluarGanancia(tablero, simbolos, apuesta);

            if (ganancia > 0) {
                saldo += ganancia;
                System.out.println("¡Felicidades! Has ganado " + ganancia + " monedas.");
            } else {
                System.out.println("Lo siento, no has ganado esta vez. ¡Sigue intentando!");
            }

            // Verificar si hay minijuego por bonificación 🎁
            if (esBonusActivado(tablero, "🎁")) {
                System.out.println("\n¡Activaste el minijuego de bonificación! 🎁🎁🎁");
                System.out.println("Presiona 'Enter' para girar la rueda del multiplicador...");
                scanner.nextLine(); // Capturar el salto de línea
                scanner.nextLine(); // Esperar el Enter del usuario

                int multiplicador = jugarMinijuego(random);
                System.out.println("¡Tu multiplicador es: x" + multiplicador + "!");

                System.out.println("\n¡Comenzamos las tiradas gratuitas!");
                int gananciaTiradasGratis = jugarTiradasGratis(simbolos, probabilidades, random, multiplicador, scanner);
                saldo += gananciaTiradasGratis;

                System.out.println("¡Terminaste las tiradas gratis! Ganaste " + gananciaTiradasGratis + " monedas.");
            }
        }

        if (saldo <= 0) {
            System.out.println("¡Te has quedado sin monedas! Fin del juego.");
        }

        scanner.close();
    }

    // Función para obtener un símbolo aleatorio según las probabilidades
    private static String obtenerSimboloAleatorio(String[] simbolos, int[] probabilidades, Random random) {
        int total = 0;
        for (int prob : probabilidades) {
            total += prob;
        }

        int valorAleatorio = random.nextInt(total);
        int acumulado = 0;
        for (int i = 0; i < simbolos.length; i++) {
            acumulado += probabilidades[i];
            if (valorAleatorio < acumulado) {
                return simbolos[i];
            }
        }
        return simbolos[0]; // Valor por defecto (debería ser inalcanzable)
    }

    // Función para evaluar las ganancias según las combinaciones
    private static int evaluarGanancia(String[][] tablero, String[] simbolos, int apuesta) {
        int ganancia = 0;

        // Comodín y bonus
        String comodin = "🌟";

        // Revisa filas, columnas y diagonales
        for (int i = 0; i < 3; i++) {
            // Filas
            if (esLineaGanadora(tablero[i][0], tablero[i][1], tablero[i][2], comodin)) {
                ganancia += calcularPremio(tablero[i][0], apuesta, simbolos, comodin);
            }
            // Columnas
            if (esLineaGanadora(tablero[0][i], tablero[1][i], tablero[2][i], comodin)) {
                ganancia += calcularPremio(tablero[0][i], apuesta, simbolos, comodin);
            }
        }
        // Diagonal principal
        if (esLineaGanadora(tablero[0][0], tablero[1][1], tablero[2][2], comodin)) {
            ganancia += calcularPremio(tablero[0][0], apuesta, simbolos, comodin);
        }
        // Diagonal inversa
        if (esLineaGanadora(tablero[0][2], tablero[1][1], tablero[2][0], comodin)) {
            ganancia += calcularPremio(tablero[0][2], apuesta, simbolos, comodin);
        }

        return ganancia;
    }

    // Verifica si hay tres símbolos de bonificación en fila horizontal
    private static boolean esBonusActivado(String[][] tablero, String bonus) {
        for (String[] fila : tablero) {
            if (fila[0].equals(bonus) && fila[1].equals(bonus) && fila[2].equals(bonus)) {
                return true;
            }
        }
        return false;
    }

    // Minijuego para seleccionar un multiplicador
    private static int jugarMinijuego(Random random) {
        // Multiplicadores y sus probabilidades
        int[] multiplicadores = {100, 75, 50, 25, 10, 5};
        int[] probabilidades = {5, 10, 15, 25, 30, 15}; // Probabilidad ajustada

        int total = 0;
        for (int prob : probabilidades) {
            total += prob;
        }

        int valorAleatorio = random.nextInt(total);
        int acumulado = 0;
        for (int i = 0; i < multiplicadores.length; i++) {
            acumulado += probabilidades[i];
            if (valorAleatorio < acumulado) {
                return multiplicadores[i];
            }
        }
        return 5; // Valor por defecto
    }

    // Jugar las 10 tiradas gratuitas, controladas por el usuario
    private static int jugarTiradasGratis(String[] simbolos, int[] probabilidades, Random random, int multiplicador, Scanner scanner) {
        int gananciaTotal = 0;
        System.out.println("\n¡Tienes 10 tiradas gratuitas! Presiona 'Enter' para tirar cada vez.");

        for (int t = 1; t <= 10; t++) {
            System.out.print("Tirada gratuita " + t + ": ");
            scanner.nextLine(); // Esperar el Enter del usuario

            String[][] tablero = new String[3][3];
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    tablero[i][j] = obtenerSimboloAleatorio(simbolos, probabilidades, random);
                }
            }

            // Mostrar el tablero
            for (String[] fila : tablero) {
                for (String simbolo : fila) {
                    System.out.print(simbolo + " ");
                }
                System.out.println();
            }

            // Calcular ganancias para esta tirada
            gananciaTotal += evaluarGanancia(tablero, simbolos, 1); // 1 moneda como apuesta base
        }

        return gananciaTotal * multiplicador;
    }

    // Verifica si una línea es ganadora (con o sin comodín)
    private static boolean esLineaGanadora(String s1, String s2, String s3, String comodin) {
        return (s1.equals(s2) && s2.equals(s3)) ||
                (s1.equals(comodin) || s2.equals(comodin) || s3.equals(comodin));
    }

    // Calcula el premio según el símbolo ganador
    private static int calcularPremio(String simbolo, int apuesta, String[] simbolos, String comodin) {
        if (simbolo.equals(comodin)) {
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
    }
}
