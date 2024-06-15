pipeline {
  agent any
  environment {
    def change = ''
  }
  stages {
    stage('Deploy') {
      steps {
        script {
          change = getChanges()
        }
        sh 'docker-compose up -d --build'
      }
    }
  }
}