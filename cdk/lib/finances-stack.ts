import { AttributeType, BillingMode, Table } from '@aws-cdk/aws-dynamodb';
import * as events from '@aws-cdk/aws-events';
import * as iam from '@aws-cdk/aws-iam';
import * as lambda from '@aws-cdk/aws-lambda';
import * as cdk from '@aws-cdk/core';
import { CommonParameter } from './commonParameter';
        import * as ec2 from '@aws-cdk/aws-ec2';


export class FinancesStack extends cdk.Construct {

    constructor(scope: cdk.Construct, parameter: CommonParameter, props?: cdk.StackProps) {
        super(scope, "FinancesStack");



        const eventBusPullFinancesTimer = new events.Rule(this, "pullFinancesTimer", {
            description: "create a timer to trigger lambda function",
            enabled: true,
            schedule: events.Schedule.rate(cdk.Duration.minutes(1))
          });
      
        const financialShipmentEventTableName = 'amz_sp_api_financial_shipment_event';
        const financialshipmentEventTable = new Table(this, 'amz_sp_api_financial_shipment_event', {
            tableName: financialShipmentEventTableName,
            partitionKey: { name: 'amazonOrderId', type: AttributeType.STRING },
            sortKey: { name: 'sellerId', type: AttributeType.STRING },
            removalPolicy: cdk.RemovalPolicy.DESTROY,
            // For dev/test purpose
            billingMode: BillingMode.PAY_PER_REQUEST
        });


        // For Finances getData
        const financeExecuteTaskForOneSeller = new lambda.Function(this, "FinancesExecuteTaskForOneSeller", {
            runtime: lambda.Runtime.JAVA_8,
            code: lambda.Code.fromAsset(parameter.codeZip),
            handler: 'cn.amazon.aws.rp.spapi.lambda.finances.ExecuteFinanceTaskForOneSeller',
            securityGroups: [parameter.lambdaSG],
            vpc: parameter.vpc,
            environment: {
                REDIS_URL: parameter.redisCluster.attrRedisEndpointAddress,
                DYNAMODB_SECRETS_TABLE: parameter.secrtesTableName,
                DYNAMODB_FINANCES_TABLE: financialShipmentEventTableName,
                EVENT_BUS_NAME: parameter.eventBus.eventBusName,
                SELLER_CENTRAL_APP_CREDENTIALS: parameter.seller_central_app_credentials,
                Role: parameter.spapiRole
            },
            timeout: cdk.Duration.seconds(60 * 10),
            memorySize: 1024,
            tracing: lambda.Tracing.ACTIVE,
            retryAttempts: 0 // Retry should be controled by request limiter.
        });
        //set permissions
        financialshipmentEventTable.grantReadWriteData(financeExecuteTaskForOneSeller);
        parameter.apiTaskTable.grantReadWriteData(financeExecuteTaskForOneSeller);
        //bus event
        events.EventBus.grantPutEvents(financeExecuteTaskForOneSeller);
        financeExecuteTaskForOneSeller.addToRolePolicy(new iam.PolicyStatement({
            resources: [parameter.spapiRole],
            actions: ['sts:AssumeRole'],
        }))
        parameter.ssm_seller_central_app_credentials.grantRead(financeExecuteTaskForOneSeller);


        // For Finances executeTask
        const financesEventsList = new lambda.Function(this, "GetAllSellerCredentialsAndPullFinances", {
            runtime: lambda.Runtime.JAVA_8,
            code: lambda.Code.fromAsset(parameter.codeZip),
            handler: 'cn.amazon.aws.rp.spapi.lambda.finances.GetAllSellerCredentialsAndPullFinances',
            securityGroups: [parameter.lambdaSG],
            vpc: parameter.vpc,
            environment: {
                REDIS_URL: parameter.redisCluster.attrRedisEndpointAddress,
                DYNAMODB_SECRETS_TABLE: parameter.secrtesTableName,
                DYNAMODB_FINANCES_TABLE: financialShipmentEventTableName,
                EVENT_BUS_NAME: parameter.eventBus.eventBusName,
                getFinancesListForOneSellerFuncName: financeExecuteTaskForOneSeller.functionName
            },
            timeout: cdk.Duration.seconds(100),
            memorySize: 1024,
            tracing: lambda.Tracing.ACTIVE,
            retryAttempts: 0 // Retry should be controled by request limiter.
        });
        financialshipmentEventTable.grantReadWriteData(financesEventsList);
        financeExecuteTaskForOneSeller.grantInvoke(financesEventsList);
        //set table read permissions
        parameter.secretsTalbe.grantReadData(financesEventsList);

        // dirty fix: https://github.com/aws-samples/aws-cdk-examples/issues/89#issuecomment-526758938 
        const eventTargets = require("@aws-cdk/aws-events-targets");
        eventBusPullFinancesTimer.addTarget(new eventTargets.LambdaFunction(financesEventsList));

    }
}