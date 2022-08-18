node("Master"){

    stage('git pull'){
        echo "============================git pull============================"

        git branch: 'release',
                credentialsId: 'ILUVIT_BACK_DEPLOY_KEY',
                url: 'git@github.com:FISOLUTION/ILUVIT_BACK.git'

        echo "============================git pull finish============================"
    }

    stage('build'){
        echo "============================build project============================"

        sh "chmod +x gradlew"
        sh "./gradlew bootJar"

        echo "============================project build end============================"
    }

    stage('build and push image'){

        echo "============================image build============================"

        def CURRENT_PROFILE
        def IDLE_PROFILE
        def IDLE_PORT

        echo "======checking CURRENT PROFILE...======"

        CURRENT_PROFILE = sh(script: "curl -s https://api.iluvit.app/profile", returnStdout: true)

        if (CURRENT_PROFILE == 'release1'){
            IDLE_PROFILE = 'release2'
            IDLE_PORT = '8082'
        } else{
            IDLE_PROFILE = 'release1'
            IDLE_PORT = '8081'
        }
        echo "{ \n" +
                "current profile: ${CURRENT_PROFILE} \n" +
                "next profile: ${IDLE_PROFILE} \n" +
                "next app port: ${IDLE_PORT} \n" +
                "}"
        image = docker.build("fisolution/iluvit_back:${env.BUILD_NUMBER}", "--build-arg IDLE_PROFILE=${IDLE_PROFILE} .")

        String imageName = sh(script: "docker images -a|grep \"iluvit_back\"")

        docker.withRegistry("", "fisolution_docker_hub"){
            image.push()
            echo "image.push"
        }
    }
    stage('delete remain images'){
        String imageNames = sh(script: "docker images -a|grep \"iluvit_back\"|awk '\$2 <= ${env.BUILD_ID} - 3 {print \$1}'", returnStdout: true)
        String tag = sh(script: "docker images -a|grep \"iluvit_back\"|awk '\$2 <= ${env.BUILD_ID} - 3 {print \$2}'", returnStdout: true)
        if(imageNames.length()){
            String[] names = imageNames.split('\n')
            String[] tags = tag.split('\n')
            int i = 0
            for(String name: names){
                echo name
                echo tags[i]
                try {
                    String temp = name + ':' + tags[i]
                    sh "sudo docker rmi ${temp} --force"
                    i++
                } catch (Exception e){
                    continue
                }
            }
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
        String ISRUN = sh(script: "docker ps -a | grep ${IDLE_PROFILE} | awk '{print \$1}'", returnStdout: true)
        if (ISRUN.length()) {
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
    stage('delete container') {
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
    stage('delete remain container'){

    }
}