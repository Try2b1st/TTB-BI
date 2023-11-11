import { listChartByPageUsingPOST } from '@/services/ttb-bi/chartController';
import { useModel } from '@umijs/max';
import { Avatar, Card, List, message } from 'antd';
import Search from 'antd/es/input/Search';
import ReactECharts from 'echarts-for-react';
import React, { useEffect, useState } from 'react';

/**
 * 生成图表
 *
 * @constructor
 */
const MyChartPage: React.FC = () => {
  const initState = {
    current: 1,
    pageSize: 2,
    sortField: 'createTime',
    sortOrder: 'desc',
  };

  //获取用户数据
  const { initialState } = useModel('@@initialState');
  const { currentUser } = initialState ?? {};
  const [searchValue, setSearchValue] = useState('');
  const [searchParams, setSearchParams] = useState<API.ChartQueryRequest>({ ...initState });
  const [chartList, setChartList] = useState<API.Chart[]>();
  const [total, setTotal] = useState<number>(0);
  const [loading, setLoading] = useState<boolean>(true);
  const loadData = async () => {
    setLoading(true);
    try {
      const res = await listChartByPageUsingPOST(searchParams);
      if (res.data) {
        setChartList(res.data.records ?? []);
        setTotal(res.data.total ?? 0);
        //隐藏图表的title
        if (res.data.records) {
          res.data.records.forEach((data) => {
            const chartOption = JSON.parse(data.genChart ?? '{}');
            chartOption.title = undefined;
            data.genChart = JSON.stringify(chartOption);
          });
        }
      } else {
        message.error('获取我的图表失败');
      }
    } catch (e: any) {
      message.error('获取我的图表失败' + e.message);
    }
    setLoading(false);
  };

  //React 的钩子 每当页面加载时和deps的值改变时，执行里面的函数
  useEffect(() => {
    loadData();
  }, [searchParams]);

  return (
    <div className="my-chart-page">
      <Search
        placeholder="请输入图标名称"
        enterButton
        loading={loading}
        onSearch={(value) => {
          setSearchValue(value);
          setSearchParams({
            ...initState,
            name: value,
          });
        }}
      />
      <div style={{ marginBottom: 16 }}></div>
      <List
        grid={{
          gutter: 16,
          xs: 1,
          sm: 1,
          md: 1,
          lg: 2,
          xl: 2,
          xxl: 2,
        }}
        pagination={{
          onChange: (page, pageSize) => {
            setSearchParams({
              ...initState,
              current: page,
              name: searchValue,
              pageSize,
            });
          },
          pageSize: searchParams.pageSize,
          current: searchParams.current,
          total: total,
        }}
        loading={loading}
        dataSource={chartList}
        renderItem={(item) => (
          <List.Item key={item.id}>
            <Card>
              <List.Item.Meta
                avatar={<Avatar src={currentUser && currentUser.userAvatar} />}
                title={'图表名称: ' + item.name}
                description={item.chartType ? '图表类型: ' + item.chartType : undefined}
              />
              <div style={{ marginBottom: 16 }}></div>
              <p>{'分析目标: ' + item.goal}</p>
              <>
                {item.status === 'wait' && (
                  <>
                    {/*<Result*/}
                    {/*  status="warning"*/}
                    {/*  title="待生成"*/}
                    {/*  subTitle={item.execMessage ?? '当前图表生成队列繁忙，请耐心等候'}*/}
                    {/*/>*/}
                    <p>图表生成任务还在队列中，请耐心等候</p>
                    <img width={450} src={"https://try2b1st-photo-1311984591.cos.ap-guangzhou.myqcloud.com/markdown%2Fwait.png"}/>
                  </>
                )}
                {item.status === 'running' && (
                  <>
                    <p>图表正在生成，信息：{item.execMessage}</p>
                    <img width={450} src={"https://try2b1st-photo-1311984591.cos.ap-guangzhou.myqcloud.com/markdown%2Frunning.jpg"}/>
                  </>
                )}
                {item.status === 'succeed' && (
                  <>
                    <ReactECharts option={JSON.parse(item.genChart ?? '{}')} />
                    <p>{'分析结果: ' + item.genResult}</p>
                  </>
                )}
                {item.status === 'failed' && (
                  <>
                    <p>图表生成失败，错误信息：{item.execMessage}</p>
                    <img width={500} src={"https://try2b1st-photo-1311984591.cos.ap-guangzhou.myqcloud.com/markdown%2Ffailed.jpg"}/>
                  </>
                )}
              </>
            </Card>
          </List.Item>
        )}
      />
    </div>
  );
};
export default MyChartPage;
