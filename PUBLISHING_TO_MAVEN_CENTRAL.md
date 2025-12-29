PUBLISHING TO MAVEN CENTRAL

Este documento descreve todos os passos necessários para publicar o artefato deste projeto no Maven Central (via OSSRH / Sonatype).

**Pré-requisitos**
- Conta no Sonatype OSSRH (https://s01.oss.sonatype.org). Solicite acesso ao `groupId` se necessário.
- Chave GPG para assinar artefatos (localmente disponível via `gpg`).
- Credenciais Maven configuradas em `~/.m2/settings.xml`.
- `pom.xml` preparado com `licenses`, `scm`, `developers`, `distributionManagement` e plugins `maven-source-plugin`, `maven-javadoc-plugin`, `maven-gpg-plugin` e `nexus-staging-maven-plugin` (já aplicado neste repositório).

**1) Verificar/Atualizar `pom.xml`**
- Confirme que o `groupId`/`artifactId`/`version` estão corretos.
- Confirme presença de `licenses`, `scm` (URL público/git), `developers`.
- Confirme `distributionManagement` apontando para OSSRH (s01). Exemplo já presente.

Arquivo de referência: [pom.xml](pom.xml)

**2) Registrar/Autorizar `groupId` no Sonatype**
- Acesse seu usuário OSSRH e, se necessário, abra um pedido (ticket) para reservar o `groupId`.
- Forneça: `groupId`, URL do SCM (GitHub), licença (Apache-2.0 no `pom.xml`), e contato do mantenedor.

**3) Gerar e publicar chave GPG (opcional: publicar em keyserver)**
Gerar chave:
```
gpg --full-generate-key
```
Listar chaves:
```
gpg --list-secret-keys --keyid-format LONG
```
Exportar chave pública (para publicar no keyserver ou keys.openpgp.org):
```
gpg --armor --export YOUR_KEY_ID > public.key
```
Publicar em keyserver (opcional):
```
# exemplo para keys.openpgp.org
gpg --send-keys --keyserver keys.openpgp.org YOUR_KEY_ID
```
O `maven-gpg-plugin` usa o cliente `gpg` local por padrão para assinar artefatos.

**4) Configurar `~/.m2/settings.xml`**
Adicione suas credenciais OSSRH (mantenha este arquivo seguro):

```
<settings>
  <servers>
    <server>
      <id>ossrh</id>
      <username>SEU_USUARIO_OSSRH</username>
      <password>SUA_SENHA_OSSRH</password>
    </server>
  </servers>
</settings>
```

Observações:
- O `<id>` deve bater com o `serverId` configurado no `nexus-staging-maven-plugin` (ex.: `ossrh`).
- Para CI, armazene credenciais em secrets e gere o `settings.xml` dinamicamente durante o job.

**5) Publicando snapshots vs releases**
- SNAPSHOT (versões terminadas em `-SNAPSHOT`):
```
mvn -DskipTests clean deploy
```
Isto enviará para o `snapshotRepository` configurado.

- RELEASE (versão sem `-SNAPSHOT`):
  1. Garanta que `version` no `pom.xml` não contenha `-SNAPSHOT`.
  2. Execute:

```
# assinar e enviar para staging
mvn -DskipTests clean deploy

# liberar o staging (ou usar nexus-staging:release)
mvn nexus-staging:release
```

Observações:
- O `mvn deploy` vai enviar o artefato para o Nexus OSSRH e manter em staging; `nexus-staging:release` fecha e libera para sync com Maven Central.
- Se preferir, combine etapas em scripts/CI que rodem `deploy` e depois `nexus-staging:release`.

**6) Verificação pós-release**
- A sincronização com Maven Central pode levar alguns minutos até horas.
- Para verificar localmente:
```
mvn dependency:get -Dartifact=br.com.leicam:secret-connector:1.0.0
```
Troque `1.0.0` pela versão publicada.

**7) Exemplo de GitHub Actions (resumido)**
- Use Secrets: `OSSRH_USERNAME`, `OSSRH_PASSWORD`, `GPG_PRIVATE_KEY`, `GPG_PASSPHRASE`.
- A ideia: configurar GPG, criar `settings.xml` com credenciais, executar `mvn clean deploy nexus-staging:release`.

Exemplo simplificado (coloque em `.github/workflows/release.yml`):

```yaml
name: Release
on:
  push:
    tags:
      - 'v*.*.*'
jobs:
  release:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Setup Java
        uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: '21'
          server-id: 'ossrh'
          server-username: ${{ secrets.OSSRH_USERNAME }}
          server-password: ${{ secrets.OSSRH_PASSWORD }}
      - name: Import GPG key
        run: |
          echo "${{ secrets.GPG_PRIVATE_KEY }}" > private.key
          gpg --batch --import private.key
      - name: Deploy
        run: mvn -DskipTests -B clean deploy nexus-staging:release
```
Este é um exemplo mínimo; adapte para tratar snapshots, criação dinâmica de `settings.xml`, e segurança de passphrases.

**8) Erros comuns e soluções rápidas**
- 403 Unauthorized ao deploy: verifique `~/.m2/settings.xml` e `serverId` correto.
- Não tem permissão para o `groupId`: abra ticket no Sonatype solicitando autorização para o `groupId`.
- GPG: "No secret key" — confirme `gpg --list-secret-keys` e que a chave está importada no ambiente CI.
- Timeout / falha de upload: re-tentar; verifique status do Sonatype.

**9) Checklist antes de release**
- [ ] `pom.xml` com `licenses`, `scm`, `developers` e `distributionManagement`.
- [ ] Versão do `pom.xml` não contém `-SNAPSHOT`.
- [ ] Chave GPG disponível e funcionando.
- [ ] Credenciais OSSRH configuradas (local ou CI).
- [ ] Testes passam (opcionalmente públicos) e build limpo.

---
Arquivo criado: [PUBLISHING_TO_MAVEN_CENTRAL.md](PUBLISHING_TO_MAVEN_CENTRAL.md)

Se desejar, eu posso:
- Gerar um `settings.xml` de exemplo com placeholders;
- Criar um workflow de GitHub Actions completo e testado;
- Adicionar um perfil `release` no `pom.xml` para parametrizar etapas de assinatura.

Qual desses você quer que eu faça em seguida?