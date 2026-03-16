# 1. Configuração de Rede (Utilizando a VPC padrão da sua conta para simplificar)
data "aws_vpc" "default" {
  default = true
}
data "aws_subnets" "default" {
  filter {
    name   = "vpc-id"
    values = [data.aws_vpc.default.id]
  }
}
# 2. Security Group (Permite tráfego HTTP na porta 8080)
resource "aws_security_group" "ecs_sg" {
  name        = "payment-api-ecs-sg"
  description = "Permitir trafego na porta 8080 para o ECS"
  vpc_id      = data.aws_vpc.default.id
  ingress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}
# 3. IAM Roles (Segurança e Permissões)
# 3.1 Role para o ECS puxar a imagem do ECR e gravar logs no CloudWatch
resource "aws_iam_role" "ecs_execution_role" {
  name = "ecs_execution_role"
  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{ Action = "sts:AssumeRole", Effect = "Allow", Principal = { Service = "ecs-tasks.amazonaws.com" } }]
  })
}
resource "aws_iam_role_policy_attachment" "ecs_execution_role_policy" {
  role       = aws_iam_role.ecs_execution_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}
# 3.2 Role para a sua API Java poder aceder ao DynamoDB (Sem precisarmos de passar chaves manuais!)
resource "aws_iam_role" "ecs_task_role" {
  name = "ecs_task_role"
  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{ Action = "sts:AssumeRole", Effect = "Allow", Principal = { Service = "ecs-tasks.amazonaws.com" } }]
  })
}
resource "aws_iam_policy" "dynamodb_access" {
  name = "PaymentApiDynamoDbAccess"
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect   = "Allow"
      Action   = ["dynamodb:PutItem", "dynamodb:GetItem", "dynamodb:Scan", "dynamodb:Query", "dynamodb:UpdateItem"]
      Resource = "*" 
    }]
  })
}
resource "aws_iam_role_policy_attachment" "ecs_task_role_policy" {
  role       = aws_iam_role.ecs_task_role.name
  policy_arn = aws_iam_policy.dynamodb_access.arn
}
# 4. Cluster ECS e CloudWatch (Logs)
resource "aws_ecs_cluster" "payment_cluster" {
  name = "payment-cluster"
}
resource "aws_cloudwatch_log_group" "ecs_logs" {
  name              = "/ecs/payment-api"
  retention_in_days = 7
}
# 5. Definição da Task (O seu Contentor)
resource "aws_ecs_task_definition" "payment_api_task" {
  family                   = "payment-api-task"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "256" # 0.25 vCPU
  memory                   = "512" # 512 MB
  execution_role_arn       = aws_iam_role.ecs_execution_role.arn
  task_role_arn            = aws_iam_role.ecs_task_role.arn
  container_definitions = jsonencode([{
    name      = "payment-api"
    image     = "${aws_ecr_repository.payment_api_repo.repository_url}:v1"
    essential = true
    portMappings = [{ containerPort = 8080, hostPort = 8080 }]
    environment  = [{ name = "AWS_REGION", value = "us-east-1" }]
    logConfiguration = {
      logDriver = "awslogs"
      options = {
        "awslogs-group"         = aws_cloudwatch_log_group.ecs_logs.name
        "awslogs-region"        = "us-east-1"
        "awslogs-stream-prefix" = "ecs"
      }
    }
  }])
}
# 6. ECS Service (O serviço que mantém a aplicação a correr)
resource "aws_ecs_service" "payment_api_service" {
  name            = "payment-api-service"
  cluster         = aws_ecs_cluster.payment_cluster.id
  task_definition = aws_ecs_task_definition.payment_api_task.arn
  launch_type     = "FARGATE"
  desired_count   = 1 # Mantém 1 instância a correr
  network_configuration {
    subnets          = data.aws_subnets.default.ids
    security_groups  = [aws_security_group.ecs_sg.id]
    assign_public_ip = true
  }
}