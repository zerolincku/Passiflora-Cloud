environments {
    // 开发环境
    dev {
        nacos {
            serverAddr = 'passiflora-nacos:8848'
        }
        redis {
            host = "passiflora-redis"
            port = "6379"
            database = '8'
        }
        minio {
            endpoint = 'http://passiflora-minio:9000'
            accessKey = 'BqzUCvFnYy8a1eECZgKR'
            secretKey = 'zQUasocpCxHGYPZx268hl9WsSOvBQIxk0QH40Z9H'
        }
        system_app {
            database {
                url = 'jdbc:postgresql://passiflora-postgres:5432/passiflora_system?reWriteBatchedInserts=true'
                username = 'postgres'
                password = 'postgres'
            }
        }
        storage_app {
            database {
                url = 'jdbc:postgresql://passiflora-postgres:5432/passiflora_storage?reWriteBatchedInserts=true'
                username = 'postgres'
                password = 'postgres'
            }
        }
    }


    // 线上环境
    prod {
        nacos {
            serverAddr = 'passiflora-nacos:8848'
        }
        redis {
            host = "passiflora-redis"
            port = "6379"
            database = '8'
        }
        minio {
            endpoint = 'http://passiflora-minio:9000'
            accessKey = 'ahCsPaHipn934FJi9oa0'
            secretKey = 'jFPZSumZREdr9MLbhZht0UisaivL92XTvvM8nr66'
        }
        system_app {
            database {
                url = 'jdbc:postgresql://passiflora-postgres:5432/passiflora_system?reWriteBatchedInserts=true'
                username = 'postgres'
                password = 'postgres'
            }
        }
        storage_app {
            database {
                url = 'jdbc:postgresql://passiflora-postgres:5432/passiflora_storage?reWriteBatchedInserts=true'
                username = 'postgres'
                password = 'postgres'
            }
        }
    }
}