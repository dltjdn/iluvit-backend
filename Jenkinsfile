node("Master"){
    stage('git pull'){
        sh "pwd"
        git branch: 'release',
                credentialsId: 'ILUVIT_BACK_DEPLOY_KEY',
                url: 'git@github.com:FISOLUTION/ILUVIT_BACK.git'
        sh "ls -lat"
    }
    stage('build'){
        sh "chmod +x gradlew"
        sh "./gradlew bootJar"
    }
    stage('build and push image'){
        def CURRENT_PROFILE
        def IDLE_PROFILE
        def IDLE_PORT
        sh "curl -s https://api.iluvit.app/profile > output"
        CURRENT_PROFILE = readFile 'output'
        echo CURRENT_PROFILE
        if (CURRENT_PROFILE == 'release1'){
            IDLE_PROFILE = 'release2'
            IDLE_PORT = '8082'
        } else{
            IDLE_PROFILE = 'release1'
            IDLE_PORT = '8081'
        }
        echo IDLE_PROFILE
        image = docker.build("fisolution/iluvit_back", "--build-arg IDLE_PROFILE=${IDLE_PROFILE} -t ${IDLE_PROFILE} .")
        docker.withRegistry("https://registry.hub.docker.com", "fisolution_docker_hub"){
            image.push("${env.BUILD_NUMBER}")
            echo "image.push"
        }
    }
}
node("ILUVIT_BACK"){
    def CURRENT_PROFILE
    def CURRENT_STATE
    def IDLE_PROFILE
    def IDLE_PORT
    stage('server run'){
        echo env.BUILD_NUMBER
        sh "curl -s https://api.iluvit.app/profile > output"
        CURRENT_STATE = readFile 'output'
        echo CURRENT_STATE
        if (CURRENT_STATE == 'release1'){
            CURRENT_PROFILE = 'release1'
            IDLE_PROFILE = 'release2'
            IDLE_PORT = '8082'
        } else {
            if (CURRENT_STATE == 'release2') {
                CURRENT_PROFILE = 'release2'
            } else {
                CURRENT_PROFILE = null
            }
            IDLE_PROFILE = 'release1'
            IDLE_PORT = '8081'
        }
        sh "docker ps -a | grep ${IDLE_PROFILE} | awk '{print \$1}' > output"
        ISRUN = readFile 'output'
        echo ISRUN
        if (ISRUN != null) {
            sh "docker stop ${IDLE_PROFILE}"
            sh "docker rm ${IDLE_PROFILE}"
        }
        echo "docker image pull"
        sh "docker pull fisolution/iluvit_back:${env.BUILD_NUMBER}"
        sh "docker run --name ${IDLE_PROFILE} -d -p ${IDLE_PORT}:8443 fisolution/iluvit_back:${env.BUILD_NUMBER}"
        sh "sleep 10"
    }
    stage('port switch'){
        echo "for loop"
        echo IDLE_PORT
        def RESPONSE
        for (int i =0; i < 10; i++){
            try {
                sh "curl -s https://api.iluvit.app/actuator/health | grep UP > output"
                RESPONSE = readFile 'output'
                echo RESPONSE
                break
            } catch (Exception e) {
                RESPONSE = false
                sh "sleep 5"
            }
        }
        if (RESPONSE){
            sh "echo 'set \$service_url https://127.0.0.1:${IDLE_PORT};' | tee /etc/nginx/conf.d/service-url.inc"
            sh "service nginx reload"
        }
        else{
            echo "> application deploy fail"
        }
    }
    stage('delete image and container') {
        def DELETED
        if (CURRENT_PROFILE != null) {
            sh "docker ps -a| grep ${CURRENT_PROFILE} > output"
            DELETED = readFile 'output'
        }
        if (DELETED != null) {
            sh "docker stop ${CURRENT_PROFILE}"
            sh "docker rm ${CURRENT_PROFILE}"
        }
    }
}