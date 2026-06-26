public class Mago extends Personaje {
    private int mana;
    private int manaMaximo;
    private TipoElemento elementoFavorito;

    public Mago(String nombre, TipoElemento elementoFavorito) {
        super(nombre, 100, 15);
        this.manaMaximo      = 60;
        this.mana            = manaMaximo;
        this.elementoFavorito = elementoFavorito;
    }

    @Override
    public String mostrarHabilidades() {
        return String.format(
            "\n✨ Habilidades de %s  [Nivel %d | Maná: %d/%d]\n" +
            "  1. Chispa Mágica                → Daño: %d  | Costo: 5 Maná\n" +
            "  2. Tormenta de %-10s      → Daño: %d  | Costo: 30 Maná\n" +
            "  3. Escudo Arcano (1 turno)       → Bloquea próximo ataque | Costo: 20 Maná", //es un texto fijo en la plantilla:
            this.nombre, this.nivel,
            this.mana, this.manaMaximo,
            this.fuerza,
            this.elementoFavorito, this.fuerza * 3
        );
    }

    @Override
    public String atacar(Personaje objetivo, int tipoAtaque) throws AccionInvalidaException {
        StringBuilder log = new StringBuilder(); //recibe a quien atacar el ataque elegido y sin notiene mana sale el error

        if (tipoAtaque == 1) {
            if (this.mana < 5)
                throw new AccionInvalidaException("Sin maná ni para una chispa.");
            this.mana -= 5;
            log.append(String.format("⚡ %s lanza una pequeña chispa.\n", this.nombre));
            int danio = objetivo.recibirDanio(this.fuerza);
            log.append(registrarGolpe(objetivo, danio));

        } else if (tipoAtaque == 2) {
            if (this.mana < 30)
                throw new AccionInvalidaException("¡Maná insuficiente para la Tormenta!");
            this.mana -= 30;
            log.append(String.format("🌩️  ¡%s desata una TORMENTA DE %s!\n", this.nombre, this.elementoFavorito));
            int danio = objetivo.recibirDanio(this.fuerza * 3);
            log.append(registrarGolpe(objetivo, danio));

        } else if (tipoAtaque == 3) {
            if (this.mana < 20)
                throw new AccionInvalidaException("¡Maná insuficiente para el Escudo Arcano!");
            this.mana -= 20;
            // Activa escudo temporal: devuelve texto y señal para que Main maneje el efecto
            log.append(String.format("🔮 %s invoca un ESCUDO ARCANO. ¡Próximo ataque será bloqueado!\n", this.nombre));

        } else {
            log.append("❓ El hechizo falla y explota inofensivamente en el aire.");
        }
        return log.toString();
    }

    // Retorna true si el escudo arcano fue activado en el turno anterior
    // (lo gestiona Main con un boolean escudoActivo)
    public String activarEscudoArcano() {
        return "🔮 ¡El Escudo Arcano bloqueó el ataque!";
    }

    @Override
    public String defender() {
        int recuperacion = 15;
        this.mana = Math.min(this.mana + recuperacion, this.manaMaximo);
        return String.format(
            "🧘 %s medita profundamente...\n" +
            "   Recupera %d de maná. Maná actual: %d/%d",
            this.nombre, recuperacion, this.mana, this.manaMaximo
        );
    }

    // ── Getters nuevos ──────────────────────────────────────────────
    public int getMana()     { return mana; }
    public int getManaMaximo(){ return manaMaximo; }

    // ── Helpers privados ────────────────────────────────────────────
    private String registrarGolpe(Personaje objetivo, int danioReal) {
        return String.format("%s recibió %d de daño. Vida: %d/%d\n",
            objetivo.getNombre(), danioReal, objetivo.getVida(), objetivo.getVidaMaxima());
    }
}
 //muestraa quien golpeamos cuanto daño le hicimos y la vida actual y vida maxima
