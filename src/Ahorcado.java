import java.util.*;

public class Ahorcado {
    private static final Scanner scanner = new Scanner(System.in);
    private final List<Jugador> jugadores;
    private final int puntosObjetivo;
    private HashSet<Character> letrasAdivinadas;
    private HashMap<Character, Integer> letrasFrecuencia;
    private String frase;

    public Ahorcado(List<Jugador> jugadores, int puntosObjetivo) {
        this.jugadores = jugadores;
        this.puntosObjetivo = puntosObjetivo;
    }

    public void iniciarJuego() {
        boolean juegoTerminado = false;

        while (!juegoTerminado) {
            solicitarFrase();
            letrasAdivinadas = new HashSet<>();
            letrasFrecuencia = contarLetrasFrase();

            boolean fraseAdivinada = false;
            Jugador ganadorRonda = null;

            while (!fraseAdivinada) {
                for (Jugador jugador : jugadores) {
                    if (jugarTurno(jugador)) {
                        System.out.println("¡" + jugador.nombre + " ha adivinado la frase completa y gana 5 puntos!");
                        jugador.sumarPuntos(5);
                        fraseAdivinada = true;
                        ganadorRonda = jugador;
                        break; // Salimos del ciclo si alguien adivina la frase
                    }
                }
            }

            // Comprobamos si algún jugador ha alcanzado el puntaje objetivo
            for (Jugador jugador : jugadores) {
                if (jugador.puntos >= puntosObjetivo) {
                    juegoTerminado = true;
                    System.out.println(jugador.nombre + " ha alcanzado el puntaje objetivo y ha ganado el juego!");
                    break; // Salimos del ciclo si un jugador ha ganado
                }
            }

            if (!juegoTerminado) {
                System.out.println("\nIniciando una nueva ronda...");
            }
        }

        mostrarClasificacionFinal();
    }

    private void solicitarFrase() {
        frase = Frases.obtenerFraseAleatoria();
        System.out.println("\nLa frase ha sido seleccionada aleatoriamente.");
    }


    private HashMap<Character, Integer> contarLetrasFrase() {
        HashMap<Character, Integer> frecuencia = new HashMap<>();
        for (char c : frase.toCharArray()) {
            if (Character.isLetter(c)) {
                frecuencia.put(c, frecuencia.getOrDefault(c, 0) + 1);
            }
        }
        return frecuencia;
    }

    private boolean jugarTurno(Jugador jugador) {
        boolean turnoFinalizado = false; // Se define si el turno debe finalizar

        while (!turnoFinalizado) {
            System.out.println("\nFrase actual: " + obtenerFraseOculta());
            System.out.println(jugador.nombre + ", tienes " + jugador.puntos + " puntos. Ingresa una letra:");
            char letra = scanner.next().toLowerCase().charAt(0);
            scanner.nextLine(); // Limpiar el buffer de entrada

            if (!Character.isLetter(letra)) {
                System.out.println("Por favor, ingresa una letra válida.");
                continue; // No termina el turno, permite intentar de nuevo
            }

            if (letrasAdivinadas.contains(letra)) {
                System.out.println("Ya se ha adivinado esta letra antes. Pierdes 3 puntos.");
                jugador.sumarPuntos(-3);
                turnoFinalizado = true; // El turno termina
            } else if (letrasFrecuencia.containsKey(letra)) {
                int frecuencia = letrasFrecuencia.get(letra);
                jugador.sumarPuntos(3 * frecuencia);
                letrasAdivinadas.add(letra);
                System.out.println("¡La letra " + letra + " aparece " + frecuencia + " veces! Ganas " + (3 * frecuencia) + " puntos.");
                if (fraseCompleta()) {
                    return true; // Si se adivina la frase, se retorna verdadero
                }
                // El turno no termina si la letra es correcta
            } else {
                System.out.println("La letra " + letra + " no está en la frase. Pierdes 1 punto.");
                jugador.sumarPuntos(-1);
                turnoFinalizado = true; // El turno termina
            }
        }

        return false; // Si no se completa la frase, se retorna falso
    }


    private boolean fraseCompleta() {
        for (char c : frase.toCharArray()) {
            if (Character.isLetter(c) && !letrasAdivinadas.contains(c)) {
                return false;
            }
        }
        return true;
    }

    private String obtenerFraseOculta() {
        StringBuilder oculta = new StringBuilder();
        for (char c : frase.toCharArray()) {
            if (Character.isLetter(c) && !letrasAdivinadas.contains(c)) {
                oculta.append("_ ");
            } else {
                oculta.append(c).append(" ");
            }
        }
        return oculta.toString().trim();
    }

    private void mostrarClasificacionFinal() {
        jugadores.sort((j1, j2) -> Integer.compare(j2.puntos, j1.puntos));
        System.out.println("\nClasificación Final:");
        for (int i = 0; i < jugadores.size(); i++) {
            Jugador jugador = jugadores.get(i);
            System.out.println((i + 1) + ". " + jugador.nombre + " - " + jugador.puntos + " puntos");
        }
    }

    public static void main(String[] args) {
        System.out.println("Bienvenido al Juego del Ahorcado Multijugador");
        int numJugadores = 0;
        while (numJugadores < 2 || numJugadores > 4) {
            System.out.print("Ingresa el número de jugadores (2-4): ");
            numJugadores = Integer.parseInt(scanner.nextLine());
            if (numJugadores < 2 || numJugadores > 4) {
                System.out.println("Número de jugadores inválido. Debe estar entre 2 y 4.");
            }
        }

        List<Jugador> jugadores = new ArrayList<>();
        for (int i = 1; i <= numJugadores; i++) {
            System.out.print("Nombre del Jugador " + i + ": ");
            String nombre = scanner.nextLine();
            jugadores.add(new Jugador(nombre));
        }

        System.out.print("Ingresa los puntos necesarios para ganar el juego: ");
        int puntosObjetivo = Integer.parseInt(scanner.nextLine());

        Ahorcado juego = new Ahorcado(jugadores, puntosObjetivo);
        juego.iniciarJuego();
    }
}