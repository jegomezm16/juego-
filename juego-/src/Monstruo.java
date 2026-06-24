import java.util.Random;

public class Monstruo extends Personaje {
    private int furia;          // sube con cada golpe recibido
    private final Random rng = new Random();

    public Monstruo(String nombre, int vida, int fuerza) {
        super(nombre, vida, fuerza);
        this.furia = 0;
    }

    @Override
    public String mostrarHabilidades() {
        return "";   // los monstruos no muestran menú al jugador
    }

    @Override
    public String atacar(Personaje objetivo, int tipoAtaque) {
        StringBuilder log = new StringBuilder();

        // Golpe crítico aleatorio (20 % de probabilidad, o 35 % si hay furia)
        boolean critico = rng.nextInt(100) < (furia > 0 ? 35 : 20);
        int danio = critico ? this.fuerza * 2 : this.fuerza;

        if (critico) {
            log.append(String.format("💢 ¡%s golpea con FURIA SALVAJE! (¡CRÍTICO!)\n", this.nombre));
        } else {
            log.append(String.format("👊 %s ataca furiosamente.\n", this.nombre));
        }

        int danioReal = objetivo.recibirDanio(danio);
        log.append(String.format("   %s recibió %d de daño. Vida: %d/%d",
            objetivo.getNombre(), danioReal, objetivo.getVida(), objetivo.getVidaMaxima()));

        return log.toString();
    }

    // Al recibir daño, el monstruo acumula furia (máx 3)
    @Override
    public int recibirDanio(int cantidad) {
        if (furia < 3) furia++;
        return super.recibirDanio(cantidad);
    }

    @Override
    public String defender() {
        return String.format("🐾 %s gruñe y aguanta el golpe... (Furia: %d/3)", this.nombre, this.furia);
    }

    // Retorna la experiencia que otorga al morir (basada en su fuerza y vida máxima)
    public int calcularExperiencia() {
        return (this.vidaMaxima / 10) + (this.fuerza * 2);
    }

    public int getFuria() { return furia; }
}
