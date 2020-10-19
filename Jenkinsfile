pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                sh 'mvn clean verify'
                sh 'pact-broker publish --consumer-app-version ${GIT_COMMIT} --broker-base-url "${PACT_BROKER_HOST}:${PACT_BROKER_PORT}" ./target/pacts --tag build'
            }
        }

        stage('Deploy to Test') {
            environment {
                ENV_TAG = 'Test'
            }
            steps {
                input 'Do you Want to Deploy To Test?'
                sh 'pact-broker can-i-deploy --pacticipant ${PACTICIPANT} --version ${GIT_COMMIT} --to ${ENV_TAG} --broker-base-url "${PACT_BROKER_HOST}:${PACT_BROKER_PORT}"'
                echo 'Deploying to Test environment'
                echo 'Deployed to Test Environment'
                sh 'pact-broker create-version-tag --pacticipant ${PACTICIPANT} --version ${GIT_COMMIT} --tag ${ENV_TAG} --broker-base-url "${PACT_BROKER_HOST}:${PACT_BROKER_PORT}"'
            }
        }
        stage('Deploy to Production') {
            environment {
                ENV_TAG = 'Prod'
            }
            steps {
                input 'Do you Want to Deploy To Production?'
                sh 'pact-broker can-i-deploy --pacticipant ${PACTICIPANT} --version ${GIT_COMMIT} --to ${ENV_TAG} --broker-base-url "${PACT_BROKER_HOST}:${PACT_BROKER_PORT}"'
                echo 'Deploying to Production environment'
                echo 'Deployed to Production Environment'
                sh 'pact-broker create-version-tag --pacticipant ${PACTICIPANT} --version ${GIT_COMMIT} --tag ${ENV_TAG} --broker-base-url "${PACT_BROKER_HOST}:${PACT_BROKER_PORT}"'
            }
        }
    }
    environment {
        PACTICIPANT = 'activity-service'
    }
}
