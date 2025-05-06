# 📺 ScreenMatch - Aplicação Java para gerenciamento de séries com integração OMDb API

Este projeto Java permite o cadastro, listagem, busca e análise de séries de TV utilizando dados da [OMDb API](https://www.omdbapi.com/). A aplicação permite persistência no banco de dados e fornece um menu interativo para interação via terminal.

## 🧩 Funcionalidades

- Buscar e cadastrar séries automaticamente via OMDb API.
- Listar séries cadastradas com opções de ordenação e filtro.
- Exibir e filtrar episódios por:
    - Nome
    - Ano de lançamento
    - Avaliação
    - Gênero
    - Ator
- Listar Top 5 séries e episódios com base na avaliação.
- Adicionar atores manualmente a uma série cadastrada.

## 💻 Tecnologias Utilizadas

- **Java 17+**
- **Spring Data JPA**
- **OMDb API** para busca de dados das séries
- **Banco de Dados** relacional (ex: PostgreSQL, H2)
- **Scanner / Console** para interação com o usuário
- **Maven/Gradle** (dependendo do seu build system)

## 🔧 Requisitos

- JDK 17 ou superior
- Variável de ambiente `OMDB_KEY` com a chave da API OMDb
- Banco de dados configurado (ver aplicação Spring para configurações)

## ▶️ Como executar

1. **Clone o repositório:**

   ```bash
   git clone https://github.com/seu-usuario/serie-manager.git
   cd serie-manager
