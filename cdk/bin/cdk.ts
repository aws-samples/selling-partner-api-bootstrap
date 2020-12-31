#!/usr/bin/env node
import 'source-map-support/register';
import * as cdk from '@aws-cdk/core';
import { SpApi } from '../lib/cdk-stack';

const app = new cdk.App();
new SpApi(app, 'CdkStack',"arn:aws:iam::716414967168:role/spapi_role_oct");
