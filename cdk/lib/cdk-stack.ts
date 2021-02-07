import * as apigw from '@aws-cdk/aws-apigateway';
import { AttributeType, BillingMode, Table } from '@aws-cdk/aws-dynamodb';
import * as ec2 from '@aws-cdk/aws-ec2';
import * as elasticache from '@aws-cdk/aws-elasticache';
import * as events from '@aws-cdk/aws-events';
import * as iam from '@aws-cdk/aws-iam';
import * as lambda from '@aws-cdk/aws-lambda';
import { SqsEventSource } from '@aws-cdk/aws-lambda-event-sources';
import * as sqs from '@aws-cdk/aws-sqs';
import * as ssm from '@aws-cdk/aws-ssm';
import * as cdk from '@aws-cdk/core';
import { CfnOutput, Duration } from '@aws-cdk/core';
import * as path from 'path';
import { FinancesStack } from './finances-stack';
import { OrderStack } from './order-stack';


// TODO rename the class as SpApi
export class SpApi extends cdk.Stack {
  constructor(scope: cdk.App, id: string, spapi_role_arn: string, props?: cdk.StackProps) {
    super(scope, id, props);

    var spapiRole = spapi_role_arn;
    const seller_central_app_credentials = "seller_central_app_credentials";

    const codeZip = path.join(__dirname, '../../lambda/build/distributions/lambda.zip');

    const ssm_seller_central_app_credentials = ssm.StringParameter.fromSecureStringParameterAttributes(this, "seller_central_app_credentials", { parameterName: seller_central_app_credentials, version: 1 });

    if (!spapiRole) {
      spapiRole = this.initCredential();
    }

    // The code that defines your stack goes here

    const { lambdaSG, vpc, redisCluster } = this.initInfra();

    // Create EventBridge
    const eventBus = new events.EventBus(this, "sp-api", { eventBusName: "sp-api" });

    // Dynamodb table
    const secrtesTableName = 'spapi-secrets';

    const secretsTalbe = new Table(this, 'secrtesTable', {
      tableName: secrtesTableName,
      partitionKey: { name: 'seller_id', type: AttributeType.STRING },
      removalPolicy: cdk.RemovalPolicy.DESTROY,
      // For dev/test purpose
      billingMode: BillingMode.PAY_PER_REQUEST
    });

    const parameter = {
      codeZip, lambdaSG, vpc, redisCluster, secrtesTableName,
       eventBus, seller_central_app_credentials, spapiRole, ssm_seller_central_app_credentials, secretsTalbe
    };

    const orderStack = new OrderStack(this, parameter);

    const financesStack = new FinancesStack(this, parameter);

    // Subscribe the event
    const spapiEventQueue = new sqs.Queue(this, "SpApiEventQueue", {
      visibilityTimeout: Duration.seconds(900)
    });

    // The principal is defined by SP API - it will put event to the queue.
    // TODO for China Region we need to disable the event subscription 
    //      and it should controlled by a variable.
    
    // spapiEventQueue.grantSendMessages(new iam.AccountPrincipal('437568002678'))

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

    // // add api gateway for lambda
    // const api = new apigw.RestApi(this, "sp-api-subscription");

    // // This is used to trigger the SP-API event subscription, in a complete project this lambda should be triggered by seller register event.
    // const integration = new apigw.LambdaIntegration(eventSubscriptionFunc);
    // api.root.addMethod("GET", integration);

  }

  private initInfra() {
    const vpc = new ec2.Vpc(this, "SpApiVpc", {
      cidr: '10.233.0.0/16',
      natGateways: 1,
      // Allow 4K IP address in each subnet.
      subnetConfiguration: [{
        cidrMask: 20,
        name: 'private',
        subnetType: ec2.SubnetType.PRIVATE
      }, {
        cidrMask: 20,
        name: 'public',
        subnetType: ec2.SubnetType.PUBLIC
      }]
      // Consider create Isolated subnet for Workspace.
    });



    const lambdaSG = new ec2.SecurityGroup(this, "SpApiLambdaSG", {
      vpc,
    });

    const redisSG = new ec2.SecurityGroup(this, "SpApiRedisSG", {
      vpc,
      allowAllOutbound: false,
    });
    const redisSubnetGroup = new elasticache.CfnSubnetGroup(this, "SpApiRedisSubnetGroup", {
      description: 'Subnet group of redis cluster',
      subnetIds: vpc.privateSubnets.map((subnet) => subnet.subnetId)
    });
    const redisCluster = new elasticache.CfnCacheCluster(this, "SpApiRedisCluster", {
      cacheNodeType: "cache.t3.micro",
      engine: "redis",
      numCacheNodes: 1,
      cacheSubnetGroupName: redisSubnetGroup.ref,
      vpcSecurityGroupIds: [redisSG.securityGroupId]
    });
    redisSG.addIngressRule(lambdaSG, ec2.Port.tcp(6379), 'Allow access from lambda function');
    return { lambdaSG, vpc, redisCluster };
  }

  private initCredential() {
    const spapi_usr = new iam.User(this, 'spapi-usr', {});
    const spapi_role = new iam.Role(this, 'spapi-role', {
      assumedBy: new iam.ArnPrincipal(spapi_usr.userArn),
    });

    spapi_role.addToPolicy(new iam.PolicyStatement({
      resources: ['arn:aws:execute-api:*:*:*'],
      actions: ['execute-api:Invoke'],
    }));


    spapi_usr.addToPolicy(new iam.PolicyStatement({
      resources: [spapi_role.roleArn],
      actions: ['sts:AssumeRole'],
    }));

    const accessKey = new iam.CfnAccessKey(this, 'AccessKey', {
      userName: spapi_usr.userName,
    });

    new CfnOutput(this, "spapi_role", { exportName: "spapirole", value: spapi_role.roleArn });
    new CfnOutput(this, "accessKey", { exportName: "accessKey", value: accessKey.ref });
    new CfnOutput(this, "Secret", { exportName: "secretKey", value: accessKey.attrSecretAccessKey });
    return spapi_role.roleArn;
  }
}

//const app = new cdk.App();
//new SpApi(app, "spapi");
//app.synth();