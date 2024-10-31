pipeline {
    agent any
    environment {
        IMAGE_NAME = "shifer/gateway:latest"
        CONSUL_HOST = "consul"
        CONSUL_PORT = "8500"
        CONFIG_URI = "http://configservice:8888"
    }
    stages {
        stage('Gradle Build') {
            steps {
                sh './gradlew clean build'
                sh 'ls -la build/libs'
            }
        }
        stage('Docker Build') {
            steps {
                sh "docker build -t ${IMAGE_NAME} ."
            }
        }
        stage('Docker Push') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'docker-credentials', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh "docker login -u ${DOCKER_USER} -p ${DOCKER_PASS}"
                    sh "docker push ${IMAGE_NAME}"
                }
            }
        }
        stage('Deploy to Minikube') {
    steps {
        script {
            def deploymentExists = sh(
                script: "kubectl get deployment gateway --ignore-not-found",
                returnStatus: true
            ) == 0

            if (!deploymentExists) {

                sh """
                    kubectl create deployment gateway --image=${IMAGE_NAME} 
                    kubectl expose deployment gateway --type=ClusterIP --port=8888
                 """
            } else {
                sh "kubectl set image deployment/gateway gateway=${IMAGE_NAME}"
            }
            sh """
                kubectl set env deployment/gateway SPRING_CLOUD_CONSUL_HOST=${CONSUL_HOST} 
                kubectl set env deployment/gateway SPRING_CLOUD_CONSUL_PORT=${CONSUL_PORT}
                kubectl set env deployment/gateway SPRING_CLOUD_CONFIG_URI=${CONFIG_URI} 
            """
                }
            }
        }
    }
}
