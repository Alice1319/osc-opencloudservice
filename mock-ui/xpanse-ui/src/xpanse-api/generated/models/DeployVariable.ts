/*
 * SPDX-License-Identifier: Apache-2.0
 * SPDX-FileCopyrightText: Huawei Inc.
 */

/**
 * OpenAPI definition
 * No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)
 *
 * OpenAPI spec version: v0
 *
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

/**
 * The variables for the deployment, which will be passed to the deployer
 */
export class DeployVariable {
    /**
     * The name of the deploy variable
     */
    'name': string;
    /**
     * The description for the deploy variable
     */
    'description': string;
    /**
     * The kind of the deploy variable
     */
    'kind': DeployVariableKindEnum;
    /**
     * The type of the deploy variable
     */
    'type': string;
    /**
     * The value of the deploy variable
     */
    'value'?: string;
    /**
     * Indicate the variable if is mandatory
     */
    'mandatory': boolean;
    /**
     * Validator of the variable
     */
    'validator'?: string;

    static readonly discriminator: string | undefined = undefined;

    static readonly attributeTypeMap: Array<{ name: string; baseName: string; type: string; format: string }> = [
        {
            name: 'name',
            baseName: 'name',
            type: 'string',
            format: '',
        },
        {
            name: 'description',
            baseName: 'description',
            type: 'string',
            format: '',
        },
        {
            name: 'kind',
            baseName: 'kind',
            type: 'DeployVariableKindEnum',
            format: '',
        },
        {
            name: 'type',
            baseName: 'type',
            type: 'string',
            format: '',
        },
        {
            name: 'value',
            baseName: 'value',
            type: 'string',
            format: '',
        },
        {
            name: 'mandatory',
            baseName: 'mandatory',
            type: 'boolean',
            format: '',
        },
        {
            name: 'validator',
            baseName: 'validator',
            type: 'string',
            format: '',
        },
    ];

    static getAttributeTypeMap() {
        return DeployVariable.attributeTypeMap;
    }

    public constructor() {}
}

export type DeployVariableKindEnum = 'fix_env' | 'fix_variable' | 'env' | 'variable';
