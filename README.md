<div align=center>
<h1>Passiflora Cloud</h1>
</div>

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
│   ├── passiflora-gateway-app （网关）
│   └── passiflora-system-app （系统应用）
└── passiflora-ui （前端项目）
```
### [授权说明](document%2Fzh%2F授权说明.md)

### [多环境说明](document%2Fzh%2F多环境说明.md)

### [开发环境搭建](document%2Fzh%2F开发环境搭建.md)

### [Docker-file 说明](document%2Fzh%2FDockerfile.md)

### [单元测试说明](document%2Fzh%2F单元测试说明.md)