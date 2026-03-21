<div align="center" style="margin: 0; padding: 0;">
  <img src="https://cdn-icons-png.flaticon.com/512/4341/4341764.png" alt="Logo do Sistema" width="120" />

  <h1>🚀 Serverless Payment System</h1>
  <p>
    <em>Um sistema de pagamentos moderno, super seguro e construído 100% na nuvem (AWS).</em>
  </p>

  <p>
    <a href="https://www.oracle.com/java/" target="_blank">
      <img src="https://img.shields.io/badge/Java_17-ED8B00?style=for-the-badge&logo=java&logoColor=white" alt="Java 17" />
    </a>
    <a href="https://spring.io/projects/spring-boot" target="_blank">
      <img src="https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring&logoColor=white" alt="Spring Boot" />
    </a>
    <a href="https://www.python.org/" target="_blank">
      <img src="https://img.shields.io/badge/Python_3.10-3776AB?style=for-the-badge&logo=python&logoColor=white" alt="Python" />
    </a>
  </p>

  <p>
    <a href="https://developer.hashicorp.com/terraform" target="_blank">
      <img src="https://img.shields.io/badge/Terraform-7B42BC?style=for-the-badge&logo=terraform&logoColor=white" alt="Terraform" />
    </a>
    <a href="https://aws.amazon.com/" target="_blank">
      <img src="https://img.shields.io/badge/AWS-232F3E?style=for-the-badge&logo=amazon-aws&logoColor=white" alt="AWS" />
    </a>
    <a href="https://www.docker.com/" target="_blank">
      <img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker" />
    </a>
  </p>
</div>

<br>

<div align="center">

| 📚 Navegação Rápida |
|---|
| <a href="#-o-que-é-este-projeto-para-iniciantes">🏦 O que é este projeto?</a> • <a href="#-como-funciona-a-arquitetura">🏗️ Arquitetura</a> • <a href="#-tecnologias-utilizadas-e-por-que">🛠️ Tecnologias</a> • <a href="#-como-rodar-os-testes-no-seu-computador">💻 Testes</a> • <a href="#-como-enviar-para-a-nuvem-deploy">☁️ Deploy</a> • <a href="#-passo-a-passo-como-testar-o-sistema-na-prática">🔐 Teste prático</a> |

</div>

---

<h2 id="-o-que-é-este-projeto-para-iniciantes">🏦 O que é este projeto? (Para Iniciantes)</h2>

<p>
Imagine que você está a construir a infraestrutura de um Banco Digital. Se milhões de pessoas tentarem transferir dinheiro ao mesmo tempo na Black Friday, um sistema comum pode "cair" (ficar fora do ar).
</p>

<p>Para evitar isso, este projeto usa uma <b>Arquitetura Orientada a Eventos</b>.</p>

<h3><b>A analogia:</b></h3>

<ol>
  <li>O cliente chega ao banco e mostra a identidade ao segurança (<b>AWS Cognito</b>).</li>
  <li>O cliente entrega o pedido de transferência ao caixa (<b>API em Java/Spring Boot</b>).</li>
  <li>Em vez de fazer a transferência na hora e fazer o cliente esperar, o caixa coloca o papel numa "caixa de entrada" infinita (<b>Amazon SQS</b>). O cliente é liberado na hora.</li>
  <li>Na sala de trás do banco, vários funcionários invisíveis (<b>AWS Lambda em Python</b>) pegam nesses papéis um a um e atualizam o saldo no cofre-forte (<b>DynamoDB</b>).</li>
</ol>

<blockquote>
  Isso garante que o sistema nunca caia, mesmo com milhões de pedidos simultâneos!
</blockquote>

---

<h2 id="-como-funciona-a-arquitetura">🏗️ Como funciona a Arquitetura?</h2>

<p>Para os desenvolvedores e arquitetos de software, o fluxo técnico acontece da seguinte forma:</p>

