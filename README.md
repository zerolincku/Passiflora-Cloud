<div align=center>
<h1>Passiflora Cloud</h1>
</div>

### 系统说明
Passiflora Cloud 是基于 JDK21、Spring Cloud、Gradle 构建的微服务项目，不同于其他的同类项目，Passiflora Cloud 具有以下优点：
1. 合理清晰的项目结构，Passiflora Cloud 不同的应用以及通用服务，拆分为不同的模块。web 服务模块以 app 名称结尾，依赖模块以及通用模块放置在 modules 目录下。
2. 高可靠的业务实现，基于版本号乐观锁+分布式锁的设计，保证所有的数据变更都不会出现并发导致的数据异常问题。
3. 通过 JUnit 进行集成测试，接口覆盖率达到 100%，进一步的保证系统可靠性。

### 项目结构
```shell
├── .github（github actions配置, 不使用 github actions 可删除）
├── passiflora-server （后端项目）
│   ├── modules
│   │   ├── passiflora-bom （依赖版本配置）
│   │   ├── passiflora-codegen （代码生成）
│   │   ├── passiflora-common （通用组件）
│   │   ├── passiflora-feign （Feign组件）
│   │   └── passiflora-model （Model组件）
│   ├── passiflora-gateway-app （网关应用）
│   ├── passiflora-iam-app （身份应用）
│   ├── passiflora-storage-app （文件储存应用）
│   └── buildSrc (构建脚本)
└── passiflora-ui （前端项目）
```
### [授权说明](document%2Fzh%2F授权说明.md)

### [多环境说明](document%2Fzh%2F多环境说明.md)

### [开发环境搭建](document%2Fzh%2F开发环境搭建.md)

### [Docker-file 说明](document%2Fzh%2FDockerfile.md)

### [集成测试说明](document%2Fzh%2F集成测试说明.md)