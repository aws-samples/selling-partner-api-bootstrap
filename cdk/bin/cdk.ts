#!/usr/bin/env node
import 'source-map-support/register';
import * as cdk from '@aws-cdk/core';
import { SpApi } from '../lib/cdk-stack';

const app = new cdk.App();
new SpApi(app, 'spapi',"arn:aws:iam::716414967168:role/spapi_role_oct", { env:{ account: "494937190409", region: "us-east-1"}});
// new SpApi(app, 'spapi',"arn:aws-cn:iam::801869647304:role/spapi-role");