# 1. Configuração do Provedor AWS
provider "aws" {
  region = "us-east-1"
}
# 2. Tabela DynamoDB para as Contas
resource "aws_dynamodb_table" "accounts" {
  name           = "accounts"
  billing_mode   = "PAY_PER_REQUEST"
  hash_key       = "accountId"
  attribute {
    name = "accountId"
    type = "S" # String
  }
  tags = {
    Environment = "dev"
    Project     = "payment-system"
  }
}
# 3. Fila SQS para Processamento Assíncrono
resource "aws_sqs_queue" "payment_queue" {
  name                      = "payment-queue"
  delay_seconds             = 0
  max_message_size          = 262144
  message_retention_seconds = 86400
  receive_wait_time_seconds = 10 # Long polling para economizar custos
  tags = {
    Environment = "dev"
    Project     = "payment-system"
  }
}
# 4. Tabela DynamoDB para Histórico de Transações
resource "aws_dynamodb_table" "transactions" {
  name           = "transactions"
  billing_mode   = "PAY_PER_REQUEST"
  # Chave Composta: accountId (Partition Key) + timestamp (Sort Key)
  hash_key       = "accountId"
  range_key      = "timestamp"
  attribute {
    name = "accountId"
    type = "S" 
  }
  attribute {
    name = "timestamp"
    type = "S" 
  }
  tags = {
    Environment = "dev"
    Project     = "payment-system"
  }
}
# 5. Repositório ECR para a Imagem Docker da API
resource "aws_ecr_repository" "payment_api_repo" {
  name                 = "payment-api"
  image_tag_mutability = "MUTABLE"
  image_scanning_configuration {
    scan_on_push = true # Escaneia a imagem em busca de vulnerabilidades de segurança
  }
  tags = {
    Environment = "dev"
    Project     = "payment-system"
  }
}