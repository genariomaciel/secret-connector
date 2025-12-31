Esta é uma iniciativa promovida pelo canal tech ponto tech.

# **Secret Connector**

Uma biblioteca Java para conectar ao **AWS Secrets Manager** e recuperar secrets de forma simples e eficiente.

## Características

- ✅ Integração com AWS SDK for Java 2.x
- ✅ Suporte a múltiplas regiões AWS
- ✅ Interface simples para recuperação de secrets
- ✅ Tratamento robusto de exceções
- ✅ Logging com SLF4J
- ✅ Testes unitários com JUnit 5

## Requisitos

- Java 11 ou superior
- Maven 3.6+
- Credenciais AWS configuradas (AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY)
- Permissões para acessar AWS Secrets Manager

## Instalação

### 1. Clonar o repositório

```bash
cd c:\dev\source\projetos\aws

git clone git@github.com:genariomaciel/secret-connector.git

cd secret-connector
```

### 2. Compilar o projeto

```bash
mvn clean install
```

### 3. Usar em seu projeto

Adicione a dependência no seu `pom.xml`:

```xml
<dependency>
    <groupId>com.tech-ponto-tech</groupId>
    <artifactId>secret-connector</artifactId>
    <version>1.0.0</version>
</dependency>
```

## API Principal

### SecretManagerConnector

#### Construtores

- `SecretManagerConnector()` - Inicializa com região padrão (us-east-1)
- `SecretManagerConnector(String region, String profileName)` - Inicializa com região e profile específicos
- `SecretManagerConnector(SecretsManagerClient secretsManagerClient)` - Inicializa com SecretsManagerClient pré-configurado
- `SecretManagerConnector(SecretConverter<?> converter, SecretsManagerClient secretsManagerClient)` - Inicializa com SecretsManagerClient e conversor especifico pré-configurados

#### Métodos

| Método | Descrição |
|--------|-----------|
| `get(String secretName)` | Recupera o valor do secret usando o conversor padrão |
| `R get(String secretName, Class<R> clazz)` | Recupera o valor do secret do tipo informado |
| `R get(String secretName, SecretConverter<R> converter)` | Recupera o secret SecretValue com conversor customizado |
| `exists(String secretName)` | Verifica se o secret existe |
| `getSecretsManagerClient()` | Retorna o cliente do Secrets Manager (uso avançado) |
| `close()` | Fecha a conexão com o Secrets Manager |


### Exceções

- `SecretManagerException` - Exceção personalizada para erros relacionados ao Secrets Manager

## Configuração de Credenciais AWS

### Opção 1: Variáveis de Ambiente

```bash
export AWS_ACCESS_KEY_ID=sua_chave_aqui
export AWS_SECRET_ACCESS_KEY=sua_chave_secreta_aqui
export AWS_REGION=us-east-1
```

### Opção 2: Arquivo de Configuração (~/.aws/credentials)

```ini
[default]
aws_access_key_id = sua_chave_aqui
aws_secret_access_key = sua_chave_secreta_aqui

[profile-name]
aws_access_key_id = sua_chave_aqui
aws_secret_access_key = sua_chave_secreta_aqui
```

### Opção 3: IAM Role (EC2)

Se executando em uma instância EC2, use uma IAM Role com permissão para acessar o Secrets Manager.

## Permissões IAM Necessárias

```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "secretsmanager:GetSecretValue",
                "secretsmanager:ListSecrets",
                "secretsmanager:DescribeSecret"
            ],
            "Resource": "arn:aws:secretsmanager:*:*:secret:*"
        }
    ]
}
```

## Estrutura do Projeto
[**Arquitetura do projeto**](ARQUITETURA.md)\
[**Instruções de utilização do componente**](<INSTRUÇÕES DE UTILIZAÇÃO.md>)


## Dependências

- **AWS SDK for Java 2.x** - Versão 2.20.0
- **SLF4J** - Versão 1.7.36
- **JUnit 5** - Versão 5.9.0

## Licença

Este projeto é fornecido como está, para fins educacionais e de desenvolvimento.

## Suporte

Para relatar problemas ou solicitar recursos, abra uma issue no repositório.


## Atualizar o projeto no code
mvn eclipse:eclipse -DdownloadSources -DdownloadJavadocs 2>&1 | tail -20