node("Master"){

    stage('git pull'){
        echo "============================git pull============================"

        git branch: 'release',
                credentialsId: 'ILUVIT_BACK_DEPLOY_KEY',
                url: 'git@github.com:FISOLUTION/ILUVIT_BACK.git'

    }

    stage('build'){
        echo "============================build project============================"

        sh "chmod +x gradlew"
        sh './gradlew'
        sh './gradlew clean build --stacktrace'

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

        echo "========= profile Infos...\n" +
                "{ \n" +
                "\tcurrent profile: ${CURRENT_PROFILE} \n" +
                "\tnext profile: ${IDLE_PROFILE} \n" +
                "\tnext app port: ${IDLE_PORT} \n" +
                "}"

        echo "============================ image build end ============================"

        image = docker.build("fisolution/iluvit_back:${env.BUILD_NUMBER}", "--build-arg IDLE_PROFILE=${IDLE_PROFILE} .")

        echo "========= show Iluvit_Backend images ========"
        sh(script: "docker images -a|grep \"iluvit_back\"")

        echo "======== push image to dockerHub..."
        docker.withRegistry("", "fisolution_docker_hub"){
            image.push()
        }
    }
    stage('delete remain images'){

        echo "============================ delete images ============================"

        echo "====== checking remain images..."
        String imageNames = sh(script: "docker images -a|grep \"iluvit_back\"|awk '\$2 <= ${env.BUILD_ID} - 3 {print \$1}'", returnStdout: true)
        String tag = sh(script: "docker images -a|grep \"iluvit_back\"|awk '\$2 <= ${env.BUILD_ID} - 3 {print \$2}'", returnStdout: true)
        if(imageNames.length()){
            String[] names = imageNames.split('\n')
            String[] tags = tag.split('\n')
            int i = 0
            for(String name: names){
                try {
                    String temp = name + ':' + tags[i]
                    sh "sudo docker rmi ${temp} --force"
                    echo "======= ${temp} deleted..."
                    i++
                } catch (Exception e){
                    continue
                }
            }
        }

        echo "============================ delete images end ============================"
    }
}

node("ILUVIT_BACK"){
    def CURRENT_PROFILE
    def CURRENT_STATE
    def IDLE_PROFILE
    def IDLE_PORT
    stage('server run'){
        "===== checking server Infos..."
        echo env.BUILD_NUMBER
        CURRENT_STATE = sh(script: "curl -s https://api.iluvit.app/profile", returnStdout: true)
        if (CURRENT_STATE == 'release1'){
            CURRENT_PROFILE = 'release1'
            IDLE_PROFILE = 'release2'
            IDLE_PORT = '8082'
        } else {
            if (CURRENT_STATE == 'release2') {
                CURRENT_PROFILE = 'release2'
            } else {
                CURRENT_STATE = 'not Running'
                CURRENT_PROFILE = 'not Running'
            }
            IDLE_PROFILE = 'release1'
            IDLE_PORT = '8081'
        }

        echo "===== server Infos..." +
                "{ \n" +
                "\tcurrent profile: ${CURRENT_PROFILE} \n" +
                "\tnext profile: ${IDLE_PROFILE} \n" +
                "\tnext app port: ${IDLE_PORT} \n" +
                "}"

        echo "===== check rest container..."
        String ISRUN = sh(script: "docker ps -a | grep ${IDLE_PROFILE} | awk '{print \$1}'", returnStdout: true)

        if (ISRUN.length()) {
            echo "current rest container is ${IDLE_PROFILE}"
            sh "docker stop ${IDLE_PROFILE}"
            sh "docker rm ${IDLE_PROFILE}"
        }

        echo "===== pull image from dockerHub..."
        sh "docker pull fisolution/iluvit_back:${env.BUILD_NUMBER}"
        sh "docker run --name ${IDLE_PROFILE} -d -p ${IDLE_PORT}:8443 fisolution/iluvit_back:${env.BUILD_NUMBER}"
        sh(script: "docker images -a|grep \"iluvit_back\"")
        sh "sleep 10"
    }

    stage('port switch'){
        def RESPONSE
        echo "======== polling to application..."
        for (int i =0; i < 10; i++){
            try {
                RESPONSE = sh(script: "curl -s https://localhost:${IDLE_PORT}/actuator/health | grep UP", returnStdout: true)
                break
            } catch (Exception e) {
                RESPONSE = false
                sh "sleep 5"
            }
        }
        if (RESPONSE){
            echo "======= application loaded..."
            echo "======= switch port..."
            sh "echo 'set \$service_url https://127.0.0.1:${IDLE_PORT};' | tee /etc/nginx/conf.d/service-url.inc"
            sh "service nginx reload"
        }
        else{
            echo "> application deploy fail"
        }
    }

    stage('delete container') {
        echo "====== delete container..."
        def DELETED
        if (CURRENT_PROFILE != null) {
            DELETED = sh(script: "docker ps -a| grep ${CURRENT_PROFILE} > output", returnStdout: true)
        }
        if (DELETED != null) {
            sh "docker stop ${CURRENT_PROFILE}"
            sh "docker rm ${CURRENT_PROFILE}"
        }
    }

    stage('delete remain container'){
        echo "====== checking remain images..."
        String imageNames = sh(script: "docker images -a|grep \"iluvit_back\"|awk '\$2 <= ${env.BUILD_ID} - 3 {print \$1}'", returnStdout: true)
        String tag = sh(script: "docker images -a|grep \"iluvit_back\"|awk '\$2 <= ${env.BUILD_ID} - 3 {print \$2}'", returnStdout: true)
        if(imageNames.length()){
            String[] names = imageNames.split('\n')
            String[] tags = tag.split('\n')
            int i = 0
            for(String name: names){
                try {
                    String temp = name + ':' + tags[i]
                    sh "sudo docker rmi ${temp} --force"
                    echo "======= ${temp} deleted..."
                    i++
                } catch (Exception e){
                    continue
                }
            }
        }
    }
}