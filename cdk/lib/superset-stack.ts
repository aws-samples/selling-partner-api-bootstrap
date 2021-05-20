import * as events from '@aws-cdk/aws-events';
import * as iam from '@aws-cdk/aws-iam';
import * as cdk from '@aws-cdk/core';
import { CommonParameter } from './commonParameter';
import * as ec2 from '@aws-cdk/aws-ec2';

/**
 * For China region, there are EC2 images prepared with Superset and Athena drivers. This stack is provisioning the EC2
 * and a jump bastion.
 * 
 * Key pair solution-
 * https://github.com/udondan/cdk-ec2-key-pair
 */
export class SuperSetEc2 extends cdk.Construct {

    constructor(scope: cdk.Construct, parameter: CommonParameter, props?: cdk.StackProps) {
        super(scope, "SupersetEC2");

        const vpc = ec2.Vpc.fromLookup(this, 'SpApiVpc', { // Created in cdk-stack.ts
            isDefault: true,
          });
          
        // Create Bastion
        // Bastion Key Solution (Only for Amazon Linux)
        // With SSH Key: https://aws.amazon.com/de/blogs/compute/new-using-amazon-ec2-instance-connect-for-ssh-access-to-your-ec2-instances/
        // With SSM: (https://aws.amazon.com/about-aws/whats-new/2019/07/session-manager-launches-tunneling-support-for-ssh-and-scp/)
        const bastion = new ec2.BastionHostLinux(this, 'SpApiBastionHost', {
            vpc,
            subnetSelection: { subnetType: ec2.SubnetType.PUBLIC },
            blockDevices: [{
                deviceName: 'EBSBastionHost',
                volume: ec2.BlockDeviceVolume.ebs(10, {
                  encrypted: true,
                }),
              }],
          });
        //   host.allowSshAccessFrom(ec2.Peer.ipv4('1.2.3.4/32'));

        /** Create Superset EC2 */
        const superSetSecurityGroup = new ec2.SecurityGroup(this, 'superset', {
            vpc,
            description: 'Allow ssh access to ec2 instances',
            allowAllOutbound: true   // Can be set to false
          });
          superSetSecurityGroup.addIngressRule(ec2.Peer.ipv4('10.233.0.0/16'), ec2.Port.tcpRange(8000, 9999), 'allow ssh access from the world');
          superSetSecurityGroup.addIngressRule(ec2.Peer.ipv4(bastion.instancePrivateIp), ec2.Port.tcp(22), 'allow ssh access from the Bastion');
          

        // For other custom (Linux) images, instantiate a `GenericLinuxImage` with
        // a map giving the AMI to in for each region:
        const superSetImage = ec2.MachineImage.genericLinux({
            'cn-northwest-1': 'ami-0029a1a3507091916',
        });

        const superset = new ec2.Instance(this, 'superset', {{}, {}, {}});
    }
}