package br.com.joseonildo.screenmatch.principal;

import br.com.joseonildo.screenmatch.model.DadosEpisodio;
import br.com.joseonildo.screenmatch.model.DadosSerie;
import br.com.joseonildo.screenmatch.model.DadosTemporada;
import br.com.joseonildo.screenmatch.model.Serie;

import java.util.List;

public class ExibeDados {
    public static double avaliacaoTemporada = 0.0;
    public static int qtdEpisodios = 0;

    public static String menuInicial() {
        return """
                
                ############# MENU #############
                
                1 - Buscar Series
                2 - Buscar Episodios
                3 - Listar Series buscadas
                
                0 - Sair
                
                ################################
                """;
    }

    public static void exibeSerie(Serie dadosSerie) {
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
                        """,
                dadosSerie.getGenero(), dadosSerie.getTitulo(), dadosSerie.getAnoLancamento(),
                dadosSerie.getPeriodoAtiva(), dadosSerie.getTotalTemporadas(),
                dadosSerie.getAvaliacao(), dadosSerie.getAtores(),
                dadosSerie.getPoster(), dadosSerie.getSinopse());
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