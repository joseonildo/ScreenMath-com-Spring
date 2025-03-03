package br.com.joseonildo.screenmatch.principal;

import br.com.joseonildo.screenmatch.model.DadosSerie;
import br.com.joseonildo.screenmatch.model.DadosTemporada;
import br.com.joseonildo.screenmatch.model.Serie;
import br.com.joseonildo.screenmatch.repository.SerieRepository;
import br.com.joseonildo.screenmatch.service.ConsumoAPI;
import br.com.joseonildo.screenmatch.service.ConverteDados;
import org.springframework.dao.DataIntegrityViolationException;

import java.io.IOException;
import java.util.*;

public class Principal {
    private final Scanner leitura = new Scanner(System.in);
    private final ConsumoAPI consumoAPI = new ConsumoAPI();
    private final ConverteDados conversor = new ConverteDados();
    private final String API_KEY = "&apikey=fc511a2f";
    private final String ENDERECO = "http://www.omdbapi.com/?&t=";
    private final List<DadosSerie> dadosSeries = new ArrayList<>();
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
                    buscarSerieWeb();
                    break;
                case "2":
                    buscarEpisodioPorSerie();
                    break;
                case "3":
                    listarSeriesBuscadas();
                    break;
                case "0":
                    System.out.println("Fechando programa...");
                    break;
                default:
                    System.out.println("Opção inválida, tente novamente");
            }
        } while (!opcao.equals("0"));
    }

    private DadosSerie getDadosSerie(String nomeSerie) throws IOException, InterruptedException {
        var json = consumoAPI.obterDados(ENDERECO + nomeSerie + API_KEY);
        return conversor.obterDados(json, DadosSerie.class);
    }

    private String teclado() {
        System.out.print("\nDigite o nome da série ou ENTER para finalizar \nNome: ");
        return leitura.nextLine().replace(" ", "+");
    }

    private void buscarSerieWeb() throws IOException, InterruptedException {
        String nomeSerie;
        while (!(nomeSerie = teclado()).isBlank()) {
            DadosSerie dadosSerie = getDadosSerie(nomeSerie);
            Serie serie = new Serie(dadosSerie);
            System.out.println();
            ExibeDados.exibeSerie(serie);
            try {
                repositorio.save(serie);
            } catch (DataIntegrityViolationException ex) {
                System.out.println("\nSérie já existente no banco de dados! Pesquise outra!!!");
            }
        }
    }

    private void buscarEpisodioPorSerie() throws IOException, InterruptedException {
        String nomeSerie;
        while (!(nomeSerie = teclado()).isBlank()) {
            DadosSerie dadosSerie = getDadosSerie(nomeSerie);
            List<DadosTemporada> temporadas = new ArrayList<>();
            for (int i = 1; i <= Integer.parseInt(dadosSerie.totalTemporadas()); i++) {
                var json = consumoAPI.obterDados(ENDERECO + dadosSerie.titulo()
                        .replace(" ", "+") + API_KEY + "&season=" + i);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }
            System.out.println();
            //temporadas.forEach(System.out::println);
            ExibeDados.exibeTeporada(temporadas);
        }
    }

    private void listarSeriesBuscadas() {
        List<Serie> series = repositorio.findAll();

        /*series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);*/

        series = series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .toList();
        series.forEach(ExibeDados::exibeSerie);

    }
}




