package br.com.joseonildo.screenmatch.service;

import br.com.joseonildo.screenmatch.model.DadosTraducao;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URLEncoder;

public class ConsultaMyMemory {
    public static String obterTraducao(String text)  {
        ObjectMapper mapper = new ObjectMapper();

        ConsumoAPI consumo = new ConsumoAPI();

        String texto = URLEncoder.encode(text);
        String langpair = URLEncoder.encode("en|pt-br");

        String url = "https://api.mymemory.translated.net/get?q=" + texto + "&langpair=" + langpair;

        String json = null;
        try {
            json = consumo.obterDados(url);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        DadosTraducao traducao;
        try {
            traducao = mapper.readValue(json, DadosTraducao.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return traducao.responseData().translatedText;
    }
}