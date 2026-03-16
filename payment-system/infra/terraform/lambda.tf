# 1. Compactar o código Python automaticamente
data "archive_file" "lambda_zip" {
  type        = "zip"
  source_file = "${path.module}/../../lambda-worker/process_payment.py"
  output_path = "${path.module}/process_payment.zip"
}
# 2. Criar a Role de Segurança (Permissões da Lambda)
resource "aws_iam_role" "lambda_exec_role" {
  name = "payment_lambda_exec_role"
  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Action = "sts:AssumeRole"
      Effect = "Allow"
      Principal = { Service = "lambda.amazonaws.com" }
    }]
  })
}
# 2.1 Permissão básica para a Lambda escrever logs no CloudWatch
resource "aws_iam_role_policy_attachment" "lambda_logs" {
  role       = aws_iam_role.lambda_exec_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
}
# 2.2 Permissão estrita para ler a SQS e escrever no DynamoDB
resource "aws_iam_policy" "lambda_sqs_dynamo" {
  name = "PaymentLambdaSqsDynamoPolicy"
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "sqs:ReceiveMessage",
          "sqs:DeleteMessage",
          "sqs:GetQueueAttributes"
        ]
        Resource = aws_sqs_queue.payment_queue.arn
      },
      {
        Effect = "Allow"
        Action = [
          "dynamodb:UpdateItem",
          "dynamodb:PutItem"
        ]
        Resource = [
          aws_dynamodb_table.accounts.arn,
          aws_dynamodb_table.transactions.arn
        ]
      }
    ]
  })
}
resource "aws_iam_role_policy_attachment" "lambda_custom_policy" {
  role       = aws_iam_role.lambda_exec_role.name
  policy_arn = aws_iam_policy.lambda_sqs_dynamo.arn
}
# 3. Criar a Função Lambda
resource "aws_lambda_function" "payment_processor" {
  filename         = data.archive_file.lambda_zip.output_path
  function_name    = "process-payment-worker"
  role             = aws_iam_role.lambda_exec_role.arn
  handler          = "process_payment.lambda_handler" 
  source_code_hash = data.archive_file.lambda_zip.output_base64sha256
  runtime          = "python3.10"
  timeout          = 10 
  tags = {
    Environment = "dev"
    Project     = "payment-system"
  }
}
# 4. Criar o Gatilho (Trigger): A Lambda vai "escutar" a Fila SQS
resource "aws_lambda_event_source_mapping" "sqs_trigger" {
  event_source_arn = aws_sqs_queue.payment_queue.arn
  function_name    = aws_lambda_function.payment_processor.arn
  batch_size       = 10 
}