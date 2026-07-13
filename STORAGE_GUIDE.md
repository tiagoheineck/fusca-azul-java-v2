# Guia de Armazenamento (Storage Layer)

Este documento explica como funciona a camada de armazenamento (repository) usada neste projeto de exemplo e fornece um guia prático para que um estudante possa aplicar os mesmos conceitos em um projeto diferente.

Sumário
- Objetivo da camada de armazenamento
- Padrão Repository / Interface
- Implementações comuns (In-memory, arquivo, JDBC/ORM)
- Assuntos importantes: concorrência, erros, transações
- Testes e boas práticas
- Como adaptar para outro projeto (passo-a-passo)

---

1) Objetivo da camada de armazenamento

- Separar a lógica de negócios (service) da persistência. Isso facilita testar, trocar a tecnologia de persistência e manter o código limpo.
- Exemplo no projeto: a interface `FuscaRepository` contém os métodos para adicionar, remover, atualizar e buscar `Fusca`. A `FuscaService` depende dessa interface e não conhece a implementação concreta.

2) Padrão Repository / Interface

- Defina uma interface com operações necessárias para a entidade. Exemplos comuns:
  - add(T entity)
  - removeById(String id)
  - update(T entity)
  - findById(String id)
  - findAll()
  - findByColor(String color) // método específico de negócio

- Vantagens:
  - Troca de implementação sem alterar a camada de serviço
  - Testabilidade (mock/implementação in-memory)
  - Contrato claro entre camadas

3) Implementações comuns

- In-memory (ex.: `InMemoryFuscaRepository`)
  - Útil para testes e protótipos.
  - Implementa a interface usando coleções (Map/List).
  - Considere sincronização se o repositório for usado por múltiplas threads.

  Exemplo simplificado (pseudocódigo):
  ```java
  public class InMemoryFuscaRepository implements FuscaRepository {
      private final Map<String, Fusca> storage = new HashMap<>();

      public void add(Fusca f) { storage.put(f.getId(), f); }
      public boolean removeById(String id) { return storage.remove(id) != null; }
      public boolean update(Fusca f) {
          if (!storage.containsKey(f.getId())) return false;
          storage.put(f.getId(), f);
          return true;
      }
      public Fusca findById(String id) { return storage.get(id); }
      public List<Fusca> findAll() { return new ArrayList<>(storage.values()); }
      public List<Fusca> findByColor(String color) { /* filter */ }
  }
  ```

- File-based (JSON/CSV)
  - Serializa objetos em disco. Bom para persistência simples sem banco de dados.
  - Escolha um formato (JSON é legível e interoperável).
  - Cuidados: concorrência (leitura/escrita), corrupção de arquivo, atomicidade (escrever em arquivo temporário e mover).

- JDBC / Relacional
  - Use quando precisar de persistência robusta, consulta complexa e escalabilidade.
  - Mapeie campos do objeto para colunas. Use PreparedStatements para evitar SQL injection.
  - Considere usar uma camada de mapeamento (DAO) ou um ORM (JPA/Hibernate) para reduzir boilerplate.

- NoSQL
  - Use quando os dados são semi-estruturados ou quando se precisa de alta escala horizontal.

4) Assuntos importantes

- Thread-safety
  - In-memory: proteja estruturas compartilhadas com Collections.synchronizedMap, ConcurrentHashMap ou sincronização explícita.
  - File-based: evite leituras/gravações concorrentes; use locks ou filas de escrita.

- Consistência e transações
  - Em bancos relacionais, use transações para operações que envolvem múltiplas alterações.
  - Em sistemas simples, documente as limitações (ex.: operação não-atomica sobre múltiplos arquivos).

- Erros e convenções de retorno
  - Decida uma convenção: retornar boolean (sucesso/fracasso), retornar Optional<T>, ou lançar exceções.
  - Exemplo do projeto: `removeById` e `update` retornam boolean; `findById` retorna o objeto ou null. Em projetos mais modernos prefira Optional<T> para evitar null.

