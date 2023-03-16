import React from 'react';
import { Table } from 'antd';
import type { ColumnsType, TableProps } from 'antd/es/table';

  interface DataType {
    key: React.Key;
    Property: string;
    Information: string;
  }

  const columns: ColumnsType<DataType> = [
    {
      title: 'Property',
      dataIndex: 'Property',
      filterMode: 'tree',
      filterSearch: true,
      width: '20%',
    },
    {
      title: 'Information',
      dataIndex: 'Information',
      filterSearch: true,
      width: '30%',
    },
  ];

/*  const data: DataType[] = [
    {
      key: '1',
      Property: 'Category',
      Information: 'MiddleWare',
    },{
      key: '2',
      Property: 'Provider',
      Information: 'New York No. 1 Lake Park',
    },
    {
      key: '3',
      Property: 'Service Version',
      Information: 'V1.0',
    },
    {
      key: '4',
      Property: 'Billing Mode',
      Information: 'Monthly Per Service Instance',
    },
    {
      key: '5',
      Property: 'Regullar Pricing',
      Information: '￥140.00',
    },
    {
      key: '6',
      Property: 'Register Time',
      Information: '2022-08-26 T08:25:15:208Z',
    },
    {
      key: '7',
      Property: 'Status',
      Information: '2022-08-26 T08:25:15:208Z',
    },
    {
      key: '8',
      Property: 'Flavors',
      Information: 'London No. Park',
    },
  ];*/

  const onChange: TableProps<DataType>['onChange'] = (pagination, filters, sorter, extra) => {
    console.log('params', pagination, filters, sorter, extra);
  };

  const ServicesTab: React.FC<{data:DataType[]}> = ({data}) => <Table columns={columns} dataSource={data} onChange={onChange} />;

export default ServicesTab;