package br.com.joseonildo.screenmatch.principal;

import br.com.joseonildo.screenmatch.model.*;
import br.com.joseonildo.screenmatch.repository.SerieRepository;
import br.com.joseonildo.screenmatch.service.ConsumoAPI;
import br.com.joseonildo.screenmatch.service.ConverteDados;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private final Scanner leitura = new Scanner(System.in);
    private final ConsumoAPI consumoAPI = new ConsumoAPI();
    private final ConverteDados conversor = new ConverteDados();
    private final String API_KEY = System.getenv("OMDB_KEY");
    private final String ENDERECO = "http://www.omdbapi.com/?&t=";
    private List<Serie> series = new ArrayList<>();
    private final SerieRepository repositorio;

    public Principal(SerieRepository repositorio) {
        this.repositorio = repositorio;
    }

    public void exibeMenu() throws IOException, InterruptedException {
        String opcao;
        do {
            System.out.print(ExibeDados.menuInicial());
            System.out.print("Digite sua opção: ");
            opcao = leitura.nextLine();

            switch (opcao) {
                case "1":
                    cadastrarSerieWeb();
                    break;
                case "2":
                    listarSeriesCadastradas(true,false);
                    break;
                case "3":
                    listarSeriesCadastradas(false,false);
                    break;
                case "4":
                    listarEpisodiosPorSerie(false);
                    break;
                case "5":
                    listarSeriesPorAtor();
                    break;
                case "6":
                    listarSeriesCadastradas(false,true);
                    break;
                case "7":
                    listarSeriesporCategoria();
                    break;
                case "8":
                    listarSeriesPorTemporadaEAvaliacao();
                    break;
                case "9":
                    listarEpisodiosPorTrecho();
                    break;
                case "10":
                    listarTop5EpisodiosPorSerie();
                    break;
                case "11":
                    listarEpisodiosPorAno();
                    break;
                case "12":
                    listarSeriePorAno();
                    break;
                case "20":
                    listarEpisodiosPorSerie(true);
                    break;
                case "0":
                    System.out.println("Fechando programa...");
                    break;
                default:
                    System.out.println("Opção inválida, tente novamente");
            }
        } while (!opcao.equals("0"));
    }

    private String teclado() {
        System.out.print("\nDigite o nome da série ou ENTER para finalizar \nNome: ");
        return leitura.nextLine();
    }

    private void cadastrarSerieWeb() throws IOException, InterruptedException {
        do {
            String nomeSerie = teclado();
            if (nomeSerie.isBlank()) break;
            var serieBuscada = verificarSerieExistente(nomeSerie);
            if (serieBuscada.isPresent()) {
                System.out.println("\nERRO: A série " +
                        serieBuscada.get().getTitulo() +
                        " já está cadastrada no banco de dados" +
                        "\nATENÇÃO: Pesquise outra!!");
            } else {
                try {
                    DadosSerie dadosSerie = obterDadosSerie(nomeSerie);
                    Serie serie = new Serie(dadosSerie);
                    obterDadosTemporada(serie);
                    repositorio.save(serie);
                    System.out.println();
                    ExibeDados.exibeSerie(serie,false);
                } catch (NumberFormatException ex) {
                    System.out.println("\nERRO!! Nenhuma série nova foi encontrada com o nome: " + nomeSerie);
                    System.out.println("ATENÇÃO: Verifique o nome e tente novamente!");
                }
            }
        } while (true);
    }

    private void listarSeriesCadastradas(boolean resumo, boolean top5) {
        series = repositorio.findAll();
        if (resumo) {
            System.out.println("\nSéries encontradas no banco de dados:\n");
            series.stream()
                    .sorted(Comparator.comparing(Serie::getId))
                    .forEach(s ->
                            System.out.println(s.getId() +
                                    " - " + s.getTitulo() +
                                    " - Temporadas: " + s.getTotalTemporadas() +
                                    " - Avaliação: " + s.getAvaliacao())
                    );
        } else if (top5){
            System.out.println("\nTOP 5 Séries encontradas no banco de dados:");
            List<Serie> serieTop5 = repositorio.findTop5ByOrderByAvaliacaoDesc();
            serieTop5.forEach(s ->
                    System.out.println(s.getTitulo() + " - Avaliação: " + s.getAvaliacao()));
        } else {
            series = series.stream()
                    .sorted(Comparator.comparing(Serie::getGenero))
                    .toList();
            series.forEach(s -> ExibeDados.exibeSerie(s,false));
        }
    }

    private void listarEpisodiosPorSerie(Boolean modificarSerie) {
        listarSeriesCadastradas(true, false);
        String nomeSerie = teclado();
        var serie = verificarSerieExistente(nomeSerie);
        if (serie.isPresent()) {
            var serieEncontrada = serie.get();
            if (modificarSerie) {
                System.out.print("Digite o nome do ator para inclusão: ");
                if (!(nomeSerie = leitura.nextLine()).isBlank()) {
                    serieEncontrada.setAtores(nomeSerie);
                    repositorio.save(serieEncontrada);
                } else {
                    System.out.print("ERRO! Nome do ator não digitado, operação cancelada!");
                }
            }
            ExibeDados.exibeSerie(serieEncontrada,true);
        }
    }

    private void listarSeriesPorAtor() {
        System.out.print("Qual o nome do ator para a busca: ");
        var nomeAtor = leitura.nextLine();
        System.out.print("Qual a Avaliação mínima: ");
        double avaliacao;
        try {
            avaliacao = Double.parseDouble(leitura.nextLine());
        } catch (NumberFormatException e) {
            avaliacao = 0.0;
        }
        List<Serie> seriesEncontradas = repositorio.seriePorAtorEAvaliacao(nomeAtor, avaliacao);
        System.out.println("\nSéries buscadas com os critérios: " +
                "\nAtor principal: " + nomeAtor +
                "\nAvaliação >= : " + avaliacao +
                "\nOrdenação: Da maior pontuação para menor");

        System.out.println("\nSéries encontradas: ");
        seriesEncontradas.stream()
                .sorted(Comparator.comparing(Serie::getAvaliacao).reversed())
                .forEach(s ->
                    System.out.println(s.getTitulo() + " - Avaliação: " + s.getAvaliacao())
                );
    }

    private void listarSeriesporCategoria() {
        System.out.println("Digite o nome da Categoria / Gênero para listar as séries!");
        System.out.print("Categoria: ");
        var nomeCategoria = leitura.nextLine();
        Categoria categoria = Categoria.fromPortugues(nomeCategoria);
        List<Serie> seriesPorCategoria = repositorio.findByGenero(categoria);

        System.out.println("\nSéries buscadas com a categoria: "+ categoria);
        System.out.println("\nSéries encontradas: ");
        seriesPorCategoria
                .forEach(s ->
                        System.out.println(s.getTitulo() + " - Avaliação: " + s.getAvaliacao())
                );
    }

    private void listarSeriesPorTemporadaEAvaliacao(){
        System.out.println("Filtrar séries até quantas temporadas? ");
        System.out.print("Temporadas: ");
        var totalTemporadas = leitura.nextInt();
        leitura.nextLine();
        System.out.println("Com avaliação a partir de que valor? ");
        System.out.print("Avaliação: ");
        var avaliacao = leitura.nextDouble();
        leitura.nextLine();
        List<Serie> filtroSeries = repositorio.seriesPorTemporadaEAvaliacao(totalTemporadas, avaliacao);
        System.out.println("\n*** Séries filtradas ***");
        filtroSeries.forEach(s ->
                System.out.print(s.getTitulo() +
                        " - Temporadas: " + s.getTotalTemporadas() +
                        "  - Avaliação: " + s.getAvaliacao() + "\n"
                )
        );
    }

    private void listarEpisodiosPorTrecho() {
        System.out.println("Digite o nome do episodio para listar as séries!");
        System.out.print("Episodio: ");
        var nomeEpisodio = leitura.nextLine();
        List<Episodio> episodioEncontrados = repositorio.episodioPorTrecho(nomeEpisodio);
        System.out.println("\nSéries encontradas: ");
        episodioEncontrados.forEach(e ->
                        System.out.println(
                                "Série: " + e.getSerie().getTitulo() +
                                " - Temporada: " + e.getTemporada() +
                                " - Episodio " + e.getNumeroEpisodio() +
                                ": " + e.getTitulo() +
                                " - Avaliação: " + e.getAvaliacao()
                        )
                );
    }

    private void listarTop5EpisodiosPorSerie() {
        listarSeriesCadastradas(true, false);
        String nomeSerie = teclado();
        var serie = verificarSerieExistente(nomeSerie);
        if (serie.isPresent()) {
            var serieEncontrada = serie.get();
            List<Episodio> topEpisodios = repositorio.top5episodiosPorSerie(serieEncontrada);
            System.out.println("\n*** Séries filtradas ***");
            topEpisodios.forEach(e ->
                    System.out.println(
                            "Série: " + e.getSerie().getTitulo() +
                                    " - Temporada: " + e.getTemporada() +
                                    " - Episodio " + e.getNumeroEpisodio() +
                                    ": " + e.getTitulo() +
                                    " - Avaliação: " + e.getAvaliacao()
                    )
            );
        }
    }

    private void listarEpisodiosPorAno() {
        listarSeriesCadastradas(true, false);
        String nomeSerie = teclado();
        var serie = verificarSerieExistente(nomeSerie);
        if (serie.isPresent()) {
            var serieEncontrada = serie.get();
            System.out.println("Digite a partir de qual ano de lançamento!");
            System.out.print("Episodio: ");
            var anoLancamento = leitura.nextInt();
            leitura.nextLine();
            List<Episodio> episodiosAno = repositorio.episodiosPorSerieEAno(serieEncontrada, anoLancamento);
            System.out.println("\n*** Séries filtradas ***");
            episodiosAno.forEach(e ->
                    System.out.println(
                            "Série: " + e.getSerie().getTitulo() +
                                    " - Temporada: " + e.getTemporada() +
                                    " - Episodio " + e.getNumeroEpisodio() +
                                    ": " + e.getTitulo() +
                                    " - Avaliação: " + e.getAvaliacao() +
                                    " - Lançamento: " + e.getDataLancamento()
                    )
            );
        }
    }

    private void listarSeriePorAno() {
        System.out.println("Digite a partir de qual ano de lançamento!");
        System.out.print("Ano: ");
        var anoLancamento = leitura.nextInt();
        leitura.nextLine();
        List<Serie> SeriesAno = repositorio.seriePorAno(anoLancamento);
        System.out.println("\n*** Séries filtradas ***");
        SeriesAno.forEach(s ->
                System.out.println(
                        "Lançamento: " + s.getDataLancamento() +
                                " - " + s.getTitulo() +
                                " - Temporadas: " + s.getTotalTemporadas() +
                                " - Avaliação: " + s.getAvaliacao()
                )
        );
    }

    private DadosSerie obterDadosSerie(String nomeSerie) throws IOException, InterruptedException {
        var json = consumoAPI.obterDados(ENDERECO +
                nomeSerie.replace(" ", "+") + API_KEY);
        return conversor.obterDados(json, DadosSerie.class);
    }

    private void obterDadosTemporada(Serie serie) throws IOException, InterruptedException {
        List<DadosTemporada> temporadas = new ArrayList<>();
        for (int i = 1; i <= serie.getTotalTemporadas(); i++) {
            var json = consumoAPI.obterDados(ENDERECO + serie.getTitulo()
                    .replace(" ", "+") + API_KEY + "&season=" + i);
            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map(e -> new Episodio(d.numero(), e)))
                    .collect(Collectors.toList());
            serie.setEpisodios(episodios);
        }
    }

    private Optional<Serie> verificarSerieExistente(String nomeSerie) {
        if (!nomeSerie.isBlank()) {
            try {
                return repositorio.findByTituloContainingIgnoreCase(nomeSerie);
            } catch (IncorrectResultSizeDataAccessException ex) {
                System.out.println("\nSéries já cadastradas com esse nome no banco de dados:");
                List<Serie> serieEncontrada = repositorio.findAll();
                serieEncontrada.stream()
                        .filter(s -> s.getTitulo().toLowerCase().contains(nomeSerie.toLowerCase()))
                        .forEach(serie -> System.out.println(serie.getTitulo()));
            }
        }
        System.out.println("Série digitada não encontrada! Verifique o nome digitado! ");
        return Optional.empty();
    }
}


