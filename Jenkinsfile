pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    // Build whenever GitHub sends a push webhook (needs the GitHub plugin)
    triggers {
        githubPush()
    }

    environment {
        DEPLOY_DIR = '/opt/demo-app'
        SERVICE    = 'demo-app'
    }

    stages {
        stage('Build & Test') {
            steps {
                sh 'mvn -B clean package'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Deploy') {
            steps {
                sh '''
                    set -e
                    # keep the previous jar so we can roll back
                    [ -f $DEPLOY_DIR/app.jar ] && cp $DEPLOY_DIR/app.jar $DEPLOY_DIR/app.jar.prev
                    # copy under a temp name, then rename (atomic swap)
                    cp target/app.jar $DEPLOY_DIR/app.jar.new
                    mv $DEPLOY_DIR/app.jar.new $DEPLOY_DIR/app.jar
                    sudo systemctl restart $SERVICE
                '''
            }
        }

        stage('Verify') {
            steps {
                sh '''
                    for i in $(seq 1 30); do
                        if curl -fs http://localhost:8081/actuator/health | grep -q '"UP"'; then
                            echo "App is healthy"
                            exit 0
                        fi
                        sleep 2
                    done
                    echo "Health check failed - rolling back"
                    if [ -f $DEPLOY_DIR/app.jar.prev ]; then
                        cp $DEPLOY_DIR/app.jar.prev $DEPLOY_DIR/app.jar
                        sudo systemctl restart $SERVICE
                    fi
                    exit 1
                '''
            }
        }
    }

    post {
        success { echo 'Deployed successfully.' }
        failure { echo 'Pipeline failed - check the stage logs above.' }
    }
}
