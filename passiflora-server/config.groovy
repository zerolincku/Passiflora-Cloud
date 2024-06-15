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
            endpoint = 'http://passiflora-minio:9090'
            accessKey = '2Uwho01oDDbxZpzEjfb4'
            secretKey = 'KIF9NCJR6SmyeVRAloQhUfrofI11FfGAhnsAXAoa'
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
            endpoint = 'http://passiflora-minio:9090'
            accessKey = 'pTHkPFHbhmBqgjVR35Ko'
            secretKey = 'aPAtdzHGFTsI2qcxM7Q0krunDI80dd7Gtgm7grCR'
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