# **Guia de Injeção de Conversor Padrão**

## **Visão Geral**

O `SecretManagerConnector` suporta **injeção de conversor padrão** no construtor. Isso permite que você defina um conversor que será utilizado em todas as chamadas sem precisar especificá-lo cada vez, alem das opções pré-definidas.

## **Construtores Disponíveis**

### 1. Construtor sem Argumentos
- Será criado uma instância usando a região padrão da aws, usando o conversor padrão do tipo `asString()`
```java
    SecretManagerConnector<String> connector = new SecretManagerConnector();
```

### 2. Construtor injetando Região e o profile
- Será criado uma instancia usando a região e o profile informado, usando o conversor padrão do tipo `asString()`
```java
    SecretManagerConnector<String> connector = new SecretManagerConnector('sa-east-1', 'dev');
```

### 3. Construtor injetando SecretsManagerClient
- Será criado uma instancia usando a instancia do SecretsManagerClient injetado usando o conversor padrão do tipo `asString()`
```java
    
    SecretManagerConnector<String> connector = new SecretManagerConnector(SecretManagerClientConfig.create())
```

### 4. Construtor injetando Região e Conversor (NOVO)
```java
    SecretsManagerClient secretsManagerClient = SecretManagerClientConfig.create();
    SecretConverter<Integer> converter = SecretConverters.asInteger()
    SecretManagerConnector<Integer> connector = new SecretManagerConnector(converter, SecretsManagerClient secretsManagerClient)

    //# Forma simplificada
    SecretManagerConnector connector = new SecretManagerConnector(
            SecretConverters.asInteger(), 
            SecretManagerClientConfig.create()
        );
```

### 4. Usando com Spring Boot
- Deve-se criar um bean que busque a credencial no Secret Manager ao iniciar a aplicação ou em algum momento após o start da aplicação
- Deve-se atentar ao custo de utilização, o serviço Secret Manager tem cobrança por requisição

## **Recuperando Secret**
### Exemplos Básicos
### Usando com o conversor padrão
- Recuperando a secret usando o conversor da instancia.
```java
    SecretManagerConnector<String> connector = new SecretManagerConnector();

    try {
        String secret = connector.get("secret");
    } finally {
        connector.close();
    }    
```

- Recuperando a secret usando o conversor especifico.

```java
    SecretManagerConnector<String> connector = new SecretManagerConnector();

    try {
        SecretDto secret = connector.get("secret", SecretConverters.asObject(SecretDto.class));
        Integr secret = connector.get("secret-integer", SecretConverters.asInteger());
    } finally {
        connector.close();
    }
    //# ou
    try {
        SecretDto secret = connector.get("secret", SecretDto.class);
        Integr secret = connector.get("secret-integer", SecretConverters.asInteger());
    } finally {
        connector.close();
    }
```

## **SecretConverters** (Classe Utilitária)
- **Localização**: `com.leicam.secretconnector.SecretConverters`
- **Responsabilidade**: Fornecer conversores pré-configurados
- **Métodos Disponíveis**:
  - `asString()` → String
  - `asInteger()` → Integer
  - `asLong()` → Long
  - `asBoolean()` → Boolean
  - `asDouble()` → Double
  - `asArray(delimiter)` → String[]
  - `asJsonObject(Class<T>)` → T (usando Jackson)
  - `custom(converter)` → T (conversor customizado)

```java
    public class SecretConverters {
        public static SecretConverter<String> asString() { ... }
        public static SecretConverter<Integer> asInteger() { ... }
        public static <T> SecretConverter<T> asJsonObject(Class<T> clazz) { ... }
        // ... outros conversores
    }
```
## **Exemplos Práticos**

### Exemplo 1: Conversor Integer Injetado

```java
SecretManagerConnector<Integer> connector = new SecretManagerConnector(
    "us-east-1",
    SecretConverters.asInteger()
);

try {
    Integer timeout = connector.get("app/timeout");
    System.out.println("Timeout: " + timeout + "ms");
    
} finally {
    connector.close();
}
```

### Exemplo 2: Conversor Array Injetado

```java
SecretManagerConnector<String[]> connector = new SecretManagerConnector(
    "us-east-1",
    SecretConverters.asArray(",")
);

try {
    String[] servidores = connector.get("infra/servidores");
    for (String servidor : servidores) {
        System.out.println(servidor);
    }
    
} finally {
    connector.close();
}
```

### Exemplo 3: Conversor Boolean Injetado

```java
SecretManagerConnector<Boolean> connector = new SecretManagerConnector(
    "us-east-1",
    SecretConverters.asBoolean()
);

try {
    Boolean prodMode = connector.get("app/prod-mode");
    if (prodMode) {
        System.out.println("Modo produção ativo");
    }
    
} finally {
    connector.close();
}
```

