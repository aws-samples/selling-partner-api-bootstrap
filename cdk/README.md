# Welcome to your CDK TypeScript project!

This is a blank project for TypeScript development with CDK.

The `cdk.json` file tells the CDK Toolkit how to execute your app.

## Useful commands
**Setup CDK**
 * `npm install -g aws-cdk`
 * `npm install -g typescript`
 * `npm link typescript`
 * `npm install @aws-cdk/aws-s3 @aws-cdk/aws-lambda @aws-cdk/aws-events-targets @aws-cdk/aws-dynamodb @aws-cdk/aws-sqs`
 * `npm install @aws-cdk/core`

 The solution for most of CDK issues can be found [here](https://docs.aws.amazon.com/cdk/latest/guide/troubleshooting.html) .

**Use CDK**
 * `npm run build`   compile typescript to js
 * `npm run watch`   watch for changes and compile
 * `npm run test`    perform the jest unit tests
 * `cdk bootstrap`   specify the default account and region for cdk
 * `cdk deploy`      deploy this stack to your default AWS account/region
 * `cdk diff`        compare deployed stack with current state
 * `cdk synth`       emits the synthesized CloudFormation template

 **Deply project**

 To deploy project you need to package lambda in another folder.
 And you need to install a jar first 

 `mvn install:install-file -Dfile=sellingpartnerapi-aa-java-1.0.1-jar-with-dependencies.jar -DgroupId=com.amazon.sellingpartnerapi -DartifactId=sellingpartnerapi-aa-java -Dversion=1.0.1 -Dpackaging=jar`

 Then package it with `gradle buildZip`

