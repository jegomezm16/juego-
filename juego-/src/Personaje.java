public abstract class Personaje {
    protected String nombre;
    protected int vida;
    protected int vidaMaxima;
    protected int fuerza;
    protected int nivel;
    protected int experiencia;

    public Personaje(String nombre, int vidaMaxima, int fuerza) {
        this.nombre    = nombre;
        this.vidaMaxima = vidaMaxima;
        this.vida      = vidaMaxima;
        this.fuerza    = fuerza;
        this.nivel     = 1;
        this.experiencia = 0;
    }

    // Retorna String con los mensajes para que Main los imprima
    public abstract String atacar(Personaje objetivo, int tipoAtaque) throws AccionInvalidaException;
    public abstract String mostrarHabilidades();
    public abstract String defender();

    // Retorna el daño real aplicado (útil para lógica de combate)
    public int recibirDanio(int cantidad) {
        int danioReal = Math.min(cantidad, this.vida);   // no puede bajar de 0
        this.vida = Math.max(0, this.vida - cantidad);
        return danioReal;  // quien llame sabe cuánto daño se hizo realmente
    }

    // Retorna la cantidad real curada (puede ser menos si ya está cerca del máximo)
    public int curar(int cantidad) {
        int antes     = this.vida;
        this.vida     = Math.min(this.vida + cantidad, this.vidaMaxima);
        return this.vida - antes;
    }

    // Retorna true si subió de nivel
    public boolean ganarExperiencia(int exp) {
        this.experiencia += exp;
        int expParaSiguienteNivel = this.nivel * 100;
        if (this.experiencia >= expParaSiguienteNivel) {
            this.experiencia -= expParaSiguienteNivel;
            this.nivel++;
            this.fuerza    += 5;
            this.vidaMaxima += 20;
            this.vida = this.vidaMaxima;  // se cura al subir de nivel
            return true;
        }
        return false;
    }

    public boolean estaVivo() { return this.vida > 0; }

    public String getNombre()   { return nombre; }
    public int    getVida()     { return vida; }
    public int    getVidaMaxima(){ return vidaMaxima; }
    public int    getFuerza()   { return fuerza; }
    public int    getNivel()    { return nivel; }
    public int    getExperiencia(){ return experiencia; }
}
