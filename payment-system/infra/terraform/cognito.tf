# 1. Criar o "Banco de Dados" de Utilizadores (User Pool)
resource "aws_cognito_user_pool" "bank_pool" {
  name = "bank-users-pool"
  # Regras de segurança para a palavra-passe
  password_policy {
    minimum_length    = 8
    require_lowercase = true
    require_numbers   = true
    require_symbols   = false
    require_uppercase = true
  }
}
# 2. Criar a porta de entrada para a nossa API (App Client)
resource "aws_cognito_user_pool_client" "bank_client" {
  name         = "bank-api-client"
  user_pool_id = aws_cognito_user_pool.bank_pool.id
  # Permite fazer login com utilizador e palavra-passe
  explicit_auth_flows = [
    "ALLOW_USER_PASSWORD_AUTH",
    "ALLOW_REFRESH_TOKEN_AUTH",
    "ALLOW_USER_SRP_AUTH"
  ]
}
# 3. Pedir ao Terraform para nos mostrar os IDs gerados no final
output "cognito_user_pool_id" {
  value = aws_cognito_user_pool.bank_pool.id
}
output "cognito_client_id" {
  value = aws_cognito_user_pool_client.bank_client.id
}