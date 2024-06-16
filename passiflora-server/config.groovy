environments {
    // 开发环境
    dev {
        nacos {
            serverAddr = 'passiflora-nacos:8848'
        }
        redis {
            host = "passiflora-redis"
            port = "6379"
            password = "123456"
            database = '8'
        }
        minio {
            endpoint = 'http://passiflora-minio:9000'
            accessKey = '2Zj1xM3B7ti9rDt8TjIR'
            secretKey = 'yHJ08hiDHAnd3ChR6CvETti8X1dqDvxgLxW0ojwJ'
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
            password = "123456"
            database = '8'
        }
        minio {
            endpoint = 'http://passiflora-minio:9000'
            accessKey = 'U2eBKxUCW5I70Xx4D0fR'
            secretKey = 'NDPeMlhQLO36o0R3pG9lE4syXeZInm5o6dW6VfOa'
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