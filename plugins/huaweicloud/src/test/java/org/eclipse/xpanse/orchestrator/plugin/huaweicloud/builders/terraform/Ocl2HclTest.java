/*
 * SPDX-License-Identifier: Apache-2.0
 * SPDX-FileCopyrightText: Huawei Inc.
 *
 */

package org.eclipse.xpanse.orchestrator.plugin.huaweicloud.builders.terraform;

import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.eclipse.xpanse.modules.ocl.loader.data.models.Compute;
import org.eclipse.xpanse.modules.ocl.loader.data.models.Network;
import org.eclipse.xpanse.modules.ocl.loader.data.models.Ocl;
import org.eclipse.xpanse.modules.ocl.loader.data.models.SecurityGroup;
import org.eclipse.xpanse.modules.ocl.loader.data.models.SecurityRule;
import org.eclipse.xpanse.modules.ocl.loader.data.models.Storage;
import org.eclipse.xpanse.modules.ocl.loader.data.models.Subnet;
import org.eclipse.xpanse.modules.ocl.loader.data.models.UserData;
import org.eclipse.xpanse.modules.ocl.loader.data.models.Vm;
import org.eclipse.xpanse.modules.ocl.loader.data.models.Vpc;
import org.eclipse.xpanse.modules.ocl.loader.data.models.enums.SecurityRuleAction;
import org.eclipse.xpanse.modules.ocl.loader.data.models.enums.SecurityRuleDirection;
import org.eclipse.xpanse.modules.ocl.loader.data.models.enums.SecurityRuleProtocol;
import org.eclipse.xpanse.modules.ocl.loader.data.models.enums.StorageSizeUnit;
import org.eclipse.xpanse.modules.ocl.loader.data.models.enums.StorageType;
import org.eclipse.xpanse.modules.ocl.loader.data.models.enums.UserDataType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Ocl2HclTest {

    private void prepareNetwork(Ocl ocl) {
        // SecurityRule
        SecurityRule secRule = new SecurityRule();
        secRule.setProtocol(SecurityRuleProtocol.TCP);
        secRule.setCidr("10.10.2.0/24");
        secRule.setDirection(SecurityRuleDirection.IN);
        secRule.setPorts("8080, 9092-9093, 2181");
        secRule.setAction(SecurityRuleAction.ALLOW);
        secRule.setName("secRuleTest");

        // SecurityRule list
        List<SecurityRule> securityRuleList = new ArrayList<>();
        securityRuleList.add(secRule);

        // SecurityGroup
        SecurityGroup securityGroup = new SecurityGroup();
        securityGroup.setName("securityTest");
        securityGroup.setRules(securityRuleList);

        // SecurityGroup list
        List<SecurityGroup> securityGroupList = new ArrayList<>();
        securityGroupList.add(securityGroup);

        // Vpc
        Vpc vpc = new Vpc();
        vpc.setName("xpanse-vpc");
        vpc.setCidr("10.10.0.0/16");
        List<Vpc> vpcList = new ArrayList<>();
        vpcList.add(vpc);

        // Subnet
        Subnet subnet = new Subnet();
        subnet.setName("xpanse-subnet");
        subnet.setCidr("10.10.0.0/24");
        subnet.setVpc("$.network.vpc[0]");
        List<Subnet> subnetList = new ArrayList<>();
        subnetList.add(subnet);

        // Network
        Network network = new Network();
        network.setVpc(vpcList);
        network.setSubnets(subnetList);
        network.setSecurityGroups(securityGroupList);

        ocl.setNetwork(network);
    }

    private void prepareCompute(Ocl ocl) {
        // Vm
        Vm vm = new Vm();
        vm.setName("my-vm");
        vm.setType("c7.large.4");
        vm.setImageId("51cc015c-a833-11ed-b62e-3ba0fb086b32");

        // Subnet JsonPath list
        List<String> subnetList = new ArrayList<>();
        subnetList.add("$.network.subnets[0]");
        vm.setSubnets(subnetList);

        // Security JsonPath list
        List<String> securityList = new ArrayList<>();
        securityList.add("$.network.securityGroups[0]");
        vm.setSecurityGroups(securityList);

        // Storage JsonPath list
        List<String> storageList = new ArrayList<>();
        storageList.add("$.storages[0]");
        vm.setStorages(storageList);

        // UserData for VM
        UserData userData = new UserData();
        userData.setType(UserDataType.SHELL);
        List<String> commands = new ArrayList<>();
        commands.add("echo \"hello world\"");
        userData.setCommands(commands);

        vm.setPublicly(true);
        vm.setUserData(userData);

        // Vm list
        List<Vm> vmList = new ArrayList<>();
        vmList.add(vm);

        // Compute
        Compute compute = new Compute();
        compute.setVms(vmList);

        ocl.setCompute(compute);
    }

    private void prepareStorage(Ocl ocl) {
        // Storage
        Storage storage = new Storage();
        storage.setName("my-storage");
        storage.setType(StorageType.SSD);
        storage.setSize(80);
        storage.setSizeUnit(StorageSizeUnit.GB);

        // Storage list
        List<Storage> storageList = new ArrayList<>();
        storageList.add(storage);

        ocl.setStorages(storageList);
    }

    private void prepareOcl(Ocl ocl) {
        prepareNetwork(ocl);
        prepareCompute(ocl);
        prepareStorage(ocl);
    }

    @Test
    public void Ocl2HclNullTest() {
        Ocl2Hcl hcl = new Ocl2Hcl(new Ocl());
        Assertions.assertThrows(IllegalArgumentException.class, hcl::getHcl);

        Assertions.assertThrows(IllegalArgumentException.class, hcl::getHclSecurityGroupRule);
        Assertions.assertThrows(IllegalArgumentException.class, hcl::getHclSecurityGroup);
        Assertions.assertThrows(IllegalArgumentException.class, hcl::getHclVpcSubnet);
        Assertions.assertThrows(IllegalArgumentException.class, hcl::getHclVpc);
        Assertions.assertThrows(IllegalArgumentException.class, hcl::getHclVm);
        Assertions.assertThrows(IllegalArgumentException.class, hcl::getHclStorage);
    }

    @Test
    public void getHclSecurityGroupRuleTest() {
        // Ocl
        Ocl ocl = new Ocl();
        prepareOcl(ocl);

        Ocl2Hcl ocl2Hcl = mock(Ocl2Hcl.class, withSettings().useConstructor(ocl));
        doCallRealMethod().when(ocl2Hcl).getHclSecurityGroupRule();
        String hcl = ocl2Hcl.getHclSecurityGroupRule();
        Assertions.assertTrue(
                Pattern.compile(
                                "resource.*\"huaweicloud_networking_secgroup_rule\".*\"secRuleTest_0")
                        .matcher(hcl)
                        .find());
        Assertions.assertTrue(
                Pattern.compile(
                                "resource.*\"huaweicloud_networking_secgroup_rule\".*\"secRuleTest_1")
                        .matcher(hcl)
                        .find());
        Assertions.assertTrue(
                Pattern.compile(
                                "resource.*\"huaweicloud_networking_secgroup_rule\".*\"secRuleTest_2")
                        .matcher(hcl)
                        .find());
        Assertions.assertTrue(
                Pattern.compile(
                                "security_group_id.*=.*huaweicloud_networking_secgroup.securityTest.id")
                        .matcher(hcl)
                        .find());
        Assertions.assertTrue(Pattern.compile("direction.*=.*\"ingress\"").matcher(hcl).find());
        Assertions.assertTrue(Pattern.compile("protocol.*=.*\"tcp\"").matcher(hcl).find());
        Assertions.assertTrue(Pattern.compile("port_range_min.*=.*\"9092\"").matcher(hcl).find());
        Assertions.assertTrue(Pattern.compile("port_range_max.*=.*\"9093\"").matcher(hcl).find());
        Assertions.assertTrue(
                Pattern.compile("remote_ip_prefix.*=.*\"10\\.10\\.2\\.0/24\"").matcher(hcl).find());

        doCallRealMethod().when(ocl2Hcl).getHclSecurityGroup();
        hcl = ocl2Hcl.getHclSecurityGroup();
        Assertions.assertTrue(
                Pattern.compile(
                                "resource.*\"huaweicloud_networking_secgroup\".*\"securityTest")
                        .matcher(hcl)
                        .find());
        Assertions.assertTrue(
                Pattern.compile("name.*=.*\"securityTest\"").matcher(hcl).find());
    }

    @Test
    public void getHclVpcTest() {
        // Ocl
        Ocl ocl = new Ocl();
        prepareOcl(ocl);

        Ocl2Hcl ocl2Hcl = mock(Ocl2Hcl.class, withSettings().useConstructor(ocl));
        doCallRealMethod().when(ocl2Hcl).getHclVpc();
        String hcl = ocl2Hcl.getHclVpc();
        Assertions.assertTrue(
                Pattern.compile("resource.*\"huaweicloud_vpc\".*\"xpanse-vpc\".*").matcher(hcl)
                        .find());
        Assertions.assertTrue(Pattern.compile("cidr.*=.*\"10.10.0.0/16\"").matcher(hcl).find());
    }

    @Test
    public void getHclVpcSubnetTest() {
        // Ocl
        Ocl ocl = new Ocl();
        prepareOcl(ocl);

        Ocl2Hcl ocl2Hcl = mock(Ocl2Hcl.class, withSettings().useConstructor(ocl));
        doCallRealMethod().when(ocl2Hcl).getHclVpcSubnet();
        Assertions.assertDoesNotThrow(ocl2Hcl::getHclVpcSubnet);
        String hcl = ocl2Hcl.getHclVpcSubnet();
        Assertions.assertTrue(
                Pattern.compile("resource.*\"huaweicloud_vpc_subnet\".*\"xpanse-subnet\".*")
                        .matcher(hcl)
                        .find());
        Assertions.assertTrue(Pattern.compile("name.*=.*\"xpanse-subnet\"").matcher(hcl).find());
        Assertions.assertTrue(Pattern.compile("cidr.*=.*\"10.10.0.0/24\"").matcher(hcl).find());
        Assertions.assertTrue(Pattern.compile("gateway_ip.*=.*\"10.10.0.1\"").matcher(hcl).find());
        Assertions.assertTrue(
                Pattern.compile("vpc_id.*=.*huaweicloud_vpc.xpanse-vpc.id").matcher(hcl).find());
    }

    @Test
    public void getHclAvailabilityZoneTest() {
        // Ocl
        Ocl ocl = new Ocl();
        prepareOcl(ocl);

        Ocl2Hcl ocl2Hcl = mock(Ocl2Hcl.class, withSettings().useConstructor(ocl));
        doCallRealMethod().when(ocl2Hcl).getHclAvailabilityZone();
        Assertions.assertDoesNotThrow(ocl2Hcl::getHclAvailabilityZone);
    }

    @Test
    public void getHclFlavorTest() {
        // Ocl
        Ocl ocl = new Ocl();
        prepareOcl(ocl);

        Ocl2Hcl ocl2Hcl = mock(Ocl2Hcl.class, withSettings().useConstructor(ocl));
        doCallRealMethod().when(ocl2Hcl).getHclFlavor();
        Assertions.assertDoesNotThrow(ocl2Hcl::getHclFlavor);
    }

    @Test
    public void getHclImageTest() {
        // Ocl
        Ocl ocl = new Ocl();
        prepareOcl(ocl);

        Ocl2Hcl ocl2Hcl = mock(Ocl2Hcl.class, withSettings().useConstructor(ocl));
        doCallRealMethod().when(ocl2Hcl).getHclImage();
        Assertions.assertDoesNotThrow(ocl2Hcl::getHclImage);
    }

    @Test
    public void getHclStorageTest() {
        // Ocl
        Ocl ocl = new Ocl();
        prepareOcl(ocl);

        Ocl2Hcl ocl2Hcl = mock(Ocl2Hcl.class, withSettings().useConstructor(ocl));
        doCallRealMethod().when(ocl2Hcl).getHclStorage();
        String hcl = ocl2Hcl.getHclStorage();
        Assertions.assertTrue(
                Pattern.compile("resource.*\"huaweicloud_evs_volume\".*\"my-storage\"")
                        .matcher(hcl)
                        .find());
        Assertions.assertTrue(Pattern.compile("name.*=.*\"my-storage\"").matcher(hcl).find());
        Assertions.assertTrue(Pattern.compile("volume_typ.* =.*\"SSD\"").matcher(hcl).find());
        Assertions.assertTrue(Pattern.compile("size.*=.*\"80\"").matcher(hcl).find());
    }

    @Test
    public void getHclVmTest() {
        // Ocl
        Ocl ocl = new Ocl();
        prepareOcl(ocl);

        Ocl2Hcl ocl2Hcl = mock(Ocl2Hcl.class, withSettings().useConstructor(ocl));
        doCallRealMethod().when(ocl2Hcl).getHclVm();

        String hcl = ocl2Hcl.getHclVm();
        Assertions.assertDoesNotThrow(ocl2Hcl::getHclVm);
        Assertions.assertTrue(
                Pattern.compile("resource.*\"huaweicloud_compute_instance\".*\"my-vm\"")
                        .matcher(hcl)
                        .find());
        Assertions.assertTrue(Pattern.compile("name.*=.*\"my-vm\"").matcher(hcl).find());
        Assertions.assertTrue(Pattern.compile("flavor_id.*=.*\"c7.large.4\"").matcher(hcl).find());
        Assertions.assertTrue(
                Pattern.compile("uuid.*=.*huaweicloud_vpc_subnet.xpanse-subnet.id").matcher(hcl)
                        .find());
        Assertions.assertTrue(
                Pattern
                        .compile(
                                "security_group_ids.*=.*huaweicloud_networking_secgroup.securityTest.id ]")
                        .matcher(hcl)
                        .find());
    }
}