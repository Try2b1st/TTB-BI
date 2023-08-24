import { GithubOutlined } from '@ant-design/icons';
import { DefaultFooter } from '@ant-design/pro-components';
import '@umijs/max';
import React from 'react';

const Footer: React.FC = () => {
  const defaultMessage = 'Try to best 团队出品';
  const currentYear = new Date().getFullYear();
  return (
    <DefaultFooter
      style={{
        background: 'none',
      }}
      copyright={`${currentYear} ${defaultMessage}`}
      links={[
        {
          key: 'TTB智能 BI',
          title: 'TTB智能 BI',
          href: 'https://pro.ant.design',
          blankTarget: true,
        },
        {
          key: 'github',
          title: <GithubOutlined />,
          href: 'https://github.com/Try2b1st',
          blankTarget: true,
        },
        {
          key: 'TTB智能 BI',
          title: 'TTB智能 BI',
          href: 'https://github.com/Try2b1st',
          blankTarget: true,
        },
      ]}
    />
  );
};
export default Footer;