<div align="center">
  <table>
    <tr>
      <td align="center"><b>1. Autenticação</b></td>
      <td align="center"><b>2. Recepção (API)</b></td>
      <td align="center"><b>3. Mensageria</b></td>
      <td align="center"><b>4. Processamento</b></td>
      <td align="center"><b>5. Banco de Dados</b></td>
    </tr>
    <tr>
      <td>O utilizador faz login no <b>Cognito</b> e recebe um Token JWT (Crachá digital).</td>
      <td>O <b>Spring Boot</b> (no ECS Fargate) recebe a requisição HTTP e valida o Token.</td>
      <td>A API publica o evento de pagamento numa fila <b>Amazon SQS</b> e responde 200 OK.</td>
      <td>Uma função <b>AWS Lambda</b> (Python) é engatilhada automaticamente pela fila.</td>
      <td>A Lambda processa a lógica e guarda o novo saldo numa tabela <b>DynamoDB</b> (NoSQL).</td>
    </tr>
  </table>
</div>

---

<h2 id="-tecnologias-utilizadas-e-por-que">🛠️ Tecnologias Utilizadas (E por que?)</h2>

<ul>
  <li>🛡️ <b>Amazon Cognito + Spring Security:</b> Garantem que apenas pessoas autorizadas com um "Token JWT" válido possam usar a API.</li>
  <li>⚙️ <b>Java 17 + Spring Boot 3:</b> O coração do sistema que recebe os pedidos. O Java é extremamente rápido e seguro para o setor financeiro.</li>
  <li>⚡ <b>Python 3.10 (AWS Lambda):</b> Código "Serverless" (sem servidor). Ele só "acorda" e cobra dinheiro quando há pagamentos para processar, economizando recursos.</li>
  <li>🗄️ <b>Amazon DynamoDB:</b> Um banco de dados NoSQL da Amazon capaz de ler e escrever milhares de dados por milissegundo.</li>
  <li>🏗️ <b>Terraform:</b> Ferramenta de "Infraestrutura como Código". Em vez de clicar em botões no site da AWS, escrevemos código que cria todos os servidores sozinhos.</li>
  <li>🤖 <b>GitHub Actions (CI/CD):</b> Um "robô" que pega o código novo, testa, empacota num <b>Docker</b> e envia para a AWS automaticamente.</li>
  <li>🧪 <b>Testcontainers:</b> Uma ferramenta incrível que simula a AWS (LocalStack) dentro do seu computador para testar o código sem gastar dinheiro na nuvem real.</li>
</ul>

---

<h2 id="-como-rodar-os-testes-no-seu-computador">💻 Como rodar os testes no seu computador</h2>

<p>Você não precisa de ter uma conta na AWS para testar se o código funciona! O sistema usa o Docker para criar uma "AWS de mentira" no seu computador.</p>

<h3><b>Pré-requisitos:</b></h3>

<ol>
  <li>Ter o <b>Docker Desktop</b> instalado e aberto.</li>
  <li>Ter o <b>Java 17</b> instalado.</li>
</ol>

<h3><b>Passo a passo:</b></h3>

<ol>
  <li>Abra o terminal na pasta <code>payment-api</code>.</li>
  <li>Execute o comando abaixo. Ele vai baixar a AWS falsa, subir o banco de dados temporário, fazer testes automáticos simulando um utilizador real e depois destruir tudo.</li>
</ol>

<pre><code class="language-bash">./mvnw clean test</code></pre>

<p>Se vir a mensagem BUILD SUCCESS no final, parabéns. O código está perfeito.</p>

---

<h2 id="-como-enviar-para-a-nuvem-deploy">☁️ Como enviar para a Nuvem (Deploy)</h2>

<p>Se quiser colocar o projeto no ar na AWS real, siga estes passos. Precisará do Terraform e do AWS CLI configurados com as suas credenciais.</p>

<p>No terminal, entre na pasta de infraestrutura:</p>

<pre><code class="language-bash">cd infra/terraform</code></pre>

<p>Prepare o ambiente (baixa os plugins necessários):</p>

<pre><code class="language-bash">terraform init</code></pre>

<p>Crie os servidores, banco de dados e filas na AWS:</p>

<pre><code class="language-bash">terraform apply</code></pre>

<p>(Digite <code>yes</code> quando for perguntado se deseja continuar).</p>

