package br.com.joseonildo.screenmatch.principal;

import br.com.joseonildo.screenmatch.model.DadosSerie;
import br.com.joseonildo.screenmatch.model.DadosTemporada;
import br.com.joseonildo.screenmatch.model.Episodio;
import br.com.joseonildo.screenmatch.model.Serie;
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
    //private String teste =

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
                    listarSeriesCadastradas(true,false,false);
                    break;
                case "3":
                    listarSeriesCadastradas(false,false,false);
                    break;
                case "4":
                    listarSeriesCadastradas(false,true,false);
                    break;
                case "5":
                    listarEpisodiosPorSerie(false);
                    break;
                case "6":
                    buscarSeriesPorAtor();
                    break;
                case "7":
                    listarSeriesCadastradas(false,false,true);
                    break;
                case "8":
                    buscarSeriesporCategoria();
                    break;
                case "9":
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
                    ExibeDados.exibeSerie(serie, false);
                } catch (NumberFormatException ex) {
                    System.out.println("\nERRO!! Nenhuma série nova foi encontrada com o nome: " + nomeSerie);
                    System.out.println("ATENÇÃO: Verifique o nome e tente novamente!");
                }
            }
        } while (true);
    }

    private void listarSeriesCadastradas(boolean lista, boolean exibeEpisodios, boolean top5) {
        recuperaDadosBanco();
        if (lista) {
            System.out.println("\nSéries encontradas no banco de dados:\n");
            series.stream()
                    .sorted(Comparator.comparing(Serie::getId))
                    .forEach(s ->
                            System.out.println(s.getId() +
                                    " - " + s.getTitulo() +
                                    " - Avaliação: " + s.getAvaliacao())
                    );
        } else if (top5){
            System.out.println("\nTOP 5 Séries encontradas no banco de dados:");
            List<Serie> serieTop5 = repositorio.findTop5ByOrderByAvaliacaoDesc();
            serieTop5.forEach(s ->
                    System.out.println(s.getTitulo() + " - Avaliação: " + s.getAvaliacao()));
            /*series.stream()
                    .sorted(Comparator.comparing(Serie::getAvaliacao).reversed())
                    .limit(5)
                    .forEach(s ->
                            System.out.println(s.getTitulo() +
                                    " - Avalição: " + s.getAvaliacao())
                    );*/
        } else {
            series = series.stream()
                    .sorted(Comparator.comparing(Serie::getGenero))
                    .toList();
            series.forEach(s -> ExibeDados.exibeSerie(s,exibeEpisodios));
        }
    }

    private void listarEpisodiosPorSerie(Boolean modificarSerie) {
        listarSeriesCadastradas(true, false, false);
        String nomeSerie = teclado();
        if (!nomeSerie.isBlank()) {
            var serie = verificarSerieExistente(nomeSerie);
            if (serie.isPresent()) {
                var serieEncontrada = serie.get();
                if (modificarSerie) {
                    System.out.print("Digite o nome do ator para inclusão: ");
                    if (!(nomeSerie = leitura.nextLine()).isBlank()) {
                        serieEncontrada.setAtores(nomeSerie);
                        repositorio.save(serieEncontrada);
                    }
                }
                ExibeDados.exibeSerie(serieEncontrada, true);
            } else {
                System.out.println("Série digitada não encontrada! Verifique o nome digitado! ");
                listarEpisodiosPorSerie(modificarSerie);
            }
        }
    }

    private void buscarSeriesPorAtor() {
        System.out.print("Qual o nome do ator para a busca: ");
        var nomeAtor = leitura.nextLine();
        List<Serie> seriesEncontradas;
        System.out.print("Qual a Avaliação mínima: ");
        double avaliacao;
        try {
            avaliacao = Double.parseDouble(leitura.nextLine());
        } catch (NumberFormatException e) {
            avaliacao = 0.0;
        }
        seriesEncontradas = repositorio.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor, avaliacao);
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

    private void buscarSeriesporCategoria() {

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

    private void recuperaDadosBanco() {
        series = repositorio.findAll();
    }

    private Optional<Serie> verificarSerieExistente(String nomeSerie) {
        try {
            return repositorio.findByTituloContainingIgnoreCase(nomeSerie);
        } catch (IncorrectResultSizeDataAccessException ex) {
            System.out.println("\nSéries já cadastradas com esse nome no banco de dados:");
            List<Serie> serieEncontrada = repositorio.findAll();
            serieEncontrada.stream()
                    .filter(s -> s.getTitulo().toLowerCase().contains(nomeSerie.toLowerCase()))
                    .forEach(serie -> System.out.println(serie.getTitulo()));
        }
        return Optional.empty();
    }
}

/*
Séries encontradas no banco de dados:

1 - Lost - Avaliação: 8.3
2 - Arrow - Avaliação: 7.5
3 - The Flash - Avaliação: 7.5
4 - Sonic X - Avaliação: 6.2
5 - Sonic Boom - Avaliação: 6.9
6 - Sonic Prime - Avaliação: 7.2
7 - Game of Thrones - Avaliação: 9.2
8 - The Mandalorian - Avaliação: 8.6
9 - The Last of Us - Avaliação: 8.7

 */




