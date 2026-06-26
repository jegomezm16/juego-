public class Guerrero extends Personaje {
    private int escudo;
    private int escudoMaximo;
    private int estamina;
    private int estaminaMaxima;

    public Guerrero(String nombre) {
        super(nombre, 150, 25);
        this.escudoMaximo   = 20;
        this.escudo         = escudoMaximo;
        this.estaminaMaxima = 50;
        this.estamina       = estaminaMaxima;
    }

    @Override
    public String mostrarHabilidades() {
        return String.format(
            "\n⚔️  Habilidades de %s  [Nivel %d | Estamina: %d/%d]\n" +
            "  1. Espadazo Rápido       → Daño: %d  | Costo: 0 Estamina\n" +
            "  2. Golpe Rompecráneos    → Daño: %d  | Costo: 25 Estamina\n" +
            "  3. Torbellino de Acero   → Daño: %d  | Costo: 40 Estamina (golpea 2 veces)",
            this.nombre, this.nivel,
            this.estamina, this.estaminaMaxima,
            this.fuerza,
            this.fuerza * 2,
            this.fuerza           // cada golpe vale fuerza, pero son 2
        );
    }

    @Override
    public String atacar(Personaje objetivo, int tipoAtaque) throws AccionInvalidaException {
        StringBuilder log = new StringBuilder(); //recibe a quien atacar el ataque elegido y sin notiene estamina sale el error

        if (tipoAtaque == 1) {
            log.append(String.format("⚔️  %s lanza un espadazo rápido!\n", this.nombre));
            int danio = objetivo.recibirDanio(this.fuerza);
            log.append(registrarGolpe(objetivo, danio));

        } else if (tipoAtaque == 2) {
            if (this.estamina < 25)
                throw new AccionInvalidaException(this.nombre + " está muy cansado para el Golpe Rompecráneos.");
            this.estamina -= 25;
            log.append(String.format("💥 %s salta y asesta un GOLPE ROMPECRÁNEOS!\n", this.nombre));
            int danio = objetivo.recibirDanio(this.fuerza * 2);
            log.append(registrarGolpe(objetivo, danio));

        } else if (tipoAtaque == 3) {
            if (this.estamina < 40)
                throw new AccionInvalidaException(this.nombre + " no tiene estamina para el Torbellino.");
            this.estamina -= 40;
            log.append(String.format("🌀 %s desata un TORBELLINO DE ACERO (2 golpes)!\n", this.nombre));
            int danio1 = objetivo.recibirDanio(this.fuerza);
            log.append("  Golpe 1 → ").append(registrarGolpe(objetivo, danio1));
            int danio2 = objetivo.recibirDanio(this.fuerza);
            log.append("  Golpe 2 → ").append(registrarGolpe(objetivo, danio2));

        } else {
            log.append("❓ El guerrero se confunde y tropieza. ¡Pierde el turno!");
        }
        return log.toString();
    }

    // Sobreescribimos recibirDanio para mitigar con escudo; retorna daño real recibido
    @Override
    public int recibirDanio(int cantidad) {
        // El daño mitigado no se "imprime" aquí, lo manejamos aparte
        if (escudo > 0) {
            int mitigado = Math.min(cantidad, escudo);
            escudo  -= mitigado;
            cantidad -= mitigado;
        }
        return super.recibirDanio(cantidad);
    }

    // Retorna texto describiendo el estado del escudo tras recibir un golpe
    public String estadoEscudo(int danioBruto) {
        // Solo informativo: llámalo antes de recibirDanio si quieres el detalle
        if (escudo > 0) {
            int mitigado = Math.min(danioBruto, escudo);
            return String.format("[ESCUDO] Absorbió %d de daño. Escudo restante: %d/%d\n",
                mitigado, Math.max(0, escudo - mitigado), escudoMaximo);
        }
        return "";
    }

    @Override
    public String defender() {
        int recuperacion = 20;
        this.estamina = Math.min(this.estamina + recuperacion, this.estaminaMaxima);
        return String.format(    //evita el error de mas estamina
            "🛡️  %s adopta una postura defensiva firme.\n" +
            "    Recupera %d de estamina. Estamina actual: %d/%d",
            this.nombre, recuperacion, this.estamina, this.estaminaMaxima
        );
    }

    // ── Getters nuevos ──────────────────────────────────────────────
    public int getEstamina()       { return estamina; }
    public int getEstaminaMaxima() { return estaminaMaxima; }
    public int getEscudo()         { return escudo; }

    // ── Helpers privados ────────────────────────────────────────────
    private String registrarGolpe(Personaje objetivo, int danioReal) {
        return String.format("%s recibió %d de daño. Vida: %d/%d\n",
            objetivo.getNombre(), danioReal, objetivo.getVida(), objetivo.getVidaMaxima());
    }
} //muestraa quien golpeamos cuanto daño le hicimos y la vida actual y vida maxima
