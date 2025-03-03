package br.com.joseonildo.screenmatch;

public enum Nivel {
    INICIANTE(1),
    INTERMEDIARIO(2),
    AVANCADO(3);

    private final int pontuacao;

    // Construtor do enum
    Nivel(int pontuacao) {
        this.pontuacao = pontuacao;
    }

    // Método para obter a pontuação associada
    public int getPontuacao() {
        return pontuacao;
    }

    public static void main(String[] args) {
        // Exemplo de uso do enum
        for (Nivel nivel : Nivel.values()) {
            System.out.println(nivel + " tem pontuação: " + nivel.getPontuacao());
        }
    }
}