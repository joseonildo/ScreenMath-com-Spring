package br.com.joseonildo.screenmatch.model;

public enum Categoria {
    ACAO("Action", "Ação"),
    ANIMACAO("Animation", "Animação"),
    AVENTURA("Adventure", "Aventura"),
    ROMANCE("Romance", "Romance"),
    COMEDIA("Comedy", "Comedia"),
    DRAMA("Drama", "Drama"),
    CRIME("Crime", "Crime");

    private final String categoriaPortugues;
    private final String categoriaOmdb;

    Categoria(String categoriaOmdb, String categoriaPortugues) {
        this.categoriaOmdb = categoriaOmdb;
        this.categoriaPortugues = categoriaPortugues;
    }

    public static Categoria fromString(String text) {
        for (Categoria categoria : Categoria.values()) {
            if (categoria.categoriaOmdb.equalsIgnoreCase(text)) {
                return categoria;
            }
        }
        throw new IllegalArgumentException("Nenhuma categoria encontrada para a categoria fornecida: " + text);
    }

    public static Categoria fromPortugues(String text) {
        for (Categoria categoria : Categoria.values()) {
            if (categoria.categoriaPortugues.equalsIgnoreCase(text)) {
                return categoria;
            }
        }
        System.out.println("Nenhuma categoria encontrada para a categoria fornecida: " + text);
        return null;
    }
}
