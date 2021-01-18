#!/usr/bin/env node
import 'source-map-support/register';
import * as cdk from '@aws-cdk/core';
import { SpApi } from '../lib/cdk-stack';

const app = new cdk.App();
new SpApi(app, 'spapi',"arn:aws-cn:iam::801869647304:role/spapi-role", { env:{
    account: "801869647304",
    region: "cn-northwest-1"}});
// new SpApi(app, 'spapi',"arn:aws-cn:iam::801869647304:role/spapi-role");