import { AttributeType, BillingMode, Table } from '@aws-cdk/aws-dynamodb';
import * as events from '@aws-cdk/aws-events';
import * as iam from '@aws-cdk/aws-iam';
import * as lambda from '@aws-cdk/aws-lambda';
import * as cdk from '@aws-cdk/core';
import { commonParameter } from './commonParameter';


export class OrderStack extends cdk.Stack {

    getOrderListForOneSellerFunc: lambda.Function;

    constructor(scope: cdk.Construct, parameter: commonParameter, props?: cdk.StackProps) {
        super(scope, "OrderStack", props);

        const ordersTableName = 'amz_sp_api_orders';


        // EventBridge rule to set a timer in order to peridically pull for new order 
        // Scheduled rules are supported only on the default event bus. 
        const eventBusPullOrderTimer = new events.Rule(this, "pullOrderTimer", {
            description: "create a timer to trigger lambda function",
            enabled: true,
            schedule: events.Schedule.rate(cdk.Duration.minutes(1))
        });


        const ordersTalbe = new Table(this, 'amz_sp_api_orders', {
            tableName: ordersTableName,
            partitionKey: { name: 'amazonOrderId', type: AttributeType.STRING },
            sortKey: { name: 'orderSellerId', type: AttributeType.STRING },
            removalPolicy: cdk.RemovalPolicy.DESTROY,
            // For dev/test purpose
            billingMode: BillingMode.PAY_PER_REQUEST
        });

        //For order getData
        this.getOrderListForOneSellerFunc = new lambda.Function(this, "GetOrderListForOneSeller", {
            runtime: lambda.Runtime.JAVA_8,
            code: lambda.Code.fromAsset(parameter.codeZip),
            handler: 'cn.amazon.aws.rp.spapi.lambda.order.GetOrderListForOneSeller',
            securityGroups: [parameter.lambdaSG],
            vpc: parameter.vpc,
            environment: {
                REDIS_URL: parameter.redisCluster.attrRedisEndpointAddress,
                DYNAMODB_SECRETS_TABLE: parameter.secrtesTableName,
                DYNAMODB_ORDERS_TABLE: ordersTableName,
                EVENT_BUS_NAME: parameter.eventBus.eventBusName,
                SELLER_CENTRAL_APP_CREDENTIALS: parameter.seller_central_app_credentials,
                Role: parameter.spapiRole
            },
            timeout: cdk.Duration.seconds(100),
            memorySize: 1024,
            tracing: lambda.Tracing.ACTIVE,
            retryAttempts: 0 // Retry should be controled by request limiter.
        });
        ordersTalbe.grantReadWriteData(this.getOrderListForOneSellerFunc);
        events.EventBus.grantPutEvents(this.getOrderListForOneSellerFunc);
        this.getOrderListForOneSellerFunc.addToRolePolicy(new iam.PolicyStatement({
            resources: [parameter.spapiRole],
            actions: ['sts:AssumeRole'],
        }));
        parameter.ssm_seller_central_app_credentials.grantRead(this.getOrderListForOneSellerFunc);


        //For order executeTask
        const getAllSellerCredentialsAndPullFunc = new lambda.Function(this, "GetAllSellerCredentialsAndPullOrders", {
            runtime: lambda.Runtime.JAVA_8,
            code: lambda.Code.fromAsset(parameter.codeZip),
            handler: 'cn.amazon.aws.rp.spapi.lambda.order.GetAllSellerCredentialsAndPullOrders',
            securityGroups: [parameter.lambdaSG],
            vpc: parameter.vpc,
            environment: {
                REDIS_URL: parameter.redisCluster.attrRedisEndpointAddress,
                DYNAMODB_SECRETS_TABLE: parameter.secrtesTableName,
                DYNAMODB_ORDERS_TABLE: ordersTableName,
                EVENT_BUS_NAME: parameter.eventBus.eventBusName,
                getOrderListForOneSellerFuncName: this.getOrderListForOneSellerFunc.functionName,
                SELLER_CENTRAL_APP_CREDENTIALS: parameter.seller_central_app_credentials,
                Role: parameter.spapiRole
            },
            timeout: cdk.Duration.seconds(100),
            memorySize: 1024,
            tracing: lambda.Tracing.ACTIVE,
            retryAttempts: 0 // Retry should be controled by request limiter.
        });
        parameter.secretsTalbe.grantReadWriteData(getAllSellerCredentialsAndPullFunc);
        this.getOrderListForOneSellerFunc.grantInvoke(getAllSellerCredentialsAndPullFunc);
        parameter.ssm_seller_central_app_credentials.grantRead(getAllSellerCredentialsAndPullFunc);
        getAllSellerCredentialsAndPullFunc.addToRolePolicy(new iam.PolicyStatement({
            resources: [parameter.spapiRole],
            actions: ['sts:AssumeRole'],
        }));

        // dirty fix: https://github.com/aws-samples/aws-cdk-examples/issues/89#issuecomment-526758938 
        const eventTargets = require("@aws-cdk/aws-events-targets");
        // Event bus time will trigger the order check action.
        eventBusPullOrderTimer.addTarget(new eventTargets.LambdaFunction(getAllSellerCredentialsAndPullFunc));
    }
}