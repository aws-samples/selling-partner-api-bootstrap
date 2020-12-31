# Welcome to your CDK TypeScript project!


The `cdk.json` file tells the CDK Toolkit how to execute your app.

## Define Deployment Environment

There are serval ways to specify where to deploy your stack. [Ref Link](https://docs.aws.amazon.com/cdk/latest/guide/environments.html).



## IAM Role
**条件说明**
如果已经按照官方文档，创建并使用了IAM Role，请将IAM Role ARN 添加至
./bin/cdk.ts中，如：
```
new CdkStack(app, 'CdkStack',"arn:aws:iam::716414967168:role/spapi_role_oct");
```
此项目会自动添加应用运行时需要的权限。

否则，可不添加IAM Role ARN，如：
```
new CdkStack(app, 'CdkStack');
```
部署后，cdk会输出IAM Role ARN，及对于的IAM User的AKSK，用于本地调试。

## Amazon App Credential
安全性要求，clientSecret 需要独立发布到AWS SSM Parameter中

## Amazon App Credential
安全性要求，请手工配置 System Manager Parameter Store 新增这个参数
名称：seller_central_app_credentials
类型：SecureString
级别：Standard
KMS key source：My current account/alias/aws/ssm 或选择其他你已有的加密 KMS Key
seller_central_app_credentials 是用于应用程序用LWA交互凭证使用,来自Seller Centrall Developer Central的LWA credentials

```
{
  "clientId": "seller central app clientId",
  "clientSecret": "seller central app clientSecret",
}
```


## Useful commands
**Setup CDK**
 * `npm install -g aws-cdk`
 * `npm install -g typescript`
 * `npm link typescript`
 * `npm install @aws-cdk/aws-s3 @aws-cdk/aws-lambda @aws-cdk/aws-events-targets @aws-cdk/aws-dynamodb @aws-cdk/aws-sqs`
 * `npm install @aws-cdk/core`
 * `npm install -g aws-cdk@latest` -- In case CDK need upgrades.

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

 ## Other Trouble Shooting

> If `cdk` don't build or run.
After upgrade the CDK with `npm update -g aws-cdk` you may check if everything is updated with `npm outdated` if there is any, you should update `package.json` and then do the following
    1. Make sure your `package.json` lists the same CDK version.
    2. Nuke node_modules
    3. Run `npm install` again


