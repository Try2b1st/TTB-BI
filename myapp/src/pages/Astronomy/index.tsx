import {doWhatUsingPOST, genChartByAiUsingPOST} from '@/services/ttb-bi/chartController';
import { UploadOutlined } from '@ant-design/icons';
import {
  Button,
  Card,
  Col,
  Divider,
  Form,
  Input,
  message,
  Row,
  Select,
  Space,
  Spin,
  Upload,
} from 'antd';
import TextArea from 'antd/es/input/TextArea';
import ReactECharts from 'echarts-for-react';
import React, { useState } from 'react';

/**
 * 生成图表
 *
 * @constructor
 */
const Astronomy: React.FC = () => {
  const [chart, setChart] = useState<string>();
  const [submitting, setSubmitting] = useState<boolean>(false);

  const onFinish = async (values: any) => {
    if (submitting) {
      return;
    }
    setSubmitting(true);
    setChart(undefined);

    try {
      const result = await doWhatUsingPOST(values, {});
      console.log(result);
      if (!result?.data) {
        message.success('回答失败');
      } else {
        message.success('成功获得回答');
          setChart(result.data);
      }
    } catch (e) {
      message.success('分析失败.' + e);
    }
    setSubmitting(false);
  };

  return (
    <div className="add_chart">
      <Row gutter={24}>
        <Col span={12}>
          <Card title="天文学知识智能问答">
            <Form
              name="addChart"
              labelAlign="left"
              labelCol={{ span: 4 }}
              wrapperCol={{ span: 16 }}
              onFinish={onFinish}
              initialValues={{}}
            >
              <Form.Item name="name" label="问题">
                <Input placeholder="请输入你的问题" />
              </Form.Item>

              <Form.Item wrapperCol={{ span: 16, offset: 4 }}>
                <Space>
                  <Button
                    type="primary"
                    htmlType="submit"
                    loading={submitting}
                    disabled={submitting}
                  >
                    提交
                  </Button>
                  <Button htmlType="reset">重置</Button>
                </Space>
              </Form.Item>
            </Form>
          </Card>
        </Col>

        <Col span={12}>
          <Card title="回答">
            {chart}
            <Spin spinning={submitting} />
          </Card>
        </Col>
      </Row>
    </div>
  );
};
export default Astronomy;
