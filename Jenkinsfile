pipeline {
    agent { dockerfile true }

    environment {
        // Название образа и путь до embedded registry
        IMAGE_NAME = 'userservice'
        NAMESPACE = 'uralostrov6-dev'
        REGISTRY = 'image-registry.openshift-image-registry.svc:5000'
        FULL_IMAGE = "${REGISTRY}/${NAMESPACE}/${IMAGE_NAME}:latest"

        // OpenShift API (для oc login)
        OCP_API = 'https://api.rm1.0a51.p1.openshiftapps.com:6443'
        OCP_TOKEN = 'sha256~7aNP_ZZV8RDQqsG0Ql7uA3ruPMco2rQncsbVgRz_B7w'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo 'Building JAR...'
                sh './gradlew clean build'
            }
        }

        stage('Test') {
            steps {
                echo 'Running tests...'
                sh './gradlew test'
            }
        }

        stage('Docker Build') {
            steps {
                echo "Building Docker image: ${FULL_IMAGE}"
                sh "docker build -t ${FULL_IMAGE} ."
            }
        }

        stage('Docker Login & Push') {
            steps {
                echo "Logging into OpenShift and pushing image..."
                sh '''
                    oc login --token=$OCP_TOKEN --server=$OCP_API
                    oc whoami -t > /tmp/token
                    docker login -u openshift -p $(cat /tmp/token) $REGISTRY
                    docker push $FULL_IMAGE
                '''
            }
        }

        stage('Deploy') {
            steps {
                echo "Rolling out deployment restart..."
                sh 'oc rollout restart deployment/userservice-deployment -n $NAMESPACE'
            }
        }
    }

    post {
        always {
            junit '**/build/test-results/test/*.xml'
        }
    }
}