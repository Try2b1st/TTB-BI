import { LoadingOutlined, PlusOutlined } from '@ant-design/icons';
import {Button, Card, Col, Form, message, Row, Space, Spin, Upload} from 'antd';

import type { UploadChangeParam } from 'antd/es/upload';
import type { RcFile, UploadFile, UploadProps } from 'antd/es/upload/interface';
import React, { useState } from 'react';
import {genChartByAiUsingPOST} from "@/services/ttb-bi/chartController";
import {handleFileUploadUsingPOST} from "@/services/ttb-bi/photoController";

const getBase64 = (img: RcFile, callback: (url: string) => void) => {
  const reader = new FileReader();
  reader.addEventListener('load', () => callback(reader.result as string));
  reader.readAsDataURL(img);
};

const Photo: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [imageUrl, setImageUrl] = useState<string>();
  const [submitting, setSubmitting] = useState<boolean>(false);
  const [sliderValue, setSliderValue] = useState(50);

  const handleChange: UploadProps['onChange'] = (info: UploadChangeParam<UploadFile>) => {
    if (info.file.status === 'uploading') {
      setLoading(true);
      return;
    } else {
      getBase64(info.file.originFileObj as RcFile, (url) => {
        setLoading(false);
        setImageUrl(url);
      });
    }
  };

  const uploadButton = (
    <div>
      {loading ? <LoadingOutlined /> : <PlusOutlined />}
      <div style={{ marginTop: 8 }}>Upload</div>
    </div>
  );

  const onFinish = async (values: any) => {
    if (submitting) {
      return;
    }
    setSubmitting(true);

    // 创建一个 FormData 实例来存储表单数据
    const formData = new FormData();
    formData.append('file', values.file.file);

    try {
      const result = await handleFileUploadUsingPOST(formData,{});
    } catch (e) {
      message.success('上传图片失败.' + e);
    }

  };

  const handleSliderChange = (event) => {
    setSliderValue(event.target.value);
  };

  return (
    <div className="Photo">
      <Row gutter={24}>
        <Col span={12}>
          <Card>
            <Form
              name="addChart"
              labelAlign="left"
              labelCol={{ span: 4 }}
              wrapperCol={{ span: 16 }}
              onFinish={onFinish}
              initialValues={{}}
            >
              <Form.Item name="file" label="旧照片">
                <Upload
                  name="avatar"
                  listType="picture-card"
                  className="avatar-uploader"
                  showUploadList={false}
                  action="https://run.mocky.io/v3/435e224c-44fb-4773-9faf-380c5e6a2188"
                  onChange={handleChange}
                >
                  {uploadButton}
                </Upload>
              </Form.Item>

              <Card>
                {imageUrl ? (
                  <img src={imageUrl} alt="avatar" style={{ width: '60%' }} />
                ) : (
                  '请上传照片'
                )}
              </Card>

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
          <Card title="修复结果">
            <div>请先在左侧上传照片并提交</div>
            <Spin spinning={submitting} />
          </Card>
        </Col>
      </Row>
      <div style={{ fontSize: '20px' }}>😭效果展示😀:</div>
      <Row>
        <Col span={6}>
          <div className="image-slider-container">
            <input
              type="range"
              min="0"
              max="100"
              style={{
                width: '100%',
              }}
              value={sliderValue}
              onChange={handleSliderChange}
              className="slider"
            />
          </div>
        </Col>
      </Row>

      <Card style={{ height: 500, width: 400 }}>
        <img
          src="/HR_result.jpg"
          alt="before"
          style={{
            position: 'absolute',
            top: 0,
            left: 0,
            zIndex: 2,
            clipPath: `inset(0 ${100 - sliderValue}% 0 0)`,
            width: '100%',
          }}
        />
        <img
          src="/HR_result2.jpg"
          alt="after"
          style={{
            position: 'absolute',
            top: 0,
            left: 0,
            zIndex: 1,
            clipPath: `inset(0 0 0 ${sliderValue}%)`,
            width: '100%',
          }}
        />
      </Card>
    </div>
  );
};
export default Photo;
