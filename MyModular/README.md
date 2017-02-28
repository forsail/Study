#MyModular

- 测试组件化的项目
- 采用的张涛的[方案](http://kymjs.com/code/2016/10/18/01)

# lib_processor
运行时注解，自定义的注解是为了方便遍历项目中所有的 Activity 和 Fragment。目前只做了 Activity ，用来尝试方案可行性

## module_login
模拟登陆模块

## module_mine
模拟个人中心模块

## router
作为跳转层,并且承担了所有的通用依赖。目前该模块只完成了 Activity 的跳转的职责，我打算加上对外暴露任意接口的职责

## 说明
项目的中心思想是，所有的通信职责交给驱动路由中转。