<p>⚠️ <b>MUITO IMPORTANTE:</b> No final deste processo, o terminal vai mostrar duas linhas verdes chamadas <code>cognito_user_pool_id</code> e <code>cognito_client_id</code>. Guarde esses códigos, você vai precisar deles no próximo passo.</p>

---

<h2 id="-passo-a-passo-como-testar-o-sistema-na-prática">🔐 Passo a Passo: Como testar o sistema na prática</h2>

<p>Com o sistema no ar, a API está trancada a sete chaves. Vamos criar um utilizador, gerar a senha (crachá/token) e tentar ver o saldo da conta.</p>

<h3><b>Passo 1: Criar uma conta no Banco (Via Terminal)</b></h3>

<p>Substitua a palavra <code>COGNITO_USER_POOL_ID</code> pelo código que você guardou no passo anterior. Cole isto no seu terminal (Git Bash ou PowerShell):</p>

<pre><code class="language-bash">aws cognito-idp admin-create-user --user-pool-id COGNITO_USER_POOL_ID --username dev@banco.com --message-action SUPPRESS --temporary-password "SenhaTemp123!"</code></pre>

<h3><b>Passo 2: Trocar a senha provisória pela senha oficial</b></h3>

<p>O sistema exige que a senha seja trocada no primeiro acesso. Execute:</p>

<pre><code class="language-bash">aws cognito-idp admin-set-user-password --user-pool-id COGNITO_USER_POOL_ID --username dev@banco.com --password "MasterPass123!" --permanent</code></pre>

<h3><b>Passo 3: Fazer o Login para pegar o Crachá (Token)</b></h3>

<p>Agora vamos autenticar. Substitua <code>COGNITO_CLIENT_ID</code> pelo seu segundo código guardado:</p>

<pre><code class="language-bash">aws cognito-idp initiate-auth --auth-flow USER_PASSWORD_AUTH --client-id COGNITO_CLIENT_ID --auth-parameters USERNAME=dev@banco.com,PASSWORD="MasterPass123!"</code></pre>

<p>Ao rodar isto, o terminal vai cuspir um texto gigante (JSON). Procure a palavra "IdToken" e copie o código gigante de letras e números que começa com <code>eyJ.....</code>. Ele é a sua chave de acesso, não copie as aspas.</p>

<h3><b>Passo 4: O Teste de Fogo (Consultar o Saldo)</b></h3>

<p>Agora vamos fingir que somos a aplicação do telemóvel a pedir o saldo ao servidor. Use o comando <code>curl</code> (que faz pedidos pela internet).</p>

<p>Substitua <code>COLE_O_SEU_TOKEN_AQUI</code> pelo código gigante que copiou no Passo 3.</p>

<pre><code class="language-bash">curl -i -X GET http://localhost:8080/accounts/67890 \
  -H "Authorization: Bearer COLE_O_SEU_TOKEN_AQUI"</code></pre>

<h3><b>O que vai acontecer?</b></h3>

<p>O Spring Security vai ver o seu crachá, verificar com a AWS se ele é verdadeiro e devolver o seu saldo com uma mensagem de Sucesso (HTTP 200 OK):</p>

<pre><code class="language-json">{
  "accountId": "67890",
  "balance": 500,
  "createdAt": "2026-03-16T11:00:03.558Z"
}</code></pre>

---

<h2>🧹 Fim do dia: Como apagar tudo e não gastar dinheiro</h2>

<p>A nuvem cobra pelo tempo que os servidores ficam ligados. Quando terminar de brincar com o seu projeto, destrua tudo de forma segura:</p>

<p>Volte à pasta do Terraform (<code>cd infra/terraform</code>).</p>

<p>Execute o comando de destruição:</p>

<pre><code class="language-bash">terraform destroy</code></pre>

<p>(Digite <code>yes</code> para confirmar. O Terraform vai apagar os bancos de dados, os utilizadores e as funções sem deixar rastos ou custos surpresa na sua conta).</p>

---

<div align="center">
  <sub>Feito com Java, Python, Terraform, AWS e uma boa dose de arquitetura serverless. ☁️</sub>
</div>