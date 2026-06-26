import java.util.Scanner;

public class Main {

    // ─── Separador visual ───────────────────────────────────────────
    private static final String SEP = "-".repeat(58);

    static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // ── Presentación ────────────────────────────────────────────
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║      ⚔️   RPG EN JAVA  ⚔️            ║");
        System.out.println("╚══════════════════════════════════════╝");
        System.out.println("Selecciona tu clase:\n  1. Guerrero\n  2. Mago");
        System.out.print("> ");
        int opcion = leerEntero(scanner);

        System.out.print("Ingresa el nombre de tu personaje: ");
        scanner.nextLine();                   // limpia buffer
        String nombreJugador = scanner.nextLine().trim();
        if (nombreJugador.isEmpty()) nombreJugador = (opcion == 1) ? "Thorin" : "Gandalf";// nombres predeterminados

        Personaje jugador;
        if (opcion == 1) {
            jugador = new Guerrero(nombreJugador);
        } else {
            System.out.println("Elige tu elemento:\n  1. FUEGO\n  2. HIELO\n  3. ELECTRICO");
            System.out.print("> ");
            int elem = leerEntero(scanner);
            TipoElemento tipo = (elem == 2) ? TipoElemento.HIELO
                    : (elem == 3) ? TipoElemento.ELECTRICO
                    : TipoElemento.FUEGO;
            jugador = new Mago(nombreJugador, tipo);
        }

        // ── Inventario básico ────────────────────────────────────────
        int pociones = 2;

        // ── Oleadas de enemigos ──────────────────────────────────────
        Monstruo[] oleada = {
                new Monstruo("Orco Salvaje",    120, 18),
                new Monstruo("Trol de Piedra",  180, 28),
                new Monstruo("Dragón Joven",    250, 40)
        };

        boolean escudoArcanoActivo = false;   // solo para el Mago

        for (Monstruo enemigo : oleada) {
            System.out.println("\n" + SEP);
            System.out.printf("🐉 ¡Un %s aparece! (Vida: %d | Fuerza: %d)%n",
                    enemigo.getNombre(), enemigo.getVida(), enemigo.getFuerza());
            System.out.println(SEP);

            // ── Bucle de combate ─────────────────────────────────────
            while (jugador.estaVivo() && enemigo.estaVivo()) {

                // Barra de estado
                System.out.printf("%n[%s] Vida: %d/%d  | Nivel: %d | Pociones: %d%n",
                        jugador.getNombre(), jugador.getVida(), jugador.getVidaMaxima(),
                        jugador.getNivel(), pociones);
                System.out.printf("[%s]  Vida: %d/%d%n%n",
                        enemigo.getNombre(), enemigo.getVida(), enemigo.getVidaMaxima());

                System.out.println("¿Qué deseas hacer?");
                System.out.println("  1. Atacar");
                System.out.println("  2. Defender / Recuperar recurso");
                if (pociones > 0)
                    System.out.println("  3. Usar poción de vida (+40 HP)");
                System.out.print("> ");
                int accion = leerEntero(scanner);

                if (accion == 1) {
                    // Mostrar habilidades y elegir
                    System.out.println(jugador.mostrarHabilidades());
                    System.out.print("Elige tu habilidad: ");
                    int tipoAtaque = leerEntero(scanner);

                    try {
                        String resultado = jugador.atacar(enemigo, tipoAtaque);
                        System.out.println(resultado);

                        // Si el mago eligió Escudo Arcano (habilidad 3), no ataca
                        if (jugador instanceof Mago && tipoAtaque == 3) {
                            escudoArcanoActivo = true;
                        }
                    } catch (AccionInvalidaException e) {
                        System.out.println("❌ " + e.getMessage());
                        System.out.println("   ¡Perdiste el turno por agotamiento!");
                    }

                } else if (accion == 2) {
                    System.out.println(jugador.defender());

                } else if (accion == 3 && pociones > 0) {
                    int curado = curar(jugador, 40);
                    pociones--;
                    System.out.printf("🧪 Usas una poción y recuperas %d de vida. Vida: %d/%d%n",
                            curado, jugador.getVida(), jugador.getVidaMaxima());

                } else {
                    System.out.println("Opción no válida, pierdes el turno.");
                }

                // ── Turno del enemigo ──────────────────────────────
                if (enemigo.estaVivo()) {
                    System.out.println("\n" + SEP.substring(0, 20) + " Turno del Enemigo " + SEP.substring(0, 19));

                    if (escudoArcanoActivo) {
                        // El escudo arcano del Mago bloquea este turno
                        System.out.println(((Mago) jugador).activarEscudoArcano());
                        escudoArcanoActivo = false;
                    } else {
                        String ataqueEnemigo = enemigo.atacar(jugador, 1);
                        System.out.println(ataqueEnemigo);
                    }
                    System.out.println(SEP);
                }
            }

            // ── ¿Sobreviviste a esta oleada? ──────────────────────
            if (!jugador.estaVivo()) break;

            // Ganar experiencia
            int exp = enemigo.calcularExperiencia();
            boolean subioNivel = jugador.ganarExperiencia(exp);
            System.out.printf("%n✅ ¡Derrotaste al %s! +%d EXP%n", enemigo.getNombre(), exp);
            if (subioNivel) {
                System.out.printf("🎉 ¡%s subió al nivel %d! +5 Fuerza, +20 Vida máxima. ¡Vida restaurada!%n",
                        jugador.getNombre(), jugador.getNivel());
            }

            // Pequeño descanso entre oleadas
            System.out.printf("💤 Recuperas 25 HP entre oleadas. Vida: %d/%d%n",
                    jugador.getVida(), jugador.getVidaMaxima());
            curar(jugador, 25);
        }

        // ── Fin del juego ──────────────────────────────────────────
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║         ⚔️  FIN DE LA BATALLA         ║");
        System.out.println("╚══════════════════════════════════════╝");
        if (jugador.estaVivo()) {
            System.out.printf("🏆 ¡Victoria! %s conquista las tierras oscuras.%n", jugador.getNombre());
            System.out.printf("   Nivel final: %d | EXP acumulada: %d%n",
                    jugador.getNivel(), jugador.getExperiencia());
        } else {
            System.out.println("💀 Has sido derrotado... Game Over.");
        }

        scanner.close();
    }

    // ── Helpers de Main ─────────────────────────────────────────────

    /** Lee un entero de forma segura; si el usuario escribe algo inválido devuelve -1. */
    private static int leerEntero(Scanner sc) {
        if (sc.hasNextInt()) {
            int val = sc.nextInt();
            sc.nextLine();  // limpiar salto de línea  cuandp sin est linea cuando el jugador presinona enteren la siguente linea lo lee

            return val;
        } else {
            sc.nextLine();   // descarta entrada inválida limpia el escaner de textos invalidos
            return -1;
        }
    }

    /**
     * Cura al personaje en 'cantidad' HP sin superar su vida máxima.
     * Delega en Personaje.curar() y retorna la cantidad real curada.
     */
    private static int curar(Personaje p, int cantidad) {
        return p.curar(cantidad);
    }
}