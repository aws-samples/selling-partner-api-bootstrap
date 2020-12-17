import * as cdk from '@aws-cdk/core';
import * as lambda from '@aws-cdk/aws-lambda';
import * as ec2 from '@aws-cdk/aws-ec2';
import * as elasticache from '@aws-cdk/aws-elasticache';
import * as apigw from '@aws-cdk/aws-apigateway';
import * as path from 'path';
import * as events from '@aws-cdk/aws-events';
import { AttributeType, Table, BillingMode } from '@aws-cdk/aws-dynamodb';
import * as sqs from '@aws-cdk/aws-sqs';
import {AccountPrincipal} from '@aws-cdk/aws-iam';
import {SqsEventSource} from '@aws-cdk/aws-lambda-event-sources'
import { Duration } from '@aws-cdk/core';


// TODO rename the class as SpApi
export class CdkStack extends cdk.Stack {
  constructor(scope: cdk.Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    // The code that defines your stack goes here

    const vpc = new ec2.Vpc(this, "TestVpc", {
      cidr: '10.233.0.0/16',
      natGateways: 1,
      subnetConfiguration: [{
        cidrMask: 22,
        name: 'private',
        subnetType: ec2.SubnetType.PRIVATE
      }, {
        cidrMask: 22,
        name: 'public',
        subnetType: ec2.SubnetType.PUBLIC
      }]
    });

    const codeZip = path.join(__dirname, '../../lambda/build/distributions/lambda.zip');

    const lambdaSG = new ec2.SecurityGroup(this, "TestLambdaSG", {
      vpc,
    });

    const redisSG = new ec2.SecurityGroup(this, "TestRedisSG", {
      vpc,
      allowAllOutbound: false,
    });
    const redisSubnetGroup = new elasticache.CfnSubnetGroup(this, "TestRedisSubnetGroup", {
      description: 'Subnet group of redis cluster',
      subnetIds: vpc.privateSubnets.map((subnet) => subnet.subnetId)
    });
    const redisCluster = new elasticache.CfnCacheCluster(this, "TestRedisCluster", {
      cacheNodeType: "cache.t3.micro",
      engine: "redis",
      numCacheNodes: 1,
      cacheSubnetGroupName: redisSubnetGroup.ref,
      vpcSecurityGroupIds: [redisSG.securityGroupId]
    });
    redisSG.addIngressRule(lambdaSG, ec2.Port.tcp(6379), 'Allow access from lambda function');

    // Create EventBridge
    const eventBus = new events.EventBus(this, "sp-api", { eventBusName: "sp-api"});

    const getOneNewOrderFunc = new lambda.Function(this, "GetOneNewOrder", {
      runtime: lambda.Runtime.JAVA_8,
      code: lambda.Code.fromAsset(codeZip),
      handler: 'cn.amazon.aws.rp.spapi.lambda.order.GetOneNewOrder',
      securityGroups: [lambdaSG],
      vpc,
      environment: {
        REDIS_URL: redisCluster.attrRedisEndpointAddress,
        EVENT_BUS_NAME: eventBus.eventBusName
      },
      timeout: cdk.Duration.seconds(900), // We may retry on throttling
      memorySize: 1024,
      tracing: lambda.Tracing.ACTIVE,
      retryAttempts: 0 // Retry should be controled by request limiter.
    });
    
    // EventBridge rule to route newOrder
    const eventBusNewOrderRule = new events.Rule(this, "newOrderRule", {
      description: "Send new order to Lambda.",
      enabled: true,
      eventBus: eventBus,
      eventPattern: {
        source: ["com.aws.rapidprototyping.spapi"],
        // This filed will carry seller_id used to get seller secretes from dynamodb.
        // E.G newOrder||seller_jim
        detailType: ["{\"prefix\":\"newOrder||\"}"]
      }
    });

    // dirty fix: https://github.com/aws-samples/aws-cdk-examples/issues/89#issuecomment-526758938 
    const eventTargets = require("@aws-cdk/aws-events-targets");
    eventBusNewOrderRule.addTarget(new eventTargets.LambdaFunction(getOneNewOrderFunc));

    // EventBridge rule to set a timer in order to peridically pull for new order 
    // Scheduled rules are supported only on the default event bus. 
    const eventBusPullOrderTimer = new events.Rule(this, "pullOrderTimer", {
      description: "create a timer to trigger lambda function",
      enabled: true,
      schedule: events.Schedule.rate(cdk.Duration.minutes(1))
    });

    const eventBusPullFinancesTimer = new events.Rule(this, "pullFinancesTimer", {
      description: "create a timer to trigger lambda function",
      enabled: true,
      schedule: events.Schedule.rate(cdk.Duration.minutes(1))
    });

    // Dynamodb table
    const secrtesTableName = 'spapi-secrets';
    const ordersTableName = 'amz_sp_api_orders';
    const financialShipmentEventTableName = 'amz_sp_api_financial_shipment_event';
    const apiTaskTableName = 'sp_api_task';

    const secretsTalbe = new Table(this, 'secrtesTable', {
      tableName: secrtesTableName,
      partitionKey: { name: 'seller_id', type: AttributeType.STRING },
      removalPolicy: cdk.RemovalPolicy.DESTROY,
      // For dev/test purpose
      billingMode: BillingMode.PAY_PER_REQUEST
    });

    const apiTaskTable = new Table(this, 'sp_api_task', {
      tableName: apiTaskTableName,
      partitionKey: { name: 'sellerKey', type: AttributeType.STRING },
      sortKey: { name:"sellerId",type: AttributeType.STRING },
      removalPolicy: cdk.RemovalPolicy.DESTROY,
      // For dev/test purpose
      billingMode: BillingMode.PAY_PER_REQUEST
    });

    const ordersTalbe = new Table(this, 'amz_sp_api_orders', {
      tableName: ordersTableName,
      partitionKey: { name: 'amazonOrderId', type: AttributeType.STRING },
      sortKey: { name: 'orderSellerId', type: AttributeType.STRING },
      removalPolicy: cdk.RemovalPolicy.DESTROY,
      // For dev/test purpose
      billingMode: BillingMode.PAY_PER_REQUEST
    });

    const financialshipmentEventTable = new Table(this, 'amz_sp_api_financial_shipment_event', {
      tableName: financialShipmentEventTableName,
      partitionKey: { name: 'amazonOrderId', type: AttributeType.STRING },
      sortKey: { name: 'sellerId', type: AttributeType.STRING },
      removalPolicy: cdk.RemovalPolicy.DESTROY,
      // For dev/test purpose
      billingMode: BillingMode.PAY_PER_REQUEST
    });

    //For order getData
    const getOrderListForOneSellerFunc = new lambda.Function(this, "GetOrderListForOneSeller", {
      runtime: lambda.Runtime.JAVA_8,
      code: lambda.Code.fromAsset(codeZip),
      handler: 'cn.amazon.aws.rp.spapi.lambda.order.GetOrderListForOneSeller',
      securityGroups: [lambdaSG],
      vpc,
      environment: {
        REDIS_URL: redisCluster.attrRedisEndpointAddress,
        DYNAMODB_SECRETS_TABLE: secrtesTableName,
        DYNAMODB_ORDERS_TABLE: ordersTableName,
        EVENT_BUS_NAME: eventBus.eventBusName
      },
      timeout: cdk.Duration.seconds(100),
      memorySize: 1024,
      tracing: lambda.Tracing.ACTIVE,
      retryAttempts: 0 // Retry should be controled by request limiter.
    });
    ordersTalbe.grantReadWriteData(getOrderListForOneSellerFunc)      
    events.EventBus.grantPutEvents(getOrderListForOneSellerFunc)

    // For Finances getData
    const financeExecuteTaskForOneSeller = new lambda.Function(this, "FinancesExecuteTaskForOneSeller", {
      runtime: lambda.Runtime.JAVA_8,
      code: lambda.Code.fromAsset(codeZip),
      handler: 'cn.amazon.aws.rp.spapi.lambda.finances.ExecuteTaskForOneSeller',
      securityGroups: [lambdaSG],
      vpc,
      environment: {
        REDIS_URL: redisCluster.attrRedisEndpointAddress,
        DYNAMODB_SECRETS_TABLE: secrtesTableName,
        DYNAMODB_FINANCES_TABLE: financialShipmentEventTableName,
        EVENT_BUS_NAME: eventBus.eventBusName
      },
      timeout: cdk.Duration.seconds(100),
      memorySize: 1024,
      tracing: lambda.Tracing.ACTIVE,
      retryAttempts: 0 // Retry should be controled by request limiter.
    });
    //set permissions
    financialshipmentEventTable.grantReadWriteData(financeExecuteTaskForOneSeller);
    apiTaskTable.grantReadWriteData(financeExecuteTaskForOneSeller);
    //bus event
    events.EventBus.grantPutEvents(financeExecuteTaskForOneSeller);

    //For order executeTask
    const getAllSellerCredentialsAndPullFunc = new lambda.Function(this, "GetAllSellerCredentialsAndPull", {
      runtime: lambda.Runtime.JAVA_8,
      code: lambda.Code.fromAsset(codeZip),
      handler: 'cn.amazon.aws.rp.spapi.lambda.seller.GetAllSellerCredentialsAndPull',
      securityGroups: [lambdaSG],
      vpc,
      environment: {
        REDIS_URL: redisCluster.attrRedisEndpointAddress,
        DYNAMODB_SECRETS_TABLE: secrtesTableName,
        DYNAMODB_ORDERS_TABLE: ordersTableName,
        EVENT_BUS_NAME: eventBus.eventBusName,
        getOrderListForOneSellerFuncName: getOrderListForOneSellerFunc.functionName
      },
      timeout: cdk.Duration.seconds(100),
      memorySize: 1024,
      tracing: lambda.Tracing.ACTIVE,
      retryAttempts: 0 // Retry should be controled by request limiter.
    });
    secretsTalbe.grantReadWriteData(getAllSellerCredentialsAndPullFunc);
    getOrderListForOneSellerFunc.grantInvoke(getAllSellerCredentialsAndPullFunc);
     
    // For Finances executeTask
    const financesEventsList = new lambda.Function(this, "FinancesEventsList", {
      runtime: lambda.Runtime.JAVA_8,
      code: lambda.Code.fromAsset(codeZip),
      handler: 'cn.amazon.aws.rp.spapi.lambda.finances.FinancesEventsList',
      securityGroups: [lambdaSG],
      vpc,
      environment: {
        REDIS_URL: redisCluster.attrRedisEndpointAddress,
        DYNAMODB_SECRETS_TABLE: secrtesTableName,
        DYNAMODB_FINANCES_TABLE: financialShipmentEventTableName,
        EVENT_BUS_NAME: eventBus.eventBusName,
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
    secretsTalbe.grantReadData(financesEventsList);

    // Event bus time will trigger the order check action.
    eventBusPullOrderTimer.addTarget(new eventTargets.LambdaFunction(getAllSellerCredentialsAndPullFunc));
    eventBusPullFinancesTimer.addTarget(new eventTargets.LambdaFunction(financesEventsList));

    // Subscribe the event
    const spapiEventQueue = new sqs.Queue(this, "SpApiEventQueue", {
      visibilityTimeout: Duration.seconds(900) 
    });

    // The principal is provided by SP API - it will put event to the queue.
    spapiEventQueue.grantSendMessages(new AccountPrincipal('437568002678'))

    const logOnlyFunc = new lambda.Function(this, "LogOnly", {
      runtime: lambda.Runtime.JAVA_8,
      code: lambda.Code.fromAsset(codeZip),
      handler: 'cn.amazon.aws.rp.spapi.lambda.log.SQSLogOnly',
      securityGroups: [lambdaSG],
      vpc,
      timeout: cdk.Duration.seconds(100),
      memorySize: 1024,
      tracing: lambda.Tracing.ACTIVE,
      retryAttempts: 0 // Retry should be controled by request limiter.
    }); 
    
    logOnlyFunc.addEventSource(new SqsEventSource(spapiEventQueue))
    
    
    const eventSubscriptionFunc = new lambda.Function(this, "EventSubscription", {
      runtime: lambda.Runtime.JAVA_8,
      code: lambda.Code.fromAsset(codeZip),
      handler: 'cn.amazon.aws.rp.spapi.lambda.notification.EventSubscription',
      securityGroups: [lambdaSG],
      vpc,
      environment: {
        REDIS_URL: redisCluster.attrRedisEndpointAddress,
        SQS_ARN: spapiEventQueue.queueArn
      },
      timeout: cdk.Duration.seconds(900),
      memorySize: 1024,
      tracing: lambda.Tracing.ACTIVE,
      retryAttempts: 0 // Retry should be controled by request limiter.
    });
    secretsTalbe.grantReadData(eventSubscriptionFunc)

    // add api gateway for lambda
    const api = new apigw.RestApi(this, "sp-api-subscription");

    // This is used to trigger the SP-API event subscription, in a complete project this lambda should be triggered by seller register event.
    const integration = new apigw.LambdaIntegration(eventSubscriptionFunc);
    api.root.addMethod("GET", integration);
  }
}
