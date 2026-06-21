pipeline {
    agent any
    stages {
        stage("Build") {
            steps {
                script {
                    // Vérification si le conteneur 'mail_app0' existe avant de le supprimer
                    def containerExists = sh(script: "docker ps -a -q -f name=agence-app", returnStdout: true).trim()
                    if (containerExists) {
                        sh "docker rm -f agence-app"
                    } else {
                        echo "Le conteneur 'agence-app' n'existe pas."
                    }

                    // Vérification si l'image 'mail_app' existe avant de la supprimer
                    def imageExists = sh(script: "docker images -q agence-app", returnStdout: true).trim()
                    if (imageExists) {
                        sh "docker rmi -f agence-app"
                    } else {
                        echo "L'image 'agence-app' n'existe pas."
                    }

                    // Compilation avec Maven
                    tool 'Maven 3.6.3'
                    def mvnStatus = sh(script: "mvn clean install -DskipTests", returnStatus: true)
                    if (mvnStatus != 0) {
                        error "La compilation Maven a échoué"
                    }
                }
            }
        }
        stage("Run") {
            steps {
                script {
                    // Construire l'image Docker
                    sh "docker build -t agence-app ."

                    // Démarrer un nouveau conteneur
                    sh "docker run -d --name agence-app --network host agence-app"
                }
            }
        }
    }
}