pipeline {
    agent any;
    
    tools {
        // Install the maven version configured as "MyMaven" and Add it to the path
        maven "MyMaven"
    }
    
    stages {
        stage("Code Checkout") {
            steps {
                git branch: "main", url: "https://github.com/urvish91/igp1.git"
            }
        }
        stage("Maven Compile") {
            steps {
                sh "mvn compile"
            }
        }
        stage("Maven Test") {
            steps {
                sh "mvn test"
            }
        }
        stage("Maven Package") {
            steps {
                sh "mvn package"
            }
        }
        stage("Publish Over SSH") {
            steps {
                sh "rm -rf target/webapp.war"
                sh "mv target/*.war target/webapp.war"
                sshPublisher(publishers: [sshPublisherDesc(configName: 'Ansible-Server', transfers: [sshTransfer(cleanRemote: false, excludes: '', execCommand: 'ansible-playbook opt/docker/create-image-igp1.yml', execTimeout: 120000, flatten: true, makeEmptyDirs: false, noDefaultExcludes: false, patternSeparator: '[, ]+', remoteDirectory: '/opt/docker', remoteDirectorySDF: false, removePrefix: '', sourceFiles: 'target/webapp.war')], usePromotionTimestamp: false, useWorkspaceInPromotion: false, verbose: true)])
            }
        }
        stage("Deploy as Pod") {
            steps {
                sshPublisher(publishers: [sshPublisherDesc(configName: 'Ansible-Server', transfers: [sshTransfer(cleanRemote: false, excludes: '', execCommand: '''ansible-playbook /opt/docker/kubernetes-deployment.yml
ansible-playbook /opt/docker/kubernetes-service.yml
''', execTimeout: 120000, flatten: false, makeEmptyDirs: false, noDefaultExcludes: false, patternSeparator: '[, ]+', remoteDirectory: '', remoteDirectorySDF: false, removePrefix: '', sourceFiles: '')], usePromotionTimestamp: false, useWorkspaceInPromotion: false, verbose: false)])
            }
        }
        
    }
}