### Exemplo 4: Conversor Customizado Injetado

```java
// Conversor que retorna só a primeira parte (antes de ":")
SecretConverter<String> usernameExtractor = (secret) -> secret.split(":")[0];

SecretManagerConnector connector = new SecretManagerConnector(
    "us-east-1",
    usernameExtractor
);

try {
    String username = connector.get("database/credentials");
    System.out.println("Usuário: " + username);
    
} finally {
    connector.close();
}
```

### Exemplo 5: Combinar Conversor Padrão com Específico

```java
SecretManagerConnector<Integer> connector = new SecretManagerConnector(
    "us-east-1",
    SecretConverters.asInteger()  // Padrão
);

try {
    // Usa o conversor padrão (Integer)
    Integer port = connector.get("database/port");
    
    // Usa um conversor específico (Boolean), sobrescrevendo o padrão
    Boolean ssl = connector.get("database/ssl", SecretConverters.asBoolean());
    
    System.out.println("Porta: " + port);
    System.out.println("SSL: " + ssl);
    
} finally {
    connector.close();
}
```


## Casos de Uso

### 1. Aplicação que sempre trabalha com Integer

```java
SecretManagerConnector<Integer> connector = new SecretManagerConnector(
    "sa-east-1",
    SecretConverters.asInteger()
);

// Todos os secrets dessa aplicação são números
Integer dbPort = connector.get("db/port");
Integer cachePort = connector.get("cache/port");
Integer apiPort = connector.get("api/port");
```

### 2. Configuração com conversor Array

```java
SecretManagerConnector<String[]> connector = new SecretManagerConnector(
    "sa-east-1",
    SecretConverters.asArray(";")  // Delimiter diferente
);

// Todos os secrets são listas separadas por ";"
String[] hosts = connector.get("db/replicas");
String[] nodes = connector.get("cluster/nodes");
String[] endpoints = connector.get("api/endpoints");
```

### 3. Conversor JSON padrão

```java
SecretManagerConnector<Void> connector = new SecretManagerConnector(
    "sa-east-1",
    SecretConverters.asJsonObject(DatabaseCredentials.class)
);

// Todos os secrets são objetos JSON do tipo DatabaseCredentials
DatabaseCredentials mainDb = connector.get("databases/main");
DatabaseCredentials replicaDb = connector.get("databases/replica");
```

## Thread Safety

- ✅ Os construtores são thread-safe
- ✅ O conversor injetado é imutável
- ✅ Múltiplas threads podem usar o mesmo conector

```java
SecretManagerConnector<Integer> connector = new SecretManagerConnector(
    "us-east-1",
    SecretConverters.asInteger()
);

// Thread 1
new Thread(() -> {
    Integer port = connector.get("db/port");
}).start();

// Thread 2
new Thread(() -> {
    Integer timeout = connector.get("app/timeout");
}).start();
```

## Logging

O logging automático inclui informações sobre o conversor injetado:

```
INFO  SecretManagerConnector inicializado com região: us-east-1 com conversor padrão: SecretConverters
DEBUG Convertendo secret 'db/port' para tipo genérico
INFO  Secret 'db/port' convertido com sucesso para tipo Integer
```

## Boas Práticas

✅ **DO's**
- Injetar um conversor padrão se a maioria dos secrets for do mesmo tipo
- Usar conversores pré-configurados quando possível
- Documentar qual conversor foi injetado
- Usar `get(secret-name)` para secrets que usam o conversor padrão
- Usar `get(secret-name, converter)` para exceções
- Criar o connector no inicio da aplicação obter a secret e fechar, evita custo com requisição desnecessária

❌ **DON'Ts**
- Não injetar um conversor muito genérico (ex: lambda complexa)
- Não mudar de conversor durante a execução (crie um novo conector)
- Não ignorar se o conversor injetado é null
- Não reutilizar conector com conversor após close()


## Exemplo Completo

```java
import com.leicam.secretconnector.*;

public class Application {
    public static void main(String[] args) {
        // Criar conector com conversor Integer injetado
        SecretManagerConnector<Integer> connector = new SecretManagerConnector(
            "sa-east-1",
            SecretConverters.asInteger()
        );
        
        try {
            
            // Usar o conversor padrão
            Integer dbPort = connector.get("database/port");
            Integer cachePort = connector.get("cache/port");
            Integer apiPort = connector.get("api/port");
            
            System.out.println("DB Port: " + dbPort);
            System.out.println("Cache Port: " + cachePort);
            System.out.println("API Port: " + apiPort);
            
        } finally {
            connector.close();
        }
    }
}
```

## Changelog

### Versão 1.0.0
- ✅ Suporte a múltiplos tipos de conversores injetáveis
- ✅ Logging automático do conversor injetado
