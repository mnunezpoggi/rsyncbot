
pipeline {
    agent { label 'docker' }

    stages {
        stage('Pull') {
            steps {
                ircNotify notifyOnStart:true
                checkout scm
            }
        }
        stage('Build') {
            steps {
                sh 'docker build -t rsyncbot:main .'
            }
        }
        stage('Migrate Database'){
            steps {
                script { try { sh 'docker container stop rsyncbot' } catch (Exception e) { } }
                script { try { sh 'docker container rm rsyncbot' } catch (Exception e) { } }
                
            }
        }
        stage('Deploy') {
            steps {
                  sh "docker run -v $RSYNCBOT_KEYS:/usr/src/app/keys -v $RSYNCBOT_CONFIG:/usr/src/app/config  --name=rsyncbot -d --restart=always rsyncbot:main"
                  //sh "echo asdf"
            }
        }

    }
    post {
        always {
            ircNotify notificationStrategy:'all'
        }
    }
}
