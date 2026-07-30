pipeline {
	agent { label 'agent-1' }

	tools {
		maven "Maven3"
		jdk "JDK21"
	}

	stages {
		stage('Checkout rawp'){
			steps {
				git branch: 'main',
					credentialsId: 'lukadjo98-git',
					url: 'https://github.com/lukadjo98/rawp.git'
			}
		}
		stage('Build'){
			steps {
				dir('rawp'){
                    withCredentials([usernamePassword(
                        credentialsId: 'lukadjo98-nexus',
                        usernameVariable: 'REPO_USERNAME',
                        passwordVariable: 'REPO_PASSWORD'
                    )]) {
                        configFileProvider([configFile(fileId: 'maven-settings', variable: 'MAVEN_SETTINGS')]) {
                            sh 'mvn -s $MAVEN_SETTINGS deploy'
                        }
                    }
				}
			}
		}
	}
}