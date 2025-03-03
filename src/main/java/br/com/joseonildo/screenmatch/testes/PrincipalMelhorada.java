package br.com.joseonildo.screenmatch.testes;

import br.com.joseonildo.screenmatch.model.DadosEpisodio;
import br.com.joseonildo.screenmatch.model.DadosSerie;
import br.com.joseonildo.screenmatch.model.DadosTemporada;
import br.com.joseonildo.screenmatch.service.ConsumoAPI;
import br.com.joseonildo.screenmatch.service.ConverteDados;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PrincipalMelhorada {
    private static final String API_KEY = "&apikey=fc511a2f";
    private static final String BASE_URL = "http://www.omdbapi.com/?&t=";
    private static final String NAO_DISPONIVEL = "N/A";

    private final Scanner scanner = new Scanner(System.in);
    private final ConsumoAPI consumoAPI = new ConsumoAPI();
    private final ConverteDados conversor = new ConverteDados();
    private List<DadosTemporada> temporadas = new ArrayList<>();
    private DadosSerie dadosSerie;

    public void exibeMenu() {
        System.out.println("\nDigite o nome da série para buscar!");
        System.out.print("Nome: ");
        String titulo = scanner.nextLine();
        try {
            realizaBusca(titulo);
            exibeResultado();
        } catch (RuntimeException e) {
            System.err.println("Erro ao buscar dados: " + e.getMessage());
        }
    }

    private void realizaBusca(String titulo) {
        temporadas = new ArrayList<>(); // Limpa temporadas anteriores
        String tituloFormatado = titulo.replace(" ", "+");
        String urlSerie = BASE_URL + tituloFormatado + API_KEY;

        try {
            String json = consumoAPI.obterDados(urlSerie);
            dadosSerie = conversor.obterDados(json, DadosSerie.class);

            int totalTemporadas = Integer.parseInt(dadosSerie.totalTemporadas());
            for (int i = 1; i <= totalTemporadas; i++) {
                String urlTemporada = urlSerie + "&season=" + i;
                String temporadaJson = consumoAPI.obterDados(urlTemporada);
                DadosTemporada temporada = conversor.obterDados(temporadaJson, DadosTemporada.class);
                temporadas.add(temporada);
            }
        } catch (IOException | InterruptedException | NumberFormatException e) {
            throw new RuntimeException("Erro ao buscar informações da série: " + e.getMessage(), e);
        }
    }

    private void exibeResultado() {
        if (dadosSerie == null) {
            System.out.println("Nenhuma informação encontrada para o título.");
            return;
        }

        System.out.printf("""
                        Nome da série:     %s
                        Ano de lançamento: %s
                        Período ativa:     %s
                        Total temporadas:  %s
                        Avaliação:         %s%n
                        """,
                dadosSerie.titulo(), dadosSerie.anoLancamento(),
                dadosSerie.periodoAtiva(), dadosSerie.totalTemporadas(),
                dadosSerie.avaliacao());

        exibirTemporadas();
    }

    private void exibirTemporadas() {
        for (DadosTemporada temporada : temporadas) {
            double avaliacaoTemporada = calcularAvaliacaoTemporada(temporada);
            System.out.printf("\nTemporada %d - Nota da temporada: %.1f%n",
                    temporada.numero(), avaliacaoTemporada);

            if (temporada.episodios() != null) {
                exibirEpisodios(temporada);
            } else {
                System.out.println("   - Informações da temporada não encontradas.");
            }
        }
    }

    private double calcularAvaliacaoTemporada(DadosTemporada temporada) {
        double somaAvaliacoes = 0.0;
        int qtdEpisodios = 0;

        for (DadosEpisodio episodio : temporada.episodios()) {
            if (!NAO_DISPONIVEL.equals(episodio.avaliacao())) {
                somaAvaliacoes += Double.parseDouble(episodio.avaliacao());
                qtdEpisodios++;
            }
        }
        return qtdEpisodios > 0 ? somaAvaliacoes / qtdEpisodios : 0.0;
    }

    private void exibirEpisodios(DadosTemporada temporada) {
        for (DadosEpisodio episodio : temporada.episodios()) {
            try {
                double avaliacao = Double.parseDouble(episodio.avaliacao());
                System.out.printf("   - Episódio %d: %s - Avaliação: %.1f%n",
                        episodio.numero(), episodio.titulo(), avaliacao);
            } catch (NumberFormatException e) {
                System.out.printf("   - Episódio %d: %s - Avaliação: %s%n",
                        episodio.numero(), episodio.titulo(), NAO_DISPONIVEL);
            }
        }
    }
}