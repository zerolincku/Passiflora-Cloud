# Docker-file 说明

### [Dockerfile-builder](../../Dockerfile-builder)
构建编译镜像，此镜像直接包含编译所需依赖，加快构建速度。
（应用镜像构建使用两阶段构建方式，创建专门用于构建的容器对项目进行编译，不使用本地环境，保证构建环境的一致性）
```shell
# 使用了交叉编译不同架构平台所使用的镜像
# zerolinck/passiflora-server-builder 为我个人的 dockerhub 仓库
docker buildx build --push --platform linux/arm64,linux/amd64 -t zerolinck/passiflora-server-builder -f Dockerfile-builder .
```

### [Dockerfile-iam](../../Dockerfile-iam)
构建系统服务镜像
```shell
# 构建并推送到 dockerhub 仓库
docker buildx build --push --platform linux/arm64,linux/amd64 -t zerolinck/passiflora-iam -f Dockerfile-iam .

# 本地构建
docker build -t zerolinck/passiflora-iam -f Dockerfile-iam .
```

### [Dockerfile-gateway](../../Dockerfile-gateway)
构建网关服务镜像
```shell
# 构建并推送到 dockerhub 仓库
docker buildx build --push --platform linux/arm64,linux/amd64 -t zerolinck/passiflora-gateway -f Dockerfile-gateway .

# 本地构建
docker build -t zerolinck/passiflora-gateway -f Dockerfile-gateway .
```
```