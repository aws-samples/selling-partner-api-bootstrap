import { Table } from '@aws-cdk/aws-dynamodb';
import * as ec2 from '@aws-cdk/aws-ec2';
import * as elasticache from '@aws-cdk/aws-elasticache';
import * as events from '@aws-cdk/aws-events';
import * as ssm from '@aws-cdk/aws-ssm';


export interface CommonParameter {
    readonly codeZip: string;
    readonly lambdaSG: ec2.SecurityGroup;
    readonly vpc: ec2.Vpc;
    readonly redisCluster: elasticache.CfnCacheCluster;
    readonly secrtesTableName: string;
    readonly eventBus: events.EventBus;
    readonly eventBusPullOrderTimer: events.Rule;
    readonly eventBusPullFinancesTimer: events.Rule;
    readonly seller_central_app_credentials: string;
    readonly spapiRole: string;
    readonly ssm_seller_central_app_credentials: ssm.IStringParameter
    readonly secretsTalbe: Table
}


