# VigIa

API REST para gestão de escolas/treinamentos: alunos, cursos, turmas, matrículas e valores de curso.

Este repositório contém a backend API desenvolvida com Spring Boot para gerenciar alunos, cursos, turmas e matrículas.

---

## Sumário

- Descrição
- Features
- Stack
- Pré-requisitos
- Configuração
- Instalação
- Executando (desenvolvimento e produção)
- Endpoints principais (exemplos de uso)
- Testes
- Contribuindo
- Licença e contato

---

## Descrição

VigIa é uma API REST desenvolvida em Java + Spring Boot para gerenciar o ciclo de vida de cursos e turmas, cadastro de alunos, controle de matrículas e valores de cursos. Foi desenvolvida com foco em simplicidade e extensibilidade.

## Features

- CRUD de Alunos
- CRUD de Cursos
- CRUD de Turmas
- Matrículas (criar, alterar status, listar por aluno/turma)
- Atribuição de valores para cursos
- Pesquisa de alunos por filtros
- Documentação OpenAPI disponível (se configurado)

## Stack

- Java 21
- Spring Boot 3.3.x
- Spring Data JPA (Hibernate)
- MySQL / PostgreSQL (configurável via properties)
- Lombok
- springdoc-openapi (UI Swagger)
- Maven

## Pré-requisitos

- Java 21 JDK instalado
- Maven (pode usar o wrapper incluido `mvnw` / `mvnw.cmd` no Windows)
- Banco de dados MySQL ou PostgreSQL (ou usar H2 para testes)

## Configuração

O arquivo de configuração principal está em `src/main/resources/application.properties`.
Exemplo (valores de exemplo — não use senhas em texto em produção):

```
# Banco (exemplo MySQL)
spring.datasource.url=jdbc:mysql://localhost:3306/vigia
spring.datasource.username=root
spring.datasource.password=pass
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.format_sql=true

# Porta do servidor
server.port=8081
```

Recomendação: prefira variáveis de ambiente ou um `application-*.properties` para separar `dev`/`prod`. Exemplos de variáveis de ambiente que funcionam com Spring Boot:

- SPRING_DATASOURCE_URL
- SPRING_DATASOURCE_USERNAME
- SPRING_DATASOURCE_PASSWORD
- SPRING_JPA_HIBERNATE_DDL_AUTO
- SERVER_PORT

## Instalação

1. Clone o repositório:

```powershell
git clone <URL-DO-REPO>
cd vig-ia
```

2. Build do projeto (Windows):

```powershell
./mvnw.cmd clean package
```

ou, se preferir usar o Maven instalado:

```powershell
mvn clean package
```

## Executando

Modo desenvolvimento (usa o wrapper):

```powershell
./mvnw.cmd spring-boot:run
```

Rodando o JAR gerado (produção):

```powershell
./mvnw.cmd clean package
java -jar target/vig-ia-0.0.1-SNAPSHOT.jar
```

Se você usa Windows PowerShell e o `mvnw.cmd` já está no projeto, execute `.\mvnw.cmd spring-boot:run` no PowerShell.

## Endpoints principais

Base: http://localhost:8081 (porta definida em `application.properties`)

Observação: sempre envie `Content-Type: application/json` nos requests que tenham body.

1) Alunos

- GET /api/alunos
  - Lista todos os alunos ativos
  - Exemplo:
    ```bash
    curl http://localhost:8081/api/alunos
    ```

- GET /api/alunos/all
  - Lista todos (incluindo removidos)

- POST /api/alunos/search
  - Pesquisa por filtros (campo `SearchDto`)
  - Body (exemplo mínimo):
    ```json
    {
      "nome": "João"
    }
    ```
  - Exemplo curl:
    ```bash
    curl -X POST "http://localhost:8081/api/alunos/search" -H "Content-Type: application/json" -d "{\"nome\":\"João\"}"
    ```

- GET /api/alunos/{id}
  - Busca aluno por UUID

- GET /api/alunos/lead
  - Retorna dados preparados para exibição em um lead/dashboard

- POST /api/alunos
  - Cria um novo aluno (body `AlunosDto`)
  - Exemplo de payload:
    ```json
    {
      "nome": "Maria Silva",
      "cpf": "12345678901",
      "rg": "MG1234567",
      "orgaoEmissor": "SSP",
      "nacionalidade": "Brasileira",
      "naturalidade": "Belo Horizonte",
      "nomeMae": "Ana Silva",
      "nomePai": "João Silva",
      "sexo": "F",
      "estadoCivil": "Solteiro",
      "escolaridade": "Ensino Médio",
      "profissao": "Estudante",
      "cep": "30140000",
      "endereco": "Rua A",
      "numero": "100",
      "complemento": "Apto 101",
      "bairro": "Centro",
      "cidade": "Belo Horizonte",
      "estado": "MG",
      "telefone": "31999999999",
      "email": "maria@example.com",
      "dataNascimento": "1990-05-20",
      "optin": true
    }
    ```
  - Exemplo curl:
    ```bash
    curl -X POST "http://localhost:8081/api/alunos" -H "Content-Type: application/json" -d @aluno.json
    ```

