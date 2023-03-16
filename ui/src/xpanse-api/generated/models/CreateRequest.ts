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

export class CreateRequest {
    'category': CreateRequestCategoryEnum;
    'name': string;
    'version': string;
    'csp': CreateRequestCspEnum;
    'flavor': string;
    'property'?: { [key: string]: string };

    static readonly discriminator: string | undefined = undefined;

    static readonly attributeTypeMap: Array<{ name: string; baseName: string; type: string; format: string }> = [
        {
            name: 'category',
            baseName: 'category',
            type: 'CreateRequestCategoryEnum',
            format: '',
        },
        {
            name: 'name',
            baseName: 'name',
            type: 'string',
            format: '',
        },
        {
            name: 'version',
            baseName: 'version',
            type: 'string',
            format: '',
        },
        {
            name: 'csp',
            baseName: 'csp',
            type: 'CreateRequestCspEnum',
            format: '',
        },
        {
            name: 'flavor',
            baseName: 'flavor',
            type: 'string',
            format: '',
        },
        {
            name: 'property',
            baseName: 'property',
            type: '{ [key: string]: string; }',
            format: '',
        },
    ];

    static getAttributeTypeMap() {
        return CreateRequest.attributeTypeMap;
    }

    public constructor() {}
}

export type CreateRequestCategoryEnum =
    | 'AI'
    | 'COMPUTE'
    | 'CONTAINER'
    | 'STORAGE'
    | 'NETWORK'
    | 'DATABASE'
    | 'MEDIA_SERVICE'
    | 'SECURITY'
    | 'MIDDLEWARE'
    | 'OTHERS';
export type CreateRequestCspEnum = 'aws' | 'azure' | 'alibaba' | 'huawei' | 'openstack';