- Validação e invariantes
  - Valide dados no service antes de chamar o repository (por exemplo, formato do id, campos obrigatórios).

5) Testes

- Unit tests
  - Teste a camada de serviço usando um repositório in-memory ou mocks (Mockito).
  - Isolar dependências: injete `FuscaRepository` no construtor de `FuscaService`.

- Testes de integração
  - Teste a implementação real do repositório (por ex. SQLite em memória, H2 ou arquivo temporário) para verificar persistência real.

- Cobertura de casos de erro
  - Simule falhas de IO, concorrência e verifique comportamento.

6) Como adaptar para outro projeto — passo-a-passo

Checklist para aplicar a arquitetura de storage em um novo projeto:

1. Identifique as entidades de domínio (ex.: Carro, Usuário).
2. Defina uma interface Repository para cada entidade (ou uma genérica parametrizada).
3. Liste operações necessárias (CRUD + buscas específicas).
4. Implemente pelo menos uma versão in-memory para testes e desenvolvimento rápido.
5. Implemente a versão de produção desejada (JDBC, ORM, arquivo, NoSQL).
6. Injete a implementação concreta nos services via construtor ou um pequeno factory/provider/DI.
7. Escreva testes unitários da service usando a in-memory implementation ou mocks.
8. Escreva testes de integração para a implementação real (banco em memória, arquivo temporário).
9. Documente limitações (concorrência, transações, performance).

Exemplo prático de adaptação (como trocar implementação no `Main`):

```java
// Em um projeto novo, ao iniciar a aplicação
FuscaRepository repo;
if (config.useInMemory) {
    repo = new InMemoryFuscaRepository();
} else {
    repo = new JdbcFuscaRepository(dataSource);
}
FuscaService service = new FuscaService(repo);
```

7) Boas práticas e recomendações

- Use interfaces claras e pequenas. Evite expor detalhes de implementação no contrato.
- Prefira retornar Optional<T> ao invés de null quando possível.
- Não acople a camada de negócio a APIs específicas de persistência.
- Trate erros de IO e documente o comportamento.
- Pense em performance: pagine findAll() em coleções grandes.
- Se o projeto crescer, considere introduzir uma camada de DTOs e mapeamento entre domínio e persistência (ex.: mappers).

8) Exemplos rápidos de código

- Assinatura da interface (exemplo):

```java
public interface FuscaRepository {
    void add(Fusca f);
    boolean removeById(String id);
    boolean update(Fusca f);
    Fusca findById(String id);
    List<Fusca> findAll();
    List<Fusca> findByColor(String color);
}
```

- Esqueleto de um repositório JDBC:

```java
public class JdbcFuscaRepository implements FuscaRepository {
    private final DataSource ds;
    public JdbcFuscaRepository(DataSource ds) { this.ds = ds; }

    public void add(Fusca f) {
        // try-with-resources, PreparedStatement, insert
    }
    public boolean removeById(String id) { /* delete */ }
    public boolean update(Fusca f) { /* update */ }
    public Fusca findById(String id) { /* select */ }
    public List<Fusca> findAll() { /* select * */ }
}
```

9) Checklist final para o estudante

- [ ] Definir as interfaces repository para suas entidades
- [ ] Implementar in-memory para desenvolvimento e testes
- [ ] Implementar a persistência desejada (arquivo, JDBC, ORM)
- [ ] Escrever testes unitários e de integração
- [ ] Garantir tratamento de erros e concorrência quando necessário
- [ ] Documentar limitações e instruções de configuração

---

Se você quiser, eu posso:
- Gerar uma implementação exemplo de `JdbcFuscaRepository` ou `FileFuscaRepository` adaptada ao seu projeto;
- Gerar testes JUnit para `FuscaService` usando `InMemoryFuscaRepository`.

Diga qual opção prefere que eu implemente a seguir.
