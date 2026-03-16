import json
import boto3
from datetime import datetime
from decimal import Decimal
# Inicializa os recursos da AWS (O boto3 já vem instalado nas Lambdas da AWS)
dynamodb = boto3.resource('dynamodb')
accounts_table = dynamodb.Table('accounts')
transactions_table = dynamodb.Table('transactions')
def lambda_handler(event, context):
    """
    Função principal que a AWS Lambda vai chamar quando chegar mensagem no SQS.
    """
    # O SQS pode enviar mensagens em lote, por isso iteramos sobre 'Records'
    for record in event['Records']:
        try:
            # 1. Receber e transformar o JSON do evento SQS
            # Usamos parse_float=Decimal porque o DynamoDB exige Decimal para números
            body = json.loads(record['body'], parse_float=Decimal)
            from_account_id = body['fromAccountId']
            to_account_id = body['toAccountId']
            amount = body['amount']
            print(f"Iniciando transferência de {amount} da conta {from_account_id} para {to_account_id}")
            # 2 e 3. Validar Saldo e Atualizar a Conta de Origem (Pagador)
            # A ConditionExpression garante atomicidade: só desconta se tiver saldo
            accounts_table.update_item(
                Key={'accountId': from_account_id},
                UpdateExpression="set balance = balance - :val",
                ConditionExpression="balance >= :val",
                ExpressionAttributeValues={':val': amount}
            )
            # 3. Atualizar a Conta de Destino (Recebedor)
            accounts_table.update_item(
                Key={'accountId': to_account_id},
                UpdateExpression="set balance = balance + :val",
                ExpressionAttributeValues={':val': amount}
            )
            # 4. Salvar Histórico (Duas transações: uma de saída, outra de entrada)
            timestamp = datetime.utcnow().isoformat() + 'Z'
            transactions_table.put_item(Item={
                'accountId': from_account_id,
                'timestamp': timestamp,
                'amount': amount,
                'type': 'TRANSFER_OUT'
            })
            transactions_table.put_item(Item={
                'accountId': to_account_id,
                'timestamp': timestamp,
                'amount': amount,
                'type': 'TRANSFER_IN'
            })
            print(f"Sucesso: Transação de {amount} concluída!")
        except Exception as e:
            # Em produção, registraríamos o erro no CloudWatch ou enviaríamos para uma DLQ (Fila de Erros)
            print(f"Erro ao processar a mensagem: {str(e)}")
            raise e # Lança o erro para o SQS tentar de novo depois