- PUT /api/alunos/{id}
  - Atualiza dados do aluno (mesmo payload do `AlunosDto`)

- DELETE /api/alunos/{id}
  - Remove logicamente o aluno

2) Cursos

- GET /api/cursos
  - Lista todos os cursos (retorna `CursosDto`)

- GET /api/cursos/{id}
  - Busca curso por id

- GET /api/cursos/lead
  - Cursos para exibição no lead

- POST /api/cursos
  - Cria novo curso (`CursosDto`)
  - Exemplo payload:
    ```json
    {
      "nome": "Java Básico",
      "descricao": "Introdução ao Java",
      "cargaHoraria": 40.0,
      "valor": 199.90
    }
    ```

- PUT /api/cursos/{id}
  - Atualiza curso

- DELETE /api/cursos/{id}
  - Deleta curso

3) CursoValor

- POST /api/curso-valor/{cursoId}/valor
  - Atribui um valor a um curso (body `CursoValor`)
  - Observação: o controller recebe `cursoId` na URL mas o serviço atual usa o objeto recebido; confira comportamento ao integrar com front-end.

- GET /api/curso-valor/{cursoId}
  - Retorna o valor atual do curso

4) Matrículas

- POST /api/matriculas
  - Adiciona aluno a turma (`MatriculaDto`)
  - Exemplo payload:
    ```json
    {
      "alunoId": "uuid-do-aluno",
      "turmaId": "uuid-da-turma",
      "status": "MATRICULADO",
      "dataMatricula": "2025-01-15"
    }
    ```

- PATCH /api/matriculas/status
  - Atualiza status da matrícula (`MatriculaStatusDto`)
  - Exemplo payload:
    ```json
    {
      "status": "CANCELADO",
      "matriculaUnica": "uuid-da-matricula"
    }
    ```

- GET /api/matriculas/aluno/{alunoId}
  - Lista matrículas de um aluno

- GET /api/matriculas/turma/{turmaId}
  - Lista matrículas de uma turma

5) Turmas

- GET /api/turmas
  - Lista todas as turmas (`TurmasDto`)

- GET /api/turmas/{id}
  - Busca turma por id

- POST /api/turmas
  - Cria turma (`TurmasDto`)
  - Exemplo payload:
    ```json
    {
      "nome": "Turma A",
      "cursoId": "uuid-do-curso",
      "turno": "NOITE",
      "dataInicio": "2025-02-01",
      "dataTermino": "2025-07-01",
      "capacidadeMaxima": 30
    }
    ```

- PUT /api/turmas/{id}
  - Atualiza turma

- PATCH /api/turmas/{id}/status?status={TurmaStatus}
  - Atualiza status da turma (enum `TurmaStatus`)

- PATCH /api/turmas/{turmaId}/aluno/{alunoId}/lead?exibirNoLead={true|false}
  - Atualiza se um aluno deve aparecer no lead daquela turma

- DELETE /api/turmas/{id}
  - Remove turma

## Swagger / OpenAPI

O projeto inclui `springdoc-openapi`. Se o aplicativo estiver em execução, a UI costuma ficar disponível em:

- http://localhost:8081/swagger-ui/index.html
- http://localhost:8081/v3/api-docs

Caso não apareça, confirme se a dependência está habilitada e se as rotas do Spring não foram customizadas.

## Testes

- Rodar testes unitários/integrados:

```powershell
./mvnw.cmd test
```

Dica: use o perfil de testes com H2 para execuções mais rápidas e independentes do banco local.

## Contribuindo

1. Fork o repositório
2. Crie uma branch feature/bugfix
3. Faça commits pequenos e com mensagem clara
4. Abra um Pull Request descrevendo a mudança

Sugestões de melhorias:
- Adicionar tratamento global de exceções
- Adicionar testes de integração
- Adicionar Dockerfile e docker-compose
- Melhorar documentação OpenAPI (exemplos nos DTOs)

## Licença

Escolha uma licença (MIT, Apache-2.0, etc.). Atualmente o `pom.xml` não contém informação de licença — adicione a sua preferida.

## Contato

Mantenedor: (Theo Ronna - theo@ronna.com.br)

Issues e dúvidas: abra uma issue neste repositório.

---
