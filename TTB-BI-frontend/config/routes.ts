export default [
  { path: '/user', name: '用户', layout: false, routes: [{ path: '/user/login', component: './User/Login' }] },
  { path: '/welcome', name: '欢迎', icon: 'smile', component: './Welcome' },
  {
    path: '/admin',
    icon: 'crown',
    access: 'canAdmin',
    routes: [
      { path: '/admin', name: '管理页面', redirect: '/admin/sub-page' },
      { path: '/admin/sub-page', name: '二级管理页面', component: './Admin' },
    ],
  },
  { icon: 'table', path: '/list', component: './TableList' },
  { path: '/', name: '查询表格', redirect: '/welcome' },
  { path: '*', layout: false, component: './404' },
];
