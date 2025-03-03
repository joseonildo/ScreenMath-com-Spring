package br.com.joseonildo.screenmatch;

import br.com.joseonildo.screenmatch.model.DadosEpisodio;
import br.com.joseonildo.screenmatch.model.DadosSerie;
import br.com.joseonildo.screenmatch.model.DadosTemporada;
import br.com.joseonildo.screenmatch.model.Episodio;
import br.com.joseonildo.screenmatch.service.ConsumoAPI;
import br.com.joseonildo.screenmatch.service.ConverteDados;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class PrincipalAntiga {
    private Scanner leitura = new Scanner(System.in);
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConverteDados conversor = new ConverteDados();
    private List<DadosTemporada> temporadas = new ArrayList<>();
    private DadosSerie dadosSerie;
    private final String API_KEY = "&apikey=fc511a2f";
    private final String ENDERECO = "http://www.omdbapi.com/?&t=";
    private String buscado;
    public static double avaliacaoTemporada = 0.0;
    public static int qtdEpisodios = 0;

    public void exibeMenu() {
        do {
            System.out.println("\n\nDigite o nome da série para buscar!");
            System.out.print("Nome: ");
            String opcao = leitura.nextLine();
            if (opcao.isBlank()) break;
            realizaBusca(opcao);
            exibeResultado();
        } while (true);
    }


    public void realizaBusca(String titulo) {
        titulo = titulo.replace(" ", "+");
        try { // Bloco try-catch para tratar as exceções
            String endereco = ENDERECO + titulo + API_KEY;
            String json = consumoAPI.obterDados(endereco);
            dadosSerie = conversor.obterDados(json, DadosSerie.class);

            int qtdTemporadas = Integer.parseInt(dadosSerie.totalTemporadas());
            for (int i = 1; i <= qtdTemporadas; i++) {
                endereco = ENDERECO + titulo + API_KEY + "&season=" + i;
                json = consumoAPI.obterDados(endereco);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
                buscado = "da série";
            }
        } catch (NumberFormatException | InterruptedException | IOException ex) { // Captura as exceções
            System.err.println("Houve um erro durante a busca: " + ex.getMessage()); // Imprime a mensagem de erro
            buscado = "do título";
            //throw new RuntimeException(ex);
        }
    }

    public void exibeResultado() {
        System.out.printf("""
                        %n
                        Nome %s:     %s
                        Ano de lançamento: %s
                        Período ativa:     %s
                        Qtd de temporadas: %s
                        Avaliação:         %s
                        """,
                buscado, dadosSerie.titulo(), dadosSerie.anoLancamento(),
                dadosSerie.periodoAtiva(), dadosSerie.totalTemporadas(),
                dadosSerie.avaliacao());


        if (buscado.contains("série")) {
            //System.out.println("\nExibir temporadas e episórios de cada temporada?");
            //System.out.print("Digite Sim ou Não: ");
            //String opcao = "s"; //leitura.nextLine();
            //if (opcao.equalsIgnoreCase("sim") || opcao.equalsIgnoreCase("s") || opcao.equals("1")) {
            System.out.println();
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
                        double avaliacao = 0.0;
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
            //}*/

            List<DadosEpisodio> dadosEpisodios = temporadas.stream()
                    .flatMap(t -> t.episodios().stream())
                    .toList();

//            System.out.println("\nTop 5 episódios: ");
//            dadosEpisodios.stream()
//                    .filter(e -> !e.avaliacao().equals("N/A"))
//                    //.peek(e-> System.out.println("Primeiro filtro N/A " + e))
//                    .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
//                    //.peek(e-> System.out.println("Ordenação " + e))
//                    .limit(10)
//                    //.peek(e-> System.out.println("Limitação " + e))
//                    //.map(e-> e.titulo().toUpperCase())
//                    //.peek(e-> System.out.println("Mapeamento " + e))
//                    .forEach(System.out::println);



            System.out.println();
            List<Episodio> episodios = temporadas.stream()
                    .flatMap(t -> t.episodios().stream()
                            .map(d -> new Episodio(t.numero(), d))
                    )
                    .toList();
                    //.collect(Collectors.toList());

//            episodios.forEach(System.out::println);

//            System.out.println("Episódios a partir de que ano? ");
//            System.out.print("Digite o ano: ");
//
//            var ano = leitura.nextInt();
//            leitura.nextLine();
//
//            LocalDate dataBusca = LocalDate.of(ano, 1, 1);
//
//            DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//            episodios.stream()
//                    .filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(dataBusca))
//                    .forEach(e -> System.out.println(
//                            "Temporada: " + e.getTemporada() +
//                                    " Episodio: " + e.getTitulo() +
//                                    " Data lançamento: " + e.getDataLancamento().format(formatador)
//                    ));

//            System.out.println("\nBuscar um episodio por nome ou parte do nome ");
//            System.out.print("Nome: ");
//
//            var busca = leitura.nextLine();
//
//            System.out.println();
//            episodios.stream()
//                    .filter(e -> e.getTitulo().toUpperCase().contains(busca.toUpperCase()))
//                    .forEach(System.out::println);
//
//            System.out.println();
//            Optional<Episodio> episodioBuscado = episodios.stream()
//                    .filter(e -> e.getTitulo().toUpperCase().contains(busca.toUpperCase()))
//                    .findFirst();
//
//            if(episodioBuscado.isPresent()){
//                System.out.println("Episodio encontrado!");
//                System.out.println("Temporada " + episodioBuscado.get().getTemporada() +
//                        ", Episodio " + episodioBuscado.get().getNumeroEpisodio() +
//                        ", " + episodioBuscado.get().getTitulo() +
//                        ", " + episodioBuscado.get().getDataLancamento() +
//                        ", Nota: " + episodioBuscado.get().getAvaliacao());
//            } else {
//                System.out.println("Episodio não encontrado!");
//            }

            Map<Integer, Double> avaliacoesPorTemporada = episodios.stream()
                    .filter(e -> e.getAvaliacao() > 0.0)
                    .collect(Collectors.groupingBy(Episodio::getTemporada,
                            Collectors.averagingDouble(Episodio::getAvaliacao)));
            //System.out.println(avaliacoesPorTemporada);

            DoubleSummaryStatistics est = episodios.stream()
                    .filter(e -> e.getAvaliacao() > 0.0)
                    .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));

            System.out.printf("""
                            Quantidade de avaliados: %d
                            Média todos episodios: %.1f
                            Melhor episodio: %.1f
                            Pior episodio:  %.1f
                            """,est.getCount(),est.getAverage(),est.getMax(),est.getMin());








        }
    }
}
