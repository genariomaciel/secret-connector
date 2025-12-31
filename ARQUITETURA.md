# Estrutura e Documentação do Projeto

## 📁 Estrutura de Diretórios

```
secret-connector/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/leicam/secretconnector/
│   │   │                  ├── config/
│   │   │                  │   └── SecretManagerClientConfig.java   # Utilitário para criação de instâncias {@link SecretsManagerClient} do AWS SDK v2.
│   │   │                  ├── converter/
│   │   │                  │   ├── impl
│   │   │                  │   │   └── SecretConverters.java        # Conversores pré-definidos
│   │   │                  │   ├── SecretConverter                  # Interface para conversores
│   │   │                  ├── exception
│   │   │                  │   └── SecretManagerException.java      # Exceção customizada
│   │   │                  └── SecretManagerConnector.java          # Classe principal
│   │   └── resources/
│   │       └── application.properties                              # Arquivo de configurações
│   │
│   └── test/
│       └── java/
│           └── com/leicam/secretconnector/
│                          ├── config/
│                          │   ├── DatabaseCredentials.java         # DTO do secret
│                          │   ├── Secret.java                      # DTO do secret
│                          │   └── SecretValue.java                 # DTO do secret
│                          ├── SecretConverterTest.java             # Testes unitários
│                          └── SecretManagerConnectorTest.java      # Testes unitários
│
├── pom.xml                                                         # Configuração Maven
├── README.md                                                       # Documentação principal
├── EXEMPLOS.md                                                     # Exemplos de uso
├── .gitignore                                                      # Configuração Git
└── ARQUITETURA.md                                                  # Este arquivo

```

## 🏗️ Arquitetura

```


┌─────────────────────────────────────────────────────────┐
│   Sua Aplicação Java                                    │
└───────────────────────────┬─────────────────────────────┘
                            │
                            │ Usa
                            ▼
┌─────────────────────────────────────────────────────────┐
│   SecretManagerConnector                                │
│  ┌───────────────────────────────────────────────────┐  │
│  │ + get(name): String                               │  │
│  │ + get(name, Class<R>): R                          │  │
│  │ + get(name, SecretConverter<R>): R                │  │
│  │ + getSecretAsObject(name)                         │  │
│  │ + exists(name): boolean                           │  │
│  │ + getSecretsManagerClient(): SecretsManagerClient │  │ 
│  │ + close()                                         │  │
│  │ - getRawSecretString(name): String                │  │
│  └───────────────────────────────────────────────────┘  │
└───────────────────────────┬─────────────────────────────┘
                            │
                            │ Depende
                            ▼
┌─────────────────────────────────────────────────────────┐
│  AWS SDK (Secrets Manager)                              │
│  (software.amazon.awssdk:secretsmanager)                │
└───────────────────────────┬─────────────────────────────┘
                            │
                            │ Comunica com
                            ▼
┌────────────────────────────────────────────────────────┐
│   AWS Secrets Manager Service                          │
│   (Nuvem)                                              │
└────────────────────────────────────────────────────────┘
```



## 📦 Dependências Principais

| Dependência | Versão | Propósito |
|-------------|--------|----------|
| aws-sdk-secretsmanager | 2.20.0 | Acesso ao Secrets Manager |
| slf4j-api | 1.7.36 | Logging |
| junit-jupiter | 5.9.0 | Testes unitários |

## 🔐 Fluxo de Autenticação AWS

```
┌─────────────────────────────────────────────────┐
│ Busca Credenciais na Ordem:                     │
├─────────────────────────────────────────────────┤
│ 1. Variáveis de Ambiente (AWS_*)                │
│ 2. Arquivo ~/.aws/credentials                   │
│ 3. Arquivo ~/.aws/config                        │
│ 4. IAM Role (se em EC2)                         │
│ 5. Provedor de credenciais padrão               │
└─────────────────────────────────────────────────┘
```

## 🎯 Classes Principais

### SecretManagerConnector
- **Responsabilidade**: Gerenciar conexão com AWS Secrets Manager
- **Métodos Públicos**:
  - `get(String)`: String
  - `get(String, Class<T>)`: T
  - `get(String, SecretConverter<T>)`: T
  - `exists(String)`: boolean
  - `close()`: void

### SecretManagerException
- **Responsabilidade**: Tratar erros específicos
- **Herança**: RuntimeException
- **Uso**: Erros ao recuperar secrets

## 🧪 Estratégia de Testes

```
SecretManagerConnectorTest
├── testConnectorCreation()           ✓ Instanciação
├── testGetRegion()                   ✓ Região padrão
├── testConnectorWithSpecificRegion() ✓ Região customizada
├── testCloseConnection()             ✓ Fechamento
├── testSecretValue()                 ✓ DTO
├── testSecretValueEmpty()            ✓ Validação
└── testSecretManagerException()      ✓ Exceção
```

## 💡 Boas Práticas Implementadas

✅ **Separação de Responsabilidades**
- Conector gerencia conexão
- SecretValue encapsula dados
- Exceção customizada para erros

✅ **Logging**
- SLF4J para abstração
- Diferentes níveis (INFO, DEBUG, ERROR)

✅ **Recurso Management**
- Método `close()` para liberação
- Try-finally no código cliente

✅ **Documentação**
- JavaDoc em todas as classes
- Exemplos práticos

✅ **Testabilidade**
- Testes unitários com JUnit 5
- Exemplos de testes de integração

✅ **Tratamento de Erros**
- Exceções customizadas
- Logging de erros

## 📋 Checklist de Requisitos

- ✅ Biblioteca Java
- ✅ Usa AWS JDK (SDK 2.x)
- ✅ Conecta ao Secrets Manager
- ✅ Retorna secret solicitado
- ✅ Interface simples
- ✅ Tratamento de erros
- ✅ Logging
- ✅ Documentação completa
- ✅ Testes unitários

## 🔍 Próximas Melhorias (Futuro)

- [ ] Implementar AutoCloseable para try-with-resources
- [ ] Cache local com TTL
- [ ] Suporte a versionamento de secrets
- [ ] Integração com Spring Cloud Config
- [ ] Métricas e monitoramento
- [ ] Retry automático com backoff
- [ ] Pool de conexões
