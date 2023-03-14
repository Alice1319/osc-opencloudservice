/*
 * SPDX-License-Identifier: Apache-2.0
 * SPDX-FileCopyrightText: Huawei Inc.
 *
 */

package org.eclipse.xpanse.orchestrator.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.xpanse.modules.database.register.RegisterServiceEntity;
import org.eclipse.xpanse.modules.models.enums.DeployVariableType;
import org.eclipse.xpanse.modules.models.resource.DeployVariable;

/**
 * OpenApiUtil.
 */
@Slf4j
public class OpenApiUtil {

    /**
     * update OpenApi for registered service .
     *
     * @param registerService Registered services.
     */
    public static String updateServiceApi(RegisterServiceEntity registerService) {
        String rootPath = System.getProperty("user.dir");
        File folder = new File(rootPath + "/openapi");
        File file = new File(folder, registerService.getId() + ".html");
        if (file.exists()) {
            file.delete();
        }
        List<DeployVariable> context = registerService.getOcl().getDeployment().getContext();
        Set<String> requiredSet = new HashSet<>();
        Map<String, Map<String, String>> propertiesMap = new HashMap<>();

        for (DeployVariable deployVariable : context) {
            Map map = new HashMap();
            //mandatory  Verify whether it is a required parameter
            if (deployVariable.getMandatory()) {
                requiredSet.add(deployVariable.getName());
            }
            // Parse validator content
            if (!Objects.isNull(deployVariable.getValidator())) {
                map.put("type", deployVariable.getType().toValue());
                map.put("description", deployVariable.getDescription());
                map.put("example", deployVariable.getValue());
                if (deployVariable.getType().equals(DeployVariableType.STRING)) {
                    String[] validArray = deployVariable.getValidator().split("\\|");
                    for (String v : validArray) {
                        String[] keyValue = v.split("=", 2);
                        if (StringUtils.contains(keyValue[0], "Length")) {
                            try {
                                Integer value = Integer.parseInt(keyValue[1]);
                                map.put(keyValue[0], value.toString());
                                map.put("isValidator", "true");
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                if (deployVariable.getType().equals(DeployVariableType.NUMBER)) {
                    String[] validArray = deployVariable.getValidator().split("\\|");
                    for (String v : validArray) {
                        String[] keyValue = v.split("=", 2);
                        if (StringUtils.contains(keyValue[0], "mum")) {
                            try {
                                Integer value = Integer.parseInt(keyValue[1]);
                                map.put(keyValue[0], value.toString());
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                propertiesMap.put(deployVariable.getName(), map);
            } else {
                map.put("type", deployVariable.getType().toValue());
                map.put("description", deployVariable.getDescription());
                map.put("example", deployVariable.getValue());
                propertiesMap.put(deployVariable.getName(), map);
            }
        }
        StringBuilder strPro = new StringBuilder();
        for (String key : propertiesMap.keySet()) {
            Map<String, String> stringMap = propertiesMap.get(key);
            strPro.append("        ").append(key).append(":").append("\n");
            strPro.append("         ").append("type:").append(" ").append(stringMap.get(
                    "type")).append(
                    "\n");
            strPro.append("         ").append("description:").append(" ").append(stringMap.get(
                    "description")).append(
                    "\n");
            strPro.append("         ").append("example:").append(" ").append(stringMap.get(
                    "example")).append(
                    "\n");
            if (stringMap.get("type").equals("string") && !Objects.isNull(stringMap.get(
                    "isValidator"))) {
                strPro.append("         ").append("minLength:").append(" ").append(stringMap.get(
                        "minLength")).append(
                        "\n");
                strPro.append("         ").append("maxLength:").append(" ").append(stringMap.get(
                        "maxLength")).append(
                        "\n");
            }
            if (stringMap.get("type").equals("number")) {
                strPro.append("         ").append("minimum:").append(" ").append(stringMap.get(
                        "minimum")).append(
                        "\n");
                strPro.append("         ").append("maximum:").append(" ").append(stringMap.get(
                        "maximum")).append(
                        "\n");
            }
        }

        StringBuilder strReq = new StringBuilder();
        for (String a : requiredSet) {
            strReq.append("        ").append("-").append(" ").append(a).append("\n");
        }
        String serviceApi = String.format("openapi: 3.0.1\n"
                + "info:\n"
                + "  title: OpenAPI definition\n"
                + "  version: v0\n"
                + "servers:\n"
                + "  - url: http://localhost:8080\n"
                + "    description: Generated server url\n"
                + "paths:\n"
                + "  /xpanse/service:\n"
                + "    post:\n"
                + "      tags:\n"
                + "        - Service\n"
                + "      operationId: start\n"
                + "      requestBody:\n"
                + "        content:\n"
                + "          application/json:\n"
                + "            schema:\n"
                + "              $ref: '#/components/schemas/CreateRequest'\n"
                + "        required: true\n"
                + "      responses:\n"
                + "        '202':\n"
                + "          description: Accepted\n"
                + "          content:\n"
                + "            '*/*':\n"
                + "              schema:\n"
                + "                $ref: '#/components/schemas/Response'\n"
                + "        '400':\n"
                + "          description: Bad Request\n"
                + "          content:\n"
                + "            '*/*':\n"
                + "              schema:\n"
                + "                $ref: '#/components/schemas/Response'\n"
                + "        '404':\n"
                + "          description: Not Found\n"
                + "          content:\n"
                + "            '*/*':\n"
                + "              schema:\n"
                + "                $ref: '#/components/schemas/Response'\n"
                + "        '500':\n"
                + "          description: Internal Server Error\n"
                + "          content:\n"
                + "            '*/*':\n"
                + "              schema:\n"
                + "                $ref: '#/components/schemas/Response'\n"
                + "components:\n"
                + "  schemas:\n"
                + "    Response:\n"
                + "      required:\n"
                + "        - code\n"
                + "        - message\n"
                + "        - success\n"
                + "      type: object\n"
                + "      properties:\n"
                + "        code:\n"
                + "          type: string\n"
                + "          description: The result code of response.\n"
                + "        message:\n"
                + "          type: string\n"
                + "          description: The result message of response.\n"
                + "        success:\n"
                + "          type: boolean\n"
                + "          description: The success boolean of response.\n"
                + "        data:\n"
                + "          type: object\n"
                + "          description: The result data of response.\n"
                + "    property:\n"
                + "      required:\n"
                + "%s\n"
                + "      type: object\n"
                + "      properties:\n"
                + "%s\n"
                + "    CreateRequest:\n"
                + "      required:\n"
                + "        - csp\n"
                + "        - flavor\n"
                + "        - name\n"
                + "        - version\n"
                + "      type: object\n"
                + "      properties:\n"
                + "        name:\n"
                + "          type: string\n"
                + "        version:\n"
                + "          type: string\n"
                + "        csp:\n"
                + "          type: string\n"
                + "          enum:\n"
                + "            - aws\n"
                + "            - azure\n"
                + "            - alibaba\n"
                + "            - huawei\n"
                + "            - openstack\n"
                + "        flavor:\n"
                + "          type: string\n"
                + "        property:\n"
                + "          $ref: '#/components/schemas/property'\n", strReq, strPro);
        String yamlFileName = String.format("%s" + ".yaml", registerService.getId());
        File openapi = new File("openapi");
        openapi.mkdir();
        try {
            try (FileWriter apiWriter =
                    new FileWriter("./openapi" + File.separator + yamlFileName)) {
                apiWriter.write(serviceApi);
            }
            String comm = "java -jar ./lib/openapi-generator-cli.jar generate -i "
                    + "./openapi/" + yamlFileName + " -g "
                    + "html2 -o ./openapi";
            Process exec = Runtime.getRuntime()
                    .exec(comm);
            exec.waitFor();
            // Modify the file name to serviceId.html
            File oldFile = new File("./openapi/index.html");
            File newFile = new File("./openapi/" + registerService.getId() + ".html");
            oldFile.renameTo(newFile);

            File yamlFile = new File(
                    "./openapi" + File.separator + yamlFileName);
            yamlFile.delete();
            log.info("serviceApi update success.");
            return "http://localhost:8080/openapi/" + registerService.getId() + ".html";
        } catch (IOException | InterruptedException ex) {
            log.error("serviceApi update failed.", ex);
            throw new RuntimeException("serviceApi update failed.", ex);
        }
    }

    /**
     * delete OpenApi for registered service using the ID.
     *
     * @param id ID of registered service.
     */
    public static void deleteServiceApi(String id) {
        File file = new File("openapi/" + id + ".html");
        file.delete();
    }

}
