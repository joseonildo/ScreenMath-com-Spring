package br.com.joseonildo.screenmatch.principal;

import br.com.joseonildo.screenmatch.model.DadosEpisodio;
import br.com.joseonildo.screenmatch.model.DadosTemporada;
import br.com.joseonildo.screenmatch.model.Serie;
import java.util.List;

public class ExibeDados {
    public static double avaliacaoTemporada = 0.0;
    public static int qtdEpisodios = 0;

    public static String menuInicial() {
        return """
                
                ############# MENU #############
                
                1 - Buscar Serie + Episodios
                2 - Listar Series (Resumo)
                3 - Listar Series (Detalhado)
                4 - Listar Series + Episodios
                5 - Escolher Série e listar episodios
                6 - Buscar Series por Ator
                7 - TOP 5 Series
                8 - Buscar Séries por Categoria
                9 - Modificar Serie (Incluir Atores)
                
                0 - Sair
                
                ################################
                """;
    }

    public static void exibeSerie(Serie dadosSerie, Boolean ExibirEpisodio) {
        System.out.printf("""                        
                        
                        Genero:            %s
                        Nome da série:     %s
                        Ano de lançamento: %s
                        Período ativa:     %s
                        Qtd de temporadas: %s
                        Avaliação:         %s
                        Atores:            %s
                        Poster:            %s
                        Sinopse:           %s
                        Episodios:         %s
                        """,
                dadosSerie.getGenero(), dadosSerie.getTitulo(), dadosSerie.getAnoLancamento(),
                dadosSerie.getPeriodoAtiva(), dadosSerie.getTotalTemporadas(),
                dadosSerie.getAvaliacao(), dadosSerie.getAtores(),
                dadosSerie.getPoster(), dadosSerie.getSinopse(),
                (ExibirEpisodio? dadosSerie.getEpisodios():"Completo"));
    }

    public static void exibeTeporada(List<DadosTemporada> temporadas) {
        for (DadosTemporada temporada : temporadas) {
            avaliacaoTemporada = 0.0;
            qtdEpisodios = 0;
            temporada.episodios().forEach(e -> {
                if (!e.avaliacao().equals("N/A")) {
                    avaliacaoTemporada += Double.parseDouble(e.avaliacao());
                    qtdEpisodios++;
                }
            });
            System.out.printf("\nTemporada %d - Nota da temporada: %.1f%n",
                    temporada.numero(),
                    avaliacaoTemporada / qtdEpisodios);
            if (temporada.episodios() != null) {
                for (DadosEpisodio episodio : temporada.episodios()) {
                    double avaliacao;
                    try {
                        avaliacao = Double.parseDouble(episodio.avaliacao());
                    } catch (NumberFormatException e) {
                        System.out.println("   - Episódio " + episodio.numero() + ": " +
                                episodio.titulo() + " - Avaliação: N/A");
                        continue;
                    }
                    System.out.printf("   - Episódio %d: %s - Avaliação: %.1f\n",
                            episodio.numero(), episodio.titulo(), avaliacao);
                }
            } else {
                System.out.println("   - Informações da temporada não encontradas.");
            }
        }
    }
}