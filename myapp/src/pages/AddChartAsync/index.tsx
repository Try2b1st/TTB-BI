import { UploadOutlined } from '@ant-design/icons';
import { Button, Card, Form, Input, message, Select, Space, Upload } from 'antd';
import TextArea from 'antd/es/input/TextArea';
import { useForm } from 'antd/lib/form/Form';
import React, { useState } from 'react';
import {genChartByAiUsingPOST} from "@/services/ttb-bi/chartController";

/**
 * 生成图表
 *
 * @constructor
 */
const AddChartAsync: React.FC = () => {
  const [form] = useForm();
  const [submitting, setSubmitting] = useState<boolean>(false);

  const onFinish = async (values: any) => {
    if (submitting) {
      return;
    }
    setSubmitting(true);
    const params = {
      ...values,
      file: undefined,
    };

    try {
      const result = await genChartByAiUsingPOST(params, {}, values.file.file.originFileObj);
      console.log(result);
      if (!result?.data) {
        message.success('分析失败');
      } else {
        message.success('提交分析数据成功，请稍后在我的图表页面查看分析结果');
        //清空表单
        form.resetFields();
      }
    } catch (e) {
      message.success('分析失败.' + e);
    }
    setSubmitting(false);
  };

  return (
    <div className="add_chart_async">
      <Card title="智能分析">
        <Form
          form={form}
          name="addChart"
          labelAlign="left"
          labelCol={{ span: 4 }}
          wrapperCol={{ span: 16 }}
          onFinish={onFinish}
          initialValues={{}}
        >
          <Form.Item name="name" label="图标名称">
            <Input placeholder="请输入你的图表名称" />
          </Form.Item>

          <Form.Item
            name="goal"
            label="分析目标"
            rules={[{ required: true, message: '请输入分析目标' }]}
          >
            <TextArea placeholder="请输入你的分析需求：分析网站用户的增长趋势" />
          </Form.Item>

          <Form.Item name="chartType" label="图表类型">
            <Select
              options={[
                { value: '折线图', label: '折线图' },
                { value: '柱状图', label: '柱状图' },
                { value: '饼图', label: '饼图' },
                { value: '散点图', label: '散点图' },
                { value: '雷达图', label: '雷达图' },
                { value: 'K线图', label: 'K线图' },
              ]}
            />
          </Form.Item>

          <Form.Item name="file" label="原始数据">
            <Upload name="file" maxCount={1}>
              <Button icon={<UploadOutlined />}>上传Excel文件</Button>
            </Upload>
          </Form.Item>

          <Form.Item wrapperCol={{ span: 16, offset: 4 }}>
            <Space>
              <Button type="primary" htmlType="submit" loading={submitting} disabled={submitting}>
                提交
              </Button>
              <Button htmlType="reset">重置</Button>
            </Space>
          </Form.Item>
        </Form>
      </Card>
    </div>
  );
};
export default AddChartAsync;
