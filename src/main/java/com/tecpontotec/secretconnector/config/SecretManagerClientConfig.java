package com.tecpontotec.secretconnector.config;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;

/**
 * Utilitário para criação de instâncias {@link SecretsManagerClient} do AWS SDK v2.
 *
 * Esta classe fornece métodos convenientes para criar um client do Secrets Manager:
 * - create(): utiliza as configurações padrão do AWS SDK (detecção automática de região e credenciais);
 * - create(String region, String profileName): cria o client para uma região específica usando um perfil de credenciais do AWS CLI.
 *
 * Exemplo:
 * SecretsManagerClient client = SecretManagerClientConfig.create("sa-east-1", "default");
 *
 * Observação: instâncias do {@code SecretsManagerClient} são thread-safe segundo o SDK; gerencie o ciclo de vida (fechamento) conforme necessário.
 */
public class SecretManagerClientConfig {
  
  /**
   * Cria um {@link SecretsManagerClient} utilizando a configuração padrão do AWS SDK.
   * O SDK resolve região e credenciais pela cadeia de providers padrão (variáveis de ambiente, profile, metadata, etc.).
   *
   * @return SecretsManagerClient configurado com as definições padrão do SDK
   */
  public static SecretsManagerClient create() {
    return SecretsManagerClient.create();
  }

  /**
   * Cria um {@link SecretsManagerClient} para a região e perfil de credenciais especificados.
   *
   * @param region nome da região AWS (ex: "sa-east-1")
   * @param profileName nome do perfil do AWS CLI a ser usado (ex: "default")
   * @return SecretsManagerClient configurado com a região e o ProfileCredentialsProvider informados
   */
  public static SecretsManagerClient create(String region, String profileName) {

    return SecretsManagerClient.builder()
      .region(Region.of(region))
      .credentialsProvider(ProfileCredentialsProvider.create(profileName))
      .build();
  }

}
